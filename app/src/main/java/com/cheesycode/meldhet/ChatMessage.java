package com.cheesycode.meldhet;

class ChatMessage {

    String sender;
    String body;
    boolean left = true;

    ChatMessage(String sender, String body) {
        this.sender = sender;
        this.body = body;
    }
}
