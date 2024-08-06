package com.github.saintedlittle.data

import java.time.LocalDateTime

data class WhitelistEntry(
    val telegramId: Long,
    val username: String,
    val addedDate: LocalDateTime?
)

data class BlockedUsername(
    val telegramId: Long,
    val username: String,
    val blockedDate: LocalDateTime?
)

data class BlockedAllUsernames(
    val telegramId: Long,
    val blockedDate: LocalDateTime?
)
