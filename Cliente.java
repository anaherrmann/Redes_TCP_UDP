/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package tcp;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 *
 * @author herrmann
 */
public class Cliente {

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost"; //127.0.0.1

    public static void main(String args[]) {

        int sizePacket = Integer.parseInt(args[1]);
        System.out.println("sizePacket: " + sizePacket);
        System.out.println("fileName: " + args[0]);

        try {
            Socket socket = new Socket(SERVIDOR, PORTA);
            FileOutputStream file = new FileOutputStream(args[0]);
            BufferedOutputStream buffer = new BufferedOutputStream(file);
            InputStream in = socket.getInputStream();

            byte[] fileChunk = new byte[sizePacket];

            int bytesRead = 0;

            while ((bytesRead = in.read(fileChunk)) != -1) {
                buffer.write(fileChunk, 0, bytesRead);
            }

            buffer.flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
