package com.vimal.retrofitdownloadwithprogress.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.tedpark.tedpermission.rx2.TedRx2Permission;
import com.vimal.retrofitdownloadwithprogress.R;
import com.vimal.retrofitdownloadwithprogress.download.DownloadListener;
import com.vimal.retrofitdownloadwithprogress.download.DownloadUtil;
import com.vimal.retrofitdownloadwithprogress.download.InputParameter;
import com.vimal.retrofitdownloadwithprogress.helpers.Helpers;

import java.io.File;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class MainActivity extends AppCompatActivity {

    RingProgressBar circularProgress;
    File imageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Helpers.Main_Folder_Name);
    String dir = imageDir + File.separator + Helpers.Ringtone_Folder_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circularProgress = findViewById(R.id.circularProgress);

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TedRx2Permission.with(MainActivity.this)
                        .setRationaleTitle(R.string.read_storage)
                        .setRationaleMessage(R.string.storage_message)
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .request()
                        .subscribe(permissionResult -> {
                                    if (permissionResult.isGranted()) {
                                        file_download(MainActivity.this,"Your File Name");
                                        //file name(abc,mp4,abc.png,abc.mp3)
                                    } else {
                                        Toast.makeText(getBaseContext(), "Permission Denied\n" + permissionResult.getDeniedPermissions().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );


            }
        });
    }

    public void file_download(Context context,String path){
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String base = Helpers.Base_Path;
        String url = base + path;
        String file_name = url.substring(url.lastIndexOf('/'));
        File filechk = getFile(path);

        if (filechk.exists()) {
            Toast.makeText(MainActivity.this,"File Already Downloaded",Toast.LENGTH_SHORT).show();
        }else {
            if (URLUtil.isValidUrl(Helpers.Base_Path + path)) {
                circularProgress.setVisibility(View.VISIBLE);
                DownloadUtil.getInstance()
                        .downloadFile(new InputParameter.Builder(Helpers.Base_Path, path, dir + file_name)
                                .setCallbackOnUiThread(true)
                                .build(), new DownloadListener() {
                            @Override
                            public void onFinish(final File file) {
                                circularProgress.setVisibility(View.GONE);
                                Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onProgress(int progress, long downloadedLengthKb, long totalLengthKb) {
                                circularProgress.setProgress(progress);
                            }

                            @Override
                            public void onFailed(String errMsg) {

                                Toast.makeText(context, "Download Faild", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(context, "File not Exists on Server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getFile(String path) {
        File file;
        try {
            String base = Helpers.Base_Path;
            String url = base + path;
            String file_name = url.substring(url.lastIndexOf('/'));
            file = new File(dir + file_name);
        } catch (StringIndexOutOfBoundsException e) {
            file = null;
        }
        return file;
    }
}