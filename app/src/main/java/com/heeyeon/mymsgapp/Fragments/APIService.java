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
                    "Authorization:key=AAAA0-kdZ2M:APA91bHcw8AZkIOL_JlMkOdeW-1rDM_OYzg5oe83JSZth4pODeAPTXfSMJHsV-Ma1PHnuOhNLiAaYK2TNS_ZHBRMOIKMmtXYBfWtXYZ-Bf4uRijpmsW9djLqJxsUfln1Yk0obiA5ImjJ"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
