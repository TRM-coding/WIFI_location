package com.example.wifilocation.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wifilocation.R;

import java.util.List;
import java.util.Map;

/**
 * 使用ViewType属性动态控制MessageAdapter加载哪一个样式文件
 * 用于区分好友发送的消息和自己发送的消息。
 **/

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final static int I = 0;

    private final static int OTHERS = 1;

    private Context context;

    private List<Map<String, Object>> data;

    public MessageAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position).get("type").toString().equals("I"))
        {
            return I;
        }
        else{
            return OTHERS;
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == OTHERS) {
            return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_message_left_item, parent, false));
        }
        else
        {
            return new MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_message_right_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.MessageViewHolder holder, int position) {
        if(data.get(position).get("type").toString().equals("I"))
        {
            holder.imageView_right.setImageResource(R.drawable.avatar1);
            holder.right_textView1.setText(data.get(position).get("time").toString());
            holder.right_textView2.setText(data.get(position).get("message").toString());
        }
        else
        {
            byte[] bytes = (byte[]) data.get(position).get("avatars");
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.imageView_left.setImageBitmap(bitmap);
            holder.left_textView1.setText(data.get(position).get("time").toString());
            holder.left_textView2.setText(data.get(position).get("message").toString());
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        TextView left_textView1, left_textView2, right_textView1, right_textView2;

        ImageView imageView_left, imageView_right;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            left_textView1 = itemView.findViewById(R.id.layout_message_left_item_time);
            left_textView2 = itemView.findViewById(R.id.layout_message_left_item_content);
            imageView_left = itemView.findViewById(R.id.layout_message_left_item_imageView1);

            right_textView1 = itemView.findViewById(R.id.layout_message_right_item_time);
            right_textView2 = itemView.findViewById(R.id.layout_message_right_item_content);
            imageView_right = itemView.findViewById(R.id.layout_message_right_item_imageView1);

        }
    }
}