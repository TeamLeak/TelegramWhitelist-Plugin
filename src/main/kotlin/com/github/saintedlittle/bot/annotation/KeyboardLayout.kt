package com.github.saintedlittle.bot.annotation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class KeyboardLayout(
    val rows: Array<String> // Каждая строка клавиатуры в формате "BUTTON1,BUTTON2"
)
