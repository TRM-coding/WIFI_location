package com.example.wifilocation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class LocateActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Context context;
    Activity activity;

    public class Pair {
        public final int first;
        public final String second;

        public Pair(int first, String second) {
            this.first = first;
            this.second = second;
        }
    }

    public class Data {
        public ArrayList<String> wifimac;
        public ArrayList<Integer> wifistrength;

        public Data() {
            this.wifimac = new ArrayList<>();
            this.wifistrength = new ArrayList<>();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        Log.d("WIFI", "wifiLocate: ");
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            System.out.println("P2 done");
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("No Permission");
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            boolean success = wifiManager.startScan();
            System.out.println(success);
            List<Pair> params = new ArrayList<>();
            List<ScanResult> results = wifiManager.getScanResults();
            System.out.println(results.toString());
            StringBuilder newText = new StringBuilder();
            for (ScanResult result : results) {
                String ssid = result.SSID;
                String bssid = result.BSSID;
                int rssi = result.level;
                if (Objects.equals(ssid, "phone.wlan.bjtu")) {
                    System.out.println(bssid);
                    Pair pair = new Pair(rssi, bssid);
                    params.add(pair);
                }
            }
            params.sort(new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    return Integer.compare(p2.first, p1.first);
                }
            });
            Data data = new Data();
            for (int i = 0; i < params.size(); ++i) {
                System.out.println(i);
                newText.append("(").append(params.get(i).second).append(",").append(params.get(i).first).append(")");
                if (i < params.size() - 1) newText.append(",");
                data.wifimac.add(params.get(i).second);
                data.wifistrength.add(params.get(i).first);
            }
            Log.d("DATA", data.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "wifiLocate: " + e);
        }
    }

}
