package spbu;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static String getDocument(String host, int port, String URI) {
        if (URI.isEmpty() || URI.charAt(0) != '/') {
            URI = '/' + URI;
        }

        Socket socket;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not connect to " + host + ":" + port);
            return null;
        }
        System.out.println("Getting document '" + host + ":" + port + URI + "'...");
        // BufferedReader input;
        Scanner input;
        PrintWriter output;
        try {
            // input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("In/out error is occurred");
            return null;
        }
        output.write("GET " + URI + " HTTP/1.1\nHost: " + host + "\n\n");
        output.flush();

        if (input.hasNextLine()) {
            String startingLine = input.nextLine();
            String[] startingLineParts = startingLine.split(" ", 3);
            if (startingLineParts.length != 3) {
                System.out.println("Starting line is broken");
                return null;
            }
            int resultCode = Integer.parseInt(startingLineParts[1]);
            if (!(200 <= resultCode && resultCode < 300)) {
                System.out.println("Server error #" + resultCode);
                return null;
            } else {
                System.out.println("Server responded with code " + resultCode);
            }
        } else {
            System.out.println("There is no line");
            return null;
        }

        while (input.hasNextLine()) {
            if (input.nextLine().isEmpty()) {
                break;
            }
        }
        String content = "";
        while (input.hasNextLine()) {
            if (!content.isEmpty()) {
                content += "\n";
            }
            content += input.nextLine();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static String getDocument(String url) {
        URL parsedUrl;
        try {
            parsedUrl = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("Wrong URL '" + url + "'");
            return null;
        }

        int port = parsedUrl.getPort() != -1 ? parsedUrl.getPort() : 80;
        String uri = parsedUrl.getPath() + (parsedUrl.getQuery() == null ? "" : "?" + parsedUrl.getQuery());

        return getDocument(parsedUrl.getHost(), port, uri);
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/index.html";
        System.out.println("Getting content of '" + url + "'...");
        String content = getDocument(url);
        System.out.println("Content = '" + content + "'");
    }
}
