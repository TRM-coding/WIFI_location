package com.example.wifilocation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wifilocation.chat.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private View view;
    private RecyclerView fragment_chat_recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ChatAdapter chatAdapter;
    private Context context;
    private List<Map<String, Object>> chatData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        fragment_chat_recyclerView = view.findViewById(R.id.fragment_chat_recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.fragment_chat_swipeRefreshLayout);
        context = getContext();

        initData();
        configSwipeRefreshLayout();

        chatAdapter = new ChatAdapter(context, chatData);
        chatAdapter.setOnItemClickListener(new ChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Map<String, Object> data) {
                ImageView avatars = view.findViewById(R.id.layout_chat_item_imageView);
                TextView name = view.findViewById(R.id.layout_chat_item_textView1);
                TextView message = view.findViewById(R.id.layout_chat_item_textView2);
                TextView time = view.findViewById(R.id.layout_chat_item_textView3);

                //跳转
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();

                //将图片转换成字节数组传递
                Bitmap bitmap = Util.drawableToBitmap(avatars.getDrawable());
                byte[] bytes = Util.bitmapToBytes(bitmap);

                bundle.putByteArray("avatars", bytes);
                bundle.putString("name", name.getText().toString());
                bundle.putString("message", message.getText().toString());
                bundle.putString("time", time.getText().toString());
                if(message.getText().toString().contains("我:"))
                {
                    bundle.putString("type", "I");
                }
                else
                {
                    bundle.putString("type", "others");
                }
                bundle.putInt("requestCode", 1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.VERTICAL);
        fragment_chat_recyclerView.setLayoutManager(manager);
        fragment_chat_recyclerView.setAdapter(chatAdapter);
        return view;
    }

    private void configSwipeRefreshLayout() {
        swipeRefreshLayout.setSize(CircularProgressDrawable.LARGE);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.system_accent1_200, android.R.color.system_neutral2_300);
        swipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == 1)
        {
            Bundle bundle = data.getExtras();
            String name = bundle.getString("name");
            String message = bundle.getString("message");
            String time = bundle.getString("time");
            //更新聊天记录
            for (int i = 0; i < chatData.size(); i++) {
                if(chatData.get(i).get("name").toString().equals(name))
                {
                    chatData.get(i).put("message", "我:" + message);
                    chatData.get(i).put("time", time);
                    chatAdapter.notifyItemChanged(i);
                    break;
                }
            }
        }
    }

    private void initData() {
        String[] name = {"AI聊天", "Jack", "Lucy", "睡不醒", "刘明"};
        String[] message = {"我是AI聊天", "哈哈哈", "今天有空吗？", "呜呜呜", "呵呵",};
        String[] time = {"", "昨天", "3月15日", "3月15日", "3月15日",};
        Integer[] avatars = {R.drawable.chatgpt, R.drawable.avatars2, R.drawable.avatars3,
                R.drawable.avatars4, R.drawable.avatars5};

        for (int i = 0; i < name.length; i++) {
            HashMap<String, Object> d = new HashMap<>();
            d.put("name", name[i]);
            d.put("message", message[i]);
            d.put("time", time[i]);
            d.put("avatars", avatars[i]);
            chatData.add(d);
        }
    }

    @Override
    public void onRefresh() {
        //延时2s
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }
}