package com.example.wifilocation.locate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wifilocation.LocateActivity;
import com.example.wifilocation.R;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Book book = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_book_item, parent, false);
        }

        TextView tvName = view.findViewById(R.id.book_name);
        ImageView ivIcon = view.findViewById(R.id.book_icon);

        if (book != null) {
            tvName.setText(book.getName());
        }

        ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent = new Intent(context, LocateActivity.class);
                intent.putExtra("book", book);
                context.startActivity(intent);
            }
        });

        return view;
    }
}
