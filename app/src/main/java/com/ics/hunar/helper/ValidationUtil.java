package com.ics.hunar.helper;

import android.util.Patterns;
import android.widget.EditText;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final String characterRegex = "^[a-zA-Z]*$";

    public static boolean aadharEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() == 12) {
                valid = true;
            } else {
                editText.setError("please enter 12 digit Aadhar number");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }

    public static boolean pinCodeEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() == 6) {
                valid = true;
            } else {
                editText.setError("please enter 6 digit pinCode");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }

    public static boolean otpEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() == 4) {
                valid = true;
            } else {
                editText.setError("please enter 6 digit otp");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }


    public static boolean yearEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() == 4) {
                valid = true;
            } else {
                editText.setError("please enter current year like:2020");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }


    public static boolean editTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            valid = true;
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }

        return valid;
    }

    public static boolean phoneEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() == 10) {
                valid = true;
            } else {
                editText.setError("please enter 10 digit mobile number");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }

    public static boolean passwordEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (editText.getText().toString().trim().length() >= 6) {
                valid = true;
            } else {
                editText.setError("please enter minimum 6 character password");
                editText.requestFocus();
                valid = false;
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }


    public static boolean emailEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", editText.getText().toString().trim())) {
                valid = true;
            } else {
                valid = false;
                editText.setError("please enter valid email address");
                editText.requestFocus();
            }

        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }

    public static boolean emailOrMobileEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (Patterns.PHONE.matcher(editText.getText().toString().trim()).matches()) {
                if (editText.getText().toString().trim().length() == 10) {
                    valid = true;
                } else {
                    editText.setError("please enter 10 digit mobile number");
                    editText.requestFocus();
                    valid = false;
                }
            } else {
                if (Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", editText.getText().toString().trim())) {
                    valid = true;
                } else {
                    valid = false;
                    editText.setError("please enter valid email address");
                    editText.requestFocus();
                }
            }
        } else {
            editText.setError("required field");
            editText.requestFocus();
            valid = false;
        }
        return valid;
    }


    public static boolean optionalEmailEditTextValidation(EditText editText) {
        boolean valid;
        if (!editText.getText().toString().trim().isEmpty()) {
            if (Pattern.matches("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$", editText.getText().toString().trim())) {
                valid = true;
            } else {
                valid = false;
                editText.setError("please enter valid email address");
                editText.requestFocus();
            }

        } else {
            valid = true;
        }
        return valid;
    }
}
