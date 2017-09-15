import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClienteTCP {

    public static final int PORTA = 8000;
    public static final String SERVIDOR = "localhost";

    public static void main(String args[]) {

        int sizePacket = Integer.parseInt(args[1]);
		String fileName = args[0].substring(args[0].lastIndexOf("/") + 1).trim();

        System.out.println("Tamanho do Pacote: " + sizePacket);
        System.out.println("Arquivo: " + fileName);

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
