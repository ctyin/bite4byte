package com.example.bite4byte.Messaging;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatBubble {
    @SerializedName("sender")
    private String sender;

    @SerializedName("contents")
    private String content;

    @SerializedName("convo_id")
    private String convo_id;

    @SerializedName("created_at")
    private Long created_at;

    public ChatBubble(String content, String sender, String convo_id, Long created_at) {
        this.content = content;
        this.sender = sender;
        this.convo_id = convo_id;
        this.created_at = created_at;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getConvo_id() { return convo_id; }

    public Long getCreated_at() { return created_at; }
}