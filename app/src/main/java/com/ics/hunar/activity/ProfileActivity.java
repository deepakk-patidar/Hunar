package com.ics.hunar.activity;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.helper.AndroidMultiPartEntity;
import com.ics.hunar.helper.AppController;
import com.ics.hunar.helper.AudienceProgress;
import com.ics.hunar.helper.CircleImageView;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileActivity extends AppCompatActivity {

    public CircleImageView imgProfile;
    public ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public int reqReadPermission = 1;
    public int reqWritePermission = 2;
    public static int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static int SELECT_FILE = 110;
    Uri fileUri;
    public ProgressBar progressBar;
    AlertDialog alertDialog;
    ;
    private String filePath = null;
    File sourceFile;
    long totalSize = 0;
    private FirebaseAuth mAuth;
    public FloatingActionButton fabProfile;
    public TextView tvEmailId, tvUpdate, tvLogout;
    public AudienceProgress progress;
    public LinearLayout edtNameLayout, edtMobileLayout;
    public EditText edtName, edtMobile;
    public RelativeLayout mainLayout;
    Toolbar toolbar;
    private File output = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        SharedPreferencesUtil.init(this);
        mAuth = FirebaseAuth.getInstance();
        mainLayout = findViewById(R.id.mainLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.update_profile));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNameLayout = findViewById(R.id.edtNameLayout);
        edtMobileLayout = findViewById(R.id.edtMobileLayout);

        edtName = findViewById(R.id.edtUserName);
        edtMobile = findViewById(R.id.edtMobile);
        tvEmailId = findViewById(R.id.tvEmailId);
        tvUpdate = findViewById(R.id.tvUpdate);
        tvLogout = findViewById(R.id.tvLogout);

        fabProfile = findViewById(R.id.fabProfile);
        progressBar = findViewById(R.id.progressBar);
        progress = findViewById(R.id.progress);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfile.setDefaultImageResId(R.drawable.ic_account);
        imgProfile.setImageUrl(Session.getUserData(Session.PROFILE, ProfileActivity.this), imageLoader);
        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectProfileImage();
            }
        });

        tvLogout.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_logout, 0, 0, 0);
        edtName.setText(Session.getUserData(Session.NAME, ProfileActivity.this));
        edtMobile.setText(Session.getUserData(Session.MOBILE, ProfileActivity.this));
        tvEmailId.setText(Session.getUserData(Session.EMAIL, ProfileActivity.this));
        edtNameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUpdate.setVisibility(View.VISIBLE);
                edtName.setEnabled(true);
            }
        });
        edtMobileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvUpdate.setVisibility(View.VISIBLE);
                edtMobile.setEnabled(true);
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, mobile;
                if (edtName.getText().toString().isEmpty())
                    name = Session.getUserData(Session.NAME, getApplicationContext());
                else
                    name = edtName.getText().toString();

                if (edtMobile.getText().toString().isEmpty())
                    mobile = Session.getUserData(Session.MOBILE, getApplicationContext());
                else
                    mobile = edtMobile.getText().toString();

                UpdateProfile(name, mobile);
            }
        });

    }


    public void UpdateProfile(final String name, final String mobile) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);

                            boolean error = obj.getBoolean("error");
                            String message = obj.getString("message");
                            Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                            if (!error) {
                                Session.setUserData(Session.MOBILE, mobile, ProfileActivity.this);
                                Session.setUserData(Session.NAME, name, ProfileActivity.this);
                                edtName.setText(name);
                                edtMobile.setText(mobile);
                                DrawerActivity.tvName.setText(name);
                                FirebaseDatabase.getInstance().getReference(Constant.DB_USER).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(Constant.USER_NAME).setValue(name);
                                tvUpdate.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.updateProfile, "1");
                params.put(Constant.email, Session.getUserData(Session.EMAIL, ProfileActivity.this));
                params.put(Constant.name, name);
                params.put(Constant.mobile, mobile);
                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);

    }

    public void SelectProfileImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, reqReadPermission);
            } else if (ContextCompat.checkSelfPermission(ProfileActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, reqWritePermission);
            } else {
                selectDialog();
            }
        } else {
            selectDialog();
        }
    }

    public void selectDialog() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.from_library), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getString(R.string.take_photo))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    output = new File(dir, "CameraContentDemo.jpeg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", output));
                    startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                } else if (items[item].equals(getString(R.string.from_library))) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals(getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setOutputCompressQuality(80)
                    .setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                    .setAspectRatio(1, 1)
                    .start(ProfileActivity.this);

        } else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.activity(FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", output)).start(ProfileActivity.this);

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                fileUri = result.getUri();
                new UploadFileToServer().execute();
            }
        }
    }


    public void Logout(View view) {
        SignOutWarningDialog();
    }

    public void UserStatistics(View view) {
        Intent intent = new Intent(getApplicationContext(), UserStatistics.class);
        startActivity(intent);
    }

    public void LeaderBoard(View view) {
        Intent intent = new Intent(getApplicationContext(), LeaderBoardActivity.class);
        startActivity(intent);
    }

    public void Bookmarks(View view) {
        Intent intent = new Intent(getApplicationContext(), BookmarkList.class);
        startActivity(intent);
    }

    public void InviteFriend(View view) {
        Intent intent = new Intent(getApplicationContext(), InviteFriendActivity.class);
        startActivity(intent);
    }

    public void Searching(View view) {
        startActivity(new Intent(ProfileActivity.this, SearchingActivity.class));
        finish();
    }

    public void logOut(View view) {
        SignOutWarningDialog();
    }


    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constant.QUIZ_URL);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                //publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });
                filePath = fileUri.getPath();
                sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart(Constant.image, new FileBody(sourceFile));
                entity.addPart(Constant.accessKey, new StringBody(Constant.accessKeyValue));
                entity.addPart(Constant.userId, new StringBody(Session.getUserData(Session.USER_ID, ProfileActivity.this)));
                entity.addPart(Constant.upload_profile_image, new StringBody("1"));

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;

            try {
                JSONObject jsonObject = new JSONObject(result);
                boolean error = jsonObject.getBoolean("error");
                if (!error) {

                    Session.setUserData(Session.PROFILE, jsonObject.getString("file_path"), ProfileActivity.this);
                    imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);
                    DrawerActivity.imgProfile.setImageUrl(jsonObject.getString("file_path"), imageLoader);
                    FirebaseDatabase.getInstance().getReference(Constant.DB_USER)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(Constant.PROFILE_PIC).setValue(jsonObject.getString("file_path"));

                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //showAlert(result);
            progressBar.setVisibility(View.GONE);
            super.onPostExecute(result);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ProfileActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final android.app.AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(ProfileActivity.this);
                SharedPreferencesUtil.clearAllPreference();
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                logOut();
                Intent intentLogin = new Intent(ProfileActivity.this, LoginActivity.class);
//                intentLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intentLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentLogin);
                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog1.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void logOut() {
        if (AccessToken.getCurrentAccessToken() != null) {
            String fb_id = SharedPreferencesUtil.read(SharedPreferencesUtil.FB_ID, "");
            GraphRequest delPermRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + fb_id + "/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
                @Override
                public void onCompleted(GraphResponse graphResponse) {
                    if (graphResponse != null) {
                        FacebookRequestError error = graphResponse.getError();
                        if (error != null) {

                        } else {
                        }
                    }
                }
            });
            Log.d("Logout FB", "Executing revoke permissions with graph path" + delPermRequest.getGraphPath());
            delPermRequest.executeAsync();
        }
        GoogleSignInAccount google_acc = GoogleSignIn.getLastSignedInAccount(ProfileActivity.this);
        if (google_acc != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.default_web_client_id))
                    .requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(ProfileActivity.this, gso);
            mGoogleSignInClient.signOut();
        } else {
            Log.e("------", "NO GOOGLE SIGN IN HERE");
        }
    }

    public void Home(View view) {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class).putExtra("type", "privacy"));
        finish();
    }

    public void StartQuiz(View view) {
        startGame();
    }

    private void startGame() {

        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                if (alertDialog != null)
                    alertDialog.show();

            } else {
                Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
            startActivity(intent);
        }

    }

    public void UserProfile(View view) {
        Utils.btnClick(view, ProfileActivity.this);
        if (!Session.isLogin(ProfileActivity.this)) {
            LoginPopUp();
        } else {
            // UpdateProfile();
        }
    }

    //    public void UpdateProfile() {
