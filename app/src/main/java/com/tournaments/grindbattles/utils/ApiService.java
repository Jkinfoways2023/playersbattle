package com.tournaments.grindbattles.utils;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("upload_kyc_details")
    Call<ResponseBody> process_kyc(
            @Part("adhar_number") RequestBody pan_number,
            @Part("user_id") RequestBody user_id,
            @Part MultipartBody.Part file,
            @Part MultipartBody.Part file2);

}
