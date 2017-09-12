package com.wdc.mycloud.youtube;

import android.content.Intent;
import android.util.Log;

import com.wdc.nassdk.BaseStartupService;
import com.wdc.nassdk.MyCloudUIServer;

public class StartupService extends BaseStartupService {
    private static final String TAG ="StartupService";

    /**
     * App must override this method and return the UI server instance.
     * @return
     */
    @Override
    public MyCloudUIServer createMyCloudUIServer() {
        return new VideoDownloaderDeviceAppUi(getApplicationContext());
    }
    /**
     * This onStartCommand() method will be called for each user in the My Cloud Device with different mycloud user-id.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "StartupService onStartCommand.... ");
        if(intent.getAction() != null) {
            Log.d(TAG, "Action-->" + intent.getAction());
        }
        if(intent.getExtras() != null) {
            Log.d(TAG, "StartupService extras-- >" + intent.getExtras().getString("MyCloudId"));
        }
        return super.onStartCommand(intent, flags, startId);
    }
}