package com.app.partyplanetvendor.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.app.partyplanetvendor.MainActivity;

import cn.pedant.SweetAlert.SweetAlertDialog;

public enum UtilMethods {
    INSTANCE;
    public boolean isNetworkAvialable(Context context)
    {
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    public void sweetAlertBoxFailed(Context context, String message, ImageView logo)
    {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("")
                .setContentText("" + "" + message)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog)
                    {sweetAlertDialog.dismiss();
                     logo.setVisibility(View.VISIBLE);



                    }
                }).show();
    }
    public void requestStoragePermission(Activity context,int STORAGE_PERMISSION_CODE)
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to download files")
                    .setPositiveButton("ok", (dialog, which) -> ActivityCompat.requestPermissions(context,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE))
                    .setNegativeButton("cancel", (dialog, which) -> dialog.dismiss())
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

}
