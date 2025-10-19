package server;

import java.util.Map;
import model.Message;

public class MessageRouter {
    private Map<Integer, ClientHandler> clients;

    public MessageRouter(Map<Integer, ClientHandler> clients) {
        this.clients = clients;
    }

    public void routeMessage(Message message) {
        ClientHandler recipient = clients.get(message.getReceiverId());
        if (recipient != null) {
            try {
                recipient.sendMessage(message);
            } catch (Exception e) {
                System.err.println("Error routing message: " + e.getMessage());
            }
        }
    }
}