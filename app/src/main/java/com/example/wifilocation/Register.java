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

public class Register extends AppCompatActivity {
    private EditText editTextPhone;
    private EditText editTextPassword;
    private EditText editTextPassword2;
    private Button registerButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextPhone = findViewById(R.id.phone);
        editTextPassword = findViewById(R.id.pwd);
        editTextPassword2 = findViewById(R.id.pwd2);
        registerButton = findViewById(R.id.button_register);
        backButton = findViewById(R.id.button_back);
        // 注册按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = editTextPhone.getText().toString();
                String pwd = editTextPassword.getText().toString();
                String pwd2 = editTextPassword2.getText().toString();
                if (!phone.matches("\\d{11}")) {
                    Toast.makeText(Register.this, "手机号格式不正确！", Toast.LENGTH_SHORT).show();
                } else if (!pwd.equals(pwd2)) {
                    Toast.makeText(Register.this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 创建URL对象
                                Log.d("Network", phone + " " + pwd + " " + pwd2);
                                URL url = new URL(getApplicationContext().getString(R.string.base_url) + "register");
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
                                jsonParam.put("password", Util.md5(pwd));
                                // jsonParam.put("pwd2", pwd2);
                                Log.d("REG", "send data:" + phone + " " + Util.md5(pwd));
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
                                                    Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_LONG).show();
                                                    // 登录成功后跳转到新的 Activity
                                                    Intent intent = new Intent(Register.this, Login.class);
                                                    startActivity(intent);
                                                } else {
                                                    Toast.makeText(Register.this, "注册失败！", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });

                                    } catch (JSONException e) {
                                        Log.e("Network", "Error parsing JSON response", e);
                                    }

                                    // 输出响应数据
                                    System.out.println("Response JSON: " + response.toString());
                                    System.out.println("Request succeeded");
                                } else {
                                    // 请求失败
                                    System.out.println("Request failed with response code: " + responseCode);
                                }

                                // 关闭连接
                                conn.disconnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("Network", "Exception", e);
                            }
                        }
                    }).start();
                }

            }
        });
        // 返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Register.this, Login.class);          //跳转到注册页面
                startActivity(intent);
                Toast.makeText(Register.this, "返回登录！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}