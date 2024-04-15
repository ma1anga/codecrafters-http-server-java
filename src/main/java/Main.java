import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        Socket clientSocket = null;

        PrintWriter out;
        BufferedReader in;

        try (ServerSocket serverSocket = new ServerSocket(4221)) {
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept();

            System.out.println("Accepted new connection");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            in.readLine(); // Required to read socket input first
            out.println("HTTP/1.1 200 OK\r\n\r\n");

            System.out.println("Closing...");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
