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

import com.example.bite4byte.Feed.PostActivity;
import com.example.bite4byte.Feed.UserFeedActivity;
import com.example.bite4byte.InternalData.Data;
import com.example.bite4byte.R;
import com.example.bite4byte.Retrofit.UserContents;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.simple.JSONObject;

import java.net.URISyntaxException;
import java.util.Set;

public class AllMsgActivity extends AppCompatActivity {
    private Socket mSocket;

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

        mSocket = MsgApplication.getInstance();

        UserContents uc = new UserContents();
        uc.setName("Chris");
        Gson g = new Gson();
        JSONObject jo = g.fromJson(g.toJson(uc), JSONObject.class);
        mSocket.emit("join", jo);

        mSocket.on("new message", onNewMessage);
        mSocket.connect();
    }

    public void updateFeed(Set<JSONObject> set) {
        ViewGroup parent = (ViewGroup) this.findViewById(R.id.conversation_container);
        parent.removeAllViews();

        for (JSONObject jo : set) {
            View view = LayoutInflater.from(this).inflate(R.layout.convo, parent, false);
            parent.addView(view);

            view.setTag(jo.get("_id"));

            TextView initials = view.findViewById(R.id.initials);
            initials.setText((String) jo.get("foodName"));

            ImageView iv = view.findViewById(R.id.imageView);
            if (!jo.get("picture").toString().isEmpty()) {
                Bitmap bm = BitmapFactory.decodeFile(jo.get("picture").toString());
                iv.setImageBitmap(bm);
            }

            // need to get the data from the post itself
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AllMsgActivity.this, PostActivity.class);

                    TextView title = v.findViewById(R.id.postTitle);
                    intent.putExtra("foodName", title.getText().toString());

                    TextView seller = v.findViewById(R.id.seller);
                    intent.putExtra("sellerUserName", seller.getText().toString());

                    TextView desc = v.findViewById(R.id.description);
                    intent.putExtra("description", desc.getText().toString());

                    intent.putExtra("id", v.getTag().toString());
                    intent.putExtra("username", username);

                    Data md = (Data) UserFeedActivity.this.getIntent().getSerializableExtra("manageData");
                    intent.putExtra("manageData", md);

                    startActivity(intent);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
    }
}
