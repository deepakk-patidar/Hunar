package com.ics.hunar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.adpter.BannerViewPagerAdapter;
import com.ics.hunar.adpter.FeaturesAdapter;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.AppController;
import com.ics.hunar.helper.BookmarkDBHelper;
import com.ics.hunar.helper.DBHelper;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.model.BannerResponse;
import com.ics.hunar.model.features.FeaturesResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends DrawerActivity implements View.OnClickListener {
    private Context context = MainActivity.this;
    private SharedPreferences settings;
    public static DBHelper dbHelper;
    public static BookmarkDBHelper bookmarkDBHelper;
    public String type;
    public View divider;
    public LinearLayout lytBattle, lytPlay;
    public LinearLayout bottomLyt;
    public LinearLayout lytMidScreen;
    public String status = "0";
    public TextView tvAlert, tvBattle, tvPlay;
    public Toolbar toolbar;
    private RecyclerView rvHomeFeature, rvHomeResumeHistory, rvHomeTrending1, rvHomeTrending2;
    private ViewPager vpBanner;
    private CircleIndicator circleIndicator;
    private ApiInterface apiInterface;
    private AlertDialog alertDialog;
    private ImageView imgLogout;
    private int currentPage = 0;
    private ProgressBar pbViewPager, pbFeatures;
    private Button btnRetry, btnFeaturesRetry;
    private ScrollView svHome;
    private TextView tvFeatureHeading, tvResumeHeading, tvTrending1Heading, tvTrending2Heading;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.transparentStatusAndNavigation(MainActivity.this);

        getLayoutInflater().inflate(R.layout.activity_main, frameLayout);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        SharedPreferencesUtil.init(this);
        rvHomeFeature = findViewById(R.id.rvHomeFeature);
        rvHomeResumeHistory = findViewById(R.id.rvHomeResumeHistory);
        rvHomeTrending1 = findViewById(R.id.rvHomeTrending1);
        rvHomeTrending2 = findViewById(R.id.rvHomeTrending2);
        tvFeatureHeading = findViewById(R.id.tvFeatureHeading);
        tvResumeHeading = findViewById(R.id.tvResumeHeading);
        tvTrending1Heading = findViewById(R.id.tvTrending1Heading);
        tvTrending2Heading = findViewById(R.id.tvTrending2Heading);
        svHome = findViewById(R.id.svHome);
        vpBanner = findViewById(R.id.vpBanner);
        circleIndicator = findViewById(R.id.indicator);
        pbViewPager = findViewById(R.id.pbViewpager);
        pbFeatures = findViewById(R.id.pbFeatures);
        btnRetry = findViewById(R.id.btnRetry);
        btnFeaturesRetry = findViewById(R.id.btnFeaturesRetry);
        pbViewPager.setIndeterminateDrawable(Utils.getCircularProgressDrawable(context, 7, 25));
        rvHomeFeature.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeFeature.setHasFixedSize(true);
        rvHomeResumeHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeResumeHistory.setHasFixedSize(true);
        rvHomeTrending1.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeTrending1.setHasFixedSize(true);
        rvHomeTrending2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeTrending2.setHasFixedSize(true);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
        }
        divider = findViewById(R.id.divider);

        Utils.GetSystemConfig(getApplicationContext());
        try {
            dbHelper = new DBHelper(getApplicationContext());
            bookmarkDBHelper = new BookmarkDBHelper(getApplicationContext());
            dbHelper.createDatabase();
            bookmarkDBHelper.createDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        drawerToggle = new ActionBarDrawerToggle
                (
                        this,
                        drawerLayout, toolbar,
                        R.string.drawer_open,
                        R.string.drawer_close
                ) {
        };

        lytBattle = findViewById(R.id.lytBattle);
        lytPlay = findViewById(R.id.lytPlay);
        lytMidScreen = findViewById(R.id.midScreen);
        bottomLyt = findViewById(R.id.bottomLayout);
        tvAlert = findViewById(R.id.tvAlert);
        imgLogout = findViewById(R.id.imgLogout);
        tvBattle = findViewById(R.id.tvBattle);
        tvBattle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.battle, 0, 0, 0);
        tvPlay = findViewById(R.id.tvPlay);
        tvPlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.play, 0, 0, 0);
        //battle button only shown when user already login
        lytBattle.setOnClickListener(this);
        lytPlay.setOnClickListener(this);

        settings = getSharedPreferences(Session.SETTING_Quiz_PREF, 0);
        type = getIntent().getStringExtra("type");

        if (!type.equals("null")) {
            if (type.equals("category")) {
                Constant.TotalLevel = Integer.valueOf(getIntent().getStringExtra("maxLevel"));
                Constant.CATE_ID = Integer.valueOf(getIntent().getStringExtra("cateId"));
                if (getIntent().getStringExtra("no_of").equals("0")) {
                    Intent intent = new Intent(MainActivity.this, LevelActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, SubcategoryActivity.class);
                    startActivity(intent);
                }
            }
        }
        navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout);
        NavigationCartCount();

