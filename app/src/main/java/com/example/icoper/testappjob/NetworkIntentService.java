package com.example.icoper.testappjob;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icoper on 18.02.17.
 */

public class NetworkIntentService extends IntentService {

    private static final int ID = 1;
    private static final int NOTIFY_ID = 1;
    private final String LOG = "Network service";
    private String response;
    private String error;

    public NetworkIntentService() {
        super("Network Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(LOG, "is start");

        String number = intent.getStringExtra("phone");
        response = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost http = new HttpPost("http://s3.logist.ua/testdata/data.php?");
        List nameValuePairs = new ArrayList(1);
        nameValuePairs.add(new BasicNameValuePair("data", number));
        try {
            http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = (String) httpclient.execute(http, new BasicResponseHandler());
        } catch (IOException i) {
            error = i.toString();
        }
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!response.equals("")) {
                    showNotification();
                } else {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    public void showNotification() {
        Intent notificationIntent = new Intent(this, EnterPhoneActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                ID, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentTitle(LOG)
                .setContentText("Ответ сервера : " + response);

        Notification n = builder.build();
        nm.notify(NOTIFY_ID, n);
    }
}
