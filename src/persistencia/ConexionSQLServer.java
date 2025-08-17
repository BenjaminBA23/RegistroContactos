/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author Ben
 */
public class ConexionSQLServer {

    // Ajusta estos valores a tu entorno:
    private static final String HOST = "localhost"; // o 127.0.0.1
    private static final int PORT = 1433;
    private static final String DB   = "RegistroContactos";

    // Si usas autenticación SQL (usuario/clave)
    private static final String USER = "sa";             // <-- cámbialo
    private static final String PASS = "112306"; // <-- cámbialo

    // Para entornos locales suele bastar con encrypt=false y trustServerCertificate=true
    private static final String URL = String.format(
        "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;trustServerCertificate=true",
        HOST, PORT, DB
    );

    public static Connection getConnection() throws SQLException {
        // No es necesario Class.forName() en JDK modernos si el driver está en el classpath
        return DriverManager.getConnection(URL, USER, PASS);
    }
}