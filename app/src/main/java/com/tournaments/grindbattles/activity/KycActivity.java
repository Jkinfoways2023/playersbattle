package com.tournaments.grindbattles.activity;

import static android.widget.Toast.LENGTH_LONG;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.tournaments.grindbattles.R;
import com.tournaments.grindbattles.databinding.ActivityKycBinding;
import com.tournaments.grindbattles.session.SessionManager;
import com.tournaments.grindbattles.utils.ApiService;
import com.tournaments.grindbattles.utils.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KycActivity extends AppCompatActivity {

    ActivityKycBinding binding;
    int last_selected=0;
    String fron_adhar = "";
    String back_adhar = "";
    private SessionManager session;
    HashMap<String, String> user;
    private ApiService apiService;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityKycBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.frontAdharCv.setOnClickListener(v -> {
            fron_adhar = "";
            last_selected=1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.e("permission", "approved");
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 121);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        imagePickerLauncher.launch(intent);
                    }
                }
                else{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.e("caleleddd","1=>casld");
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
                    } else {
                   /* Log.e("permission","called");
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);*/
                        Log.e("caleleddd","casld");
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent,101);
                    }
                }
            }
            else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent,101);
            }
        });

        binding.backAdhar.setOnClickListener(v -> {
            back_adhar = "";
            last_selected=2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.e("permission", "approved");
                        requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 121);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        imagePickerLauncher.launch(intent);
                    }
                }
                else{
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        Log.e("caleleddd","1=>casld");
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 121);
                    } else {
                   /* Log.e("permission","called");
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);*/
                        Log.e("caleleddd","casld");
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent,101);
                    }
                }
            } else {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent,101);
            }
        });

    }

    public void init()
    {
        session = new SessionManager(this);
        user = session.getUserDetails();
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    Log.e("imagepicker", "line49");
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("imagepicker", "line51");
                        Intent data = result.getData();
                        if (data != null) {
                            Uri selectedImageUri = data.getData();
                            // Do something with the URI, e.g., display the image
                            ;
                            if(last_selected==1)
                            {
                                fron_adhar = getRealPathFromURI(selectedImageUri);
                                if (fron_adhar != null && new File(fron_adhar).exists()) {
                                } else {
                                    Log.e("responseisssss", "Error file not exist");
                                }
                                Log.e("imagepicker", "line57" + selectedImageUri);
                            }
                            else if(last_selected==2){
                                back_adhar = getRealPathFromURI(selectedImageUri);
                                if (back_adhar != null && new File(back_adhar).exists()) {
                                } else {
                                    Log.e("responseisssss", "Error file not exist");
                                }
                                Log.e("imagepicker", "line57" + selectedImageUri);
                            }



                        } else {
                            Log.e("imagepicker", "line56");
                        }
                    } else {
                        Log.e("imagepicker", "line59");
                    }
                });

        binding.txtContinue.setOnClickListener(v->{
            if(fron_adhar.equalsIgnoreCase(""))
            {
                Toast.makeText(this, "Select front side of adharcard", Toast.LENGTH_SHORT).show();

            }
            else if(back_adhar.equalsIgnoreCase(""))
            {
                Toast.makeText(this, "Select back side of adharcard", Toast.LENGTH_SHORT).show();
            }
            else if(binding.etNumber.getText().toString().trim().equalsIgnoreCase(""))
            {
                Toast.makeText(this, "Enter adhar number", Toast.LENGTH_SHORT).show();
            }
            else{
                updatekyc_request();
            }


        });
    }

    private void updatekyc_request()
    {
        File file1 = new File(fron_adhar);
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("image/*"), file1);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("adhar_front", file1.getName(), fileRequestBody);

        File file2 = new File(back_adhar);
        RequestBody fileRequestBody2 = RequestBody.create(MediaType.parse("image/*"), file2);
        MultipartBody.Part filePart2 = MultipartBody.Part.createFormData("adhar_back", file1.getName(), fileRequestBody2);

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"),  user.get(SessionManager.KEY_ID));
        RequestBody pan_number = RequestBody.create(MediaType.parse("text/plain"), binding.etNumber.getText().toString().trim());

        apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ResponseBody> call = apiService.process_kyc(pan_number,user_id,filePart,filePart2);
        binding.txtContinue.setVisibility(View.GONE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e("responseisssss", String.valueOf(response.message()));
                try {
                    if (response.isSuccessful()) {
                        Log.e("responseisssss", "2");
                        ResponseBody responseBody = response.body();
                        Log.e("responseisssss", "3");
                        String s = responseBody.string();
                        Log.e("responseisssss", "response is => " + responseBody.string());
                        Log.e("responseisssss", "response is => " + response.code());
                        JSONObject object = new JSONObject(s);
                        Log.e("responseisssss", String.valueOf(object));
                        if (object.getJSONArray("result").getJSONObject(0).getString("success").equalsIgnoreCase("1")) {
                            //recreate();
                            Toast.makeText(KycActivity.this, "Kyc Request submitted...", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(KycActivity.this,MainActivity.class));
                            finish();
                        }
                    } else {
                        ResponseBody responseBody = response.errorBody();
                        Log.e("responseisssss", "Error " + responseBody.string());
                        Toast.makeText(KycActivity.this, responseBody.string(), LENGTH_LONG).show();
                        binding.txtContinue.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    binding.txtContinue.setVisibility(View.VISIBLE);
                    Log.e("responseisssss", "exception " + e);

                    e.printStackTrace();
                } catch (IOException e) {
                    binding.txtContinue.setVisibility(View.VISIBLE);
                    Log.e("responseisssss", "130 " + e);

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("responseisssss", "125 " + t.getMessage());

                binding.txtContinue.setVisibility(View.VISIBLE);
                Toast.makeText(KycActivity.this, t.getMessage(), LENGTH_LONG).show();
            }
        });
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String filePath = cursor.getString(column_index);
            cursor.close();
            return filePath;
        }
        return null;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri selectedImageUri = data.getData();
        if (selectedImageUri != null) {
            if(last_selected==1)
            {
                fron_adhar=getRealPathFromURI(selectedImageUri);
                binding.frontAdhartv.setText(fron_adhar);
            }
            else{
                back_adhar=getRealPathFromURI(selectedImageUri);
                binding.backAdharTv.setText(back_adhar);
            }
        }
    }
}