package com.ics.hunar.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.auth.FirebaseAuth;
import com.ics.hunar.R;
import com.ics.hunar.adpter.SearchingCategoryAdapter;
import com.ics.hunar.adpter.SearchingSubCategoryAdapter;
import com.ics.hunar.adpter.SearchingVideoAdapter;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.AppController;
import com.ics.hunar.helper.NetworkUtils;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.helper.ValidationUtil;
import com.ics.hunar.model.searching.SearchingResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchingActivity extends AppCompatActivity implements Utils.OnLangChangeListener {
    private FirebaseAuth mAuth;
    private AlertDialog alertDialog;
    private ImageButton ibSearch, ibSearchBack;
    private RecyclerView rvSearchCategory, rvSearchSubCategory, rvSearchVideo;
    private ImageView ivLangSearch;
    private EditText etSearch;
    private ProgressBar pbSearching;
    private ApiInterface apiInterface;
    private Context context = SearchingActivity.this;
    private String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        SharedPreferencesUtil.init(context);
        if (!Session.getUserData(Session.USER_ID, context).equals("")) {
            userId = Session.getUserData(Session.USER_ID, context);
        } else if (!SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "").equals("")) {
            userId = SharedPreferencesUtil.read(SharedPreferencesUtil.USER_ID, "");
        }

        mAuth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        ibSearch = findViewById(R.id.ibSearch);
        ibSearchBack = findViewById(R.id.ibBackSearch);
        etSearch = findViewById(R.id.etSearch);
        rvSearchCategory = findViewById(R.id.rvSearchCategory);
        rvSearchSubCategory = findViewById(R.id.rvSearchSubCategory);
        rvSearchVideo = findViewById(R.id.rvSearchVideo);
        ivLangSearch = findViewById(R.id.ivLangSearch);
        pbSearching = findViewById(R.id.pbSearching);
        rvSearchCategory.setLayoutManager(new LinearLayoutManager(this));
        rvSearchCategory.setHasFixedSize(true);
        rvSearchSubCategory.setLayoutManager(new LinearLayoutManager(this));
        rvSearchSubCategory.setHasFixedSize(true);
        rvSearchVideo.setLayoutManager(new LinearLayoutManager(this));
        rvSearchVideo.setHasFixedSize(true);

        ibSearchBack.setOnClickListener(v -> {
            startActivity(new Intent(SearchingActivity.this, MainActivity.class).putExtra("type", "privacy"));
            finish();
        });
        ibSearch.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkConnected(context)) {
                if (ValidationUtil.editTextValidation(etSearch)) {
                    searchingFromApi(etSearch.getText().toString().trim());
                }
            } else {
                Toast.makeText(context, "internet not available", Toast.LENGTH_SHORT).show();
            }
        });
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (NetworkUtils.isNetworkConnected(context)) {
                    if (ValidationUtil.editTextValidation(etSearch)) {
                        searchingFromApi(etSearch.getText().toString().trim());
                    }
                } else {
                    Toast.makeText(context, "internet not available", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });
        ivLangSearch.setOnClickListener(v -> {
            if (alertDialog != null) {
                alertDialog.show();
            }
        });
    }

    private void searchingFromApi(String search_key) {
        pbSearching.setVisibility(View.VISIBLE);
        apiInterface.search("6808", "1", search_key, userId, Session.getCurrentLanguage(context)).enqueue(new Callback<SearchingResponse>() {
            @Override
            public void onResponse(Call<SearchingResponse> call, Response<SearchingResponse> response) {
                pbSearching.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            rvSearchCategory.setAdapter(new SearchingCategoryAdapter(context, response.body().getSearching().getCategories()));
                            rvSearchSubCategory.setAdapter(new SearchingSubCategoryAdapter(context, response.body().getSearching().getSubcategories()));
                            rvSearchVideo.setAdapter(new SearchingVideoAdapter(context, response.body().getSearching().getVideos()));
                        } else {
                            AlertDialogUtil.showAlertDialogBox(context, "Warning", "not found", false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(context, "Info", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchingResponse> call, Throwable t) {
                pbSearching.setVisibility(View.GONE);
                AlertDialogUtil.showAlertDialogBox(context, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    @Override
    protected void onPause() {
        AppController.StopSound();
        super.onPause();
    }

//    public void LanguageDialog() {
//        final AlertDialog.Builder dialog = new AlertDialog.Builder(SearchingActivity.this);
//        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View dialogView = inflater1.inflate(R.layout.language_dialog, null);
//        dialog.setView(dialogView);
//        RecyclerView languageView = dialogView.findViewById(R.id.recyclerView);
//
//        languageView.setLayoutManager(new LinearLayoutManager(SearchingActivity.this));
//        alertDialog = dialog.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        Utils.GetLanguage(languageView, SearchingActivity.this, alertDialog);
//
//
//    }


    @Override
    protected void onResume() {
        super.onResume();
        Utils.setOnLangChangeListener(this);
        AppController.playSound();
        if (Utils.isNetworkAvailable(SearchingActivity.this)) {
            Utils.GetSystemConfig(getApplicationContext());

            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                LanguageDialog(SearchingActivity.this);
            }
            invalidateOptionsMenu();
            if (Session.isLogin(SearchingActivity.this)) {
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
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            Utils.GetLanguage(languageView, activity, alertDialog);
        }
    }

    @Override
    public void onLangChange(boolean change) {
        if (true) {
            if (NetworkUtils.isNetworkConnected(context)) {
                if (ValidationUtil.editTextValidation(etSearch)) {
                    searchingFromApi(etSearch.getText().toString().trim());
                }
            } else {
                Toast.makeText(context, "internet not available", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void Home(View view) {
        startActivity(new Intent(SearchingActivity.this, MainActivity.class).putExtra("type", "privacy"));
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SearchingActivity.this, MainActivity.class).putExtra("type", "privacy"));
        finish();
    }

    public void Searching(View view) {
    }

    public void Favorite(View view) {
        Utils.btnClick(view, SearchingActivity.this);
        Utils.CheckVibrateOrSound(SearchingActivity.this);
        Intent playQuiz = new Intent(SearchingActivity.this, FavoriteActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
        finish();
    }

    public void UserProfile(View view) {
        Utils.btnClick(view, SearchingActivity.this);
        if (!Session.isLogin(SearchingActivity.this)) {
            LoginPopUp();
        } else {
            UpdateProfile();
        }
    }

    public void UpdateProfile() {
        Intent intent = new Intent(SearchingActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }


    public void logOut(View view) {
        if (!Session.isLogin(SearchingActivity.this)) {
            LoginPopUp();
        } else {
            SignOutWarningDialog();
        }
    }

    public void LoginPopUp() {
        Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
        startActivity(intent);
    }

    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(SearchingActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final android.app.AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(SearchingActivity.this);
                SharedPreferencesUtil.clearAllPreference();
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                logOut();
                Intent intentLogin = new Intent(SearchingActivity.this, LoginActivity.class);
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
        GoogleSignInAccount google_acc = GoogleSignIn.getLastSignedInAccount(SearchingActivity.this);
        if (google_acc != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.default_web_client_id))
                    .requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(SearchingActivity.this, gso);
            mGoogleSignInClient.signOut();
        } else {
            Log.e("------", "NO GOOGLE SIGN IN HERE");
        }
    }

}