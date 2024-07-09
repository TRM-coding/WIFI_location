package com.example.wifilocation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wifilocation.locate.Book;
import com.example.wifilocation.locate.MapContainer;
import com.example.wifilocation.locate.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class LocateActivity extends AppCompatActivity implements MapContainer.OnMarkerClickListner {
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Context context;
    Activity activity;
    private TextView textCurFloor;
    MapContainer mMapContainer;
    ArrayList<Marker> mMarkers;
    ArrayList<Book> books;
    ImageView back;
    Book book;

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
        setContentView(R.layout.activity_locate);
        context = this;
        activity = this;
        textCurFloor = findViewById(R.id.text_current_floor);
        mMapContainer = findViewById(R.id.mc_map);
        back = findViewById(R.id.back);
        setbackListener();
        // 获得当前显示market的书本
        book = getIntent().getParcelableExtra("book");
        // 初始化地图
        initMap();
        // 每3秒钟进行一次定位
        startPhoneSelfLocation();
    }

    /**
     * 接收当前手机位置的WIFI指纹
     *
     * @param context
     * @param activity
     */
    private void getWifiFinger(Context context, Activity activity) {
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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 创建URL对象
                        URL url = new URL(getApplicationContext().getString(R.string.base_url) + "location");
                        // 创建HttpURLConnection对象
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        // 设置请求方法
                        conn.setRequestMethod("POST");
                        // 设置请求头为JSON
                        conn.setRequestProperty("Content-Type", "application/json");
                        // 设置输出流，以便发送POST请求
                        conn.setDoOutput(true);
                        // 获取输出流
                        OutputStream os = conn.getOutputStream();
                        // 创建一个JSONObject对象
                        JSONObject jsonParam = new JSONObject();
                        // 添加你的数据
                        jsonParam.put("mac_list", data.wifimac);
                        jsonParam.put("mac_strength", data.wifistrength);
                        // 写入数据
                        os.write(jsonParam.toString().getBytes());
                        os.flush();
                        os.close();
                        // 获取响应码
                        int responseCode = conn.getResponseCode();
                        if (responseCode == 200) {
                            // 请求成功，读取响应数据
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            // 输出收到的完整 JSON 字符串
                            Log.d("Network", "Received JSON: " + response.toString());
                            try {
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                float x = (float) jsonResponse.getDouble("x");
                                float y = (float) jsonResponse.getDouble("y");
                                float z = (float) jsonResponse.getDouble("z");
                                Marker m = mMarkers.get(0);
                                m.setScaleX(transferScaleX(x));
                                m.setScaleY(transferScaleY(y));
                                m.setFloorZ(z);
                                updateMap();
                                textCurFloor.setText("当前楼层：" + String.valueOf((int) z) + "F");
                                Log.d("CHANGE", "Self marker change successfully");
                            } catch (JSONException e) {
                                Log.e("Network", "Error parsing JSON response", e);
                            }
                        } else {
                            // 请求失败
                            Log.d("Network", "Request failed with response code: " + responseCode);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("Network", "Exception", e);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error", "wifiLocate: " + e);
        }
    }

    /**
     * 根据jsonResponse在地图上生成对应的标记点
     */
    private void initMap() {
        Log.d("INIT", "initMap called"); // 添加日志输出
        LocateActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapContainer != null) {
                    mMapContainer.getMapView().setImageResource(R.drawable.map);
                    mMarkers = new ArrayList<>();
                    mMarkers.add(new Marker(0, 0, 1, "Me", R.drawable.coordinate)); // 自身位置标记
                    mMarkers.add(new Marker(transferScaleX(book.getScaleX()), transferScaleY(book.getScaleY()), book.getFloorZ(), "Book", R.drawable.location));
                    Log.d("Marker", "x:" + transferScaleX(book.getScaleX()) + " y:" + transferScaleY(book.getScaleY()));
                    mMapContainer.setMarkers(mMarkers);
                    mMapContainer.setOnMarkerClickListener(LocateActivity.this);
                    Log.d("INIT", "init successfully");
                    updateMap();
                } else {
                    Log.d("INIT", "mMapContainer is null");
                }
            }
        });
    }


    @Override
    public void onClick(View view, int position) {
        Toast.makeText(LocateActivity.this, "你点击了第" + position + "个marker", Toast.LENGTH_SHORT).show();
    }

    /**
     * 更新地图上面的标记
     */
    private void updateMap() {
        RectF rectF = new RectF();
        rectF.left = mMapContainer.getLeft();
        rectF.top = mMapContainer.getTop();
        rectF.right = mMapContainer.getRight();
        rectF.bottom = mMapContainer.getBottom();

        mMapContainer.onChanged(rectF);
    }

    /**
     * 开始自身手机定位
     */
    private void startPhoneSelfLocation() {
        final Handler handler = new Handler();
        final Runnable updateMarketRunnable = new Runnable() {
            @Override
            public void run() {
                getWifiFinger(context, activity);
                // 延迟3秒后再次执行
                handler.postDelayed(this, 3000);
            }
        };

        // 第一次调用，延迟0秒后执行
        handler.post(updateMarketRunnable);
    }

    private float transferScaleX(float x) {
//        float width = mMapContainer.getWidth();
//        return (x % width) / width;
        return (x % 94) / 94;
    }

    private float transferScaleY(float y) {
//        float height = mMapContainer.getHeight();
//        return (y % height) / height;
        return (y % 50) / 50;
    }

    /**
     * 设置导航栏显示状态
     *
     * @param visible
     */
    private void setNavigationBarVisibility(boolean visible) {
        int flag = 0;
        if (!visible) {
            flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        getWindow().getDecorView().setSystemUiVisibility(flag);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
    }

    /**
     * 返回主菜单
     */
    private void setbackListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
