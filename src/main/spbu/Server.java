package spbu;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Server {
    public static final int DEFAULT_PORT = 8080;

    private static void handleSocket(Socket socket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream());

            String startLine = input.readLine();
            String[] startLineParts = startLine.split(" ");
            String fileName = URLDecoder.decode(startLineParts[1], "UTF-8");
            if (!fileName.isEmpty()) {
                fileName = fileName.substring(1);
            }
            if (fileName.isEmpty()) {
                fileName = "index.html";
            }
            String content;
            try {
                content = new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException e) {
                output.print("HTTP/1.1 404 Not Found");
                output.close();
                System.out.println("Required document '" + fileName + "', that not found");
                return;
            }
            System.out.println("Requiring document '" + fileName + "', that found");
            output.println("HTTP/1.1 200 OK");
            output.println();
            output.print(content);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Reading or writing error is occurred");
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Socket closing is failed");
        }
    }

    public static void startServer(int port) {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server is started on port " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Server is not running on port " + port);
            return;
        }
        while (true) {
            try {
                handleSocket(serverSocket.accept());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error accepting socket");
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting server...");
        startServer(DEFAULT_PORT);
    }
}