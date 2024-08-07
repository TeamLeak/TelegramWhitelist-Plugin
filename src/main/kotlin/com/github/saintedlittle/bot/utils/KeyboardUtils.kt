package com.github.saintedlittle.bot.utils

import com.github.saintedlittle.bot.annotation.KeyboardLayout
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

object CreateKeyboard {

    fun createKeyboardRow(buttons: List<KeyboardButton>): KeyboardRow {
        return KeyboardRow(buttons)
    }

    fun createReplyKeyboard(rows: List<KeyboardRow>): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup().apply {
            resizeKeyboard = true
            keyboard = rows
        }
    }
}

object KeyboardUtil {

    fun createKeyboardFromAnnotation(clazz: KClass<*>): ReplyKeyboardMarkup? {
        val annotation = clazz.findAnnotation<KeyboardLayout>() ?: return null

        val rows = annotation.rows.map { row ->
            KeyboardRow(row.split(",").map { KeyboardButton(it) })
        }

        return CreateKeyboard.createReplyKeyboard(rows)
    }
}
