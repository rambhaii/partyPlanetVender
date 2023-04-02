package com.app.partyplanetvendor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.partyplanetvendor.Utils.UtilMethods;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {
    private final int STORAGE_PERMISSION_CODE = 1;
    private final int FILECHOOSER_RESULTCODE = 1;
    private WebView mWebView;
    ImageView logo;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    protected void onCreate(Bundle savedInstanceState)
    {
        UtilMethods.INSTANCE.requestStoragePermission(MainActivity.this,STORAGE_PERMISSION_CODE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = findViewById(R.id.webView);
        logo = findViewById(R.id.logo);
        if (UtilMethods.INSTANCE.isNetworkAvialable(MainActivity.this)==false)
           {
              UtilMethods.INSTANCE.sweetAlertBoxFailed(MainActivity.this,"Please check your Internet connection ! ",logo);
            mWebView.setVisibility(View.GONE);
           }
         else
           {
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            mWebView.setWebViewClient(new HelloWebViewClient());
            mWebView.setDownloadListener((url, userAgent, contentDisposition, mimeType, contentLength) -> {

                if (url.startsWith("data:")) {  //when url is base64 encoded data
                    String path = createAndSaveFileFromBase64Url(url);
                    Toast.makeText(this, "File is downloaded :" + path, Toast.LENGTH_LONG);
                    return;
                }
                Log.e("url@@", url);
                Uri source = Uri.parse(url);
                DownloadManager.Request request = new DownloadManager.Request(source);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading File...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            });
            mWebView.setWebChromeClient(new WebChromeClient() {
                //For Android 3.0+
                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), FCR);
                }

                // For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this
                public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    MainActivity.this.startActivityForResult(
                            Intent.createChooser(i, "File Browser"),
                            FCR);
                }

                //For Android 4.1+
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    mUM = uploadMsg;
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("*/*");
                    MainActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), MainActivity.FCR);
                }

                //For Android 5.0+
                public boolean onShowFileChooser(
                        WebView webView, ValueCallback<Uri[]> filePathCallback,
                        WebChromeClient.FileChooserParams fileChooserParams) {
                    if (mUMA != null) {
                        mUMA.onReceiveValue(null);
                    }
                    mUMA = filePathCallback;
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                            takePictureIntent.putExtra("PhotoPath", mCM);
                        } catch (IOException ex) {
                            Log.e("TAG", "Image file creation failed", ex);
                        }
                        if (photoFile != null) {
                            mCM = "file:" + photoFile.getAbsolutePath();
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                        } else {
                            takePictureIntent = null;
                        }
                    }
                    Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    contentSelectionIntent.setType("*/*");
                    Intent[] intentArray;
                    if (takePictureIntent != null) {
                        intentArray = new Intent[]{takePictureIntent};
                    } else {
                        intentArray = new Intent[0];
                    }
                    Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an Action");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                    startActivityForResult(chooserIntent, FCR);
                    return true;
                }
            });
            mWebView.loadUrl("https://www.thepartyplanets.com/app-vendor"); //Replace The Link Here
        }}


    ValueCallback<Uri> mUploadMessage;
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {

        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback uploadMsg, String acceptType) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(
                Intent.createChooser(i, "File Browser"),
                FILECHOOSER_RESULTCODE);
    }

    //For Android 4.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);

    }

    private static class HelloWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url)
        {

            view.loadUrl(url);
            return true;
        }
    }
    private File createImageFile() throws IOException {
        //  @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + System.currentTimeMillis() + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public class Callback extends WebViewClient {
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed()
    {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    private final static int FCR = 1;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }
    public String createAndSaveFileFromBase64Url(String url) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String filetype = url.substring(url.indexOf("/") + 1, url.indexOf(";"));
        String filename = System.currentTimeMillis() + "." + "xls";
        File file = new File(path, filename);
        try {
            if(!path.exists())
                path.mkdirs();
            if(!file.exists())
                file.createNewFile();
            Uri swatchImageUri;
            if (android.os.Build.VERSION.SDK_INT > 9) {
                swatchImageUri = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        file);
//                StrictMode.ThreadPolicy policy =
//                        new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                StrictMode.setThreadPolicy(policy);
            }else{
                swatchImageUri =Uri.fromFile(file);
            }
            String base64EncodedString = url.substring(url.indexOf(",") + 1);
            byte[] decodedBytes = Base64.decode(base64EncodedString, Base64.DEFAULT);
            OutputStream os = new FileOutputStream(file);
            os.write(decodedBytes);
            os.close();
            Toast.makeText(getApplicationContext(), R.string.msg_file_downloaded+file.toString(), Toast.LENGTH_LONG).show();

            //Tell the media scanner about the new file so that it is immediately available to the user.
            MediaScannerConnection.scanFile(this,
                    new String[]{file.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            //Set notification after download complete and add "click to view" action to that
            String mimetype = url.substring(url.indexOf(":") + 1, url.indexOf("/"));
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(swatchImageUri, (mimetype + "/*"));
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            String channelId = "channel-01";
            String channelName = "Channel Name";
            Notification notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(getString(R.string.msg_file_downloaded))
                    .setContentTitle(filename)
                    .setContentIntent(pIntent)
                    .build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            int notificationId = 85851;
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                assert notificationManager != null;
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationManager.notify(notificationId, notification);
        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
            Toast.makeText(getApplicationContext(), R.string.error_downloading, Toast.LENGTH_LONG).show();
        }

        return file.toString();
    }
}