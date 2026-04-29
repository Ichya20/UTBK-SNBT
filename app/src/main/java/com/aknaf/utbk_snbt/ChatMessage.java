package com.aknaf.utbk_snbt;

public class ChatMessage {
    private String message;
    private String sender;
    private long timestamp;

    // Konstruktor kosong wajib ada untuk Firebase
    public ChatMessage() {}

    public ChatMessage(String message, String sender, long timestamp) {
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getter & Setter
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}