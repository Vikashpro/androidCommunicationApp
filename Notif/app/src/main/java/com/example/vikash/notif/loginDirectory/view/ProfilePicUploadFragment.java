package com.example.vikash.notif.loginDirectory.view;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.vikash.notif.R;
import com.example.vikash.notif.api.APIService;
import com.example.vikash.notif.api.APIUrl;
import com.example.vikash.notif.loginDirectory.helper.SharedPrefManager;
import com.example.vikash.notif.loginDirectory.model.ProfilePicUpload;
import com.example.vikash.notif.loginDirectory.model.UpdateFlags;
import com.example.vikash.notif.notifications.helper.CircleTransform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vikash on 4/19/18.
 */

public class ProfilePicUploadFragment extends Fragment implements  EasyPermissions.PermissionCallbacks{

  ImageView profilePicImageView;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_GALLERY_CODE = 200;
    private static final int READ_REQUEST_CODE = 300;

    private Uri uri;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.upload_profile_pic_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Upload Profile Picture");
        profilePicImageView = (ImageView) view.findViewById(R.id.profilePicImageView);
        Button buttonUploadImage = (Button)view.findViewById(R.id.buttonUploadImage);
        buttonUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           uploadImage();
            }
        });
        String imageUrl = APIUrl.BASE_URL + SharedPrefManager.getInstance(getContext()).getUser().getImage();
        System.out.println(SharedPrefManager.getInstance(getContext()).getUser().getImage());
        //Glide.with(profilePicImageView.getContext()).load(imageUrl).override(100, 100) // resizes the image to these dimensions (in pixel)
           //     .centerCrop().into(profilePicImageView);

        Glide.with(getContext()).load(imageUrl).override(100, 100).centerCrop()
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(profilePicImageView);

    }
    public void uploadImage() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK);
        openGalleryIntent.setType("image/*");
        startActivityForResult(openGalleryIntent, REQUEST_GALLERY_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_GALLERY_CODE && resultCode == Activity.RESULT_OK){
            uri = data.getData();
            if(EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                String filePath = getRealPathFromURIPath(uri, getActivity());
                File file = new File(filePath);
                System.out.println("Filename " + file.getName());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
                RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

                Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIUrl.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                final APIService profilePicUpload = retrofit.create(APIService.class);
                String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();


                                Call<ProfilePicUpload> fileUpload = profilePicUpload.uploadProfilePic(fileToUpload, filename,email);

                fileUpload.enqueue(new Callback<ProfilePicUpload>() {
                    @Override
                    public void onResponse(Call<ProfilePicUpload> call, Response<ProfilePicUpload> response) {
                       if(!response.body().getError()) {
                           System.out.println("Response " + response.raw().message());
                           System.out.println("Success " + response.body().getMessage());
                           Toast.makeText(getContext(), "Response " + response.raw().message(), Toast.LENGTH_LONG).show();
                           Toast.makeText(getContext(), "Success " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                           System.out.println(response.body().getImageUrl());
                           SharedPrefManager.getInstance(getContext()).setImageUrl(response.body().getImageUrl());
                           String imageUrl = APIUrl.BASE_URL + response.body().getImageUrl();

                           //Glide.with(profilePicImageView.getContext()).load(imageUrl).into(profilePicImageView);

                           Glide.with(getContext()).load(imageUrl).override(100, 100).centerCrop()
                                   .thumbnail(0.5f)
                                   .crossFade()
                                   .diskCacheStrategy(DiskCacheStrategy.ALL)
                                   .into(profilePicImageView);
                       }else{
                           Toast.makeText(getContext(),"Error " + response.body().getMessage(),Toast.LENGTH_LONG).show();
                           System.out.println("Error "+ response.body().getMessage());
                       }
                    }
                    @Override
                    public void onFailure(Call<ProfilePicUpload> call, Throwable t) {
                        Log.d(TAG, "Error " + t.getMessage());
                    }
                });
            }else{
                EasyPermissions.requestPermissions(this, getString(R.string.read_file), READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }
    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(uri != null){
            String filePath = getRealPathFromURIPath(uri, getActivity());
            File file = new File(filePath);
            RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);
            RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());


            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(APIUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
            APIService profilePicUpload = retrofit.create(APIService.class);
            String email = SharedPrefManager.getInstance(getContext()).getUser().getEmail();
            Call<ProfilePicUpload> fileUpload = profilePicUpload.uploadProfilePic(fileToUpload, filename,email);
            fileUpload.enqueue(new Callback<ProfilePicUpload>() {
                @Override
                public void onResponse(Call<ProfilePicUpload> call, Response<ProfilePicUpload> response) {
                    Toast.makeText(getContext(), "Success " + response.message(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Success " + response.body().toString(), Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Call<ProfilePicUpload> call, Throwable t) {
                    Log.d(TAG, "Error " + t.getMessage());
                    System.out.println("2OnFailure " + t.getMessage());
                }
            });
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }
}
