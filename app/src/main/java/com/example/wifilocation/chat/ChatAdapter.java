package com.example.wifilocation.chat;

import android.content.Context;
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
 * 修改ChatAdapter类(添加点击RecyclerView中的item的相关代码):
 **/

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;

    private List<Map<String, Object>> data;

    private OnItemClickListener onItemClickListener;

    public ChatAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
    }

    /**
     * 定义 RecyclerView 选项单击事件的回调接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, Map<String, Object> data);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.textView1.setText(data.get(position).get("name").toString());
        holder.textView2.setText(data.get(position).get("message").toString());
        holder.textView3.setText(data.get(position).get("time").toString());
        holder.imageView.setImageResource(Integer.parseInt(data.get(position).get("avatars").toString()));
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView textView1, textView2, textView3;

        ImageView imageView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.layout_chat_item_textView1);
            textView2 = itemView.findViewById(R.id.layout_chat_item_textView2);
            textView3 = itemView.findViewById(R.id.layout_chat_item_textView3);
            imageView = itemView.findViewById(R.id.layout_chat_item_imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此处回传点击监听事件
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(v, data.get(getLayoutPosition()));
                    }
                }
            });
        }
    }
}