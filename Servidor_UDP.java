
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author herrmann
 */
public class Servidor_UDP {

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost"; //127.0.0.1

    public static void main(String[] args) throws SocketException {
        
        DatagramSocket serverSocket = new DatagramSocket(PORTA);
        int sizePacket = Integer.parseInt(args[1]);
        long sequenceNumber = 0;
        try {

        	System.out.println("Aguardando conexao");
            byte[] receiveData = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(receivePacket);
            InetAddress IPClient = receivePacket.getAddress();
            
            
            if("Send me the file.".equals(Arrays.toString(receiveData))){
                
                System.out.println("Enviando arquivo para " + IPClient.toString());
                
                File send = new File(args[0]); //primeiro parâmetro
                long fileLength = send.length(); 
                
                byte[] sendLength = {(byte) fileLength};
                DatagramPacket length = new DatagramPacket(sendLength, sendLength.length, IPClient, PORTA);
                serverSocket.send(length);
                
                FileInputStream file_send = new FileInputStream(send);
                BufferedInputStream buff_file = new BufferedInputStream(file_send);
//confraria arquizio vavo 

                byte[] fileChunk;

                long position = 0; //"ponteiro"

                while(position != fileLength){
                	sequenceNumber++;

                    if(fileLength - position >= sizePacket)
                        position += sizePacket; 
                   else {
                        sizePacket = (int) (fileLength - position);
                        position = fileLength;
                    }
                    fileChunk = new byte[sizePacket];
                    fileChunk[0] = ByteUtils.convertToBytes(sequenceNumber)[0];
                    fileChunk[1] = ByteUtils.convertToBytes(sequenceNumber)[1];
                    
                    buff_file.read(fileChunk, 0, sizePacket);
                    DatagramPacket packet = new DatagramPacket(fileChunk, sizePacket, IPClient, PORTA);
                    DatagramSocket connection = new DatagramSocket();
                    connection.send(packet);
                    connection.close();                
                }

                System.out.println("Arquivo enviado.");
                
                
            } else {
                System.out.println("Solicitação não reconhecida.");
            }
            
            
        } catch (IOException ioe) {

        }
    }
}
