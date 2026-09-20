package com.pixelchat.ui

import android.view.Gravity
import android.widget.LinearLayout
import android.widget.ScrollView

fun MainActivity.showHome() {

    val root = createRoot()

    val header = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val info = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    info.addView(
        pcTitle(
            "PIXEL CHAT",
            25f
        )
    )

    info.addView(
        pcLabel("Чаты")
    )

    header.addView(
        info,
        LinearLayout.LayoutParams(
            0,
            -2,
            1f
        )
    )

    header.addView(
        pcIcon("+") {
            showContacts()
        }
    )

    root.addView(header)

    pcSpace(root, 12)

    root.addView(
        pcInput("Поиск чатов")
    )

    val scroll = ScrollView(this)

    val list = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    scroll.addView(list)

    root.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )

    val chats = listOf(
        Triple(
            "Pixel Dev",
            "Привет! Как проект?",
            "12:41"
        ),
        Triple(
            "Alex",
            "Ок, договорились",
            "11:58"
        ),
        Triple(
            "Miko",
            "Давно не общались",
            "10:21"
        ),
        Triple(
            "Game Team",
            "Новое сообщение",
            "09:44"
        )
    )

    chats.forEach {
        addChat(
            list,
            it.first,
            it.second,
            it.third
        )
    }

    pcNavigation(
        root,
        "chats"
    )
}

private fun MainActivity.addChat(
    parent: LinearLayout,
    name: String,
    message: String,
    time: String
) {

    val row = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setPadding(
            dp(12),
            dp(10),
            dp(10),
            dp(10)
        )

        background = pcBackground(
            panel,
            15,
            line
        )

        layoutParams =
            LinearLayout.LayoutParams(
                -1,
                dp(72)
            ).apply {
                bottomMargin = dp(8)
            }

        setOnClickListener {
            showChat(name)
        }
    }

    row.addView(
        pcAvatar(
            name.first().toString()
        )
    )

    val info = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL

        setPadding(
            dp(12),
            0,
            0,
            0
        )
    }

    info.addView(
        pcTitle(
            name,
            16f
        )
    )

    info.addView(
        pcLabel(
            message,
            13f
        )
    )

    row.addView(
        info,
        LinearLayout.LayoutParams(
            0,
            -2,
            1f
        )
    )

    row.addView(
        pcLabel(
            time,
            11f
        )
    )

    parent.addView(row)
}

fun MainActivity.showContacts() {

    val root = createRoot()

    pcHeader(
        root,
        "Контакты",
        "Мои контакты"
    )

    addContact(
        root,
        "Pixel Dev",
        "онлайн"
    )

    addContact(
        root,
        "Alex",
        "был недавно"
    )

    addContact(
        root,
        "Miko",
        "офлайн"
    )

    addContact(
        root,
        "Game Team",
        "онлайн"
    )

    root.addView(
        pcButton(
            "+ Добавить контакт"
        ) {}
    )

    pcNavigation(
        root,
        "contacts"
    )
}

private fun MainActivity.addContact(
    parent: LinearLayout,
    name: String,
    status: String
) {

    val row = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL

        setPadding(
            dp(12),
            dp(9),
            dp(12),
            dp(9)
        )

        background = pcBackground(
            panel,
            15,
            line
        )

        layoutParams =
            LinearLayout.LayoutParams(
                -1,
                dp(64)
            ).apply {
                bottomMargin = dp(8)
            }
    }

    row.addView(
        pcAvatar(
            name.first().toString(),
            42
        )
    )

    val info = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL

        setPadding(
            dp(12),
            0,
            0,
            0
        )
    }

    info.addView(
        pcTitle(
            name,
            16f
        )
    )

    info.addView(
        pcLabel(
            status,
            12f
        ).apply {
            if (status == "онлайн") {
                setTextColor(green)
            }
        }
    )

    row.addView(
        info,
        LinearLayout.LayoutParams(
            0,
            -2,
            1f
        )
    )

    row.addView(
        pcIcon("→") {
            showChat(name)
        }
    )

    parent.addView(row)
}

fun MainActivity.showCalls() {

    val root = createRoot()

    pcHeader(
        root,
        "Звонки",
        "История"
    )

    val calls = listOf(
        "Pixel Dev" to
            "Исходящий • сегодня 12:10",

        "Alex" to
            "Пропущенный • вчера",

        "Miko" to
            "Входящий • вчера"
    )

    calls.forEach { call ->

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL

            setPadding(
                dp(12),
                dp(9),
                dp(12),
                dp(9)
            )

            background = pcBackground(
                panel,
                15,
                line
            )

            layoutParams =
                LinearLayout.LayoutParams(
                    -1,
                    dp(65)
                ).apply {
                    bottomMargin = dp(8)
                }
        }

        row.addView(
            pcAvatar(
                call.first.first().toString(),
                42
            )
        )

        val info = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL

            setPadding(
                dp(12),
                0,
                0,
                0
            )
        }

        info.addView(
            pcTitle(
                call.first,
                16f
            )
        )

        info.addView(
            pcLabel(
                call.second,
                12f
            )
        )

        row.addView(info)

        root.addView(row)
    }

    pcNavigation(
        root,
        "calls"
    )
}
