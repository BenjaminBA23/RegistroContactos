/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import dominio.Contacto;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import servicios.ContactoServicio;


/**
 *
 * @author Ben
 */
// clase que crea la ventana principal de la app
public class MainFrame extends JFrame {

    private final ContactoServicio servicio = new ContactoServicio();

    // UI
    private JTextField txtNombre, txtTelefono, txtCorreo, txtFiltro;
    private JButton btnAgregar, btnEliminar, btnEditar, btnMostrar, btnExportar;
    private JTable tabla;
    private DefaultTableModel modelo;
    private TableRowSorter<DefaultTableModel> sorter;

    // estado edición
    private Integer idEnEdicion = null;

    public MainFrame() {
        setTitle("agenda de contactos");
        setSize(650, 520);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initComponents();
        refrescarTabla(); // carga inicial
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        Color celeste = new Color(173, 216, 230);
        panel.setBackground(celeste);
        getContentPane().setBackground(celeste);

        // Labels
        JLabel lblNombre = new JLabel("nombre:");
        lblNombre.setBounds(20, 20, 80, 25);
        panel.add(lblNombre);

        JLabel lblTelefono = new JLabel("telefono:");
        lblTelefono.setBounds(20, 60, 80, 25);
        panel.add(lblTelefono);

        JLabel lblCorreo = new JLabel("correo:");
        lblCorreo.setBounds(20, 100, 80, 25);
        panel.add(lblCorreo);

        // Campos
        txtNombre = new JTextField();
        txtNombre.setBounds(100, 20, 220, 25);
        panel.add(txtNombre);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(100, 60, 220, 25);
        panel.add(txtTelefono);

        txtCorreo = new JTextField();
        txtCorreo.setBounds(100, 100, 220, 25);
        panel.add(txtCorreo);

        // Botones
        btnAgregar  = new JButton("agregar");   // en edición cambia a "guardar"
        btnAgregar.setBounds(340, 20, 130, 25);
        panel.add(btnAgregar);

        btnEliminar = new JButton("eliminar");
        btnEliminar.setBounds(340, 60, 130, 25);
        panel.add(btnEliminar);

        btnEditar   = new JButton("editar");
        btnEditar.setBounds(340, 100, 130, 25);
        panel.add(btnEditar);

        btnMostrar  = new JButton("mostrar contactos");
        btnMostrar.setBounds(480, 20, 150, 25);
        panel.add(btnMostrar);

        btnExportar = new JButton("exportar PDF");
        btnExportar.setBounds(480, 60, 150, 25);
        panel.add(btnExportar);

        // Filtro
        JLabel lblFiltro = new JLabel("filtrar:");
        lblFiltro.setBounds(20, 140, 80, 22);
        panel.add(lblFiltro);

        txtFiltro = new JTextField();
        txtFiltro.setBounds(100, 140, 220, 22);
        panel.add(txtFiltro);

        // Tabla
        modelo = new DefaultTableModel(new Object[]{"id","nombre","telefono","correo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabla = new JTable(modelo);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBounds(20, 175, 610, 300);
        panel.add(scroll);

        add(panel);

        // Eventos
        btnAgregar.addActionListener(e -> guardarOAgregar());
        btnEliminar.addActionListener(e -> eliminarContacto());
        btnEditar.addActionListener(e -> iniciarEdicion());
        btnMostrar.addActionListener(e -> refrescarTabla());
        btnExportar.addActionListener(e -> exportarPdf());

        txtFiltro.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltro(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltro(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });
    }

    private void aplicarFiltro() {
        String texto = txtFiltro.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        }
    }

    // Inserta si no hay idEnEdicion; actualiza si sí hay.
    private void guardarOAgregar() {
        String nombre = txtNombre.getText().trim();
        String tel    = txtTelefono.getText().trim();
        String correo = txtCorreo.getText().trim();

        if (nombre.isEmpty() || tel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "nombre y telefono son obligatorios");
            return;
        }

        try {
            if (idEnEdicion == null) { // INSERT
                servicio.agregarContacto(nombre, tel, correo.isEmpty() ? null : correo);
            } else {                   // UPDATE
                Contacto c = new Contacto(idEnEdicion, nombre, tel, correo.isEmpty() ? null : correo);
                boolean ok = servicio.actualizarContacto(c);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "no se pudo actualizar");
                    return;
                }
                idEnEdicion = null;
                btnAgregar.setText("agregar");
            }
            limpiarCampos();
            refrescarTabla();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "error: " + ex.getMessage(), "validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarEdicion() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "seleccione un contacto en la tabla");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);

        idEnEdicion = (Integer) modelo.getValueAt(filaModelo, 0);
        txtNombre.setText(String.valueOf(modelo.getValueAt(filaModelo, 1)));
        txtTelefono.setText(String.valueOf(modelo.getValueAt(filaModelo, 2)));
        Object cObj = modelo.getValueAt(filaModelo, 3);
        txtCorreo.setText(cObj == null ? "" : cObj.toString());

