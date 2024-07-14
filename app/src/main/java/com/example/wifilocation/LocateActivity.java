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
import android.os.Looper;
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

    private boolean isWifiFingerComplete = false;
    private boolean isDrawShortestPathComplete = false;

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
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        handler = new Handler();
//        updateRunnable = new Runnable() {
//            @Override
//            public void run() {
//                // 开始定位，并更新坐标
//                try {
//                    if (isExactLoc) {
//                        // 清空最短路径
//                        if (mMarkers.size() > 2)
//                            mMarkers.subList(2, mMarkers.size()).clear();
//                        // 得到精确定位
//                        getExactWifiFinger(context, activity);
//                    } else {
//                        // 启动房间定位，并在完成后绘制最短路径
//                        getWifiFinger(context, activity, new WifiFingerCallback() {
//                            @Override
//                            public void onWifiFingerCompleted() {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        drawShortestPath();
//                                    }
//                                });
//                            }
//                        });
//                    }
//                    sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                // 改变坐标位置
//                updateMap();
//                handler.postDelayed(this, 2000);
//            }
//        };
//        handler.post(updateRunnable);
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        if (isExactLoc) {
                            if (mMarkers.size() > 2) {
                                mMarkers.subList(2, mMarkers.size()).clear();
                                mMapContainer.setMarkers(mMarkers);
                                mMapContainer.setOnMarkerClickListener(LocateActivity.this);
                            }
                            getExactWifiFinger(context, activity, new ExactWifiFingerCallback() {
                                @Override
                                public void onExactWifiFingerCompleted(int lx, int ly, int lz) {
                                    runOnUiThread(() -> {
                                        Marker m = mMarkers.get(0); // Assuming mMarkers is defined elsewhere
                                        float x = (20 + (float) lx / 4 * 110) / 428f;
                                        float y = (30 + (float) ly / 3 * 120) / 1244f;
                                        m.setScaleX(x);
                                        m.setScaleY(y);
                                        m.setFloorZ(lz * 1.0f);
                                        m.setRoom("415");
                                        room = "415";
                                        updateMap(); // Call updateMap() after setting marker details
                                        // 循环执行 updateRunnable
                                        handler.postDelayed(updateRunnable, 2000);
                                    });
                                }
                            });
                        } else {
                            getWifiFinger(getApplicationContext(), LocateActivity.this, new WifiFingerCallback() {
                                @Override
                                public void onWifiFingerCompleted() {
                                    runOnUiThread(() -> {
                                        drawShortestPath(new DrawShortestPathCallback() {
                                            @Override
                                            public void onDrawShortestPathCompleted() {
                                                runOnUiThread(() -> {
                                                    updateMap();
                                                    // 循环执行 updateRunnable
                                                    handler.postDelayed(updateRunnable, 2000);
                                                });
                                            }
                                        });
                                    });
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        };


        // 开始第一次执行
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
        final MapView mMapView = mMapContainer.getMapView();
        if (mMapView != null) {
            try {
                // 在主线程上运行
                LocateActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 确保 textCurFloor 不为 null
                            if (textCurFloor != null) {
                                textCurFloor.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            textCurFloor.setText("当前房间:" + room);
                                            Log.d("ROOM_updateMap", "序列：" + mMarkers.toString() + " room:" + room);
                                        } catch (Exception e) {
                                            Log.e("updateMap", "Error updating textCurFloor", e);
                                        }
                                    }
                                });
                            } else {
                                Log.e("updateMap", "textCurFloor is null");
                            }
                        } catch (Exception e) {
                            Log.e("updateMap", "Error in runOnUiThread", e);
                        }
                    }
                });

                // 检查 OnChangedListener 是否为空
                if (mMapView.getOnChangedListner() != null) {
                    mMapView.getOnChangedListner().onChanged(mMapView.getMatrixRect());
                    Log.d("ROOM_updateMap", "onChanged");
                } else {
                    Log.e("updateMap", "OnChangedListener is null");
                }
            } catch (Exception e) {
                Log.e("updateMap", "Exception in updateMap", e);
            }
        } else {
            Log.e("updateMap", "mMapView is null");
        }
    }


    public interface WifiFingerCallback {
        void onWifiFingerCompleted();
    }

    public interface DrawShortestPathCallback {
        void onDrawShortestPathCompleted();
    }

    public interface ExactWifiFingerCallback {
        void onExactWifiFingerCompleted(int lx, int ly, int lz);
    }


    /**
     * 得到房间定位
     *
     * @param context
     * @param activity
     */
    private void getWifiFinger(Context context, Activity activity, WifiFingerCallback callback) {
        new Thread(() -> {
            try {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("No Permission");
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return;
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
                params.sort(Comparator.comparingInt(p -> -p.first));
                Data data = new Data();
                for (int i = 0; i < params.size(); ++i) {
                    newText.append("(").append(params.get(i).second).append(",").append(params.get(i).first).append(")");
                    if (i < params.size() - 1) newText.append(",");
                    data.wifimac.add(params.get(i).second);
                    data.wifistrength.add(params.get(i).first);
                }
                Log.d("DATA", data.toString());
                URL url = new URL(getApplicationContext().getString(R.string.base_url) + "location");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("mac_list", data.wifimac);
                jsonParam.put("mac_strength", data.wifistrength);
                Log.d("PARAM", "mac_list:" + data.wifimac + " mac_strength:" + data.wifistrength);
                os.write(jsonParam.toString().getBytes());
                os.flush();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    try {
                        JSONObject jsonResponse = new JSONObject(response.toString());
                        room = jsonResponse.getString("room");
                        Log.d("ROOM_getWifiFinger", "更新当前位置：" + room);

                        Marker m = mMarkers.get(0);
                        float[] array = map.get(room);
                        assert array != null;
                        m.setScaleX(transferScaleX(array[0]));
                        m.setScaleY(transferScaleY(array[1]));
                        m.setFloorZ(4);
                        m.setRoom(room);
                    } catch (JSONException e) {
                        Log.e("Network", "Error parsing JSON response", e);
                    }
                } else {
                    Log.d("Network", "Request failed with response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Network", "Exception", e);
            }

            // 任务完成，调用回调
            if (callback != null) {
                callback.onWifiFingerCompleted();
            }
        }).start();
    }

    /**
     * 得到精确定位
     *
     * @param context
     * @param activity
     */
    private void getExactWifiFinger(Context context, Activity activity, ExactWifiFingerCallback callback) {
        ArrayList<Data> data_list = new ArrayList<>();
        Handler handler2 = new Handler(Looper.getMainLooper()); // Ensure handler is associated with the main thread's Looper
        int[] runCount = {0}; // Used to track the number of runs
        final int MAX_RUN_COUNT = 5; // Maximum number of runs

        Runnable dataRunnable = new Runnable() {
            @Override
            public void run() {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("Permission", "No ACCESS_FINE_LOCATION permission");
                    ActivityCompat.requestPermissions(activity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    return; // Return if permission is not granted
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
                data_list.add(data);

                runCount[0]++;
                if (runCount[0] < MAX_RUN_COUNT) {
                    handler2.postDelayed(this, 1000);
                } else {
                    // Code to execute after five runs
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // Periodically check the flag
                                URL url = new URL(context.getString(R.string.base_url) + "precise_location");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json");
                                conn.setDoOutput(true);
                                OutputStream os = conn.getOutputStream();
                                JSONArray jsonArray = new JSONArray();
                                for (Data data : data_list) {
                                    JSONObject jsonData = new JSONObject();
                                    jsonData.put("mac_list", new JSONArray(data.wifimac));
                                    jsonData.put("mac_strength", new JSONArray(data.wifistrength));
                                    jsonArray.put(jsonData);
                                }
                                Log.d("JSON_ARRAY", "jsonArray:" + jsonArray.toString());
                                // Write data
                                os.write(jsonArray.toString().getBytes());
                                os.flush();
                                os.close();
                                // Get response code
                                int responseCode = conn.getResponseCode();
                                if (responseCode == 200) {
                                    // Request successful, read response data
                                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    StringBuilder response = new StringBuilder();
                                    String inputLine;
                                    while ((inputLine = in.readLine()) != null) {
                                        response.append(inputLine);
                                    }
                                    in.close();
                                    try {
                                        JSONObject jsonResponse = new JSONObject(response.toString());
                                        int lx = jsonResponse.getInt("lx");
                                        int ly = jsonResponse.getInt("ly");
                                        int lz = jsonResponse.getInt("lz");
                                        Log.d("ROOM_xyz", lx + " " + ly + " " + lz);
                                        // Notify callback with location data
                                        callback.onExactWifiFingerCompleted(lx, ly, lz);
                                    } catch (JSONException e) {
                                        Log.e("Network", "Error parsing JSON response", e);
                                    }
                                } else {
                                    // Request failed
                                    Log.d("Network", "Request failed with response code: " + responseCode);
                                }
                                conn.disconnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Network", "Exception", e);
                            }
                        }
                    }).start();
                }
            }
        };

        handler2.post(dataRunnable); // Start the dataRunnable on the main thread
    }


    private void drawShortestPath(DrawShortestPathCallback callback) {
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
                    jsonParam.put("room", mMarkers.get(0).getRoom());
                    jsonParam.put("book_id", book.getId());
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
                        try {
                            JSONArray roomsArray = new JSONArray(response.toString());
                            // 删除原先的路径坐标
                            if (mMarkers.size() > 2) {
                                mMarkers.subList(2, mMarkers.size()).clear();
                            }
                            for (int i = 0; i < roomsArray.length(); i++) {
                                JSONObject roomObject = roomsArray.getJSONObject(i);
                                String r = roomObject.getString("room");
                                float[] array = map.get(r);
                                assert array != null;
                                mMarkers.add(new Marker(transferScaleX(array[0]), transferScaleY(array[1]), 4f, r, R.drawable.dot, r));
                            }
                            mMapContainer.setMarkers(mMarkers);
                            Log.d("ROOM_updNewArray", "从" + room + "开始的最短路径：" + mMarkers.toString());
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

                // 任务完成，调用回调
                if (callback != null) {
                    callback.onDrawShortestPathCompleted();
                }
            }
        }).start();
    }


    /**
     * 根据jsonResponse在地图上生成对应的标记点
     */
    private void initMap() {
        map.put("415", new float[]{1135, 236});
        map.put("414", new float[]{1135, 411});
        map.put("413", new float[]{1135, 563});
        map.put("406", new float[]{1377, 1273});
        map.put("408", new float[]{1241, 1308});
        map.put("409", new float[]{1238, 1162});
        map.put("0", new float[]{1211, 271});
        map.put("1", new float[]{1211, 340});
        map.put("2", new float[]{1211, 430});
        map.put("3", new float[]{1211, 575});
        map.put("4", new float[]{1211, 744});
        map.put("5", new float[]{1272, 858});
        map.put("6", new float[]{1314, 1041});
        map.put("7", new float[]{1314, 1166});
        map.put("8", new float[]{1314, 1328});
        LocateActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapContainer != null) {
                    mMapContainer.getMapView().setImageResource(R.drawable.map);
                    mMarkers = new ArrayList<>();
                    mMarkers.add(new Marker(0.5f, 0.5f, 1, "Me", R.drawable.arrow_u, "")); // 自身位置标记
                    String r = book.getBook_Room();
                    Log.d("Book_Room", "book" + book.toString());
                    if (r != null) {
                        float[] array = map.get(r);
                        assert array != null;
                        mMarkers.add(new Marker(transferScaleX(array[0]), transferScaleY(array[1]), book.getFloorZ(), "Book", R.drawable.location, book.getRoom()));
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
        return (x - 1066) / 428;
    }

    private float transferScaleY(float y) {
//        float height = mMapContainer.getHeight();
//        return (y % height) / height;
        return (y - 150) / 1244;
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
                onOutdoorButtonClick(view);
            }
        });
    }

    public void onOutdoorButtonClick(View view) {
        // 处理按钮点击事件，跳转到 OutdoorActivity
        Intent intent = new Intent(this, OutdoorActivity.class);
        startActivity(intent);
        finish();
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
            if (mMarkers.size() > 0 && mMarkers.get(0).getMarkerView() != null) {
                mMarkers.get(0).getMarkerView().startAnimation(animation);
            } else {
                Log.e("Sensor", "MarkerView is null or mMarkers list is empty");
            }

            lastRotateDegree = rotateDegree;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
