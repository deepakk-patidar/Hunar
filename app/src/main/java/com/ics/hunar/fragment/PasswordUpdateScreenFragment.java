package com.ics.hunar.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ics.hunar.R;
import com.ics.hunar.activity.LoginActivity;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.NetworkUtils;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.ValidationUtil;
import com.ics.hunar.model.PasswordUpdateResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordUpdateScreenFragment extends Fragment {
    private EditText etForgotPassword;
    private Button btnNewPassword;
    private View view;
    private Activity activity;
    private ProgressDialog mProgressDialog;

    public PasswordUpdateScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_password_update_screen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        SharedPreferencesUtil.init(activity);
        etForgotPassword = view.findViewById(R.id.etForgotPassword);
        btnNewPassword = view.findViewById(R.id.btnNewPasswordSubmit);
        btnNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkConnected(activity)) {
                    if (ValidationUtil.passwordEditTextValidation(etForgotPassword)) {
                        updatePassword(etForgotPassword.getText().toString().trim());
                    }
                } else {
                    AlertDialogUtil.showAlertDialogBox(activity, "Warning", "no internet available", false, "Ok", "", "response");
                }
            }
        });
    }

    private void updatePassword(String newPassword) {
        showProgressDialog();
        String userId = SharedPreferencesUtil.read(SharedPreferencesUtil.FORGOT_USER_ID, "");
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        apiInterface.passwordUpdate("6808", "1", userId, newPassword).enqueue(new Callback<PasswordUpdateResponse>() {
            @Override
            public void onResponse(Call<PasswordUpdateResponse> call, Response<PasswordUpdateResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(activity, LoginActivity.class));
                            activity.finish();
                        } else {
                            AlertDialogUtil.showAlertDialogBox(activity, "Info", response.body().getMessage(), false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(activity, "Info", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<PasswordUpdateResponse> call, Throwable t) {
                hideProgressDialog();
                AlertDialogUtil.showAlertDialogBox(activity, "Error", t.getLocalizedMessage(), false, "Ok", "", "response");
            }
        });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(activity);
            mProgressDialog.setMessage("please wait...");
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}