package com.example.bite4byte.Messaging;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConversationResult {
    @SerializedName("participants")
    private List<String> participants;

    @SerializedName("convo_id")
    private String convo_id;


    public List<String> getParticipants() {
        return participants;
    }

    public String getConvo_id() {
        return convo_id;
    }
}
