
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JOptionPane;
import java.nio.ByteBuffer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author herrmann
 */
public class Cliente_UDP {

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost"; //127.0.0.1

    public static void main(String[] args) {

        int sizePacket = Integer.parseInt(args[1]);
        System.out.println("sizePacket: " + sizePacket);
        System.out.println("fileName: " + args[0]);

        try {
            FileOutputStream file = new FileOutputStream(args[0]);
            BufferedOutputStream buffer = new BufferedOutputStream(file);
            
            String requestMsg = "Send me the file.";
            byte[] reqMsg = requestMsg.getBytes();
            

            InetAddress IPAddress = InetAddress.getByName(SERVIDOR);
            DatagramSocket clientSocket = new DatagramSocket();
            
            DatagramSocket request = new DatagramSocket(PORTA);
            DatagramPacket sendPacket = new DatagramPacket(reqMsg, reqMsg.length, IPAddress, PORTA);
            clientSocket.send(sendPacket);
            
            byte[] receiveData = new byte[4];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);   

            int fileLength = ByteBuffer.wrap(receivePacket.getData()).getInt();
            System.out.println(fileLength);
            byte[] fileChunk = new byte[sizePacket];

            int bytesRead = 0;
            
            while (true) {
            	int sequenceNumber;
                receivePacket = new DatagramPacket(fileChunk, sizePacket);
                clientSocket.receive(receivePacket);
                sequenceNumber[0] = fileChunk[0];
                sequenceNumber[1] = fileChunk[1];
                System.out.println(fileChunk);
                buffer.write(fileChunk, 2, bytesRead);
               
            }

            buffer.flush();
            
            
            
            
            
            
            
            
            
            //Preparando o buffer de recebimento da mensagem
            byte[] msg = new byte[sizePacket];
            //Prepara o pacote de dados
            DatagramPacket pkg = new DatagramPacket(msg, msg.length);
            //Recebimento da mensagem
            packet.receive(pkg);
            JOptionPane.showMessageDialog(null, new String(pkg.getData()).trim(),
                    "Mensagem recebida", 1);
            packet.close();
        } catch (IOException ioe) {
        }
    }
}
