import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class ShowBooks extends JFrame {
    private Connection connection;
    private PreparedStatement pst;
    private ResultSet rs;
    
    public ShowBooks() {
        connection = Connect.ConnectToDB(); // Assuming ConnectToDB() is correctly implemented
        initComponents();
        loadBooks();
    }

    private void loadBooks() {
        try {
            String query = "SELECT * FROM library.book"; 
            pst = connection.prepareStatement(query);
            rs = pst.executeQuery();
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            JTable table = new JTable(data, columnNames);
            table.setFillsViewportHeight(true);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

            JScrollPane scrollPane = new JScrollPane(table);
            getContentPane().add(scrollPane, BorderLayout.CENTER);
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading books: " + ex.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponents() {
        setTitle("Library Books");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel headerLabel = new JLabel("Show Books");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(headerLabel);

        JButton closeButton = new JButton("X");
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton);

        getContentPane().add(headerPanel, BorderLayout.NORTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShowBooks().setVisible(true));
    }
}
