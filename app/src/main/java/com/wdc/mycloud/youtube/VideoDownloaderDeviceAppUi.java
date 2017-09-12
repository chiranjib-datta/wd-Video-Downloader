package com.wdc.mycloud.youtube;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.wdc.mycloud.youtube.vget.AppManagedDownload;
import com.wdc.nassdk.MyCloudUIServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;


public class VideoDownloaderDeviceAppUi extends MyCloudUIServer {
    private static String TAG = VideoDownloaderDeviceAppUi.class.getName();
    public static final String ACTION_TASK_CREATE_FOLDER = "ACTION_TASK_CREATE_FOLDER";
    public static final String KEY_FOLDER_NAME = "mycloud_sample";
    public static final String KEY_MYCLOUD_USER = "mycloud_userid";


    int i = 0;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Context mContext;

    public VideoDownloaderDeviceAppUi(Context context) {
        super(context);
        mContext = context;
        //Create a folder for my APP
        Intent i = new Intent(mContext, DownloaderService.class);
        i.setAction(ACTION_TASK_CREATE_FOLDER);
        i.putExtra(KEY_FOLDER_NAME, "MYCLOUD");
        i.putExtra(KEY_MYCLOUD_USER, "37241");
        mContext.startService(i);
    }

    @Override
    public Response get(IHTTPSession ihttpSession) {
        String uri = ihttpSession.getUri();
        if (uri.endsWith("/")) {
            String htmlSource = getHtmlSource();
            return newFixedLengthResponse( HTTP_OK, MIME_HTML, htmlSource);
        }
        if (uri.endsWith("styles.css")) {
            String cssSource = getStyleCSSSource();
            return newFixedLengthResponse( HTTP_OK, MIME_CSS, cssSource);
        }
        else if (uri.endsWith("jquery.min.js")) {
            String cssSource = getJqueryJSResource();
            return newFixedLengthResponse( HTTP_OK, MIME_JS, cssSource);
        }
        else if (uri.endsWith("custom.js")) {
            String cssSource = getCustomJs();
            return newFixedLengthResponse( HTTP_OK, MIME_JS, cssSource);
        }
        return null;
    }

    @Override
    public Response post(IHTTPSession session) {
        File f = new File(MyCloudUIServer.getRootFolder(mContext, getMyCloudUserId(session)), "youtubes/");
        if (!f.exists()) {
            f.mkdirs();
        }
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (ResponseException re) {
                re.printStackTrace();
            }
        }
        String postBody = session.getQueryParameterString();
        String url = session.getParameters().get("url").get(0);
        try {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        AppManagedDownload.download(url,f.getAbsolutePath());
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            return createResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "Failed");
        }
        return createResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, "Downloading");
    }
    // Announce that the file server accepts partial content requests
    private Response createResponse(Response.Status status, String mimeType, String message) {
        Response res = newFixedLengthResponse(status, mimeType, message);
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }


    private String getHtmlSource() {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("index.html")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "<h1>Sorry index.html cannot be loaded</h1>";
        }
    }
    private String getStyleCSSSource() {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("css/style.css")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "<h1>Sorry index.html cannot be loaded</h1>";
        }
    }
    private String getJqueryJSResource() {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("js/jquery.min.js")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "<h1>Sorry index.html cannot be loaded</h1>";
        }
    }
    private String getCustomJs() {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(mContext.getAssets().open("js/custom.js")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return "<h1>Sorry index.html cannot be loaded</h1>";
        }
    }



    private static final String
            MIME_PLAINTEXT = "text/plain",
            MIME_HTML = "text/html",
            MIME_JS = "application/javascript",
            MIME_CSS = "text/css",
            MIME_PNG = "image/png",
            MIME_DEFAULT_BINARY = "application/octet-stream",
            MIME_XML = "text/xml";
    private Response.Status HTTP_OK= Response.Status.OK;
}
