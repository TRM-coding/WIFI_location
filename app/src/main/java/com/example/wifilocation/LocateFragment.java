package com.example.wifilocation;

import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.wifilocation.imgmarker.MapContainer;
import com.example.wifilocation.imgmarker.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class LocateFragment extends Fragment implements MapContainer.OnMarkerClickListner {

    private MapContainer mMapContainer;
    private ArrayList<Marker> mMarkers;
    private Handler handler = new Handler();
    private Marker position;

    private EditText editMsg;
    private Button queryButton;
    private TextView textCurFloor;

    public LocateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_locate, container, false);
        mMapContainer = rootView.findViewById(R.id.mc_map);
        editMsg = rootView.findViewById(R.id.msg);
        queryButton = rootView.findViewById(R.id.chat);
        textCurFloor = rootView.findViewById(R.id.text_current_floor);

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editMsg.getText().toString();
                if (!msg.isEmpty()) {
                    editMsg.setText(""); // 清空文本框内容
                    sendHttpRequest(msg); // 发送查询图书位置请求
                    startCoordinateLocation(); // 开始对自身位置定位
                }
            }
        });

        return rootView;
    }

    private void sendHttpRequest(String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(requireContext().getString(R.string.base_url) + "book");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("msg", msg);
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

                        String jsonString = response.toString();
                        Log.d("Network", "Received JSON: " + jsonString);
                        try {
                            JSONObject jsonResponse = new JSONObject(jsonString);
                            // 将jsonResponse传入
                            initMap(jsonResponse);
                        } catch (JSONException e) {
                            Log.e("Network", "Error parsing JSON response", e);
                        }
                    } else {
                        Log.d("Network", "Request failed with response code: " + responseCode);
                    }
                } catch (Exception e) {
                    Log.e("Network", "Exception", e);
                }
            }
        }).start();
    }

    private void initMap(JSONObject jsonResponse) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mMapContainer != null) {
                    mMapContainer.getMapView().setImageResource(R.drawable.map);
                    mMarkers = new ArrayList<>();
                    mMarkers.add(new Marker(0, 0, 1, "Me", R.drawable.coordinate));// 自身位置标记

                    // 解析JSON数据
                    JSONArray marketsArray = null;
                    try {
                        marketsArray = jsonResponse.getJSONArray("markets");
                        for (int i = 0; i < marketsArray.length(); i++) {
                            JSONObject marketObject = marketsArray.getJSONObject(i);
                            int id = marketObject.getInt("id");
                            String name = marketObject.getString("name");
                            float x = (float) marketObject.getDouble("x");
                            float y = (float) marketObject.getDouble("y");
                            float z = (float) marketObject.getDouble("z");
                            mMarkers.add(new Marker(transferScaleX(x), transferScaleY(y), z, name, R.drawable.location));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    mMapContainer.setMarkers(mMarkers);
                    mMapContainer.setOnMarkerClickListner(LocateFragment.this);

                    updateMap();
                }
            }
        });
    }

    @Override
    public void onClick(View view, int position) {
        Toast.makeText(getActivity(), "你点击了第" + position + "个marker", Toast.LENGTH_SHORT).show();
    }

    private void updateMap() {
        RectF rectF = new RectF();
        rectF.left = mMapContainer.getLeft();
        rectF.top = mMapContainer.getTop();
        rectF.right = mMapContainer.getRight();
        rectF.bottom = mMapContainer.getBottom();

        mMapContainer.onChanged(rectF);
    }

    private void startCoordinateLocation() {
        final Handler handler = new Handler();
        final float[] coordinates = {0, 0, 0}; // 初始坐标
        final Runnable updateMarketRunnable = new Runnable() {
            @Override
            public void run() {
                // 开始获得wifi指纹
                LocateActivity locateActivity = new LocateActivity();

                // 自增坐标值
                coordinates[0] += 50; // x每秒增加50
                coordinates[1] += 50; // y每秒增加50
                coordinates[2] += 1;

                if (mMarkers != null && mMarkers.size() > 0) {
                    Marker m = mMarkers.get(0);
                    m.setScaleX(transferScaleX(coordinates[0]));
                    m.setScaleY(transferScaleY(coordinates[1]));
                    m.setFloorZ(coordinates[2]);
                    updateMap();
                   // Log.d("ME", "X:" + transferScaleX(coordinates[0]) + " Y:" + transferScaleY(coordinates[1]));
                    textCurFloor.setText("当前楼层：" + String.valueOf((int) coordinates[2]) + "F");
                }

                // 每隔1秒钟执行一次
                handler.postDelayed(this, 3000); // 1000毫秒 = 1秒
            }
        };

        // 第一次调用，延迟0秒后执行
        handler.post(updateMarketRunnable);
    }

    private float transferScaleX(float x) {
        float width = mMapContainer.getWidth();
        return (x % width) / width;
    }

    private float transferScaleY(float y) {
        float height = mMapContainer.getHeight();
        return (y % height) / height;
    }
}
