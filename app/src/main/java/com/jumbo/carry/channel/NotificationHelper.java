package com.jumbo.carry.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.jumbo.carry.R;

public class NotificationHelper extends ContextWrapper {
    private static final String CHANNEL_ID="com.jumbo.carry";
    private static final String CHANNEL_NAME="Carry";

    private NotificationManager manager;

    public NotificationHelper(Context context){
        super(context);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            crearChannel();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void crearChannel(){
        NotificationChannel notificationChannel= null;
            notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.GRAY);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getmManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getmManager() {
        if(manager==null){
            manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotification(String titulo,String body){
        return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID).
                setContentTitle(titulo).setContentText(body).setAutoCancel(true)
                .setColor(Color.GRAY).setSmallIcon(R.drawable.nuevo_logo_icono_carry).
                setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(titulo));
    }
}
