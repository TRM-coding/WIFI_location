package com.example.wifilocation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.imgmarker.MapContainer;
import com.example.imgmarker.Marker;
import com.example.imgmarker.Position;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNavigationBarVisibility(false);
        setContentView(R.layout.activity_location);
        mMapContainer = findViewById(R.id.mc_map);
        // 选择
        mMapContainer.getMapView().setImageResource(R.drawable.icon);
        mMarkers = new ArrayList<>();
        position = new Marker(0.5f,0.5f,R.drawable.coordinate);
        mMarkers.add(new Marker(0.3f, 0.8f, R.drawable.location));
        mMarkers.add(new Marker(0.4f, 0.2f, R.drawable.location));
        mMarkers.add(new Marker(0.5f, 0.5f, R.drawable.location));
        mMarkers.add(position);

        mMapContainer.setMarkers(mMarkers);
        mMapContainer.setOnMarkerClickListner((MapContainer.OnMarkerClickListner) this);

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
                            URL url = new URL("http://10.60.136.41:5000/location");
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