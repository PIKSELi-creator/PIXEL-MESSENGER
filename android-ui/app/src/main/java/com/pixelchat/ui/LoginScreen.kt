package com.pixelchat.ui

import android.text.InputType
import android.view.Gravity

fun MainActivity.showLogin() {

    val root = createRoot()

    root.gravity = Gravity.CENTER_HORIZONTAL

    pcSpace(root, 55)

    root.addView(
        pcTitle(
            "PIXEL CHAT",
            31f
        )
    )

    root.addView(
        pcLabel(
            "PRIVATE MESSENGER"
        )
    )

    pcSpace(root, 28)

    val login = pcInput("Логин")

    val password = pcInput("Пароль").apply {
        inputType =
            InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    root.addView(login)
    root.addView(password)

    root.addView(
        pcButton("Войти") {
            showHome()
        }
    )

    root.addView(
        pcButton(
            "Создать аккаунт",
            false
        ) {
            showRegister()
        }
    )

    pcSpace(root, 10)

    root.addView(
        pcLabel(
            "Безопасный приватный мессенджер",
            12f
        )
    )
}

fun MainActivity.showRegister() {

    val root = createRoot()

    root.addView(
        pcTitle(
            "Создание аккаунта"
        )
    )

    root.addView(
        pcLabel(
            "Регистрация PIXEL CHAT"
        )
    )

    pcSpace(root, 22)

    root.addView(
        pcInput("Логин")
    )

    root.addView(
        pcInput("Пароль").apply {
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    )

    root.addView(
        pcInput("Повторите пароль").apply {
            inputType =
                InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    )

    root.addView(
        pcButton(
            "Зарегистрироваться"
        ) {
            showHome()
        }
    )

    root.addView(
        pcButton(
            "Назад",
            false
        ) {
            showLogin()
        }
    )
}
