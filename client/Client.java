import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 1234);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            Scanner input = new Scanner(System.in);

            // Thread banaya jo continuously server ka message sunega
            new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        System.out.println(serverMsg);
                    }
                } catch (IOException e) {

                    System.out.println("Disconnected from server.");
                }
            }).start();

            // User input bhejna server ko
            System.out.println("Type your messages (QUIT to exit): ");
            while (true) {
                String msg = input.nextLine();
                out.println(msg);
                if (msg.equalsIgnoreCase("QUIT")) {
                    out.println(msg);
                    break;
                }
            }

            socket.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
