package spbu;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class ClientServerTest {
    @Test
    public void clientServerTest() {
        class ServerRunner implements Runnable {
            Thread thread;
            public void run() {
                Server.startServer(Server.DEFAULT_PORT);
            }
            public void startServer() {
                thread = new Thread(this);
                thread.start();
            }
            public void stopServer() {
                thread.interrupt();
                thread = null;
            }
        }
        ServerRunner runner = new ServerRunner();
        runner.startServer();
        String file = "ServerClient.iml";
        String contentFile = "";
        try {
            contentFile = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String gettedContent = Client.getDocument("localhost", Server.DEFAULT_PORT, file);
        runner.stopServer();

        assertTrue(contentFile.equals(gettedContent));
    }
}
