import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class RataRataAppTest {
    private RataRataApp app;

    @BeforeEach
    public void setUp() {
        // Inisialisasi instance RataRataApp untuk setiap test
        app = new RataRataApp();
    }

    @Test
    public void testValidateInputValid() {
        // Tidak ada exception untuk input yang valid
        assertDoesNotThrow(() -> app.validateInput("Alice", 85.0));
    }

    @Test
    public void testValidateInputInvalidName() {
        // Nama kosong harus menghasilkan IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> app.validateInput("", 85.0));
        assertEquals("Nama atau nilai tidak valid!", exception.getMessage());
    }

    @Test
    public void testValidateInputInvalidValue() {
        // Nilai di bawah 0 atau di atas 100 harus menghasilkan IllegalArgumentException
        Exception exception = assertThrows(IllegalArgumentException.class, () -> app.validateInput("Alice", -10));
        assertEquals("Nama atau nilai tidak valid!", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> app.validateInput("Alice", 110));
        assertEquals("Nama atau nilai tidak valid!", exception.getMessage());
    }

    @Test
    public void testUpdateAverageWithValues() {
        // Menambahkan beberapa nilai dan memeriksa rata-rata
        app.nilaiList = new ArrayList<>();
        app.nilaiList.add(80.0);
        app.nilaiList.add(90.0);
        app.nilaiList.add(100.0);

        // Perbarui rata-rata
        app.updateAverage();

        // Verifikasi hasil rata-rata
        assertEquals("Rata-rata: 90.00", app.resultLabel.getText());
    }

    @Test
    public void testUpdateAverageWithoutValues() {
        // Tidak ada nilai dalam list, rata-rata harus "-".
        app.nilaiList = new ArrayList<>();

        app.updateAverage();

        assertEquals("Rata-rata: -", app.resultLabel.getText());
    }

    @Test
    public void testAddData() {
        // Simulasi menambahkan data
        app.nameField.setText("Alice");
        app.valueField.setText("85");

        app.addData();

        // Verifikasi data masuk ke tableModel
        DefaultTableModel model = app.tableModel;
        assertEquals(1, model.getRowCount());
        assertEquals("Alice", model.getValueAt(0, 0));
        assertEquals(85.0, model.getValueAt(0, 1));

        // Verifikasi nilai masuk ke nilaiList
        assertEquals(1, app.nilaiList.size());
        assertEquals(85.0, app.nilaiList.get(0));
    }

    @Test
    public void testRemoveData() {
        // Simulasi menambahkan data terlebih dahulu
        app.nameField.setText("Alice");
        app.valueField.setText("85");
        app.addData();

        app.nameField.setText("Bob");
        app.valueField.setText("90");
        app.addData();

        // Pilih baris pertama (index 0) dan hapus
        app.table.setRowSelectionInterval(0, 0); // Pilih baris pertama
        app.removeData();

        // Verifikasi hasil setelah penghapusan
        DefaultTableModel model = app.tableModel;
        assertEquals(1, model.getRowCount());
        assertEquals("Bob", model.getValueAt(0, 0));
        assertEquals(90.0, model.getValueAt(0, 1));

        // Verifikasi nilai dalam nilaiList
        assertEquals(1, app.nilaiList.size());
        assertEquals(90.0, app.nilaiList.get(0));
    }
}
