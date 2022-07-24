package com.example.accessibilitydemo

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    val appName = "com.tencent.wework"
    val activityName = "com.tencent.wework.launch.LaunchSplashActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.enter_setting_bt).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.open_wework_btn).setOnClickListener {
            val componentName = ComponentName(appName, activityName)
            val intent = Intent()
            intent.component = componentName
            startActivity(intent)
        }
    }

}