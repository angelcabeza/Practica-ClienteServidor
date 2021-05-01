import java.io.IOException;
import java.util.Scanner;

public class Vista {
	
	Scanner entrada; //Entrada de teclado

	public Vista( ) {
		entrada = new Scanner(System.in); 
	}

	// Aquí se reciben los datos del servidor:
	void entrada(String msg){
		System.out.println(msg);
	}

	// Aquí se devuelve la salida del usuario:
	String salida(){
		System.out.println("Introduce tu respuesta:");
		return entrada.nextLine();
	}

}
