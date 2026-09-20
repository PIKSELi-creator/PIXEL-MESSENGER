package com.pixelchat.ui

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.Switch

fun MainActivity.showProfile() {

    val root = createRoot()

    pcHeader(
        root,
        "Профиль",
        "Мой аккаунт"
    )

    pcSpace(root, 12)

    root.addView(
        pcAvatar(
            "P",
            88
        )
    )

    pcSpace(root, 12)

    root.addView(
        pcTitle(
            "Pixel User",
            21f
        )
    )

    root.addView(
        pcLabel(
            "@pixel_user"
        )
    )

    pcSpace(root, 20)

    root.addView(
        pcButton(
            "Изменить профиль"
        ) {
            showEditProfile()
        }
    )

    root.addView(
        pcButton(
            "Настройки",
            false
        ) {
            showSettings()
        }
    )

    root.addView(
        pcButton(
            "Выйти",
            false
        ) {
            showLogin()
        }
    )

    pcNavigation(
        root,
        "profile"
    )
}

fun MainActivity.showEditProfile() {

    val root = createRoot()

    pcHeader(
        root,
        "Профиль",
        "Редактирование"
    )

    root.addView(
        pcInput("Имя")
    )

    root.addView(
        pcInput("@username")
    )

    root.addView(
        pcInput("О себе")
    )

    root.addView(
        pcButton(
            "Сохранить"
        ) {
            showProfile()
        }
    )

    root.addView(
        pcButton(
            "Назад",
            false
        ) {
            showProfile()
        }
    )
}

fun MainActivity.showSettings() {

    val root = createRoot()

    pcHeader(
        root,
        "Настройки",
        "PIXEL CHAT"
    )

    pcSection(
        root,
        "АККАУНТ"
    )

    root.addView(
        pcButton(
            "Изменить профиль"
        ) {
            showEditProfile()
        }
    )

    root.addView(
        pcButton(
            "Безопасность",
            false
        ) {}
    )

    pcSection(
        root,
        "ПРИЛОЖЕНИЕ"
    )

    addSwitchRow(
        root,
        "Уведомления",
        true
    )

    addSwitchRow(
        root,
        "Звуки",
        true
    )

    addSwitchRow(
        root,
        "Тёмная тема",
        true
    )

    pcSection(
        root,
        "ИНФОРМАЦИЯ"
    )

    root.addView(
        pcButton(
            "О PIXEL CHAT",
            false
        ) {
            showAbout()
        }
    )

    root.addView(
        pcButton(
            "Назад",
            false
        ) {
            showProfile()
        }
    )
}

private fun MainActivity.addSwitchRow(
    parent: LinearLayout,
    title: String,
    enabled: Boolean
) {

    val row = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setPadding(
            dp(15),
            dp(7),
            dp(10),
            dp(7)
        )

        background = pcBackground(
            panel,
            15,
            line
        )

        layoutParams =
            LinearLayout.LayoutParams(
                -1,
                dp(58)
            ).apply {
                bottomMargin = dp(8)
            }
    }

    row.addView(
        pcLabel(
            title,
            15f
        ),
        LinearLayout.LayoutParams(
            0,
            -2,
            1f
        )
    )

    row.addView(
        Switch(this).apply {
            isChecked = enabled
            isLongClickable = false
        }
    )

    parent.addView(row)
}

fun MainActivity.showAbout() {

    val root = createRoot()

    pcHeader(
        root,
        "О PIXEL CHAT",
        "Информация"
    )

    pcSpace(root, 15)

    root.addView(
        pcTitle(
            "PIXEL CHAT",
            28f
        )
    )

    pcSpace(root, 6)

    root.addView(
        pcLabel(
            "Версия 0.1.0"
        )
    )

    pcSpace(root, 18)

    root.addView(
        pcLabel(
            "Нативный Android-интерфейс PIXEL CHAT."
        )
    )

    root.addView(
        pcLabel(
            "UI написан на Kotlin."
        )
    )

    root.addView(
        pcLabel(
            "Сетевая часть проекта остаётся на C++ и Java."
        )
    )

    pcSpace(root, 22)

    root.addView(
        pcButton(
            "Назад",
            false
        ) {
            showSettings()
        }
    )
}
