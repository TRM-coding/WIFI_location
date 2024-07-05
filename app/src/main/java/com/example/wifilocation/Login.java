package com.example.wifilocation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Login extends AppCompatActivity {
    private EditText editTextPhone;
    private EditText editTextPassword;
    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextPhone = findViewById(R.id.msg);
        editTextPassword = findViewById(R.id.pwd);
        loginButton = findViewById(R.id.login);
        registerButton = findViewById(R.id.reg);
        // 登录按钮 提交数据
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取文本框的内容
                String phone = editTextPhone.getText().toString();
                String pwd = editTextPassword.getText().toString();
                // 启动一个新的线程来发送HTTP请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 创建URL对象
                            Log.d("Network", phone + " " + pwd);
                            // 10.60.136.41:5000
                            // 172.17.0.3:8080
                            URL url = new URL(getApplicationContext().getString(R.string.base_url) + "login");
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
                            jsonParam.put("phone", phone);
                            jsonParam.put("password", pwd);
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
                                    // 将响应转换为 JSONObject
                                    JSONObject jsonResponse = new JSONObject(response.toString());

                                    // 处理返回的 JSON 数据示例
                                    String result = jsonResponse.getString("result");

                                    // 根据 result 值显示不同的 Toast 消息
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ("1".equals(result)) {
                                                Toast.makeText(Login.this, "登录成功！", Toast.LENGTH_LONG).show();
                                                // 登录成功后跳转到新的 Activity
                                                Intent intent = new Intent(Login.this, MainActivity.class);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(Login.this, "手机号或密码错误！", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

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
        // 注册按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Login.this, Register.class);          //跳转到注册页面
                startActivity(intent);
                Toast.makeText(Login.this, "前往注册！", Toast.LENGTH_SHORT).show();
            }
        });

    }
}