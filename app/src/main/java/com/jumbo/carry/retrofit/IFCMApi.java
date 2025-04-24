package com.jumbo.carry.retrofit;

import com.jumbo.carry.models.FCMBody;
import com.jumbo.carry.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvxqUToA:APA91bHkTnl5I0oPKjxC-W6AHEctZ69cUveX2kmNhC2MLoxsd7Q1hxouY9tfUruuEcDSFP3l_LvFozUF5c8862JtJEBnA02Aqzn8W4mtyiqw2U5zl1wDnYw_tg9oS1oJfHFvbpXqyY9S"
    })
    @POST("fcm/send")
    Call<FCMResponse>send(@Body FCMBody body);
}
