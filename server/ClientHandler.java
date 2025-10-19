package server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import db.MessageDAO;
import model.Message;

public class ClientHandler extends Thread {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Map<Integer, ClientHandler> clients;
    private int userId;
    private boolean isConnected = true;

    public ClientHandler(Socket socket, Map<Integer, ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
        try {
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            this.isConnected = false;
            System.err.println("Error creating streams: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Object obj = input.readObject();
                if (obj instanceof Message) {
                    handleMessage((Message) obj);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + userId);
        } catch (ClassNotFoundException e) {
            System.err.println("Invalid message format");
        } finally {
            cleanup();
        }
    }

    private void handleConnect(Message message) {
        this.userId = message.getSenderId();
        clients.put(userId, this);
        System.out.println("User connected: " + userId);
    }

    private void handleMessage(Message message) throws IOException {
        try {
            // For connection messages
            if (message.getReceiverId() == 0) {
                handleConnect(message);
                return;
            }

            MessageDAO dao = new MessageDAO();
            int messageId = dao.saveMessage(message);
            message.setId(messageId);

            // Mark as delivered
            dao.updateDeliveryStatus(messageId, true);

            // Forward to recipient
            ClientHandler recipient = clients.get(message.getReceiverId());
            if (recipient != null) {
                recipient.sendMessage(message);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    public synchronized void sendMessage(Message message) throws IOException {
        if (isConnected) {
            try {
                output.writeObject(message);
                output.flush();
            } catch (IOException e) {
                isConnected = false;
                throw e;
            }
        }
    }

    private void cleanup() {
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            System.err.println("Error cleaning up resources: " + e.getMessage());
        }
    }
}