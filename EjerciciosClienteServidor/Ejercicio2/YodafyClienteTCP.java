//
// YodafyServidorIterativo
// (CC) jjramos, 2012
//
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class YodafyClienteTCP {

	public static void main(String[] args) {
		
		String bufferEnvio;
		String bufferRecepcion;
		int bytesLeidos=0;
		
		// Nombre del host donde se ejecuta el servidor:
		String host="localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio = null;
		
		try {
			// Creamos un socket que se conecte a "host" y "port":
			socketServicio= new Socket(host, port);		
			
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
	    		BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));
			
	    		outPrinter.println("Al monte del volcán debes ir sin demora");
			
			// Aunque le indiquemos a TCP que queremos enviar varios arrays de bytes, sólo
			// los enviará efectivamente cuando considere que tiene suficientes datos que enviar...
			// Podemos usar "flush()" para obligar a TCP a que no espere para hacer el envío:
			outPrinter.flush();
			
			// Mostremos la cadena de caracteres recibidos:
	    		bufferRecepcion = inReader.readLine();
	    		System.out.println("Recibido: ");
	    		System.out.print(bufferRecepcion);
			
			// Una vez terminado el servicio, cerramos el socket
			socketServicio.close();
			
			// Excepciones:
		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
