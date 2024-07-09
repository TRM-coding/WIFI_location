package com.example.wifilocation;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FragmentManager fm = getSupportFragmentManager();
    private LinearLayout linearLayout1, linearLayout2, linearLayout3;
    private ImageView imageView1, imageView2, imageView3;
    private TextView textView1, textView2, textView3;

    private Fragment chat = new ChatFragment();
    private Fragment locate = new LocateFragment();
    private Fragment self = new SelfFragment();

    //测试
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
        setContentView(R.layout.activity_main);

        initFragment();
        initImageView();
        showFragment(0);

        linearLayout1.setOnClickListener(this);
        linearLayout2.setOnClickListener(this);
        linearLayout3.setOnClickListener(this);

        // 测试
        context = this;
        activity = this;
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
            Log.d("DARA WIFI", data.wifimac.toString());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "wifiLocate: " + e);
        }

    }

    private void initFragment() {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.fragment, chat);
        transaction.add(R.id.fragment, locate);
        transaction.add(R.id.fragment, self);
        transaction.commit();
    }

    private void initImageView() {
        imageView1 = findViewById(R.id.chat_img);
        imageView2 = findViewById(R.id.locate_img);
        imageView3 = findViewById(R.id.self_img);

        textView1 = findViewById(R.id.chat_text);
        textView2 = findViewById(R.id.locate_text);
        textView3 = findViewById(R.id.self_text);

        linearLayout1 = findViewById(R.id.chat_layout);
        linearLayout2 = findViewById(R.id.locate_layout);
        linearLayout3 = findViewById(R.id.self_layout);
    }

    private void hideFragment(FragmentTransaction transaction) {
        transaction.hide(chat);
        transaction.hide(locate);
        transaction.hide(self);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        hideFragment(fragmentTransaction);
        resetImg();
        int i = view.getId();
        if (i == R.id.chat_layout)
            showFragment(0);
        else if (i == R.id.locate_layout)
            showFragment(1);
        else if (i == R.id.self_layout)
            showFragment(2);
    }

    private void resetImg() {    //调用灰色的图片，以让点击过后的图片回复原色
        imageView1.setImageResource(R.drawable.chat);
        imageView2.setImageResource(R.drawable.locate);
        imageView3.setImageResource(R.drawable.self);
    }

    private void showFragment(int i) {    //控制图片颜色的变换，其意义是点击一个图片之后该图片就会变亮
        FragmentTransaction transaction = fm.beginTransaction();
        hideFragment(transaction);
        switch (i) {
            case 0:
                transaction.show(chat);
                imageView1.setImageResource(R.drawable.chat_pressed);
                break;
            case 1:
                transaction.show(locate);
                imageView2.setImageResource(R.drawable.locate_pressed);
                break;
            case 2:
                transaction.show(self);
                imageView3.setImageResource(R.drawable.self_pressed);
                break;
            default:
                break;
        }
        transaction.commit();
    }

}