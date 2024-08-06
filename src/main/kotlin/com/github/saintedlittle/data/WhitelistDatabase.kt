package com.github.saintedlittle.data

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WhitelistDatabase(dbUrl: String) {
    private val connection: Connection = DriverManager.getConnection(dbUrl)
    private val blockedUsernamesCache: HashMap<String, Long> = HashMap()

    init {
        createTables()
        loadBlockedUsernamesIntoCache()
    }

    private fun createTables() {
        val createWhitelistTableSQL = """
            CREATE TABLE IF NOT EXISTS whitelist (
                telegram_id INTEGER NOT NULL,
                username TEXT NOT NULL,
                added_date TEXT,
                limit INTEGER DEFAULT 5, -- Default limit if not set
                PRIMARY KEY (telegram_id, username)
            )
        """
        val createBlockedUsernamesTableSQL = """
            CREATE TABLE IF NOT EXISTS blocked_usernames (
                telegram_id INTEGER NOT NULL,
                username TEXT NOT NULL,
                blocked_date TEXT,
                PRIMARY KEY (telegram_id, username)
            )
        """
        val createBlockedAllUsernamesTableSQL = """
            CREATE TABLE IF NOT EXISTS blocked_all_usernames (
                telegram_id INTEGER NOT NULL,
                blocked_date TEXT,
                PRIMARY KEY (telegram_id)
            )
        """

        connection.createStatement().use { statement ->
            statement.execute(createWhitelistTableSQL)
            statement.execute(createBlockedUsernamesTableSQL)
            statement.execute(createBlockedAllUsernamesTableSQL)
        }
    }

    fun addEntry(entry: WhitelistEntry) {
        val currentLimit = getLimit(entry.telegramId)

        if (getEntryCount(entry.telegramId) >= currentLimit) {
            throw SQLException("Maximum number of usernames for this telegram ID reached.")
        }

        val insertSQL = """
            INSERT INTO whitelist (telegram_id, username, added_date)
            VALUES (?, ?, ?)
        """
        connection.prepareStatement(insertSQL).use { statement ->
            statement.setLong(1, entry.telegramId)
            statement.setString(2, entry.username)
            statement.setString(3, entry.addedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            statement.executeUpdate()
        }
    }

    fun getEntry(telegramId: Long, username: String): WhitelistEntry? {
        val selectSQL = """
            SELECT telegram_id, username, added_date
            FROM whitelist
            WHERE telegram_id = ? AND username = ?
        """
        connection.prepareStatement(selectSQL).use { statement ->
            statement.setLong(1, telegramId)
            statement.setString(2, username)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                val id = resultSet.getLong("telegram_id")
                val user = resultSet.getString("username")
                val addedDateStr = resultSet.getString("added_date")
                val addedDate = addedDateStr?.let { LocalDateTime.parse(it, DateTimeFormatter.ISO_LOCAL_DATE_TIME) }
                WhitelistEntry(id, user, addedDate)
            } else {
                null
            }
        }
    }

    fun removeEntry(telegramId: Long, username: String) {
        val deleteSQL = """
            DELETE FROM whitelist
            WHERE telegram_id = ? AND username = ?
        """
        connection.prepareStatement(deleteSQL).use { statement ->
            statement.setLong(1, telegramId)
            statement.setString(2, username)
            statement.executeUpdate()
        }
    }

    fun blockUsername(entry: BlockedUsername) {
        val insertSQL = """
            INSERT INTO blocked_usernames (telegram_id, username, blocked_date)
            VALUES (?, ?, ?)
        """
        connection.prepareStatement(insertSQL).use { statement ->
            statement.setLong(1, entry.telegramId)
            statement.setString(2, entry.username)
            statement.setString(3, entry.blockedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            statement.executeUpdate()
        }

        // Update the cache
        blockedUsernamesCache[entry.username] = entry.telegramId
    }

    fun unblockUsername(telegramId: Long, username: String) {
        val deleteSQL = """
            DELETE FROM blocked_usernames
            WHERE telegram_id = ? AND username = ?
        """
        connection.prepareStatement(deleteSQL).use { statement ->
            statement.setLong(1, telegramId)
            statement.setString(2, username)
            statement.executeUpdate()
        }

        // Update the cache
        blockedUsernamesCache.remove(username)
    }

    fun blockAllUsernames(entry: BlockedAllUsernames) {
        val insertSQL = """
            INSERT INTO blocked_all_usernames (telegram_id, blocked_date)
            VALUES (?, ?)
        """
        connection.prepareStatement(insertSQL).use { statement ->
            statement.setLong(1, entry.telegramId)
            statement.setString(2, entry.blockedDate?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            statement.executeUpdate()
        }

        // Update the cache
        val usernamesToBlock = getUsernamesForTelegramId(entry.telegramId)
        usernamesToBlock.forEach { username ->
            blockedUsernamesCache[username] = entry.telegramId
        }
    }

    fun unblockAllUsernames(telegramId: Long) {
        val deleteSQL = """
            DELETE FROM blocked_all_usernames
            WHERE telegram_id = ?
        """
        connection.prepareStatement(deleteSQL).use { statement ->
            statement.setLong(1, telegramId)
            statement.executeUpdate()
        }

        // Update the cache
        val usernamesToUnblock = getUsernamesForTelegramId(telegramId)
        usernamesToUnblock.forEach { username ->
            blockedUsernamesCache.remove(username)
        }
    }

    fun isUsernameBlocked(telegramId: Long, username: String): Boolean {
        return blockedUsernamesCache.containsKey(username) && blockedUsernamesCache[username] == telegramId
    }

    fun isUsernameBlocked(username: String): Boolean {
        return blockedUsernamesCache.containsKey(username)
    }

    fun isAllUsernamesBlocked(telegramId: Long): Boolean {
        val selectSQL = """
            SELECT 1 FROM blocked_all_usernames
            WHERE telegram_id = ?
        """
        connection.prepareStatement(selectSQL).use { statement ->
            statement.setLong(1, telegramId)
            val resultSet = statement.executeQuery()
            return resultSet.next()
        }
    }

    fun getLimit(telegramId: Long): Int {
        val selectSQL = """
            SELECT limit FROM whitelist
            WHERE telegram_id = ?
            LIMIT 1
        """
        connection.prepareStatement(selectSQL).use { statement ->
            statement.setLong(1, telegramId)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSet.getInt("limit") else 5 // Default limit if not set
        }
    }

    fun updateLimit(telegramId: Long, newLimit: Int) {
        val updateSQL = """
            UPDATE whitelist
            SET limit = ?
            WHERE telegram_id = ?
        """
        connection.prepareStatement(updateSQL).use { statement ->
            statement.setInt(1, newLimit)
            statement.setLong(2, telegramId)
            statement.executeUpdate()
        }
    }

    private fun getEntryCount(telegramId: Long): Int {
        val countSQL = """
            SELECT COUNT(*) FROM whitelist
            WHERE telegram_id = ?
        """
        connection.prepareStatement(countSQL).use { statement ->
            statement.setLong(1, telegramId)
            val resultSet = statement.executeQuery()
            return if (resultSet.next()) resultSet.getInt(1) else 0
        }
    }

    private fun getUsernamesForTelegramId(telegramId: Long): List<String> {
        val selectSQL = """
            SELECT username FROM whitelist
            WHERE telegram_id = ?
        """
        return connection.prepareStatement(selectSQL).use { statement ->
            statement.setLong(1, telegramId)
            val resultSet = statement.executeQuery()
            val usernames = mutableListOf<String>()
            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"))
            }
            usernames
        }
    }

    private fun loadBlockedUsernamesIntoCache() {
        val selectSQL = """
            SELECT username, telegram_id FROM blocked_usernames
        """
        connection.prepareStatement(selectSQL).use { statement ->
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val username = resultSet.getString("username")
                val telegramId = resultSet.getLong("telegram_id")
                blockedUsernamesCache[username] = telegramId
            }
        }
    }

    fun blockUserByUsername(username: String) {
        val selectSQL = """
        SELECT telegram_id FROM whitelist
        WHERE username = ?
        LIMIT 1
    """
        val telegramId: Long? = connection.prepareStatement(selectSQL).use { statement ->
            statement.setString(1, username)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) resultSet.getLong("telegram_id") else null
        }

        if (telegramId != null) {
            val insertSQL = """
            INSERT INTO blocked_usernames (telegram_id, username, blocked_date)
            VALUES (?, ?, ?)
        """
            connection.prepareStatement(insertSQL).use { statement ->
                statement.setLong(1, telegramId)
                statement.setString(2, username)
                statement.setString(3, LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                statement.executeUpdate()
            }

            // Update the cache
            blockedUsernamesCache[username] = telegramId
        } else {
            throw SQLException("Username not found.")
        }
    }

    fun blockAllAccountsByUsername(username: String) {
        val selectSQL = """
        SELECT telegram_id FROM whitelist
        WHERE username = ?
    """
        val telegramIds = mutableListOf<Long>()

        connection.prepareStatement(selectSQL).use { statement ->
            statement.setString(1, username)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                telegramIds.add(resultSet.getLong("telegram_id"))
            }
        }

        val insertSQL = """
        INSERT INTO blocked_usernames (telegram_id, username, blocked_date)
        VALUES (?, ?, ?)
    """
        val now = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        connection.prepareStatement(insertSQL).use { statement ->
            for (id in telegramIds) {
                statement.setLong(1, id)
                statement.setString(2, username)
                statement.setString(3, now)
                statement.addBatch()
            }
            statement.executeBatch()
        }

        // Update the cache
        blockedUsernamesCache[username] = telegramIds.first() // Keep the first telegram ID in cache
    }

    fun close() {
        connection.close()
    }
}
