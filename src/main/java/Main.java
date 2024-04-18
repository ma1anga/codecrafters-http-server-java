import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static String HTTP_RESPONSE_PATTERN = "HTTP/1.1 {0}\r\n\r\n";

    private static String HTTP_MESSAGE_OK = "200 OK";
    private static String HTTP_MESSAGE_NOT_FOUND = "404 Not Found";

    private static String REQUEST_START_LINE_DIVIDER = " ";


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

            String requestStartLine = in.readLine();

            if (getRequestPath(requestStartLine).equals("/")) {
                out.println(MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_OK));
            } else {
                out.println(MessageFormat.format(HTTP_RESPONSE_PATTERN, HTTP_MESSAGE_NOT_FOUND));
            }

            System.out.println("Closing connection...");
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String getRequestPath(String requestStartLine) {
        final List<String> startLineParts = Arrays.asList(requestStartLine.split(REQUEST_START_LINE_DIVIDER));

        // Start line format -> "GET /index.html HTTP/1.1"
        return startLineParts.get(1);
    }
}