        btnAgregar.setText("guardar");
        txtNombre.requestFocus();
        txtNombre.selectAll();
    }

    private void eliminarContacto() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "seleccione un contacto para eliminar");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int id = (Integer) modelo.getValueAt(filaModelo, 0);

        if (servicio.eliminarContacto(id)) {
            modelo.removeRow(filaModelo);
            if (idEnEdicion != null && idEnEdicion == id) {
                idEnEdicion = null; btnAgregar.setText("agregar"); limpiarCampos();
            }
        } else {
            JOptionPane.showMessageDialog(this, "no se pudo eliminar");
        }
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        for (Contacto c : servicio.obtenerTodos()) {
            modelo.addRow(new Object[]{ c.getId(), c.getNombre(), c.getTelefono(), c.getCorreo() });
        }
        aplicarFiltro();
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
    }

    // ===== Exportar a PDF SIN JARs (usa impresora PDF del sistema) =====
    private void exportarPdf() {
        Object[] opciones = { "Seleccionado", "Todos (visibles)" };
        int choice = JOptionPane.showOptionDialog(this,
                "¿Qué deseas exportar?",
                "Exportar a PDF",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (choice == JOptionPane.CLOSED_OPTION) return;

        List<String> lines = (choice == 0) ? buildLineasSeleccionado() : buildLineasVisibles();
        if (lines == null || lines.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar");
            return;
        }
        imprimirLineasComoPDF(choice == 0 ? "Contacto" : "Lista de contactos", lines);
    }

    private List<String> buildLineasSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona un contacto en la tabla");
            return null;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        int id = (Integer) modelo.getValueAt(filaModelo, 0);
        String nombre = String.valueOf(modelo.getValueAt(filaModelo, 1));
        String tel    = String.valueOf(modelo.getValueAt(filaModelo, 2));
        Object cObj   = modelo.getValueAt(filaModelo, 3);
        String correo = cObj == null ? "" : cObj.toString();

        List<String> lines = new ArrayList<>();
        lines.add("ID: " + id);
        lines.add("Nombre: " + nombre);
        lines.add("Teléfono: " + tel);
        lines.add("Correo: " + correo);
        return lines;
    }

    private List<String> buildLineasVisibles() {
        List<String> lines = new ArrayList<>();
        lines.add("id | nombre | telefono | correo");
        lines.add("----------------------------------------");

        for (int i = 0; i < tabla.getRowCount(); i++) {
            int filaModelo = tabla.convertRowIndexToModel(i);
            int id = (Integer) modelo.getValueAt(filaModelo, 0);
            String nombre = String.valueOf(modelo.getValueAt(filaModelo, 1));
            String tel    = String.valueOf(modelo.getValueAt(filaModelo, 2));
            Object cObj   = modelo.getValueAt(filaModelo, 3);
            String correo = cObj == null ? "" : cObj.toString();

            lines.add(id + " | " + nombre + " | " + tel + " | " + correo);
        }
        return lines;
    }

    /** Manda a imprimir usando “Microsoft Print to PDF” (u otra impresora PDF). */
    private void imprimirLineasComoPDF(String titulo, List<String> lines) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName(titulo);

        Printable printable = (Graphics g, PageFormat pf, int pageIndex) -> {
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());

            int margin = 40;
            int yStart = margin;

            Font titleFont = new Font("SansSerif", Font.BOLD, 14);
            Font bodyFont  = new Font("SansSerif", Font.PLAIN, 11);

            g2.setFont(bodyFont);
            int lineHeight = g2.getFontMetrics().getHeight();
            int usableH = (int) pf.getImageableHeight() - margin * 2;
            int linesPerPage = Math.max(1, usableH / lineHeight) - 2; // deja espacio para título

            int totalLines = lines.size() + 1; // +1 por título
            int totalPages = (int) Math.ceil(totalLines / (double) linesPerPage);

            if (pageIndex >= totalPages) return Printable.NO_SUCH_PAGE;

            // Título
            g2.setFont(titleFont);
            g2.drawString(titulo, margin, yStart);
            g2.setFont(bodyFont);

            int y = yStart + lineHeight * 2;
            int startLine = pageIndex * linesPerPage - 1; // -1 por el título
            if (pageIndex == 0) startLine = 0;
            int endLine = Math.min(lines.size(), (pageIndex + 1) * linesPerPage - 1);

            for (int i = startLine; i < endLine; i++) {
                if (i >= 0) {
                    g2.drawString(lines.get(i), margin, y);
                    y += lineHeight;
                }
            }

            String footer = "Página " + (pageIndex + 1) + " de " + totalPages;
            int fw = g2.getFontMetrics().stringWidth(footer);
            g2.drawString(footer, (int) (pf.getImageableWidth() - fw - margin),
                                 (int) (pf.getImageableHeight() - margin));
            return Printable.PAGE_EXISTS;
        };

        job.setPrintable(printable);
        boolean ok = job.printDialog(); // elige “Microsoft Print to PDF” y dónde guardar
        if (ok) {
            try { job.print(); }
            catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Error al imprimir: " + e.getMessage());
            }
        }
    }
}