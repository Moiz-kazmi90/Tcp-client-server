import java.io.*;
import java.net.*;
import java.util.*;

import javax.print.DocFlavor.STRING;

// Har connected client ko ek thread handle karega
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // client ko list me add karo
            clientHandlers.add(this);

            String msg;
            while ((msg = in.readLine()) != null) {
                if (msg.equalsIgnoreCase("QUIT")) {
                    out.println("You are disconnected from server...");
                    break;
                }

                System.out.println("Client says: " + msg);

                // broadcast to all clients
                broadcast(msg, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientHandlers.remove(this); // remove from list
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Ye function sab clients ko message bhejega
    private void broadcast(String msg, ClientHandler sender) {
        synchronized (clientHandlers) {
            for (ClientHandler ch : clientHandlers) {
                if (ch != sender) { // sender ko dobara na bheje
                    ch.out.println("Message from another client: " + msg);
                }
            }
        }
    }
}

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(1234);
        System.out.println("Chat Server started...");

        while (true) {
            Socket clientSocket = server.accept();
            System.out.println("New client connected!");

            // multiple thread banae ga client ke liye
            ClientHandler handler = new ClientHandler(clientSocket);
            Thread t = new Thread(handler);
            t.start();
        }
    }
}
