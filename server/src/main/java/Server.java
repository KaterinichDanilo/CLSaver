import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 4000;

    public Server() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started!");
            while (true){
                Socket socket = server.accept();
                System.out.println("New client connected!");
                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
                server.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
