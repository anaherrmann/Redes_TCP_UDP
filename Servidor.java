
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author herrmann
 */
public class Servidor {
    
    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost"; //127.0.0.1
    
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public static void main(String[] args)  {
        try {
            
            int sizePacket = Integer.parseInt(args[1]);
            try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
                System.out.println("Aguardando conexão...");
                Socket socket = serverSocket.accept();
                System.out.println("Conectanto com: " + socket.getInetAddress().getHostAddress());
                
                System.out.println("sizePacket: " + sizePacket);
                System.out.println("fileName: " + args[0]);
                File send = new File(args[0]); //primeiro parâmetro
                long fileLength = send.length(); 
                FileInputStream file_send = new FileInputStream(send);
                BufferedInputStream buff_file = new BufferedInputStream(file_send);
                
                OutputStream output = socket.getOutputStream();
                
                byte[] fileChunk;
                long position = 0; //"ponteiro"
                
                while(position != fileLength){
                    if(fileLength - position >= sizePacket)
                        position += sizePacket; 
                   else {
                        sizePacket = (int) (fileLength - position);
                        position = fileLength;
                    }
                    fileChunk = new byte[sizePacket];
                    buff_file.read(fileChunk, 0, sizePacket);
                    output.write(fileChunk);
                }

                System.out.println("File sent");

                output.flush();
                socket.close();                
                serverSocket.close();
            }
                   
        
        } catch (IOException ex) {
            Logger.getLogger("tcp").log(Level.SEVERE, null, ex);
        }
    }
    
}
