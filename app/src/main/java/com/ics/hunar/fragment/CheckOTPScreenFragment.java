package com.ics.hunar.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.ics.hunar.R;
import com.ics.hunar.helper.AlertDialogUtil;
import com.ics.hunar.helper.ApiClient;
import com.ics.hunar.helper.ApiInterface;
import com.ics.hunar.helper.NetworkUtils;
import com.ics.hunar.helper.SharedPreferencesUtil;
import com.ics.hunar.helper.ValidationUtil;
import com.ics.hunar.model.CheckOTPResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOTPScreenFragment extends Fragment {
    private EditText etForgotOTP;
    private Button btnCheckOTP;
    private View view;
    private Activity activity;
    private ProgressDialog mProgressDialog;

    public CheckOTPScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check_o_t_p_screen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        SharedPreferencesUtil.init(activity);
        etForgotOTP = view.findViewById(R.id.etForgotOTP);
        btnCheckOTP = view.findViewById(R.id.btnOTPSubmit);
        btnCheckOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkConnected(activity)) {
                    if (ValidationUtil.editTextValidation(etForgotOTP)) {
                        checkOTP(etForgotOTP.getText().toString().trim());
                    }
                } else {
                    AlertDialogUtil.showAlertDialogBox(activity, "Warning", "no internet available", false, "Ok", "", "response");
                }
            }
        });
    }

    private void checkOTP(String otp) {
        showProgressDialog();
        String mobile = SharedPreferencesUtil.read(SharedPreferencesUtil.FORGOT_MOBILE_NUMBER, "");
        ApiInterface apiInterface = ApiClient.getMainClient().create(ApiInterface.class);
        apiInterface.checkOTP("6808", "1", mobile, otp).enqueue(new Callback<CheckOTPResponse>() {
            @Override
            public void onResponse(Call<CheckOTPResponse> call, Response<CheckOTPResponse> response) {
                hideProgressDialog();
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            SharedPreferencesUtil.write(SharedPreferencesUtil.FORGOT_USER_ID, response.body().getUserId());
                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            loadFragments(new PasswordUpdateScreenFragment());
                        } else {
                            AlertDialogUtil.showAlertDialogBox(activity, "Info", response.body().getMessage(), false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(activity, "Info", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckOTPResponse> call, Throwable t) {
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

    private void loadFragments(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            fragmentManager.beginTransaction().replace(R.id.fl_ForgotPassword, fragment).commit();
        }
    }

}