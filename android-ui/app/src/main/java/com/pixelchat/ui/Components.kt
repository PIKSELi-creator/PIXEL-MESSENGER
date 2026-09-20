package com.pixelchat.ui

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView

fun MainActivity.pcText(
    value: String,
    size: Float,
    color: Int = white,
    bold: Boolean = false
): TextView {
    return TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)

        if (bold) {
            setTypeface(typeface, Typeface.BOLD)
        }

        isLongClickable = false
        setTextIsSelectable(false)
    }
}

fun MainActivity.pcTitle(
    value: String,
    size: Float = 25f
): TextView {
    return pcText(
        value = value,
        size = size,
        color = white,
        bold = true
    )
}

fun MainActivity.pcLabel(
    value: String,
    size: Float = 14f
): TextView {
    return pcText(
        value = value,
        size = size,
        color = muted
    )
}

fun MainActivity.pcBackground(
    color: Int,
    radius: Int,
    stroke: Int? = null
): GradientDrawable {
    return GradientDrawable().apply {
        setColor(color)
        cornerRadius = dp(radius).toFloat()

        if (stroke != null) {
            setStroke(dp(1), stroke)
        }
    }
}

fun MainActivity.pcInput(
    hintText: String
): EditText {
    return EditText(this).apply {
        hint = hintText
        textSize = 16f

        setTextColor(white)
        setHintTextColor(muted)

        setSingleLine(true)

        background = pcBackground(
            panel2,
            17,
            line
        )

        setPadding(
            dp(16),
            0,
            dp(16),
            0
        )

        layoutParams = LinearLayout.LayoutParams(
            -1,
            dp(54)
        ).apply {
            bottomMargin = dp(10)
        }
    }
}

fun MainActivity.pcButton(
    textValue: String,
    primary: Boolean = true,
    action: () -> Unit
): Button {
    return Button(this).apply {
        text = textValue
        textSize = 15f
        isAllCaps = false

        setTextColor(white)

        background = pcBackground(
            if (primary) blue else panel2,
            16,
            if (primary) null else line
        )

        stateListAnimator = null
        isLongClickable = false

        setOnClickListener {
            action()
        }

        layoutParams = LinearLayout.LayoutParams(
            -1,
            dp(52)
        ).apply {
            bottomMargin = dp(10)
        }
    }
}

fun MainActivity.pcSpace(
    parent: LinearLayout,
    height: Int
) {
    parent.addView(
        Space(this),
        LinearLayout.LayoutParams(
            1,
            dp(height)
        )
    )
}

fun MainActivity.pcDivider(
    parent: LinearLayout
) {
    parent.addView(
        View(this).apply {
            setBackgroundColor(line)
        },
        LinearLayout.LayoutParams(
            -1,
            dp(1)
        )
    )
}

fun MainActivity.pcAvatar(
    value: String,
    size: Int = 46
): TextView {
    return pcText(
        value,
        if (size >= 80) 35f else 18f,
        Color.WHITE,
        true
    ).apply {
        gravity = Gravity.CENTER

        background = pcBackground(
            blue,
            16
        )

        layoutParams = LinearLayout.LayoutParams(
            dp(size),
            dp(size)
        )
    }
}

fun MainActivity.pcIcon(
    value: String,
    action: () -> Unit
): TextView {
    return pcText(
        value,
        22f,
        white
    ).apply {
        gravity = Gravity.CENTER

        background = pcBackground(
            panel2,
            14,
            line
        )

        layoutParams = LinearLayout.LayoutParams(
            dp(46),
            dp(46)
        ).apply {
            leftMargin = dp(5)
        }

        setOnClickListener {
            action()
        }
    }
}

fun MainActivity.pcHeader(
    parent: LinearLayout,
    title: String,
    subtitle: String
) {
    parent.addView(pcTitle(title))
    parent.addView(pcLabel(subtitle))

    pcSpace(parent, 15)
}

fun MainActivity.pcSection(
    parent: LinearLayout,
    value: String
) {
    pcSpace(parent, 8)

    parent.addView(
        pcLabel(value, 11f)
    )

    pcSpace(parent, 7)
}

fun MainActivity.pcNavigation(
    parent: LinearLayout,
    selected: String
) {
    pcDivider(parent)

    val navigation = LinearLayout(this).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
    }

    val items = listOf(
        "Чаты" to "chats",
        "Контакты" to "contacts",
        "Звонки" to "calls",
        "Профиль" to "profile"
    )

    items.forEach { item ->

        val label = pcText(
            item.first,
            11f,
            if (item.second == selected) {
                blue
            } else {
                muted
            }
        ).apply {
            gravity = Gravity.CENTER

            setOnClickListener {
                when (item.second) {
                    "chats" -> showHome()
                    "contacts" -> showContacts()
                    "calls" -> showCalls()
                    "profile" -> showProfile()
                }
            }
        }

        navigation.addView(
            label,
            LinearLayout.LayoutParams(
                0,
                -1,
                1f
            )
        )
    }

    parent.addView(
        navigation,
        LinearLayout.LayoutParams(
            -1,
            dp(58)
        )
    )
}
