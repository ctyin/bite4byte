package com.example.bite4byte.Feed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.bite4byte.R;

public class PostActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_activity);

        Intent i = getIntent();
        String foo = i.getStringExtra("postId");

        Toast.makeText(this, foo, Toast.LENGTH_LONG).show();
    }

}
