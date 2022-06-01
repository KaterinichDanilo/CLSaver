package com.saver.client;

import java.io.*;
import java.net.Socket;

public class Network {
    private Socket socket;

    private DataInputStream is;
    private DataOutputStream os;
    private final static int FILE_SIZE = 6022386;
    private FileOutputStream fos;
    private BufferedOutputStream bos;

    public Network(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
    }

    public String readString() throws IOException {
        return is.readUTF();
    }

    public int readInt() throws IOException {
        return is.readInt();
    }

    public void writeMessage(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public DataOutputStream getOs() {
        return os;
    }

    public DataInputStream getIs() {
        return is;
    }

    public void downloadFile(String FILE_TO_RECEIVED) throws IOException {
        int bytesRead;
        int current = 0;

        System.out.println("downloadFile method started");
        byte [] mybytearray  = new byte [FILE_SIZE];
        System.out.println("array was created");
        InputStream is = this.socket.getInputStream();
        System.out.println("inputstream was created");
        fos = new FileOutputStream(FILE_TO_RECEIVED);
        bos = new BufferedOutputStream(fos);
        System.out.println("fos and bos was created");
        bytesRead = is.read(mybytearray,0, mybytearray.length);
        System.out.println("bytesRead");
        current = bytesRead;
        System.out.println("DO started");

        do {
            bytesRead =
                    is.read(mybytearray, current, (mybytearray.length-current));
            if(bytesRead >= 0) current += bytesRead;
        } while(bytesRead > -1);
        System.out.println("DO finished");

        bos.write(mybytearray, 0 , current);
        bos.flush();
        System.out.println("bos.flush");
        System.out.println("File " + FILE_TO_RECEIVED
                + " downloaded (" + current + " bytes read)");
    }
}

