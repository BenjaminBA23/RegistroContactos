Agenda de Contactos (Java Swing + SQL Server)

Aplicación de escritorio en Java Swing para gestionar una agenda de contactos con persistencia en SQL Server.
Incluye CRUD completo, filtro en tiempo real, botón mostrar contactos, edición con selección desde la tabla y exportación a PDF usando la impresora PDF del sistema (sin librerías externas para PDF).

✨ Funcionalidades

Crear contactos (nombre, teléfono, correo).

Listar/Mostrar contactos (recarga desde BD).

Editar: selecciona en la tabla → se cargan datos → guarda cambios.

Eliminar contacto seleccionado.

Filtrar (buscar en todas las columnas) en tiempo real.

Exportar a PDF:

Solo el contacto seleccionado.

Todos los contactos visibles (respeta el filtro).

Sin JARs: usa “Microsoft Print to PDF” / “Guardar como PDF”.

🧱 Requisitos

JDK 8+ (recomendado 17).

NetBeans / Ant (o tu IDE preferido).

SQL Server y SQL Server Management Studio (SSMS 20).

Driver JDBC de SQL Server (obligatorio para la BD):

mssql-jdbc-<versión>.jre8.jar o mssql-jdbc-<versión>.jre17.jar según tu JDK.

Para PDF no necesitas librerías externas.

🗄️ Base de datos (SQL Server)

Ejecuta este script en SSMS:

IF DB_ID('RegistroContactos') IS NULL
    CREATE DATABASE RegistroContactos;
GO
USE RegistroContactos;
GO

IF OBJECT_ID('dbo.Contactos', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Contactos (
        id          INT IDENTITY(1,1) PRIMARY KEY,
        nombre      VARCHAR(100) NOT NULL,
        telefono    VARCHAR(20)  NOT NULL,
        correo      VARCHAR(120) NULL,
        created_at  DATETIME2    NOT NULL DEFAULT SYSDATETIME()
    );
END
GO

-- Datos de ejemplo (opcional)
INSERT INTO dbo.Contactos(nombre, telefono, correo)
VALUES ('Ana Pérez','8888-8888','ana@example.com'),
       ('Juan Mora','8712-3456','juan@correo.com');
GO


Configura SQL Server:

Habilita TCP/IP (SQL Server Configuration Manager → Protocols → Enable TCP/IP).

Modo de autenticación mixto (SQL + Windows) o usa un usuario SQL dedicado.

Verifica el puerto 1433 y firewall.

🔌 Añadir el driver JDBC (NetBeans/Ant)

Descarga el Microsoft JDBC Driver for SQL Server (misma versión que tu JDK).

Crea la carpeta libs/sqlserver/ en el proyecto y coloca el .jar.

En NetBeans: Proyecto → Properties → Libraries → Add JAR/Folder → selecciona el .jar.

⚙️ Configuración de conexión

Edita persistencia/ConexionSQLServer.java:

private static final String HOST = "localhost";
private static final int    PORT = 1433;
private static final String DB   = "RegistroContactos";
private static final String USER = "sa";              // cámbialo
private static final String PASS = "TuPassword123!";  // cámbialo

private static final String URL = String.format(
  "jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=false;trustServerCertificate=true",
  HOST, PORT, DB
);


Si usas otra instancia/puerto, ajusta HOST/PORT. En productivo usa encrypt=true y certificado válido.
```bash
🧩 Estructura principal
src/
├─ dominio/
│  └─ Contacto.java

├─ persistencia/
│  ├─ ConexionSQLServer.java
│  └─ ContactoDAO.java         // CRUD JDBC real

├─ servicios/
│  └─ ContactoServicio.java    // orquesta DAO para la UI

├─ ui/
│  └─ MainFrame.java           // ventana principal (Swing)

└─ main/
   └─ Main.java                // arranque de la app
```
▶️ Ejecución

Asegúrate de que SQL Server esté iniciado y la BD creada.

Abre el proyecto en NetBeans y añade el driver JDBC a Libraries.

Compila y ejecuta (main/Main.java):

public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> new ui.MainFrame().setVisible(true));
}

🖱️ Uso

Agregar: completa nombre y teléfono → agregar.

Mostrar contactos: botón mostrar contactos (recarga desde BD).

Editar:

Selecciona una fila en la tabla.

Pulsa editar → se cargan los datos en los campos.

Modifica y pulsa guardar (el botón “agregar” cambia a “guardar” en modo edición).

Eliminar: selecciona una fila → eliminar.

Filtrar: escribe en filtrar (busca en todas las columnas).

Exportar a PDF:

exportar PDF → elige Seleccionado o Todos (visibles).

Se abre el diálogo de impresión: selecciona Microsoft Print to PDF (Windows) o Guardar como PDF (macOS).

La exportación no requiere JARs porque usa la impresora PDF del sistema mediante PrinterJob.

