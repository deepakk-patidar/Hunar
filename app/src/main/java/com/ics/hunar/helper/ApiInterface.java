package com.ics.hunar.helper;

import com.ics.hunar.model.BannerResponse;
import com.ics.hunar.model.CheckOTPResponse;
import com.ics.hunar.model.FavoriteResponse;
import com.ics.hunar.model.NormalLoginResponse;
import com.ics.hunar.model.NormalSignUpResponse;
import com.ics.hunar.model.PasswordUpdateResponse;
import com.ics.hunar.model.Resume_List_Model;
import com.ics.hunar.model.SendOtpResponse;
import com.ics.hunar.model.UnlockStatusResponse;
import com.ics.hunar.model.VideoNewResponse;
import com.ics.hunar.model.VideoStatusResponse;
import com.ics.hunar.model.features.FeaturesResponse;
import com.ics.hunar.model.searching.SearchingResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {
    @Headers("Content-Type: text/plain")
    @POST("create")
    Call<Map> create(@Body Map map);

    //  Call<Map> createRoom(@Body Map map);
    @FormUrlEncoded
    @POST("./")
    Call<VideoNewResponse> getVideoByLevel(@Field("access_key") String access_key, @Field("get_videos_by_level") String get_videos_by_level, @Field("level") String level, @Field("subcategory") String subcategory, @Field("language_id") String language_id, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("./")
    Call<FavoriteResponse> postFavoriteVideo(@Field("access_key") String access_key, @Field("video_id") String video_id, @Field("is_video_favourite") String is_video_favourite, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("./")
    Call<VideoStatusResponse> getVideoStatus(@Field("access_key") String access_key, @Field("get_video_status_of_user") String get_video_status_of_user, @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("./")
    Call<SendOtpResponse> sendOtpToUser(@Field("access_key") String access_key, @Field("send_otp_to_user") String send_otp_to_user, @Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("./")
    Call<NormalSignUpResponse> normalSignUpResponse(@Field("access_key") String access_key, @Field("normal_user_signup") String normal_user_signup, @Field("name") String name, @Field("email") String email, @Field("mobile") String mobile, @Field("password") String password, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("./")
    Call<NormalLoginResponse> normalLoginResponse(@Field("access_key") String access_key, @Field("normal_user_login") String normal_user_login, @Field("email_or_number") String email_or_number, @Field("password") String password);

    @FormUrlEncoded
    @POST("./")
    Call<CheckOTPResponse> checkOTP(@Field("access_key") String access_key, @Field("check_otp") String check_otp, @Field("mobile") String mobile, @Field("otp") String otp);

    @FormUrlEncoded
    @POST("./")
    Call<PasswordUpdateResponse> passwordUpdate(@Field("access_key") String access_key, @Field("update_password") String update_password, @Field("user_id") String user_id, @Field("password") String password);

    @FormUrlEncoded
    @POST("./")
    Call<UnlockStatusResponse> unlockVideoStatus(@Field("access_key") String access_key, @Field("post_video_unlocked_status") String post_video_unlocked_status, @Field("user_id") String user_id, @Field("video_id") String video_id);

    @FormUrlEncoded
    @POST("./")
    Call<SearchingResponse> search(@Field("access_key") String access_key, @Field("search") String search, @Field("search_key") String search_key, @Field("user_id") String user_id, @Field("language_id") String language_id);

    @FormUrlEncoded
    @POST("./")
    Call<BannerResponse> getBanner(@Field("access_key") String access_key, @Field("get_banners") String get_banners);

    @FormUrlEncoded
    @POST("./")
    Call<FeaturesResponse> getFeatures(@Field("access_key") String access_key, @Field("get_featured") String get_featured);

    @FormUrlEncoded
    @POST("./")
    Call<FavoriteResponse> send_resume_timing(
            @Field("access_key") String access_key,
            @Field("post_currently_playing_video") String post_currently_playing_video,
            @Field("user_id") String user_id,
            @Field("video_id") String video_id,
            @Field("palying_time") String palying_time,
            @Field("total_time") String total_time
    );

    @FormUrlEncoded
    @POST("./")
    Call<Resume_List_Model> get_resume_time(
            @Field("access_key") String access_key,
            @Field("get_currently_playing_video") String get_currently_playing_video,
            @Field("user_id") String user_id,
            @Field("video_id") String video_id
    );



}
