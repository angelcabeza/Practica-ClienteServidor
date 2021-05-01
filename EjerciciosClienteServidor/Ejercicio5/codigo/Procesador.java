import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;
import java.io.PrintWriter;
import java.io.BufferedReader;


//
// Nota: si esta clase extendiera la clase Thread, y el procesamiento lo hiciera el método "run()",
// ¡Podríamos realizar un procesado concurrente! 
//
public class Procesador {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;
	// stream de lectura (por aquí se recibe lo que envía el cliente)
	private InputStream inputStream;
	// stream de escritura (por aquí se envía los datos al cliente)
	private OutputStream outputStream;

	// Para que la respuesta sea siempre diferente, usamos un generador de números aleatorios.
	private Random random;

	private int saldo;
	private int apuesta_actual;
	private int apuesta_minima;

	Contexto contextoActual;

	boolean jugando = true;

	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public Procesador(Socket socketServicio) {
		this.socketServicio=socketServicio;

		contextoActual = Contexto.INICIO;
		random = new Random();
		saldo = 500;
		apuesta_minima = 100;
		apuesta_actual = 0;
	}

	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){

		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		String datosRecibidos;

		try {
			// Obtiene los flujos de escritura/lectura
			BufferedReader inReader = new BufferedReader (new InputStreamReader(socketServicio.getInputStream()));
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(),true);

			do{
				datosRecibidos = inReader.readLine();
			
				// Se interpreta el mensaje...
				String respuesta = procesarPeticion(datosRecibidos);

				// ... y enviamos la respuesta
				outPrinter.println(respuesta);
			}while(jugando);

		} catch (IOException e) {
			System.err.println("Error al obtener los flujos de entrada/salida.");
		}

	}

	// Aquí se decide que hacer con la opcion recibida
	private String procesarPeticion(String peticion) {
		String respuesta = "";

		if(peticionValida(peticion)){
			switch(contextoActual){
				case INICIO:
					respuesta = "Bienvenido al simulador de apuestas mas didactico del mundo.\n El saldo inicial es de: "+ Integer.toString(saldo)+ "\n" + mostrarMenu();
					contextoActual = Contexto.MENU;
					break;

				case MENU:
					respuesta = procesarMenu(peticion);
					break;

				case ELECCION:
					respuesta = procesarEleccion(peticion);
					break;

				case APOSTAR:
					respuesta = procesarApuesta(peticion);
					break;

				case MOSTRARSALDO:
					respuesta=procesarSaldo() +"\n"+ mostrarMenu();
					contextoActual= Contexto.MENU;
					break;

				case EXPULSAR:
					respuesta="BYE";
					break;
				
			}
		}else{
			respuesta = "Error: La respuesta " + peticion + " no es valida; debe introducir un numero.";
		}

		return respuesta + "\n~";
	}

	//Se define el comportamiento de cada opción del menú
	private String procesarMenu(String peticion){
		int opcion = Integer.parseInt(peticion);
		String respuesta ="";

		switch (opcion){
			case 1:
				contextoActual = Contexto.APOSTAR;
				respuesta = "Que cantidad desea apostar?";
				break;
			case 2:
				contextoActual= Contexto.MENU;
				respuesta = procesarSaldo() + mostrarMenu();
				break;
			case 3:
				respuesta = "BYE";
				jugando = false;
				break;
			default:
				respuesta = "Error: Opcion " + peticion + " no disponible\n" + mostrarMenu();
				break;
		}
		return respuesta;

	}

	//Se define el proceso de apuesta en el servidor
	private String procesarApuesta(String peticion){
		int apostado = Integer.parseInt(peticion);
		String respuesta="";

		if(apostado <= saldo){
			if(apostado < apuesta_minima){
				respuesta= "---Apuesta no completada: La cantidad apostada debe ser mayor que 100!----\nVuelva a introducir una cantidad valida: ";
			}else{
				contextoActual = Contexto.ELECCION;
				respuesta = "Seleccione una opcion: \n\t[1] Cara\n\t[2] Cruz";
				apuesta_actual = apostado;
				saldo -= apuesta_actual;
			}
		}else{
			respuesta = "---Apuesta no completada: No posee la cantidad de dinero que quiere apostar----\nVuelva a introducir una cantidad valida: ";
		}

		return respuesta;
	}

	private String procesarEleccion(String peticion){
		int elegido = Integer.parseInt(peticion);

		int numeroDecisivo = random.nextInt(2) + 1;
		String respuesta = "";

		if(elegido == 1 || elegido == 2){
			if(elegido == numeroDecisivo){
				respuesta = "|-----------------------------------------------|\n|---------------|  Has ganado!  |---------------|\n|-----------------------------------------------|" +
						"\n\t";
				saldo += apuesta_actual*2;

				respuesta += procesarSaldo() + "\n" + mostrarMenu();
				contextoActual = Contexto.MENU;
			}else if(saldo > apuesta_minima){
				respuesta = "\tHas perdido. Intentalo otra vez!\n\t";

				respuesta += procesarSaldo() + "\n" + mostrarMenu();
				contextoActual = Contexto.MENU;
			}else{
				respuesta = "\tHas perdido.\nSin el saldo minimo para apostar no puedes jugar mas.\nIntroduzca cualquier respuesta...\t";
				contextoActual = Contexto.EXPULSAR;
			}

			return respuesta;
		}else{
			respuesta = "Error: Opcion " + peticion + " no disponible, seleccione otra";
			return respuesta;
		}
	}

	private String procesarSaldo(){
		return "Su saldo actual es: " + Integer.toString(saldo)+"\n";
	}

	//Muestra las opciones del menú
	private String mostrarMenu(){
		return "Seleccione una opcion:\n"+
		"\t[1] Apostar\n\t[2] Mostrar Saldo\n\t[3] Salir";
	}

	private boolean peticionValida(String msg){
		if(contextoActual != Contexto.INICIO && contextoActual != Contexto.EXPULSAR){
			try{
				Integer.parseInt(msg);
			}catch(NumberFormatException e){
				return false;
			}
		}

		return true;
	}
}

enum Contexto{
	INICIO,
	MENU,
	ELECCION,
	APOSTAR,
	MOSTRARSALDO,
	EXPULSAR
}
