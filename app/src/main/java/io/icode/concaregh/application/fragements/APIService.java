package io.icode.concaregh.application.fragements;

import io.icode.concaregh.application.notifications.MyResponse;
import io.icode.concaregh.application.notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA91e8Zac:APA91bHbjO_ue4XRH-6zegoMWKrzQheXLbGV3YFf8zi8-Y_akskyvQ0KJw4omJCiWdbKsFTrHvD3-gOeKhZEfHzeyHzoYIJbr1m9u6ntVLNpx-BMtRUbuzrcu9KhfLv6Enb8lh86mpq_"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
