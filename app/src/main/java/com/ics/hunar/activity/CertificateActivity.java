package com.ics.hunar.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hendrix.pdfmyxml.PdfDocument;
import com.hendrix.pdfmyxml.viewRenderer.AbstractViewRenderer;
import com.ics.hunar.R;
import com.ics.hunar.helper.Session;
import com.ics.hunar.helper.SharedPreferencesUtil;

import java.io.File;

public class CertificateActivity extends AppCompatActivity {
    private String name, name1, courseName, courseName1;
    private TextView tvFullName, tvCourseName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        SharedPreferencesUtil.init(this);
        tvFullName = findViewById(R.id.tvFullName);
        tvCourseName = findViewById(R.id.tvCourseName);
        name = SharedPreferencesUtil.read(SharedPreferencesUtil.USER_NAME, "");
        name1 = Session.getUserData(Session.NAME, CertificateActivity.this);
        courseName = SharedPreferencesUtil.read(SharedPreferencesUtil.SUB_CATEGORY_NAME, "");
        courseName1 = Session.getSubCategoryName(Session.SUB_CAT_NAME, this);
        layoutToPDF(this);
        if (!name.equals("")) {
            tvFullName.setText(String.format("%s has completed", name));
        } else if (!name1.equals("")) {
            tvFullName.setText(String.format("%s has completed", name1));
        } else {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CertificateActivity.this, "UserName not found", Toast.LENGTH_SHORT).show());
        }
        if (!courseName.equals("")) {
            tvCourseName.setText(String.format("%s successfully", courseName));
        } else if (!courseName1.equals("")) {
            tvCourseName.setText(String.format("%s successfully", courseName1));
        } else {
            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CertificateActivity.this, "CourseName not found", Toast.LENGTH_SHORT).show());
        }

    }

    private void layoutToPDF(Context context) {
        AbstractViewRenderer page = new AbstractViewRenderer(context, R.layout.activity_certificate) {
            @Override
            protected void initView(View view) {
                TextView tvFullName = view.findViewById(R.id.tvFullName);
                TextView tvCourseName = view.findViewById(R.id.tvCourseName);
                if (!name.equals("")) {
                    tvFullName.setText(String.format("%s has completed", name));
                } else if (!name1.equals("")) {
                    tvFullName.setText(String.format("%s has completed", name1));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CertificateActivity.this, "UserName not found", Toast.LENGTH_SHORT).show());
                }
                if (!courseName.equals("")) {
                    tvCourseName.setText(String.format("%s successfully", courseName));
                } else if (!courseName1.equals("")) {
                    tvCourseName.setText(String.format("%s successfully", courseName1));
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(CertificateActivity.this, "CourseName not found", Toast.LENGTH_SHORT).show());
                }
            }
        };

// you can reuse the bitmap if you want
        page.setReuseBitmap(true);

        PdfDocument doc = new PdfDocument(context);
        File file = new File(context.getExternalFilesDir(null) + "/HunarCertificate");
        String fileName = courseName + System.currentTimeMillis();
// add as many pages as you have
        doc.addPage(page);

        doc.setRenderWidth(2115);
        doc.setRenderHeight(2000);
        doc.setOrientation(PdfDocument.A4_MODE.PORTRAIT);
        doc.setProgressTitle(R.string.gen_please_wait);
        doc.setProgressMessage(R.string.gen_pdf_file);
        doc.setFileName(fileName);
        doc.setSaveDirectory(context.getExternalFilesDir(null));
        doc.setInflateOnMainThread(false);
        doc.setListener(new PdfDocument.Callback() {
            @Override
            public void onComplete(File file) {
                Toast.makeText(context, "completed\n" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("Doc Error: " + e.getLocalizedMessage());
            }
        });

        doc.createPdf(context);
    }
}