//        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
//        startActivity(intent);
//    }
    public void Settings(View view) {
        Utils.btnClick(view, ProfileActivity.this);
        Utils.CheckVibrateOrSound(ProfileActivity.this);
        Intent playQuiz = new Intent(ProfileActivity.this, FavoriteActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void LoginPopUp() {
        Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        AppController.StopSound();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.playSound();
        if (Utils.isNetworkAvailable(ProfileActivity.this)) {
            Utils.GetSystemConfig(getApplicationContext());

            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                LanguageDialog(ProfileActivity.this);
            }
            invalidateOptionsMenu();
            if (Session.isLogin(ProfileActivity.this)) {
                if (FirebaseAuth.getInstance().getUid() != null)
                    Utils.RemoveGameRoomId(FirebaseAuth.getInstance().getUid());
            }
        }
    }

    public void LanguageDialog(Activity activity) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        LayoutInflater inflater1 = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater1.inflate(R.layout.language_dialog, null);
        dialog.setView(dialogView);

        RecyclerView languageView = dialogView.findViewById(R.id.recyclerView);
        languageView.setLayoutManager(new LinearLayoutManager(activity));
        alertDialog = dialog.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Utils.GetLanguage(languageView, activity, alertDialog);

    }

    public void Favorite(View view) {
        Utils.btnClick(view, ProfileActivity.this);
        Utils.CheckVibrateOrSound(ProfileActivity.this);
        Intent playQuiz = new Intent(ProfileActivity.this, FavoriteActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class).putExtra("type", "privacy"));
        finish();
    }

}
