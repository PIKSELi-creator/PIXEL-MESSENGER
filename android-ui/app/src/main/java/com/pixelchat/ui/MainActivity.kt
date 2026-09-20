package com.pixelchat.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    val bg = Color.rgb(7, 11, 18)
    val panel = Color.rgb(13, 20, 32)
    val panel2 = Color.rgb(17, 27, 41)
    val blue = Color.rgb(61, 139, 255)
    val white = Color.rgb(244, 247, 251)
    val muted = Color.rgb(137, 149, 168)
    val line = Color.rgb(27, 39, 55)
    val green = Color.rgb(57, 217, 138)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = bg
        window.navigationBarColor = bg

        showLogin()
    }

    fun createRoot(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setBackgroundColor(bg)
            setPadding(
                dp(18),
                dp(14),
                dp(18),
                dp(8)
            )
            setContentView(this)
        }
    }

    fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
