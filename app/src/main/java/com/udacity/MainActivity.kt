package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.content_main.*

private const val TAG = "#LAPP MainActivity"

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createChannel()

        button_download.setOnClickListener {
            // If any of the radio buttons is selected
            val selectedRadio = findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            Log.i(TAG, "selectedRadio Id: ${selectedRadio}")

            when (radioGroup.checkedRadioButtonId) {
                R.id.radioGlide -> {
                    URL = "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
                    selectedFile = applicationContext.getString(R.string.main_activity_glide)
                }
                R.id.radioLoad -> {
                    URL =
                        "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
                    selectedFile = applicationContext.getString(R.string.main_activity_loadapp)
                }
                else -> {
                    URL = "https://github.com/square/retrofit/archive/refs/heads/master.zip"
                    selectedFile = applicationContext.getString(R.string.main_activity_retrofit)
                }
            }

            if (selectedRadio == null) {
                Toast.makeText(applicationContext, "Please select a option", Toast.LENGTH_SHORT)
                    .show()
            } else {
                download()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                val downloadStatus =
                    if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                        "SUCCESS"
                    } else {
                        "FAILED"
                    }

                val notificationManager = ContextCompat.getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.sendNotification(
                    applicationContext,
                    selectedFile,
                    downloadStatus
                )
            }
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
        Log.i(TAG, "selected URL: $URL")

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private var URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"

        private var selectedFile = ""
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                applicationContext.getString(R.string.notification_button_channel_id),
                applicationContext.getString(R.string.notification_button_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Complete"
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}