//        if (Utils.isNetworkAvailable(MainActivity.this)) {
//            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
//                LanguageDialog(MainActivity.this);
//            if (Session.isLogin(getApplicationContext())) {
//                imgLogout.setImageResource(R.drawable.logout);
//                GetUserStatus();
//            } else {
//                imgLogout.setImageResource(R.drawable.login);
//            }

        //}
        btnRetry.setOnClickListener(v -> getBanner());

        btnFeaturesRetry.setOnClickListener(v -> getFeatures());
        getFeatures();
        getBanner();
    }

    private void getFeatures() {
        svHome.setVisibility(View.GONE);
        pbFeatures.setVisibility(View.VISIBLE);
        btnFeaturesRetry.setVisibility(View.GONE);
        apiInterface.getFeatures("6808", "1").enqueue(new Callback<FeaturesResponse>() {
            @Override
            public void onResponse(Call<FeaturesResponse> call, retrofit2.Response<FeaturesResponse> response) {
                pbFeatures.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            svHome.setVisibility(View.VISIBLE);
                            List<String> stringList = new ArrayList<>();
                            stringList.add("Features");
                            stringList.add("Resume your Courses");
                            stringList.add(response.body().getTrending1());
                            stringList.add(response.body().getTrending2());
                            tvFeatureHeading.setText(stringList.get(0));
                            tvResumeHeading.setText(stringList.get(1));
                            tvTrending1Heading.setText(stringList.get(2));
                            tvTrending2Heading.setText(stringList.get(3));
                            rvHomeFeature.setAdapter(new FeaturesAdapter(context, response.body().getCategories(), response.body().getSubcategories(), stringList.get(0)));
                            rvHomeTrending1.setAdapter(new FeaturesAdapter(context, response.body().getCategories(), response.body().getSubcategories(), stringList.get(2)));
                            rvHomeTrending2.setAdapter(new FeaturesAdapter(context, response.body().getCategories(), response.body().getSubcategories(), stringList.get(3)));

                        } else {
                            AlertDialogUtil.showAlertDialogBox(context, "warning", "not found", false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(context, "warning", "null", false, "Ok", "", "response");

                    }
                }
            }

            @Override
            public void onFailure(Call<FeaturesResponse> call, Throwable t) {
                svHome.setVisibility(View.GONE);
                pbFeatures.setVisibility(View.GONE);
                btnFeaturesRetry.setVisibility(View.VISIBLE);
                //  AlertDialogUtil.showAlertDialogBox(context, "error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    //Method to set timer to slider
    private void setSliderAutoTimer(int NUM_PAGES) {
        final Handler handler = new Handler();
        final Runnable update = () -> {
            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            try {
                vpBanner.setCurrentItem(currentPage++, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 5000);
    }

    private void getBanner() {
        btnRetry.setVisibility(View.GONE);
        pbViewPager.setVisibility(View.VISIBLE);
        apiInterface.getBanner("6808", "1").enqueue(new Callback<BannerResponse>() {
            @Override
            public void onResponse(Call<BannerResponse> call, retrofit2.Response<BannerResponse> response) {
                pbViewPager.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            vpBanner.setAdapter(new BannerViewPagerAdapter(context, response.body().getBanner()));
                            circleIndicator.setViewPager(vpBanner);
                            setSliderAutoTimer(response.body().getBanner().size());
                            vpBanner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                @Override
                                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                }

                                @Override
                                public void onPageSelected(int position) {
                                    currentPage = position;
                                }

                                @Override
                                public void onPageScrollStateChanged(int state) {

                                }
                            });
                        } else {
                            AlertDialogUtil.showAlertDialogBox(context, "warning", "not found", false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(context, "warning", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<BannerResponse> call, Throwable t) {
                pbViewPager.setVisibility(View.GONE);
                btnRetry.setVisibility(View.VISIBLE);
                // AlertDialogUtil.showAlertDialogBox(context, "error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
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

    public void GetUserStatus() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.QUIZ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            boolean error = obj.getBoolean("error");
                            if (!error) {
                                JSONObject jsonobj = obj.getJSONObject("data");
                                if (jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                                    Session.clearUserSession(getApplicationContext());
                                    FirebaseAuth.getInstance().signOut();
                                    LoginManager.getInstance().logOut();
                                    Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                                    startActivity(intentLogin);
                                    finish();
                                } else {
                                    Constant.TOTAL_COINS = Integer.parseInt(jsonobj.getString(Constant.COINS));
                                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                        @Override

                                        public void onSuccess(InstanceIdResult instanceIdResult) {
                                            String token = instanceIdResult.getToken();
                                            if (!token.equals(Session.getUserData(Session.FCM, getApplicationContext()))) {
                                                Utils.postTokenToServer(getApplicationContext(), token);
                                            }
                                        }
                                    });
                                    Utils.RemoveGameRoomId(FirebaseAuth.getInstance().getUid());
                                }
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
                params.put(Constant.GET_USER_BY_ID, "1");
                params.put(Constant.ID, Session.getUserData(Session.USER_ID, getApplicationContext()));

                return params;
            }
        };
        AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(stringRequest);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lytBattle:
                Utils.btnClick(view, MainActivity.this);
                if (!Session.isLogin(MainActivity.this)) {
                    LoginPopUp();
                } else {
                    if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                        if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                            if (alertDialog != null)
                                alertDialog.show();
                        } else {
                            Intent intent = new Intent(MainActivity.this, GetOpponentActivity.class);
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(MainActivity.this, GetOpponentActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case R.id.lytPlay:
                Utils.btnClick(view, MainActivity.this);
                startGame();
                break;
        }
    }


    public void UpdateProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        finish();
        startActivity(intent);
    }


    //send registration token to server

    public void LeaderBoard(View view) {
        Utils.btnClick(view, MainActivity.this);
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            Intent intent1 = new Intent(MainActivity.this, LeaderBoardActivity.class);
            startActivity(intent1);
        }
    }

//    public void Logout(View view) {
//        Utils.btnClick(view, MainActivity.this);
//        if (!Session.isLogin(MainActivity.this)) {
//            LoginPopUp();
//        } else {
//            Utils.SignOutWarningDialog(MainActivity.this);
//        }
//    }

    public void StartQuiz(View view) {
        startGame();
    }

    private void startGame() {

        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
            if (Session.getCurrentLanguage(getApplicationContext()).equals(Constant.D_LANG_ID)) {
                if (alertDialog != null)
                    alertDialog.show();

            } else {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        }

    }

    public void UserProfile(View view) {
        Utils.btnClick(view, MainActivity.this);
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            UpdateProfile();
        }
    }

    public void Settings(View view) {
        Utils.btnClick(view, MainActivity.this);
        Utils.CheckVibrateOrSound(MainActivity.this);
        Intent playQuiz = new Intent(MainActivity.this, FavoriteActivity.class);
        startActivity(playQuiz);
        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    public void LoginPopUp() {
        Intent intent = new Intent(getApplicationContext(), LoginPopup.class);
        startActivity(intent);
    }

    public void NavigationCartCount() {

        View viewCount = navigationView.getMenu().getItem(3).setActionView(R.layout.cart_count_layout).getActionView();
        TextView tvCount = viewCount.findViewById(R.id.tvCount);
        if (Session.getNCount(getApplicationContext()) == 0) {
            tvCount.setVisibility(View.GONE);
        } else {
            tvCount.setVisibility(View.VISIBLE);
        }
        tvCount.setText(String.valueOf(Session.getNCount(getApplicationContext())));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (Session.getBoolean(Session.LANG_MODE, getApplicationContext()))
            menu.findItem(R.id.language).setVisible(true);
        else
            menu.findItem(R.id.language).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.language:
                if (alertDialog != null)
                    alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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
        if (Utils.isNetworkAvailable(MainActivity.this)) {
            Utils.GetSystemConfig(getApplicationContext());

            if (Session.getBoolean(Session.LANG_MODE, getApplicationContext())) {
                LanguageDialog(MainActivity.this);
            }
            invalidateOptionsMenu();
            if (Session.isLogin(MainActivity.this)) {
                NavigationCartCount();
                if (FirebaseAuth.getInstance().getUid() != null)
                    Utils.RemoveGameRoomId(FirebaseAuth.getInstance().getUid());
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void downloadNavigation(View view) {
        Toast.makeText(this, "not available", Toast.LENGTH_SHORT).show();
    }

    public void logOut(View view) {
        if (!Session.isLogin(MainActivity.this)) {
            LoginPopUp();
        } else {
            SignOutWarningDialog();
        }

    }

    public void SignOutWarningDialog() {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.logout_warning));
        alertDialog.setCancelable(false);
        final android.app.AlertDialog alertDialog1 = alertDialog.create();

        // Setting OK Button
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                Session.clearUserSession(MainActivity.this);
                SharedPreferencesUtil.clearAllPreference();
                LoginManager.getInstance().logOut();
                LoginActivity.mAuth.signOut();
                logOut();
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
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
        GoogleSignInAccount google_acc = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (google_acc != null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getResources().getString(R.string.default_web_client_id))
                    .requestEmail().build();
            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);
            mGoogleSignInClient.signOut();
        } else {
            Log.e("------", "NO GOOGLE SIGN IN HERE");
        }
    }

    public void Searching(View view) {
        startActivity(new Intent(MainActivity.this, SearchingActivity.class));
        finish();
    }
}
