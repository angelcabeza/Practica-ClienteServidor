import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorIterativo {

	public static void main(String[] args) {

		// Puerto de escucha
		int port=8989;
		ServerSocket socketServidor;

		// array de bytes auxiliar para recibir o enviar datos.
		byte[] buffer = new byte[256];
		// Número de bytes leídos
		int bytesLeidos = 0;

		try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			System.out.println("Intentando abrir el puerto...");
			socketServidor = new ServerSocket (port);
			System.out.println("¡Puerto abierto con exito!");

			do {

				Socket socketServicio = null;

				// Aceptamos una nueva conexión con accept()
				try{
					System.out.println("Esperando conexion...");
					socketServicio = socketServidor.accept();
					System.out.println("¡Conexion aceptada!");
				}
				catch (IOException e){
					System.err.println("Error: no se pudo aceptar la conexion solicitada");
				}

				// Creamos un objeto de la clase ProcesadorYodafy, pasándole como 
				// argumento el nuevo socket, para que realice el procesamiento
				// Este esquema permite que se puedan usar hebras más fácilmente.
				Procesador procesador = new Procesador(socketServicio);
				procesador.procesa();

			} while (true);

		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}

	}

}