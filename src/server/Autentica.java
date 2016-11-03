package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64.Encoder;
import java.util.Date;

public class Autentica implements Runnable, Protocolo {

	Socket mSocket;
	public static final String MSG_WELCOME = "Bienvenido al servidor de pruebas";
	public static final String MSG_QUIT = "�Adios, hasta la vista!";
	public static final String MSG_ERRORAUT = "Usuario o clave incorrectos";

	public Autentica(Socket s) {
		mSocket = s;
	}

	@Override
	public void run() {
		String inputData = null;
		String outputData = "";
		String comando = "";
		String parametro = "";
		String tempUser = "";
		boolean salir = false;
		int estado = S_USER;

		if (mSocket != null) {
			try {
                           
				// Conexi�n con la entrada y salida del socket
				DataOutputStream outputStream = new DataOutputStream(mSocket.getOutputStream());
				BufferedReader inputStream = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

				// Se env�a el mensaje de bienvenida
				outputData=OK+SP+MSG_WELCOME+CRLF;
				outputStream.write(outputData.getBytes());
				outputStream.flush();

				while ((inputData = inputStream.readLine()) != null && !salir) {
					System.out.println("SERVIDOR [Recibido]> " + inputData);
					String fields[] = inputData.split(" ");

					// Se compreba si el mensaje es un comando solo o comando
					// con par�metro
					if (fields.length == 1) {// No hay espacios, debe ser solo
												// un comando
						comando = inputData;
						parametro = null;
					} else if (fields.length >= 2) {// Hay al menos un espacio
													// ya qe el m�todo split ha
													// devuelto dos o m�s
													// cadenas
						comando = fields[0];// El comando debe ser el primer
											// campo
						// Se extrae el par�metro que se considera que es todo
						// aquello a partir del primer espacio
						parametro = inputData.substring(inputData.indexOf(" "));
						parametro = parametro.trim();// Se limpian los espacios
														// adicionales
					}
					switch (estado) {
					case S_USER:// Estado USER
						if (comando.equalsIgnoreCase(QUIT)) {// En todos los
										// estados, si
									// de salida
							outputData = OK + SP + MSG_QUIT + CRLF;
							salir = true;
						} else if (comando.equalsIgnoreCase(USER) && parametro != null) {
							tempUser = parametro;
							outputData = OK + CRLF;
							estado++;// Se incrementa el estado aunque a�n no se
										// compara
						} else {// No es un comando v�lido
							outputData = ERROR + CRLF;
						}
						break;
					case S_PASS:// Estado PASS
						if (comando.equalsIgnoreCase(QUIT)) {
							outputData = OK + SP + MSG_QUIT + CRLF;
							salir = true;
						} else if (comando.equals(PASS) && parametro != null) {
							if (tempUser.compareTo(USERNAME) == 0 && parametro.compareTo(PASSWORD) == 0) {
                                                            
                                                            String key=new String(java.util.Base64.getEncoder().encode(parametro.getBytes()));
                                                            Date fecha = new Date(System.currentTimeMillis()+3600000);
                                                            SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd-H-m-s");
                                                            String expires = dt1.format(fecha);
								outputData = "SESION-ID=SID"+tempUser+key+"&EXPIRES="+expires + CRLF;
								estado++;// Como el usuario y clave coinciden se
											// incrementa estado
							} else {//Autenticaci�n err�nea
								outputData = ERROR +SP +MSG_ERRORAUT+ CRLF;
								estado = S_USER;
							}
						}else {// No es un comando v�lido
							outputData = ERROR + CRLF;
						}
						break;
					case S_OPER:// Estado OPERACION

						if (comando.equalsIgnoreCase(QUIT)) {
							outputData = OK + SP + MSG_QUIT + CRLF;
							salir = true;
						} else if (comando.equalsIgnoreCase(ECHO)) {
							if (parametro != null)
								outputData = OK + SP + parametro + CRLF;// se
																		// responde
																		// con
																		// la
																		// misma
																		// cadena
																		// recibida
							else// El cliente no envi� par�metro alguno
								outputData = ERROR + CRLF;
						} else// No es un comando v�lido
							outputData = ERROR + CRLF;
						break;
					}
					outputStream.write(outputData.getBytes());
					outputStream.flush();
				}
				System.out.println("SERVIDOR> Conexi�n finalizada");
                                outputStream.close();
                                inputStream.close();
				mSocket.close();
			} catch (SocketException se) {
				System.err.println("SERVIDOR [Finalizado]> " + se.getMessage());
			} catch (IOException ioe) {
				System.err.println("SERVIDOR [Finalizado]> " + ioe.getMessage());
			}

		}

	}

}
