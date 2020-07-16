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
import com.ics.hunar.model.SendOtpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberScreenFragment extends Fragment {
    private Button btnMobileSubmit;
    private EditText etMobileNumber;
    private ProgressDialog mProgressDialog;
    private View view;
    private Activity activity;

    public MobileNumberScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mobile_number_screen, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = getActivity();
        SharedPreferencesUtil.init(activity);
        btnMobileSubmit = view.findViewById(R.id.btnMobileSubmit);
        etMobileNumber = view.findViewById(R.id.etFMobileNumber);
        btnMobileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkConnected(activity)) {
                    if (ValidationUtil.phoneEditTextValidation(etMobileNumber)) {
                        sendOtp(etMobileNumber.getText().toString().trim());
                    }
                } else {
                    AlertDialogUtil.showAlertDialogBox(activity, "Warning", "no internet available", false, "Ok", "", "response");
                }
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
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (!response.body().getError()) {
                            SharedPreferencesUtil.write(SharedPreferencesUtil.FORGOT_MOBILE_NUMBER, mobile);
                            Toast.makeText(activity, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            loadFragments(new CheckOTPScreenFragment());
                        } else {
                            AlertDialogUtil.showAlertDialogBox(activity, "Info", response.body().getMessage(), false, "Ok", "", "response");
                        }
                    } else {
                        AlertDialogUtil.showAlertDialogBox(activity, "Info", "null", false, "Ok", "", "response");
                    }
                }
            }

            @Override
            public void onFailure(Call<SendOtpResponse> call, Throwable t) {
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