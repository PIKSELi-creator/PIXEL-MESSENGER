package com.pixelchat.ui

import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView

fun MainActivity.showChat(
    name: String
) {

    val root = createRoot()

    val header = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    header.addView(
        pcIcon("‹") {
            showHome()
        }
    )

    val info = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL

        setPadding(
            dp(10),
            0,
            0,
            0
        )
    }

    info.addView(
        pcTitle(
            name,
            19f
        )
    )

    info.addView(
        pcLabel(
            "● онлайн",
            12f
        ).apply {
            setTextColor(green)
        }
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
        pcIcon("☎") {}
    )

    root.addView(header)

    pcDivider(root)

    val scroll = ScrollView(this)

    val messages = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL

        setPadding(
            0,
            dp(12),
            0,
            dp(12)
        )
    }

    scroll.addView(messages)

    root.addView(
        scroll,
        LinearLayout.LayoutParams(
            -1,
            0,
            1f
        )
    )

    addMessage(
        messages,
        "Привет!",
        false
    )

    addMessage(
        messages,
        "Привет! Как дела?",
        true
    )

    addMessage(
        messages,
        "Нормально. PIXEL CHAT уже собираем.",
        false
    )

    addMessage(
        messages,
        "Кайф. Жду релиз.",
        true
    )

    val sendBar = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
    }

    val messageInput = pcInput(
        "Сообщение..."
    ).apply {
        layoutParams = LinearLayout.LayoutParams(
            0,
            dp(54),
            1f
        )
    }

    val sendButton = Button(this).apply {
        text = "➤"
        textSize = 18f
        setTextColor(white)

        background = pcBackground(
            blue,
            17
        )

        isLongClickable = false

        layoutParams = LinearLayout.LayoutParams(
            dp(55),
            dp(54)
        ).apply {
            leftMargin = dp(8)
        }
    }

    sendButton.setOnClickListener {

        val message =
            messageInput.text
                .toString()
                .trim()

        if (message.isNotEmpty()) {

            addMessage(
                messages,
                message,
                true
            )

            messageInput.text.clear()

            scroll.post {
                scroll.fullScroll(
                    ScrollView.FOCUS_DOWN
                )
            }
        }
    }

    sendBar.addView(messageInput)
    sendBar.addView(sendButton)

    root.addView(sendBar)
}

private fun MainActivity.addMessage(
    parent: LinearLayout,
    message: String,
    mine: Boolean
) {

    val row = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL

        gravity =
            if (mine) {
                Gravity.END
            } else {
                Gravity.START
            }

        setPadding(
            dp(2),
            dp(4),
            dp(2),
            dp(4)
        )
    }

    val bubble = pcText(
        message,
        15f
    ).apply {

        setPadding(
            dp(14),
            dp(10),
            dp(14),
            dp(10)
        )

        background = pcBackground(
            if (mine) blue else panel2,
            17,
            if (mine) null else line
        )

        isLongClickable = false
        setTextIsSelectable(false)
    }

    row.addView(
        bubble,
        LinearLayout.LayoutParams(
            dp(270),
            -2
        )
    )

    parent.addView(row)
}
