package com.jumbo.carry.services;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jumbo.carry.channel.NotificationHelper;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Map<String,String> data=message.getData();
        String title = data.get("title");
        String body = data.get("body");
        if(title!=null){
            if(title.contains("cuidador")){
                mostrarNotifiacion(title,body);
            }else{
                int idNotification= Integer.parseInt(data.get("idNotificacion"));
                mostrarNotifiacionMensaje(title,body,idNotification);
            }
        }
    }

    private void mostrarNotifiacion(String title, String body){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder= notificationHelper.getNotification(title,body);
        Random random = new Random();
        int n = random.nextInt(10000);
        notificationHelper.getmManager().notify(n,builder.build());
    }
    private void mostrarNotifiacionMensaje(String title, String body, int idNotificacion){
        NotificationHelper notificationHelper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder= notificationHelper.getNotification(title,body);
        notificationHelper.getmManager().notify(idNotificacion,builder.build());
    }

}
