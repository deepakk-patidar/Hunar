package com.ics.hunar.activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.ics.hunar.Constant;
import com.ics.hunar.R;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.AppController;
import com.ics.hunar.helper.NetworkUtils;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.helper.ValidationUtil;
import com.ics.hunar.model.NormalLoginResponse;
import com.ics.hunar.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";
    int RC_SIGN_IN = 9001;
    CallbackManager mCallbackManager;
    String token;

    public static FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    TextView tvPrivacy;
    ProgressDialog mProgressDialog;
    public EditText edtEmail, edtPassword;
    public TextInputLayout inputEmail, inputPass;

    public LoginActivity() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        SharedPreferencesUtil.init(this);
        tvPrivacy = findViewById(R.id.tvPrivacy);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        if (Session.isLogin(getApplicationContext())) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("type", "default");
            startActivity(intent);
            finish();
        }
        if (!Utils.isNetworkAvailable(LoginActivity.this)) {
            setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
        }
        token = Session.getDeviceToken(getApplicationContext());
        if (token == null) {
            token = "token";
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

        edtEmail.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0);
        edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.ic_show, 0);
        edtPassword.setTag("show");
        edtPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (edtPassword.getRight() - edtPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        if (edtPassword.getTag().equals("show")) {
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.ic_hide, 0);
                            edtPassword.setTransformationMethod(null);
                            edtPassword.setTag("hide");
                        } else {
                            edtPassword.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, R.drawable.ic_show, 0);
                            edtPassword.setTransformationMethod(new PasswordTransformationMethod());
                            edtPassword.setTag("show");
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        PrivacyPolicy();
        Utils.GetSystemConfig(getApplicationContext());
    }

    public void LoginWithFacebook(View view) {
        if (Utils.isNetworkAvailable(LoginActivity.this)) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
            LoginManager.getInstance().registerCallback(mCallbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            setResult(RESULT_OK);
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            hideProgressDialog();
                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d(TAG, "facebook:onError", error);
                            error.printStackTrace();
                        }
                    });

        } else
            setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
    }

    public void LoginWithGoogle(View view) {
        if (Utils.isNetworkAvailable(LoginActivity.this))
            signIn();
        else
            setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
    }

    public void PlayAsGuest(View view) {
        if (Utils.isNetworkAvailable(LoginActivity.this)) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("type", "default");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        } else
            setSnackBar(getString(R.string.msg_no_internet), getString(R.string.retry));
    }


    public void ShowReferDialog(final String referCode, final String name, final String email, final String profile, final String type) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.refer_dailog, null);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        final AlertDialog alertDialog = dialog.create();

        TextView tvCancel = dialogView.findViewById(R.id.tvCancel);
        TextView tvApply = dialogView.findViewById(R.id.tvApply);
        final EditText edtRefCode = dialogView.findViewById(R.id.edtRefCode);

        Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSignUpWithSocialMedia(edtRefCode.getText().toString(), referCode, name, email, profile, type);
                alertDialog.dismiss();
            }
        });
        tvApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                UserSignUpWithSocialMedia(edtRefCode.getText().toString(), referCode, name, email, profile, type);

                alertDialog.dismiss();

            }
        });
        alertDialog.show();

    }


    public void UserSignUpWithSocialMedia(final String fCode, final String referCode, final String name, final String email, final String profile, final String type) {
        StringRequest strReq = new StringRequest(Request.Method.POST, Constant.QUIZ_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        JSONObject jsonobj = obj.getJSONObject("data");
                        if (!jsonobj.getString(Constant.status).equals(Constant.DE_ACTIVE)) {
                            Session.saveUserDetail(getApplicationContext(),
                                    jsonobj.getString(Constant.userId),
                                    jsonobj.getString(Constant.name),
                                    jsonobj.getString(Constant.email),
                                    jsonobj.getString(Constant.mobile),
                                    jsonobj.getString(Constant.PROFILE), referCode);
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_ID, jsonobj.getString(Constant.userId));
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_NAME, jsonobj.getString(Constant.name));
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_EMAIL, jsonobj.getString(Constant.email));
                            SharedPreferencesUtil.write(SharedPreferencesUtil.MOBILE_NUMBER, jsonobj.getString(Constant.mobile));
                            SharedPreferencesUtil.write(SharedPreferencesUtil.PROFILE, jsonobj.getString(Constant.PROFILE));
                            User user = new User(jsonobj.getString(Constant.name), jsonobj.getString(Constant.email), "0", false, jsonobj.getString(Constant.PROFILE), "0", token, jsonobj.getString(Constant.userId));

                            FirebaseDatabase.getInstance().getReference("user").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task1) {
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    i.putExtra("type", "default");
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    finish();
                                }
                            });
                            hideProgressDialog();
                        } else
                            setSnackBarStatus();
                    } else {
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }

        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(Constant.accessKey, Constant.accessKeyValue);
                params.put(Constant.userSignUp, "1");
                params.put(Constant.email, email);
                params.put(Constant.name, name);
                params.put(Constant.PROFILE, profile);
                params.put(Constant.fcmId, token);
                params.put(Constant.type, type);
                params.put(Constant.mobile, "");
                params.put(Constant.REFER_CODE, referCode);
                params.put(Constant.FRIENDS_CODE, fCode);
                WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
                params.put(Constant.ipAddress, ip);
                System.out.println("---params social  " + params.toString());
                return params;

            }
        };

        // AppController.getInstance().getRequestQueue().getCache().clear();
        AppController.getInstance().addToRequestQueue(strReq);

    }


    private void handleFacebookAccessToken(AccessToken token) {
        showProgressDialog();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                                FirebaseUser user = mAuth.getCurrentUser();
                                String[] userName = user.getEmail().split("@");
                                if (isNew) {
                                    hideProgressDialog();
                                    ShowReferDialog("", user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString(), "fb");
                                } else
                                    UserSignUpWithSocialMedia("", "", user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString(), "fb");
                            } else {
                                // If sign in fails, display a message to the user.

                                LoginManager.getInstance().logOut();
                                hideProgressDialog();
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {

                                    setSnackBar(invalidEmail.getMessage(), getString(R.string.ok));
                                } catch (Exception e) {
                                    e.printStackTrace();

                                    setSnackBar(e.getMessage(), getString(R.string.ok));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser user = mAuth.getCurrentUser();
                            assert user != null;
                            String personName = user.getDisplayName();

//                            if (personName.contains(" ")) {
//                                personName = personName.substring(0, personName.indexOf(" "));
//                            }
//                            String email = user.getEmail();
//                            String[] userName = user.getEmail().split("@");

                            if (isNew) {
                                hideProgressDialog();
                                ShowReferDialog("", user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), "gmail");
                            } else
                                UserSignUpWithSocialMedia("", "", user.getDisplayName(), user.getEmail(), String.valueOf(user.getPhotoUrl()), "gmail");
                        } else {
                            hideProgressDialog();
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidCredentialsException | FirebaseAuthInvalidUserException | FirebaseAuthUserCollisionException invalidEmail) {
                                setSnackBar(invalidEmail.getMessage(), getString(R.string.ok));
                            } catch (Exception e) {
                                e.printStackTrace();
                                setSnackBar(e.getMessage(), getString(R.string.ok));
                            }
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        showProgressDialog();
    }

    public void ForgotPasswordWithApi(View view) {
        startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        finish();
    }


    public void SignUpWithEmail(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }


    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }


    public void PrivacyPolicy() {
        tvPrivacy.setClickable(true);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        String message = getString(R.string.term_privacy);
        String s2 = getString(R.string.terms);
        String s1 = getString(R.string.privacy_policy);
        final Spannable wordtoSpan = new SpannableString(message);

        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "privacy");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.isUnderlineText();
            }
        }, message.indexOf(s1), message.indexOf(s1) + s1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PrivacyPolicy.class);
                intent.putExtra("type", "terms");
                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.isUnderlineText();
            }
        }, message.indexOf(s2), message.indexOf(s2) + s2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacy.setText(wordtoSpan);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        //String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_alert_1));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            inputEmail.setError(getString(R.string.email_alert_2));
        } else {
            inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            inputPass.setError(getString(R.string.pass_alert));
            valid = false;
        } else {
            inputPass.setError(null);
        }


        return valid;
    }

    public void LoginWithEmailOrMobile(View view) {
        if (NetworkUtils.isNetworkConnected(this)) {
            if (ValidationUtil.emailOrMobileEditTextValidation(edtEmail)) {
                if (ValidationUtil.passwordEditTextValidation(edtPassword)) {
                    loginWithApi(edtEmail.getText().toString().trim(), edtPassword.getText().toString().trim());
                }
            }

        } else {
            AlertDialogUtil.showAlertDialogBox(this, "Warning", "no internet available", false, "Ok", "", "response");
        }
    }

    private void loginWithApi(String emailOrMobile, String password) {
        showProgressDialog();
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        apiInterface.normalLoginResponse("6808", "1", emailOrMobile, password).enqueue(new Callback<NormalLoginResponse>() {
            @Override
            public void onResponse(Call<NormalLoginResponse> call, retrofit2.Response<NormalLoginResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            Session.saveUserDetail(getApplicationContext(),
                                    response.body().getNormalLogin().getUserId(),
                                    response.body().getNormalLogin().getName(),
                                    response.body().getNormalLogin().getEmail(),
                                    response.body().getNormalLogin().getMobile(),
                                    response.body().getNormalLogin().getProfile(), "referCode");
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_ID, response.body().getNormalLogin().getUserId());
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_NAME, response.body().getNormalLogin().getName());
                            SharedPreferencesUtil.write(SharedPreferencesUtil.USER_EMAIL, response.body().getNormalLogin().getEmail());
                            SharedPreferencesUtil.write(SharedPreferencesUtil.MOBILE_NUMBER, response.body().getNormalLogin().getMobile());
                            SharedPreferencesUtil.write(SharedPreferencesUtil.PROFILE, response.body().getNormalLogin().getProfile());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("type", "normal"));
                            finish();
                        } else {
                            AlertDialogUtil.showAlertDialogBox(LoginActivity.this, "Info", "please login with valid user id and password", false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(LoginActivity.this, "Info", "null", false, "Ok", "", "response");

                    }
                }
            }

            @Override
            public void onFailure(Call<NormalLoginResponse> call, Throwable t) {
                hideProgressDialog();
                AlertDialogUtil.showAlertDialogBox(LoginActivity.this, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    public void setSnackBar(String message, String action) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(LoginActivity.this)) {
                    snackbar.dismiss();
                } else {
                    snackbar.show();
                }
            }
        });
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setMaxLines(5);
        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
    }

    public void setSnackBarStatus() {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.account_deactivate), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Session.clearUserSession(getApplicationContext());
                mAuth.signOut();
                LoginManager.getInstance().logOut();

            }
        });

        snackbar.setActionTextColor(Color.RED);
        snackbar.show();
        hideProgressDialog();


    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with FireBase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {

            }
        } else {
            // Pass the activity result back to the Facebook SDK
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}