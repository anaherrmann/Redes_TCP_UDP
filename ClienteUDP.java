import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import javax.xml.bind.DatatypeConverter;

public class ClienteUDP {

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost"; //127.0.0.1

    public static void main(String[] args) throws UnknownHostException, SocketException, IOException {

        int sizePacket = Integer.parseInt(args[1]);
        String fileName = args[0].substring(args[0].lastIndexOf("/") + 1).trim();
        
        System.out.println("Tamanho do Pacote: " + sizePacket);
        System.out.println("Arquivo recebido: " + fileName);

        try {
            FileOutputStream file = new FileOutputStream(args[0]);
            BufferedOutputStream buffer = new BufferedOutputStream(file);

            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress IPAddress = InetAddress.getLocalHost();

            int port = clientSocket.getLocalPort();
            byte[] reqMsg = new byte[1024];
            String requestMsg = "Conexao cliente";
            reqMsg = requestMsg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(reqMsg, reqMsg.length, IPAddress, PORTA);
            clientSocket.send(sendPacket);

            byte[] receiveData = new byte[4];

            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            clientSocket.receive(receivePacket);

            String hex = "0x" + DatatypeConverter.printHexBinary(receiveData);
            int fileLength = Integer.decode(hex);

            byte[] fileChunk = new byte[sizePacket];

            int positionPtr = 0;
            
            while(positionPtr != fileLength){
                receivePacket = new DatagramPacket(fileChunk, sizePacket);
                clientSocket.receive(receivePacket);
                
                if (fileLength - positionPtr >= sizePacket) {
                    positionPtr += sizePacket;
                } else {
                    sizePacket = (int) (fileLength - positionPtr);
                    positionPtr = fileLength;
                }
                buffer.write(fileChunk, 0, sizePacket);
            }
            clientSocket.close();
            buffer.flush();
        } catch (IOException ioe) {

        } 

    }
}
