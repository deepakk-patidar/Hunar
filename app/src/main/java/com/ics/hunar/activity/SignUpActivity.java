package com.ics.hunar.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.ics.hunar.R;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.NetworkUtils;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.Utils;
import com.ics.hunar.helper.ValidationUtil;
import com.ics.hunar.model.NormalSignUpResponse;
import com.ics.hunar.model.SendOtpResponse;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {


    public EditText edtName, edtEmail, edtPassword, edtMobile, edtOTP;
    public TextInputLayout inputName, inputEmail, inputPass, inputOtp, inputMobile;
    public ProgressDialog mProgressDialog;
    public static FirebaseAuth mAuth;
    private Button btnSendOTP;
    private ProgressBar progressBar;
    String token;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        edtName = findViewById(R.id.edtNameSignUp);
        edtEmail = findViewById(R.id.edtEmailSignUp);
        edtPassword = findViewById(R.id.edtPasswordSignUp);
        edtMobile = findViewById(R.id.edtMobileSignUp);
        edtOTP = findViewById(R.id.edtOTPSignUp);
        inputMobile = findViewById(R.id.inputMobile);
        inputOtp = findViewById(R.id.inputOtp);
        btnSendOTP = findViewById(R.id.btnSendOTP);
        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPass = findViewById(R.id.inputPass);
        progressBar = findViewById(R.id.pbSignUp);
        token = Session.getDeviceToken(getApplicationContext());
        if (token == null) {
            token = "token";
        }
        mAuth = FirebaseAuth.getInstance();

        edtName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person, 0, 0, 0);
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
    }

    public void ForgotPassword(View view) {

        FirebaseAuth.getInstance().sendPasswordResetEmail("user@example.com")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                        }
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        //String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(name)) {
            inputName.setError("Required.");
            valid = false;
        } else {
            inputName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            inputEmail.setError(getString(R.string.email_alert_1));
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            valid = false;
            inputEmail.setError(getString(R.string.email_alert_1));
        } else {
            inputEmail.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            inputPass.setError("Required.");
            valid = false;
        } else if (password.length() < 6) {
            inputPass.setError(getString(R.string.password_valid));
            valid = false;
        } else {
            inputPass.setError(null);
        }


        return valid;
    }

    private void sendEmailVerification() {

        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        //findViewById(R.id.verify_email_button).setEnabled(true);

                        if (task.isSuccessful()) {
                            //String refer = edtRefer.getText().toString();
//                            if (!refer.isEmpty())
//                                Session.setFCode(refer, getApplicationContext());
                            Toast.makeText(SignUpActivity.this, getString(R.string.verify_email_sent) + user.getEmail(), Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                        } else {

                            Toast.makeText(SignUpActivity.this, getString(R.string.verify_email_sent_f), Toast.LENGTH_LONG).show();
                            final FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential authCredential = EmailAuthProvider.getCredential(edtEmail.getText().toString(), edtPassword.getText().toString());
                            user1.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    user1.delete();
                                }
                            });

                            //auth.getCurrentUser().delete();
                        }
                        // [END_EXCLUDE]
                    }
                });

        // [END send_email_verification]
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("please wait otp sending...");
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

    public void SignUpWithNormal(View view) {
        if (NetworkUtils.isNetworkConnected(this)) {
            if (ValidationUtil.editTextValidation(edtName)) {
                if (ValidationUtil.optionalEmailEditTextValidation(edtEmail)) {
                    if (ValidationUtil.phoneEditTextValidation(edtMobile)) {
                        if (ValidationUtil.editTextValidation(edtOTP)) {
                            if (ValidationUtil.passwordEditTextValidation(edtPassword)) {
                                signUpWithApi(edtName.getText().toString().trim(), edtEmail.getText().toString().trim(), edtMobile.getText().toString().trim(), edtPassword.getText().toString().trim(), edtOTP.getText().toString().trim());
                            }
                        }

                    }
                }
            }
        } else {
            AlertDialogUtil.showAlertDialogBox(this, "Warning", "no internet available", false, "Ok", "", "response");
        }
    }

    private void signUpWithApi(String name, String email, String mobile, String password, String otp) {
        progressBar.setVisibility(View.VISIBLE);
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        apiInterface.normalSignUpResponse("6808", "1", name, email, mobile, password, otp).enqueue(new Callback<NormalSignUpResponse>() {
            @Override
            public void onResponse(Call<NormalSignUpResponse> call, Response<NormalSignUpResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getError().equalsIgnoreCase("false")) {
                            Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Info", response.body().getMessage(), false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Info", "null", false, "Ok", "", "response");
                    }

                }
            }

            @Override
            public void onFailure(Call<NormalSignUpResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");

            }
        });
    }

    private void sendOtp(String mobile) {
        showProgressDialog();
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        apiInterface.sendOtpToUser("6808", "1", mobile).enqueue(new Callback<SendOtpResponse>() {
            @Override
            public void onResponse(Call<SendOtpResponse> call, Response<SendOtpResponse> response) {
                hideProgressDialog();
                Utils.retro_call_info(""+response.raw().request().url(),""+new Gson().toJson(response.body()));
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Info", "otp sent to: " + mobile, false, "Ok", "", "response");
                        } else {
                            AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Info", "otp not sent", false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Info", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<SendOtpResponse> call, Throwable t) {
                hideProgressDialog();
                AlertDialogUtil.showAlertDialogBox(SignUpActivity.this, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    public void sendOTPToUser(View view) {
        if (NetworkUtils.isNetworkConnected(this)) {
            if (ValidationUtil.phoneEditTextValidation(edtMobile)) {
                sendOtp(edtMobile.getText().toString().trim());
            }
        } else {
            AlertDialogUtil.showAlertDialogBox(this, "Warning", "no internet available", false, "Ok", "", "response");
        }
    }
}
