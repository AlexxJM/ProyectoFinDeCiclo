package com.jumbo.carry.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.jumbo.carry.providers.AuthProvider;
import com.jumbo.carry.providers.UsersProvider;

import java.util.List;

public class MensajeVistoHelper {

    public static void actualizarOnline(boolean estado, final Context context){
        UsersProvider usersProvider = new UsersProvider();
        AuthProvider authProvider= new AuthProvider();
        if(authProvider.getUid()!=null){
            if(segundoPlano(context)){
                usersProvider.actualizarEstado(authProvider.getUid(),estado);
            }else if (estado){
                usersProvider.actualizarEstado(authProvider.getUid(),estado);

            }
        }
    }

    public static boolean segundoPlano(final Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo>tasks=activityManager.getRunningTasks(1);
        if(!tasks.isEmpty()){
            ComponentName topActivity=tasks.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())){
                return true;
            }
        }
        return false;
    }
}
