package com.example.wifilocation;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.SensorEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.wifilocation.locate.Book;
import com.example.wifilocation.locate.MapContainer;
import com.example.wifilocation.locate.MapView;
import com.example.wifilocation.locate.Marker;
import com.suke.widget.SwitchButton;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class LocateActivity extends AppCompatActivity implements SensorEventListener, MapContainer.OnMarkerClickListner {
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    Context context;
    Activity activity;
    private TextView textCurFloor;
    MapContainer mMapContainer;
    ArrayList<Marker> mMarkers;
    ArrayList<Book> books;
    ImageView back;
    Book book;
    // 当前房间位置
    String room;

    //通过加速度传感器和地磁传感器指向身前
    private SensorManager sensorManager;
    private float lastRotateDegree;
    float[] accelerometerValues = new float[3];
    float[] magneticValues = new float[3];

    // 切换精确定位
    SwitchButton switchButton;
    boolean isExactLoc = false;
    // 切换室外导航
    Button outdoor;

    Map<String, float[]> map = new HashMap<>();

    private Handler handler;
    private Runnable updateRunnable;

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
        switchButton = findViewById(R.id.switch_button);
        outdoor = findViewById(R.id.outdoor_button);
        setbackListener();
        setOutdoorListener();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //加速度感应器
        Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        //地磁感应器
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);

        switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "开启精确定位", Toast.LENGTH_SHORT).show();
                    isExactLoc = true;
                } else {
                    Toast.makeText(getApplicationContext(), "关闭精确定位", Toast.LENGTH_SHORT).show();
                    isExactLoc = false;
                }
            }
        });
        

        // 获得当前显示market的书本
        book = getIntent().getParcelableExtra("book");
        // 初始化地图
        initMap();
        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // 开始定位，并更新坐标
                if (isExactLoc) {
                    // 清空最短路径
                    if (mMarkers.size() > 2)
                        mMarkers.subList(2, mMarkers.size()).clear();

                } else {
                    getWifiFinger(context, activity);
                }
                // 绘制最短路径
                drawShortestPath();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                // 改变坐标位置
                updateMap();
                handler.postDelayed(this, 2000);
            }
        };
        handler.post(updateRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除 Runnable，停止定时任务
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    /**
     * 更新地图上面坐标
     */
    private void updateMap() {
        // 改变坐标位置
        MapView mMapView = mMapContainer.getMapView();
        if (mMapView != null) {
            LocateActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textCurFloor.post(new Runnable() {
                        @Override
                        public void run() {
                            textCurFloor.setText("当前房间" + room);
                            Log.d("ROOM_updateMap", "序列：" + mMarkers.toString());
                        }
                    });
                }
            });
            mMapView.getOnChangedListner().onChanged(mMapView.getMatrixRect());
        }
    }

    /**
     * 得到房间定位
     *
     * @param context
     * @param activity
     */
    private void getWifiFinger(Context context, Activity activity) {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("No Permission");
                ActivityCompat.requestPermissions(activity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
            boolean success = wifiManager.startScan();
            List<Pair> params = new ArrayList<>();
            List<ScanResult> results = wifiManager.getScanResults();
            StringBuilder newText = new StringBuilder();
            for (ScanResult result : results) {
                String ssid = result.SSID;
                String bssid = result.BSSID;
                int rssi = result.level;
                if (Objects.equals(ssid, "phone.wlan.bjtu")) {
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
                        // 定期检查标志位
                        URL url = new URL(getApplicationContext().getString(R.string.base_url) + "location");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setDoOutput(true);
                        OutputStream os = conn.getOutputStream();
                        JSONObject jsonParam = new JSONObject();
                        // 添加你的数据
                        jsonParam.put("mac_list", data.wifimac);
                        jsonParam.put("mac_strength", data.wifistrength);
                        Log.d("PARAM", "mac_list:" + data.wifimac + " mac_strength:" + data.wifistrength);
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
                            // 依据房间定位
                            try {
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                room = jsonResponse.getString("room");
                                Log.d("ROOM_getWifiFinger", "当前位置" + room);

                                Marker m = mMarkers.get(0);
                                float[] array = map.get(room);
                                assert array != null;
                                m.setScaleX(array[1] / 33);
                                m.setScaleY(array[0] / 92 + 0.05f);
                                m.setFloorZ(4);

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
     * 得到精确定位
     *
     * @param context
     * @param activity
     */
    private void getExactWifiFinger(Context context, Activity activity) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No Permission");
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        boolean success = wifiManager.startScan();
        List<Pair> params = new ArrayList<>();
        List<ScanResult> results = wifiManager.getScanResults();
        StringBuilder newText = new StringBuilder();
        for (ScanResult result : results) {
            String ssid = result.SSID;
            String bssid = result.BSSID;
            int rssi = result.level;
            if (Objects.equals(ssid, "phone.wlan.bjtu")) {
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
            newText.append("(").append(params.get(i).second).append(",").append(params.get(i).first).append(")");
            if (i < params.size() - 1) newText.append(",");
            data.wifimac.add(params.get(i).second);
            data.wifistrength.add(params.get(i).first);
        }
        Log.d("DATA", data.toString());
    }

    /**
     * 绘制最短路路径
     */
    private void drawShortestPath() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 定期检查标志位
                    URL url = new URL(getApplicationContext().getString(R.string.base_url) + "navigate");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);
                    OutputStream os = conn.getOutputStream();
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("room", 415);
                    jsonParam.put("book_id", 1);
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
                        Log.d("ROOM_shortPathArray", "Received JSON: " + response.toString());
                        // 将路径序列模拟成一个个房间
                        try {
                            JSONArray roomsArray = new JSONArray(response.toString());
                            // 删除原先的路径坐标
                            if (mMarkers.size() > 2) {
                                mMarkers.subList(2, mMarkers.size()).clear();
                                Log.d("ROOM_clear", "清空路径坐标");
                            }
                            for (int i = 0; i < roomsArray.length(); i++) {
                                JSONObject roomObject = roomsArray.getJSONObject(i);
                                String r = roomObject.getString("room");
                                float[] array = map.get(r);
                                assert array != null;
                                mMarkers.add(new Marker(array[1] / 33, array[0] / 92 + 0.05f, 4f, r, R.drawable.dot, r));
                            }
                            mMapContainer.setMarkers(mMarkers);
                            Log.d("ROOM_updNewArray", "更新路径坐标");
                            mMapContainer.setOnMarkerClickListener(LocateActivity.this);
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
    }


    /**
     * 根据jsonResponse在地图上生成对应的标记点
     */
    private void initMap() {
        map.put("415", new float[]{6, 4});
        map.put("414", new float[]{20, 4});
        map.put("413", new float[]{32, 4});
        map.put("406", new float[]{84, 26});
        map.put("408", new float[]{86, 16});
        map.put("409", new float[]{76, 16});
        map.put("420", new float[]{6, 8});
        map.put("401", new float[]{6, 10});
        LocateActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapContainer != null) {
                    mMapContainer.getMapView().setImageResource(R.drawable.map);
                    mMarkers = new ArrayList<>();
                    mMarkers.add(new Marker(0.5f, 0.5f, 1, "Me", R.drawable.arrow_u, "")); // 自身位置标记
                    if (book != null) {
                        mMarkers.add(new Marker(transferScaleX(book.getScaleX()), transferScaleY(book.getScaleY()), book.getFloorZ(), "Book", R.drawable.location, ""));
                        Log.d("Marker", "x:" + transferScaleX(book.getScaleX()) + " y:" + transferScaleY(book.getScaleY()));
                    }
                    mMapContainer.setMarkers(mMarkers);
                    mMapContainer.setOnMarkerClickListener(LocateActivity.this);
                    Log.d("INIT", "init successfully");
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


    private float transferScaleX(float x) {
//        float width = mMapContainer.getWidth();
//        return (x % width) / width;
        return (x % 20) / 20;
    }

    private float transferScaleY(float y) {
//        float height = mMapContainer.getHeight();
//        return (y % height) / height;
        return (y % 20) / 20;
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

    private void setOutdoorListener() {
        outdoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBottomRightButtonClick(view);
            }
        });
    }

    public void onBottomRightButtonClick(View view) {
        // 处理按钮点击事件，跳转到 OutdoorActivity
        Intent intent = new Intent(this, OutdoorActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 判断当前是加速度感应器还是地磁感应器
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            //赋值调用clone方法
            accelerometerValues = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            //赋值调用clone方法
            magneticValues = event.values.clone();
        }
        float[] R = new float[9];
        float[] values = new float[3];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
        sensorManager.getOrientation(R, values);
        // Log.d("Main", "values[0] :" + Math.toDegrees(values[0]));
        //values[0]的取值范围是-180到180度。
        //+-180表示正南方向，0度表示正北，-90表示正西，+90表示正东

        float rotateDegree = (float) Math.toDegrees(values[0]);
        if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
            RotateAnimation animation = new RotateAnimation(lastRotateDegree, rotateDegree, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setFillAfter(true);
            if (mMarkers.size() > 0)
                mMarkers.get(0).getMarkerView().startAnimation(animation); //动画效果转动传感器
            lastRotateDegree = rotateDegree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
