import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ClientHandler implements Runnable {
    private Socket socket;

    private final String serverDir = "server_files";
    private DataInputStream is;
    private DataOutputStream dos;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    OutputStream os = null;
    private String fileToDownload;

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client accepted");
        sendListOfFiles(serverDir);
    }

    private void sendListOfFiles(String dir) throws IOException {
        dos.writeUTF("#list#");
        List<String> files = getFiles(serverDir);
        dos.writeInt(files.size());
        for (String file : files) {
            dos.writeUTF(file);
        }
        dos.flush();
    }

    private List<String> getFiles(String dir) {
        String[] list = new File(dir).list();
        assert list != null;
        return Arrays.asList(list);
    }

    @Override
    public void run() {
        byte[] buf = new byte[256];
        try {
            while (true) {
                String command = is.readUTF();
                System.out.println("received: " + command);
                if (command.equals("#file#")) {
                    String fileName = is.readUTF();
                    long len = is.readLong();
                    File file = Path.of(serverDir).resolve(fileName).toFile();
                    try(FileOutputStream fos = new FileOutputStream(file)) {
                        for (int i = 0; i < (len + 255) / 256; i++) {
                            int read = is.read(buf);
                            fos.write(buf, 0 , read);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendListOfFiles(serverDir);
                } else if (command.equals("#fileDOWNLOAD#")) {
                    System.out.println("#fileDOWNLOAD#");
                    fileToDownload = is.readUTF();
                    System.out.println(fileToDownload);
                    downloadFile(fileToDownload, this.socket);
                }
            }
        } catch (Exception e) {
            System.err.println("Connection was broken");
        }
    }

    private void downloadFile(String FILE_TO_SEND, Socket socket) throws IOException {
        System.out.println("downloadFileMethod");
        File myFile = new File (FILE_TO_SEND);
        System.out.println(FILE_TO_SEND);
        byte [] mybytearray  = new byte [(int)myFile.length()];
        fis = new FileInputStream(myFile);
        bis = new BufferedInputStream(fis);
        bis.read(mybytearray,0,mybytearray.length);
        os = socket.getOutputStream();
        System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
        os.write(mybytearray,0,mybytearray.length);
        os.flush();
        System.out.println("Done.");
    }
}
