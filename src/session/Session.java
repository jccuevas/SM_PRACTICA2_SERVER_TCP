/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import server.Protocolo;

/**
 *
 * @author Juan Carlos
 */
public class Session {

    public static final String SID="SID";
   

    /**
     * Devuelve la cadena para el cliente formateada
     * @param user Nombre de usuario
     * @param password Clave del usuario
     * @return 
     */
    public static String getNewSession(String user, String password) {
        String key = new String(java.util.Base64.getEncoder().encode(password.getBytes()));
        String expires = SessionManager.newSession();

        return  Protocolo.PARAMETER_SESSION+"="+SID + user + key + "&"+Protocolo.PARAMETER_EXPIRES+"=" + expires + Protocolo.CRLF;
    }
}
