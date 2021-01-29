package com.heeyeon.mymsgapp.Fragments;

import com.heeyeon.mymsgapp.Notification.MyResponse;
import com.heeyeon.mymsgapp.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                "Content-Type:application/json",
                    "Authorization:key=\tAAAAAoN2Zno:APA91bHkZrmzKgWNfTP6wXycM6-sbjncV7HgsnYyepFXeNVbv11QQBOklZr_ScpK8RgMYSDRwpOeHtvPSVqR5wUIARyefaR-0G6b71DfhLKLxjfaWLdzoUN59scnWA-crSEKrKLV4qlP"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
