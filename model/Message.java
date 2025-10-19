package model;

import java.sql.Timestamp;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private String message;
    private boolean seenStatus;
    private boolean delivered;
    private Timestamp timestamp;

    public Message() {
        this.id = 0;
        this.senderId = 0;
        this.receiverId = 0;
        this.message = "";
        this.seenStatus = false;
        this.delivered = false;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }

    public Message(int id, int senderId, int receiverId, String message, 
                  boolean seenStatus, boolean delivered, Timestamp timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.message = message;
        this.seenStatus = seenStatus;
        this.delivered = delivered;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }
    public int getReceiverId() { return receiverId; }
    public void setReceiverId(int receiverId) { this.receiverId = receiverId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public boolean isSeenStatus() { return seenStatus; }
    public void setSeenStatus(boolean seenStatus) { this.seenStatus = seenStatus; }
    public boolean isDelivered() { return delivered; }
    public void setDelivered(boolean delivered) { this.delivered = delivered; }
    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }
}