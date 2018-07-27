package com.example.vikash.notif.api;

import com.example.vikash.notif.public_notice_board.model.NoticeBoard;
import com.example.vikash.notif.updates.model.RetrieveUpdates;
import com.example.vikash.notif.conversations.model.Convos;
import com.example.vikash.notif.conversations.model.Messages;
import com.example.vikash.notif.loginDirectory.model.ProfilePicUpload;
import com.example.vikash.notif.loginDirectory.model.Result;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.example.vikash.notif.notifications.model.Notifications;
import com.example.vikash.notif.notifications.model.SendNotice;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

/**
 * Created by vikash on 3/28/18.
 */

public interface APIService {

    //the signin call
    @FormUrlEncoded
    @POST("api/user/login")
    Call<Result> userLogin(
            @Field("email") String email,
            @Field("pass") String pass
    );

    @GET("api/notification/notices")
    Call<Notifications> getNotifications();

    @Multipart
    @POST("api/update/profile_pic")
    Call<ProfilePicUpload> uploadProfilePic(@Part MultipartBody.Part file, @Part("name") RequestBody name, @Part("email") String email);

    @FormUrlEncoded
    @POST("api/notice/add")
    Call<SendNotice> sendNotice(
            @Field("receiver") String receiver,
            @Field("subject") String subject,
            @Field("description") String description,
            @Field("sender_email") String senderEmail

    );
    @FormUrlEncoded
    @POST("api/notice/isImportant")
    Call<UpdateFlags> setNoticeIsImportant(
            @Field("email") String email,
            @Field("notice_id") String notice_id,
            @Field("flag") boolean flag
    );
    @FormUrlEncoded
    @POST("api/notice/isRead")
    Call<UpdateFlags> setNoticeIsRead(
            @Field("email") String email,
            @Field("notice_id") String notice_id
    );
    @FormUrlEncoded
    @POST("api/conversations")
    Call<Convos> getConversations(
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("api/conversation/isImportant")
    Call<UpdateFlags> setConversationIsImportant(
            @Field("email") String email,
            @Field("c_id") String c_id,
            @Field("flag") boolean flag
    );
    @FormUrlEncoded
    @POST("api/conversation/unReadCounts")
    Call<UpdateFlags> setConversationUnReadCounts(
            @Field("email") String email,
            @Field("c_id") String c_id,
            @Field("unReadCounts") int unReadCounts
    );

    @FormUrlEncoded
    @POST("api/messages")
    Call<Messages> getMessages(
            @Field("c_id") String c_id
    );

    @FormUrlEncoded
    @POST("api/add/message")
    Call<UpdateFlags> sendMessage(
            @Field("c_id") String c_id,
            @Field("message") String message,
            @Field("sender_email") String email

    );
    @FormUrlEncoded
    @POST("api/update_gcm")
    Call<UpdateFlags> registerFirebaseToken(
            @Field("token") String token,
            @Field("email") String email

    );
    @GET("api/updates")
    Call<RetrieveUpdates> getUpdates();

    @FormUrlEncoded
    @POST("api/add/conversation")
    Call<UpdateFlags> sendNewMessage(
            @Field("receiver") String receiver,
            @Field("subject") String subject,
            @Field("message") String description,
            @Field("sender_email") String senderEmail

    );

    @GET("api/notice_board/public_notices")
    Call<NoticeBoard> getPublicNotices();

    @FormUrlEncoded
    @POST("api/test")
    Call<UpdateFlags> test(
            @Field("name") String name

    );

}
