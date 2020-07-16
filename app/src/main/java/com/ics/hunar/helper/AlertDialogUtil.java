package com.ics.hunar.helper;

import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

public class AlertDialogUtil {
    private static AlertDialog alertDialog;

    public static void showAlertDialogBox(Context context, String title, String msg, boolean cancelable, String pos, String neg, String hint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(msg).setCancelable(cancelable);
        builder.setPositiveButton(pos, (dialog, which) -> {
            if (hint.equalsIgnoreCase("INTERNET")) {
                getSetting(context);
            } else if (hint.equalsIgnoreCase("RESPONSE")) {
                dialog.dismiss();
            }else {
                dialog.dismiss();
            }
        }).setNegativeButton(neg, (dialog, which) -> dialog.cancel());
        alertDialog = builder.create();
        alertDialog.show();
    }

    private static void getSetting(Context context) {
        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(dialogIntent);
    }

    public static void hideAlertDialogBox() {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }
}
