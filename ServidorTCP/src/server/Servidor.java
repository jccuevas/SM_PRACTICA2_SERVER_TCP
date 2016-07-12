package server;

import java.io.IOException;
import java.net.*;

public class Servidor implements Protocolo{


	static ServerSocket mServidor = null;
	public static int mConexiones=0;

	public static void main(String[] args) {
		System.out.println("SERVIDOR> Iniciando servidor");
		try {
			mConexiones=0;
			//InetAddress local = InetAddress.getLocalHost();
			//InetAddress[] direcciones = InetAddress.getAllByName(local.getCanonicalHostName());
			
			//mServidor = new ServerSocket(TCP_SERVICE_PORT,20,local);// Se crea el socket
														// servidor, equivale a
														// socket(), bind() y listen()
			mServidor = new ServerSocket(TCP_SERVICE_PORT);// Se crea el socket
			// servidor, equivale a
			// socket(), bind() y listen()
		} catch (IOException e) {// Control de error
			System.err.println("SERVIDOR ERROR> " + e.getMessage());
			return;//Si la creación del servidor falla se cierra
		}
		while (true) {// Bucle infinito para emular el servicio
			Socket nuevaConexion;
			try {
				nuevaConexion = mServidor.accept();// Se acepta una nueva
				// conexión entrante
				System.out.println("SERVIDOR> Conexión entrante desde " + nuevaConexion.getInetAddress().toString() + ":"
						+ nuevaConexion.getPort());
				//Conexion con = new Conexion(nuevaConexion);
				//Thread servicio = new Thread(con);
				//servicio.start();
				new Thread(new Conexion(nuevaConexion)).start();// La nueva
																// conexión se
																// pasa a una
																// nueva hebra
			} catch (IOException e) {// Excepción al aceptar la conexión
				System.err.println("SERVIDOR ERROR> " + e.getMessage());
			}

		}

	}
	
	public static synchronized void nuevoCliente()
	{
		mConexiones++;
	}
	
	public static synchronized void eliminarCliente()
	{
		mConexiones--;
	}
	

}