/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;

import dominio.Contacto;
import java.util.List;
import persistencia.ContactoDAO;

/**
 *
 * @author Ben
 */
// clase para manejar la logica de negocio
public class ContactoServicio {
    private final ContactoDAO dao = new ContactoDAO(); // acceso a la "base de datos"

    // metodo para agregar contacto con validacion
    public Contacto agregarContacto(String nombre, String telefono, String correo) throws Exception {
        if (nombre == null || nombre.isBlank()) {
            throw new Exception("el nombre no puede estar vacio");
        }
        if (telefono == null || telefono.isBlank()) {
            throw new Exception("el telefono no puede estar vacio");
        }
        return dao.agregar(nombre, telefono, correo);
    }

    // metodo para obtener lista de contactos
    public List<Contacto> listarContactos() {
        return dao.obtenerTodos();
    }

    // metodo para eliminar contacto por id
    public boolean eliminarContacto(int id) {
        return dao.eliminar(id);
    }

    // metodo para actualizar un contacto
    public boolean actualizarContacto(Contacto contacto) {
        return dao.actualizar(contacto);
    }
    
      // <-- agrega este método
    public List<Contacto> obtenerTodos() {
        return dao.obtenerTodos();
    }
    
    
}