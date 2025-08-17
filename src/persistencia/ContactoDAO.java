/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import dominio.Contacto;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Ben
 */
// clase que simula la base de datos en memoria
public class ContactoDAO {

    // CREATE
    public Contacto agregar(String nombre, String telefono, String correo) {
        String sql = "INSERT INTO dbo.Contactos(nombre, telefono, correo) VALUES (?, ?, ?)";
        try (Connection cn = ConexionSQLServer.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, telefono);
            if (correo == null || correo.isBlank()) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, correo);

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("No se insertó ningún registro.");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int nuevoId = rs.getInt(1);
                    Contacto c = new Contacto();
                    c.setId(nuevoId);
                    c.setNombre(nombre);
                    c.setTelefono(telefono);
                    c.setCorreo(correo);
                    return c;
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar contacto: " + e.getMessage(), e);
        }
    }

    // READ ALL
    public List<Contacto> obtenerTodos() {
        String sql = "SELECT id, nombre, telefono, correo FROM dbo.Contactos ORDER BY id DESC";
        List<Contacto> lista = new ArrayList<>();
        try (Connection cn = ConexionSQLServer.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Contacto c = new Contacto();
                c.setId(rs.getInt("id"));
                c.setNombre(rs.getString("nombre"));
                c.setTelefono(rs.getString("telefono"));
                c.setCorreo(rs.getString("correo"));
                lista.add(c);
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar contactos: " + e.getMessage(), e);
        }
    }

    // READ by ID
    public Optional<Contacto> buscarPorId(int id) {
        String sql = "SELECT id, nombre, telefono, correo FROM dbo.Contactos WHERE id = ?";
        try (Connection cn = ConexionSQLServer.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contacto c = new Contacto();
                    c.setId(rs.getInt("id"));
                    c.setNombre(rs.getString("nombre"));
                    c.setTelefono(rs.getString("telefono"));
                    c.setCorreo(rs.getString("correo"));
                    return Optional.of(c);
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar contacto: " + e.getMessage(), e);
        }
    }

    // DELETE
    public boolean eliminar(int id) {
        String sql = "DELETE FROM dbo.Contactos WHERE id = ?";
        try (Connection cn = ConexionSQLServer.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar contacto: " + e.getMessage(), e);
        }
    }

    // UPDATE
    public boolean actualizar(Contacto actualizado) {
        String sql = "UPDATE dbo.Contactos SET nombre = ?, telefono = ?, correo = ? WHERE id = ?";
        try (Connection cn = ConexionSQLServer.getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {

            ps.setString(1, actualizado.getNombre());
            ps.setString(2, actualizado.getTelefono());
            if (actualizado.getCorreo() == null || actualizado.getCorreo().isBlank()) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, actualizado.getCorreo());
            }
            ps.setInt(4, actualizado.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar contacto: " + e.getMessage(), e);
        }
    }
}