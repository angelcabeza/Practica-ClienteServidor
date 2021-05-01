import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteTCP {

	public static void main(String[] args) {
		
		String bufferEnvio;
		String bufferRecepcion;
		String msg;
		int bytesLeidos=0;

		// Nombre del host donde se ejecuta el servidor:
		String host = "localhost";
		// Puerto en el que espera el servidor:
		int port=8989;
		
		// Socket para la conexión TCP
		Socket socketServicio = null;

		Vista vista = new Vista();

		boolean empezado = false;
		
		try {
			// Creamos un socket que se conecte a "host" y "port":
			socketServicio = new Socket(host, port);		
			
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
			BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));

			bufferRecepcion = "";
			msg = "";
			
			while(!msg.contains("BYE")){

				if(empezado){
					outPrinter.println(vista.salida());
				}else{
					outPrinter.println("HELO");
					empezado = true;
				}
				
				// Aunque le indiquemos a TCP que queremos enviar varios arrays de bytes, sólo
				// los enviará efectivamente cuando considere que tiene suficientes datos que enviar...
				// Podemos usar "flush()" para obligar a TCP a que no espere para hacer el envío:
				outPrinter.flush();
				
				// Mostremos la cadena de caracteres recibidos:
				msg = "";
				bufferRecepcion = "";
				
				//He visto que para añadir mensajes con saltos de linea no lo hacia de forma correcta
				//Así que he utilizado este método:
				do{
					msg += bufferRecepcion + "\n";
					bufferRecepcion = inReader.readLine();
				}while(!bufferRecepcion.equals("~"));

				//Siempre y cuando el servidor no envie un mensaje de despedida...
				if(!msg.contains("BYE")){

					//Mostrar mensaje del servidor
					vista.entrada(msg);
				}
			}

			vista.entrada("Gracias por su visita.");
			empezado = false;

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
