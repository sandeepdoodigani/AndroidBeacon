package com.example.manage.beacon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.Collection;

public class RangingActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;
    TextView range;
    WebView w1;
    String temurl;
    Thread mWebView;
    Thread thread;
    int state;
    String url;
    public static final String ALTBEACON = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String ALTBEACON2 = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    public static final String EDDYSTONE_TLM =  "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";
    public static final String EDDYSTONE_UID = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    public static final String EDDYSTONE_URL = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v";
    public static final String IBEACON = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);
        range=(TextView)findViewById(R.id.range);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(ALTBEACON2));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_TLM));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_UID));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(EDDYSTONE_URL));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON));
        beaconManager.bind(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

                for (org.altbeacon.beacon.Beacon beacon: beacons) {
                    if (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 0x10) {
                        // This is a Eddystone-URL frame

                         url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());

                        Log.d("Eddystone", "I see a beacon transmitting a url: " + url +
                                " approximately " + beacon.getDistance() + " meters away.");
                        if( URLUtil.isValidUrl(url) && !url.equals("http://")) {
                            state++;
                           if(state==1){
                               temurl=url;
                               Intent in = new Intent(RangingActivity.this, com.example.manage.beacon.WebView.class);
                               in.putExtra("url", url);
                               in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               startActivity(in);
                               finish();
                           }
                        }
                        if(!TextUtils.isEmpty(temurl)){
                      if(!temurl.equals(url)){
                            state=0;
                      }}
                    }
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

}
