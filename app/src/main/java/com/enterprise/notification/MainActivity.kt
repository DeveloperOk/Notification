package com.enterprise.notification

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.enterprise.notification.ui.theme.NotificationTheme
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Medium post
        //https://medium.com/@anandmali/creating-a-basic-android-notification-5e5ee1614aae

         val objectRegisterForActivityResult = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    // Permission is granted
                    // we can proceed to create a notification

                    sendNotification(context = this)

                } else {
                    // Permission is denied
                    // We need to show rational dialogue to ask for the permission
                }
            }


        enableEdgeToEdge()
        setContent {
            NotificationTheme {

                NotificationApp(objectRegisterForActivityResult = objectRegisterForActivityResult)

            }
        }

    }
}


@Composable
fun NotificationApp(
    objectRegisterForActivityResult: ActivityResultLauncher<String>
) {

    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxSize().background(color = Color.Green)){

        Scaffold(modifier = Modifier.systemBarsPadding().fillMaxSize()) { innerPadding ->

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(innerPadding).fillMaxSize()
                    .background(color = Color.White)){

                Button(colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    onClick = {

                    requestPermission(context = context, objectRegisterForActivityResult = objectRegisterForActivityResult)

                }) {

                    Text(text = stringResource(id = R.string.main_activity_send_notification_button_text))

                }

            }

        }

    }

}

fun requestPermission(
    context: Context,
    objectRegisterForActivityResult: ActivityResultLauncher<String>
) {
    if (
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        // Permission is granted
        // Proceed forward to create a notification

        sendNotification(context = context)

    } else {
        // Permission is not granted yet
        // Request for the permission to post notifications

        objectRegisterForActivityResult.launch(Manifest.permission.POST_NOTIFICATIONS)



    }
}

fun sendNotification(context: Context) {

    val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "Unique id per package"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Channel name"
        val description = "Explain little about the channel for user to understand"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, name, importance)
        channel.description = description

        notificationManager.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_menu_book_24)
        .setContentTitle("Notification App Notification Title")
        .setContentText("Notification App Notification Content Text")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationId: Int = 1

    with(NotificationManagerCompat.from(context)) {

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        notify(notificationId, builder.build())

    }


}
