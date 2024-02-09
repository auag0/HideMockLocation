package io.github.auag0.hidemocklocation

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import io.github.auag0.hidemocklocation.MyApp.Companion.isModuleEnabled

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusTitle: TextView = findViewById(R.id.status_title)
        val statusIcon: ImageView = findViewById(R.id.status_icon)
        if (isModuleEnabled()) {
            statusTitle.setText(R.string.status_activated)
            statusIcon.setImageResource(R.drawable.ic_check)
        } else {
            statusTitle.setText(R.string.status_not_activated)
            statusIcon.setImageResource(R.drawable.ic_batsu)
        }
    }
}