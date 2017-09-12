package com.wdc.mycloud.youtube;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.wdc.mycloud.youtube.vget.AppManagedDownload;

public class DownloaderService extends IntentService {
    private String TAG=DownloaderService.class.getName();
    public DownloaderService() {
        super("DownloaderService");
    }
    public DownloaderService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        final String url = workIntent.getStringExtra("URL");
        final String filePath = workIntent.getStringExtra("FILE_PATH");
        new Runnable() {
            @Override
            public void run() {
                try{
                    AppManagedDownload.download(url,filePath);
                    Log.d("TAG","Downloaded");
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
    }
}