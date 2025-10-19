package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import util.Constants;    

public class ChatServer {
    private static final int PORT = Constants.SERVER_PORT;
    private static Map<Integer, ClientHandler> clients = new HashMap<>();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat Server is listening on port " + PORT);
            
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                
                ClientHandler clientHandler = new ClientHandler(socket, clients);
                clientHandler.start();
            }
        } catch (IOException e) {
            System.err.println("Error in the server: " + e.getMessage());
        }
    }
    
    public static synchronized void addClient(int userId, ClientHandler clientHandler) {
        clients.put(userId, clientHandler);
    }
    
    public static synchronized void removeClient(int userId) {
        clients.remove(userId);
    }
    
    public static synchronized ClientHandler getClientHandler(int userId) {
        return clients.get(userId);
    }
}