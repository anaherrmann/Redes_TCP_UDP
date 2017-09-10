import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor {

	public static final int PORTA = 8000;
	public static final String SERVIDOR = "localhost"; // 127.0.0.1

	public static void main(String[] args) {
        
        int sizePacket = Integer.parseInt(args[1]);
        String fileName = args[0].substring(args[0].lastIndexOf("\\") + 1).trim();
		
        try {
			try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
				
                System.out.println("Aguardando conexao...");
				Socket socket = serverSocket.accept();
				System.out.println("Conectado com: " + socket.getInetAddress().getHostAddress());
                System.out.println("Tamanho do pacote: " + sizePacket);
                System.out.println("Enviando arquivo: " + fileName);
				
                File send = new File(args[0]);
				long fileLength = send.length();

				FileInputStream file_send = new FileInputStream(send);
				BufferedInputStream buff_file = new BufferedInputStream(file_send);
				OutputStream output = socket.getOutputStream();

				byte[] fileChunk;
				long postionPtr = 0; // "ponteiro"
				
				long startTime = System.nanoTime();
				while (postionPtr != fileLength) {
					if (fileLength - postionPtr >= sizePacket)
						postionPtr += sizePacket;
					else {
						sizePacket = (int) (fileLength - postionPtr);
						postionPtr = fileLength;
					}
					fileChunk = new byte[sizePacket];
					buff_file.read(fileChunk, 0, sizePacket);
					output.write(fileChunk);
				}
				long endTime = System.nanoTime();
				
				System.out.println("Arquivo enviado");
				
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("times_TCP.txt"), true));
				writer.write(args[0] + "\t" + args[1] + "\t" + (endTime - startTime)/1000000 + "ms\n");
								
				output.flush();
				socket.close();
				serverSocket.close();
				writer.close();
			}

		} catch (IOException ex) {
			Logger.getLogger("tcp").log(Level.SEVERE, null, ex);
		}
	}

}
