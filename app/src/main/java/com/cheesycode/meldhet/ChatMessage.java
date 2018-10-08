package com.cheesycode.meldhet;

public class ChatMessage {

    String sender;
    String body;
    boolean left = true;

    public ChatMessage(String sender, String body) {
        this.sender = sender;
        this.body = body;
    }
}
