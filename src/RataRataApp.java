import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;

public class RataRataApp extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField nameField, valueField;
    private JButton addButton, removeButton, editButton;
    private JLabel resultLabel;
    private ArrayList<Double> nilaiList;

    public RataRataApp() {
        // Set the title and layout of the JFrame
        setTitle("Aplikasi Hitung Rata-Rata Nilai");
        setLayout(new BorderLayout());
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialize components
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Nama");
        tableModel.addColumn("Nilai");

        table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);

        nameField = new JTextField(10);
        valueField = new JTextField(5);
        addButton = new JButton("Tambah");
        removeButton = new JButton("Hapus");
        editButton = new JButton("Edit");
        resultLabel = new JLabel("Rata-rata: -");

        // Set background color of the buttons to mint
        Color mintColor = new Color(189, 252, 201); // Mint color

        addButton.setBackground(mintColor);
        removeButton.setBackground(mintColor);
        editButton.setBackground(mintColor);

        nilaiList = new ArrayList<>();

        // Panel for input and buttons
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Nama:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Nilai:"));
        inputPanel.add(valueField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);
        inputPanel.add(editButton);

        // Panel for result label (mengubah latar belakang menjadi mint)
        JPanel resultPanel = new JPanel();
        resultPanel.setBackground(new Color(189, 252, 201)); // Mint color
        resultPanel.add(resultLabel);

        // Add components to the frame
        add(inputPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(resultPanel, BorderLayout.SOUTH);

        // Event handling
        addButton.addActionListener(e -> addData());
        removeButton.addActionListener(e -> removeData());
        editButton.addActionListener(e -> editData());

        // Start the API server
        startAPIServer();

        setVisible(true);
    }

    // Method to validate input for name and value
    private void validateInput(String name, double value) {
        if (name.isEmpty() || value < 0 || value > 100) {
            throw new IllegalArgumentException("Nama atau nilai tidak valid!");
        }
    }

    private void addData() {
        try {
            String name = nameField.getText();
            double value = Double.parseDouble(valueField.getText());

            // Validate input
            validateInput(name, value);

            // Add data to table and nilaiList
            tableModel.addRow(new Object[]{name, value});
            nilaiList.add(value);

            // Calculate and update the average automatically
            updateAverage();

            nameField.setText("");
            valueField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Input nilai harus berupa angka!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            // Remove selected row from table and nilaiList
            tableModel.removeRow(selectedRow);
            nilaiList.remove(selectedRow);

            // Recalculate the average after removal
            updateAverage();
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String newName = nameField.getText();
            String newValueStr = valueField.getText();

            try {
                double newValue = Double.parseDouble(newValueStr);

                // Validate input
                validateInput(newName, newValue);

                // Update the selected row with new values
                tableModel.setValueAt(newName, selectedRow, 0); // Update Nama
                tableModel.setValueAt(newValue, selectedRow, 1); // Update Nilai

                // Update the nilaiList for correct average calculation
                nilaiList.set(selectedRow, newValue);

                // Recalculate the average
                updateAverage();

                nameField.setText("");
                valueField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Input nilai harus berupa angka!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin diedit!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateAverage() {
        if (nilaiList.isEmpty()) {
            resultLabel.setText("Rata-rata: -");
            return;
        }

        double sum = 0;
        for (double value : nilaiList) {
            sum += value;
        }

        double average = sum / nilaiList.size();
        resultLabel.setText("Rata-rata: " + String.format("%.2f", average));
    }

    // Method to start the API server
    private void startAPIServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/data", new DataHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("API Server running at http://localhost:8080");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class to handle API requests
    class DataHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder response = new StringBuilder();
            response.append("[");
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                response.append("{");
                response.append("\"name\":\"").append(tableModel.getValueAt(i, 0)).append("\",");
                response.append("\"value\":").append(tableModel.getValueAt(i, 1));
                response.append("}");
                if (i < tableModel.getRowCount() - 1) response.append(",");
            }
            response.append("]");

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.toString().getBytes());
            os.close();
        }
    }

    public static void main(String[] args) {
        new RataRataApp();
    }
}
