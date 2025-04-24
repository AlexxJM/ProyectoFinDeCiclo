package com.jumbo.carry.providers;

import com.jumbo.carry.models.FCMBody;
import com.jumbo.carry.models.FCMResponse;
import com.jumbo.carry.retrofit.IFCMApi;
import com.jumbo.carry.retrofit.RetrofitClient;

import retrofit2.Call;

public class NotificacionesProvider {
    private String url="https://fcm.googleapis.com";
    public NotificacionesProvider(){

    }
    public Call<FCMResponse> sendNotificayion(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
