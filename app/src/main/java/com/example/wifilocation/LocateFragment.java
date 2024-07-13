package com.example.wifilocation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wifilocation.locate.Book;
import com.example.wifilocation.locate.BookAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LocateFragment extends Fragment {

    private View view;
    private ListView listView;
    private EditText editMsg;
    private Button sendButton;
    private TextView headerView;
    private ArrayList<Book> books;
    private BookAdapter adapter;

    private LoadingDialog loadingDialog;

    public LocateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_locate, container, false);

        books = new ArrayList<>();
        adapter = new BookAdapter(getContext(), books);

        listView = view.findViewById(R.id.book_list);
        editMsg = view.findViewById(R.id.msg);
        sendButton = view.findViewById(R.id.send);
        headerView = view.findViewById(R.id.header);

        listView.setAdapter(adapter);

        // 发送按钮，显示list
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editMsg.getText().toString();
                if (!msg.isEmpty()) {
                    editMsg.setText("");
                    sendHttpRequest(msg);
                }
            }
        });
        return view;
    }

    private void sendHttpRequest(String msg) {
        // 显示加载动画
        loadingDialog = new LoadingDialog(getContext());
        loadingDialog.show();
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
                    jsonParam.put("temperature", 0.5);
                    Log.d("DATA", "Post data:" + jsonParam);
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

//                        JSONObject jsonResponse = new JSONObject(response.toString());
//                        String respond = jsonResponse.getString("respond");
//                        Log.d("Network", "Received respond: " + respond);

                        // 如果返回的是一个列表
                        String jsonString = response.toString();
                        try {
                            JSONObject jsonResponse = new JSONObject(jsonString);
                            String booksString = jsonResponse.getString("respond");
                            Log.d("BOOK", booksString);
                            JSONArray booksArray = new JSONArray(booksString);
                            List<Book> bookList = new ArrayList<>();

                            for (int i = 0; i < booksArray.length(); i++) {
                                JSONObject bookObject = booksArray.getJSONObject(i);
                                int id = bookObject.getInt("id");
                                String name = bookObject.getString("name");
                                float x = (float) bookObject.getDouble("lx");
                                float y = (float) bookObject.getDouble("ly");
                                float z = (float) bookObject.getDouble("lz");
                                Book book = new Book(id, name, x, y, z, "");
                                bookList.add(book);
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    books.clear();
                                    books.addAll(bookList);
                                    adapter.notifyDataSetChanged();
                                }
                            });

                        } catch (JSONException e) {
                            Log.e("Network", "Error parsing JSON response", e);
                            Toast.makeText(getContext(),"网络错误",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d("Network", "Request failed with response code: " + responseCode);
                        Toast.makeText(getContext(),"网络错误",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("Network", "Exception", e);
                    Toast.makeText(getContext(),"网络错误",Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            }
        }).start();
    }

}
