import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static java.lang.Math.ceil;
import static java.lang.System.exit;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class ServidorUDP {

    public static byte[] intToByteArray(int value) {
        return new byte[]{
            (byte) (value >>> 24),
            (byte) (value >>> 16),
            (byte) (value >>> 8),
            (byte) value};
    }

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost";

    public static void main(String[] args) throws SocketException, IOException {

        DatagramSocket serverSocket = new DatagramSocket(PORTA);
        int sizePacket = Integer.parseInt(args[1]);
        String fileName = args[0].substring(args[0].lastIndexOf("/") + 1).trim();
        try {

            System.out.println("\nAguardando conexao");

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            System.out.println(new String(receiveData, 0, receiveData.length, "UTF-8"));
            
            InetAddress IPClient = receivePacket.getAddress();
            int port = receivePacket.getPort();

            System.out.println("Conectado com: " + IPClient.toString());
            System.out.println("Tamanho do pacote: " + sizePacket);

            File send = new File(args[0]);
            int fileLength = (int) send.length();

            double aux = (double) fileLength / (double) sizePacket;
            int packageCount = (int) Math.ceil(aux);

            System.out.println("Enviando arquivo: " + fileName + " - " + fileLength + "bytes");
            System.out.println("Quantidade de blocos necessarios com pacote de " + sizePacket + " bytes: " + packageCount);

            FileInputStream file_send = new FileInputStream(send);
            BufferedInputStream buff_file = new BufferedInputStream(file_send);

            byte[] sendLength = new byte[4];
            sendLength = intToByteArray(fileLength);
            ByteBuffer.wrap(sendLength).putInt(fileLength);

            DatagramPacket lengthData = new DatagramPacket(sendLength, sendLength.length, IPClient, port);
            serverSocket.send(lengthData);

            byte[] fileChunk;
            int sequenceNumber = 1;
            DatagramSocket connection;
            long positionPtr = 0;

            long startTime = System.nanoTime();

            while (positionPtr != fileLength) {      

                if (fileLength - positionPtr >= sizePacket) {
                    positionPtr += sizePacket;
                } else {
                    sizePacket = (int) (fileLength - positionPtr);
                    positionPtr = fileLength;
                }

                fileChunk = new byte[sizePacket];
                buff_file.read(fileChunk, 0, sizePacket);
                DatagramPacket packet = new DatagramPacket(fileChunk, sizePacket, IPClient, port);

                try {
                    connection = new DatagramSocket();
                    connection.send(packet);
                    serverSocket.setSoTimeout(2000);
                    connection.close();
                } catch (SocketTimeoutException so){
                    connection = new DatagramSocket();
                    connection.send(packet);
                    serverSocket.setSoTimeout(2000);
                    connection.close();
                }        
            }
            long endTime = System.nanoTime();

            System.out.println(positionPtr + " " + fileLength + " " + sizePacket);
            System.out.println("Arquivo enviado.");
            System.out.println("Tempo (ms) = " + (endTime - startTime) / 1000000);

            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("times_UDP.txt"), true));
            writer.write(args[0] + "\t" + args[1] + "\t" + (endTime - startTime)/1000000 + "ms\n");
			writer.close();

        } catch (IOException ioe) {

        }
    }
}
