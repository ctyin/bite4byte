package com.example.bite4byte.Messaging;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bite4byte.Feed.UploadItemActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.example.bite4byte.account.UserProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DirectMessagingActivity extends AppCompatActivity {

    private ListView listView;
    private View btnSend;
    private EditText editText;
    UserContents uc;
    private List<ChatBubble> ChatBubbles;
    private ArrayAdapter<ChatBubble> adapter;
    String convo_id;
    IMyService iMyService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        Intent i = getIntent();
        uc = (UserContents) i.getSerializableExtra("user");
        convo_id = i.getStringExtra("convo_id");
        String receiver = i.getStringExtra("receiver");

        TextView ml = findViewById(R.id.meLbl);
        ml.setText(uc.getUsername());
        TextView fl = findViewById(R.id.friendLabel);
        fl.setText(receiver);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        Call<List<ChatBubble>> call = iMyService.getSingleCorr(convo_id);

        call.enqueue(new Callback<List<ChatBubble>>() {
            @Override
            public void onResponse(Call<List<ChatBubble>> call, Response<List<ChatBubble>> response) {
                ChatBubbles = response.body();
                if (ChatBubbles == null) {
                    ChatBubbles = new ArrayList<>();
                }

                listView = (ListView) findViewById(R.id.list_msg);
                btnSend = findViewById(R.id.btn_chat_send);
                editText = (EditText) findViewById(R.id.msg_type);

                //set ListView adapter first
                adapter = new MessageAdapter(DirectMessagingActivity.this, R.layout.shape_bg_incoming_bubble, ChatBubbles, uc.getUsername());
                listView.setAdapter(adapter);

                //event for button SEND
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().toString().trim().equals("")) {
                            Toast.makeText(DirectMessagingActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                        } else {
                            //add message to list
                            Long created_at = System.currentTimeMillis();
//                            System.out.println(uc.getUsername());
                            ChatBubble ChatBubble = new ChatBubble(editText.getText().toString(), uc.getUsername(), convo_id, created_at);
                            ChatBubbles.add(ChatBubble);
                            adapter.notifyDataSetChanged();

                            sendChatToServer(editText.getText().toString(), uc.getUsername(),
                                    convo_id, created_at);

                            editText.setText("");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<List<ChatBubble>> call, Throwable t) {
                Toast.makeText(DirectMessagingActivity.this,"fuck2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendChatToServer(String contents, String sender, String convo_id, Long created_at) {
        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        Call<String> call = iMyService.saveMessage(sender, convo_id, contents, created_at);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                return;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(DirectMessagingActivity.this,"Didn't save successfully(?)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onUploadClick(View view) {
        try {
            Intent i = new Intent(this, UploadItemActivity.class);
            i.putExtra("user", uc);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void onFeedButtonClick(View view) {
        Intent intent = new Intent(this, UserFeedActivity.class);
        intent.putExtra("user", uc);
        startActivity(intent);
    }

    public void onProfileClick(View view) {
        try {
            Intent i = new Intent(this, UserProfileActivity.class);
            i.putExtra("user", uc);
            startActivity(i);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}