package com.example.wifilocation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.imgmarker.MapContainer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText editMsg;
    private Button queryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                            URL url = new URL(getApplicationContext().getString(R.string.base_url) + "chat");
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
                                String decodedJsonString = decodeUnicode(jsonString);
                                Log.d("Network", "Decoded JSON: " + decodedJsonString);


                                // 登录成功后跳转到新的 Activity
                                Intent intent = new Intent(MainActivity.this, Location.class);
                                startActivity(intent);
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

    }

    // 完成解码Unicode
    public static String decodeUnicode(String unicode) {
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(unicode);
        int lastEnd = 0;
        while (matcher.find()) {
            sb.append(unicode, lastEnd, matcher.start());
            String code = matcher.group(2);
            char ch = (char) Integer.parseInt(code, 16);
            sb.append(ch);
            lastEnd = matcher.end();
        }
        sb.append(unicode.substring(lastEnd));
        return sb.toString();
    }
}