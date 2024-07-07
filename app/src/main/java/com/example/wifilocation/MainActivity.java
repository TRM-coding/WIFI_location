package com.example.wifilocation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

//public class MainActivity extends AppCompatActivity {
//    private EditText editMsg;
//    private Button queryButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        editMsg = findViewById(R.id.msg);
//        queryButton = findViewById(R.id.chat);
//
//        queryButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 获取文本框的内容
//                String msg = editMsg.getText().toString();
//
//                // 启动一个新的线程来发送HTTP请求
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            // 创建URL对象
//                            Log.d("Network", msg);
//                            URL url = new URL(getApplicationContext().getString(R.string.base_url) + "chat");
//                            // 创建HttpURLConnection对象
//                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                            // 设置请求方法
//                            conn.setRequestMethod("POST");
//                            // 设置请求头为JSON
//                            conn.setRequestProperty("Content-Type", "application/json");
//                            // 设置输出流，以便发送POST请求
//                            conn.setDoOutput(true);
//                            // 获取输出流
//                            OutputStream os = conn.getOutputStream();
//                            // 创建一个JSONObject对象
//                            JSONObject jsonParam = new JSONObject();
//                            // 添加你的数据
//                            jsonParam.put("msg", msg);
//                            // 写入数据
//                            os.write(jsonParam.toString().getBytes());
//                            os.flush();
//                            os.close();
//                            // 获取响应码
//                            int responseCode = conn.getResponseCode();
//                            if (responseCode == 200) {
//                                // 请求成功，读取响应数据
//                                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                                StringBuilder response = new StringBuilder();
//                                String inputLine;
//                                while ((inputLine = in.readLine()) != null) {
//                                    response.append(inputLine);
//                                }
//                                in.close();
//
//                                // 输出收到的完整 JSON 字符串
//                                String jsonString = response.toString();
//                                Log.d("Network", "Received JSON: " + jsonString);
//
//                                // 解码 Unicode 转义字符
//                                String decodedJsonString = decodeUnicode(jsonString);
//                                Log.d("Network", "Decoded JSON: " + decodedJsonString);
//
//
//                                // 登录成功后跳转到新的 Activity
//                                Intent intent = new Intent(MainActivity.this, Location.class);
//                                startActivity(intent);
//                                try {
//                                    // 将响应转换为 JSONObject
//                                    JSONObject jsonResponse = new JSONObject(response.toString());
//
//                                    // 处理返回的 JSON 数据示例
//                                    // String result = jsonResponse.getString("result");
//                                    //  Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                                    //  startActivity(intent);
//                                } catch (JSONException e) {
//                                    Log.e("Network", "Error parsing JSON response", e);
//                                }
//
//                            } else {
//                                // 请求失败
//                                Log.d("Network", "Request failed with response code: " + responseCode);
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            Log.e("Network", "Exception", e);
//                        }
//                    }
//                }).start();
//            }
//        });
//
//    }
//
//    // 完成解码Unicode
//    public static String decodeUnicode(String unicode) {
//        StringBuilder sb = new StringBuilder();
//        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
//        Matcher matcher = pattern.matcher(unicode);
//        int lastEnd = 0;
//        while (matcher.find()) {
//            sb.append(unicode, lastEnd, matcher.start());
//            String code = matcher.group(2);
//            char ch = (char) Integer.parseInt(code, 16);
//            sb.append(ch);
//            lastEnd = matcher.end();
//        }
//        sb.append(unicode.substring(lastEnd));
//        return sb.toString();
//    }
//}

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private ChatFragment chatFragment;
    private LocateFragment locateFragment;
    private SelfFragment selfFragment;

    private View chatLayout;
    private View locateLayout;
    private View selfLayout;

    /*声明组件变量*/
    private ImageView chatImg;
    private ImageView locateImg;
    private ImageView selfImg;

    private TextView chatText;
    private TextView locateText;
    private TextView selfText;

    private FragmentManager fragmentManager = null;// 用于对Fragment进行管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//要求窗口没有title
        super.setContentView(R.layout.activity_main);
        // 初始化布局元素
        initViews();
        fragmentManager = getSupportFragmentManager();
        // 设置默认的显示界面
        setTabSelection(0);
    }

    /**
     * 在这里面获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件
     */
    @SuppressLint("NewApi")
    public void initViews() {
        fragmentManager = getSupportFragmentManager();

        chatLayout = findViewById(R.id.chat_layout);
        locateLayout = findViewById(R.id.locate_layout);
        selfLayout = findViewById(R.id.self_layout);


        chatImg = (ImageView) findViewById(R.id.chat_img);
        locateImg = (ImageView) findViewById(R.id.locate_img);
        selfImg = (ImageView) findViewById(R.id.self_img);

        chatText = (TextView) findViewById(R.id.chat_text);
        locateText = (TextView) findViewById(R.id.locate_text);
        selfText = (TextView) findViewById(R.id.self_text);

        //处理点击事件
        chatLayout.setOnClickListener(this);
        locateLayout.setOnClickListener(this);
        selfLayout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i==R.id.chat_layout)
            setTabSelection(0);
        else if (i== R.id.locate_layout)
            setTabSelection(1);
        else if (i==R.id.self_layout)
            setTabSelection(2);
    }


    /**
     * 根据传入的index参数来设置选中的tab页 每个tab页对应的下标。0表示chat，1表示locate，2表示self
     */
    @SuppressLint("NewApi")
    private void setTabSelection(int index) {
        clearSelection();// 每次选中之前先清除掉上次的选中状态
        FragmentTransaction transaction = fragmentManager.beginTransaction(); // 开启一个Fragment事务
        hideFragments(transaction);// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        switch (index) {
            case 0:
                // 当点击了我的tab时改变控件的图片和文字颜色
                chatImg.setImageResource(R.drawable.chat_pressed);//修改布局中的图片
                chatText.setTextColor(Color.parseColor("#0090ff"));//修改字体颜色

                if (chatFragment == null) {
                    Intent intent = getIntent();
                    String number = intent.getStringExtra("number");
                    // 如果FirstFragment为空，则创建一个并添加到界面上
                    chatFragment = new ChatFragment(number);
                    transaction.add(R.id.fragment, chatFragment);

                } else {
                    // 如果FirstFragment不为空，则直接将它显示出来
                    transaction.show(chatFragment);//显示的动作
                }
                break;
            // 以下和firstFragment类同
            case 1:
                locateImg.setImageResource(R.drawable.locate_pressed);
                locateText.setTextColor(Color.parseColor("#0090ff"));
                if (locateFragment == null) {
                    Intent intent = getIntent();
                    String number = intent.getStringExtra("number");
                    locateFragment = new LocateFragment(number);
                    transaction.add(R.id.fragment, locateFragment);
                } else {
                    transaction.show(locateFragment);
                }
                break;
            case 2:
                selfImg.setImageResource(R.drawable.self_pressed);
                selfText.setTextColor(Color.parseColor("#0090ff"));
                if (selfFragment == null) {
                    Intent intent = getIntent();
                    String number = intent.getStringExtra("number");
                    selfFragment = new SelfFragment(number);
                    transaction.add(R.id.fragment, selfFragment);
                } else {
                    transaction.show(selfFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 清除掉所有的选中状态
     */
    private void clearSelection() {
        chatImg.setImageResource(R.drawable.chat);
        chatText.setTextColor(Color.parseColor("#82858b"));

        locateImg.setImageResource(R.drawable.locate);
        locateText.setTextColor(Color.parseColor("#82858b"));


        selfImg.setImageResource(R.drawable.self);
        selfText.setTextColor(Color.parseColor("#82858b"));
    }

    /**
     * 将所有的Fragment都设置为隐藏状态 用于对Fragment执行操作的事务
     */
    @SuppressLint("NewApi")
    private void hideFragments(FragmentTransaction transaction) {
        if (chatFragment != null) {
            transaction.hide(chatFragment);
        }
        if (locateFragment != null) {
            transaction.hide(locateFragment);
        }
        if (selfFragment != null) {
            transaction.hide(selfFragment);
        }
    }

    //封装一个AlertDialog
    private void exitDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("您确定要退出程序吗?")
                .setPositiveButton("关闭", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).create();
        dialog.show();//显示对话框
    }

    /**
     * 返回菜单键监听事件
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果是返回按钮
            exitDialog();
        }
        return super.onKeyDown(keyCode, event);
    }

}