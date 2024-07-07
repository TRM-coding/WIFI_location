package com.example.wifilocation;



import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.imgmarker.MapContainer;
import com.example.imgmarker.Marker;
import com.example.imgmarker.Position;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Location extends AppCompatActivity implements MapContainer.OnMarkerClickListner {
    MapContainer mMapContainer;
    ArrayList<Marker> mMarkers;
    Handler handler = new Handler();
    Marker position;

    EditText editMsg;
    Button queryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarVisibility(false);
        setContentView(R.layout.activity_location);
        mMapContainer = findViewById(R.id.mc_map);
        mMapContainer.getMapView().setImageResource(R.drawable.icon);
        mMarkers = new ArrayList<>();
        position = new Marker(0.5f,0.5f,R.drawable.coordinate);
        mMarkers.add(new Marker(0.3f, 0.8f, R.drawable.location));
        mMarkers.add(new Marker(0.4f, 0.2f, R.drawable.location));
        mMarkers.add(new Marker(0.5f, 0.5f, R.drawable.location));
        mMarkers.add(position);

        mMapContainer.setMarkers(mMarkers);
        mMapContainer.setOnMarkerClickListner((MapContainer.OnMarkerClickListner) this);

        editMsg = findViewById(R.id.msg);
        queryButton = findViewById(R.id.chat);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取文本框的内容
                String msg = editMsg.getText().toString();

                // 启动一个新的线程来发送HTTP请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 创建URL对象
                            Log.d("Network", msg);
                            URL url = new URL(getApplicationContext().getString(R.string.base_url) + "book");
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
                            jsonParam.put("msg", msg);
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
                                String jsonString = response.toString();
                                Log.d("Network", "Received JSON: " + jsonString);

                                // 解码 Unicode 转义字符
                                // String decodedJsonString = decodeUnicode(jsonString);
                                // Log.d("Network", "Decoded JSON: " + decodedJsonString);

                                try {
                                    // 将响应转换为 JSONObject
                                    JSONObject jsonResponse = new JSONObject(response.toString());

                                    // 处理返回的 JSON 数据示例
                                    // String result = jsonResponse.getString("result");
                                    //  Intent intent = new Intent(MainActivity.this, MainActivity.class);
                                    //  startActivity(intent);
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
        });

//        // 使用 Handler 来延迟调用
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                position.setScaleX(0.4f);
//                position.setScaleY(0.4f);
//                Log.d("Location", "第二次");
//                mMapContainer.setMarkers(mMarkers);
//                mMapContainer.setOnMarkerClickListner((MapContainer.OnMarkerClickListner) Location.this);
//            }
//        }, 2000); // 延迟2秒
//
//        //startCoordinateLocation();
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(Location.this, "你点击了第" + position + "个marker", Toast.LENGTH_SHORT).show();
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

    private void startCoordinateLocation() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://10.60.136.41:5000/book");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            conn.setDoOutput(true);

                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("request", "location_update");

                            OutputStream os = conn.getOutputStream();
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
                                JSONObject jsonResponse = new JSONObject(response.toString());
                                final float x = (float) jsonResponse.getDouble("x");
                                final float y = (float) jsonResponse.getDouble("y");
                                Log.d("TAG", "x:" + x + " y:" + y);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        float scaleX = transferScaleX(x);
                                        float scaleY = transferScaleY(y);
                                        updatePosition(scaleX,scaleY);
                                        Log.d("UPDATE", "update");
                                    }
                                });
                            } else {
                                Log.d("Network", "Request failed with response code: " + responseCode);
                            }
                        } catch (Exception e) {
                            Log.e("Network", "Exception", e);
                        }
                    }
                }).start();
                handler.postDelayed(this, 100); //设置延迟100ms
            }
        }, 100); // 初始延迟
    }

    private void updatePosition(float x, float y) {
        position.setScaleX(x);
        position.setScaleY(y);
        mMapContainer.setMarkers(mMarkers);
        mMapContainer.setOnMarkerClickListner((MapContainer.OnMarkerClickListner) this);
    }

    private float transferScaleX(float x) {
        float width = mMapContainer.getMapView().getWidth();
        return (x % width) / width;
    }

    private float transferScaleY(float y) {
        float height = mMapContainer.getMapView().getHeight();
        return (y % height) / height;
    }

}