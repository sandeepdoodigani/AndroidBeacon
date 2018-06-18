package com.example.manage.beacon;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class WebView extends AppCompatActivity {
    android.webkit.WebView w1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        w1=(android.webkit.WebView)findViewById(R.id.webView);

        Intent in=getIntent();
        String url=in.getStringExtra("url");
        w1.getSettings().setLoadsImagesAutomatically(true);
        w1.getSettings().setJavaScriptEnabled(true);
        w1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        w1.loadUrl(url);
        showNotification(url);

    }
    void showNotification(String message){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker("Detected Beacon").setWhen(0)

                .setAutoCancel(true)
                .setContentTitle("Detected Beacon")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Uri alarmSound =  RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        notification.sound=alarmSound;
        NotificationManager notificationManager = (NotificationManager) WebView.this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);


    }
}
