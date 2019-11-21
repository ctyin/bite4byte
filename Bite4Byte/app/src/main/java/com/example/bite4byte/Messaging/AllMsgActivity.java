package com.example.bite4byte.Messaging;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bite4byte.Feed.PostActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.IMyService;
import com.example.bite4byte.Retrofit.RetrofitClient;
import com.example.bite4byte.Retrofit.UserContents;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AllMsgActivity extends AppCompatActivity {
    private Socket mSocket;
    IMyService iMyService;
    private UserContents uc;
    String receiver = "";

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AllMsgActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    String message;
                    username = (String) data.get("username");
                    message = (String) data.get("message");

                    // add the message to view
//                    addMessage(username, message);
                }
            });
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_messages);

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);

        mSocket = MsgApplication.getInstance();
//        uc = (UserContents) getIntent().getSerializableExtra("user");

        uc = new UserContents();
        uc.setName("jon");

//        Gson g = new Gson();
//        JSONObject jo = g.fromJson(g.toJson(uc), JSONObject.class);
//        mSocket.emit("join", jo);

        updateFeed();

        mSocket.on("new message", onNewMessage);
        mSocket.connect();
    }

    public void updateFeed() {
        Call<List<ConversationResult>> call = iMyService.getConversations(uc.getUsername());

        call.enqueue(new Callback<List<ConversationResult>>() {
            @Override
            public void onResponse(Call<List<ConversationResult>> call, Response<List<ConversationResult>> response) {
                List<ConversationResult> convos = response.body();
                ViewGroup parent = (ViewGroup) AllMsgActivity.this.findViewById(R.id.conversation_container);
                parent.removeAllViews();

                System.out.println("reaches reponse");

                for (ConversationResult cr : convos) {
                    System.out.println("reaches 2");

                    View view = LayoutInflater.from(AllMsgActivity.this).inflate(R.layout.convo, parent, false);
                    parent.addView(view);

                    view.setTag(cr.getConvo_id());
//                    view.setId(View.generateViewId());

                    receiver = "";
                    for (String s : cr.getParticipants()) {
                        if (!s.equals(uc.getUsername())) {
                            receiver = s;
                        }
                    }

                    TextView initials = view.findViewById(R.id.initials);
                    String content1 = Character.toString(receiver.trim().charAt(0));
                    initials.setText(content1);

                    TextView name = view.findViewById(R.id.convoName);
                    name.setText(receiver);

                    TextView desc = view.findViewById(R.id.lastMsg);
                    String text = getString(R.string.message_hint, receiver.trim());
                    desc.setText(text);

                    // need to get the data from the post itself
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(AllMsgActivity.this, DirectMessagingActivity.class);
                            intent.putExtra("user", uc);
                            intent.putExtra("convo_id", (String) v.getTag());
                            TextView tv = v.findViewById(R.id.convoName);
                            intent.putExtra("receiver", tv.getText());

                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ConversationResult>> call, Throwable t) {
                Toast.makeText(AllMsgActivity.this,"Failed to get response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onNewMessageClick(View view) {
        EditText et = findViewById(R.id.newConversation);
        String inputFriend = et.getText().toString();
        if (inputFriend.equals("")) {
            Toast.makeText(this, "Enter a friend's username!", Toast.LENGTH_SHORT).show();
        } else {
            // search for friend
            inputFriend.trim();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}