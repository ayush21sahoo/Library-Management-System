import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class GenreHandler extends JFrame {

    private Connection c;
    private JComboBox<String> genreComboBox;
    private JButton loadBooksButton;
    private JTable booksTable;
    private JLabel genreIconLabel; // Label for the genre icon

    public GenreHandler() {
        this.c = Connect.ConnectToDB(); // Assuming Connect.ToDB() is your method to establish a database connection
        
        // Setup the JFrame window
        setTitle("Select Genre");
        setSize(800, 600); // Adjust the size as needed
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window
        
        // Create the layout
        setLayout(new BorderLayout());
        
        // Create a JPanel for genre selection and genre icon
        JPanel genrePanel = new JPanel();
        genrePanel.setLayout(new FlowLayout());

        // Genre label and combo box
        JLabel genreLabel = new JLabel("Select Genre:");
        genreComboBox = new JComboBox<>();
        loadGenres(); // Load the genres from the database
        genrePanel.add(genreLabel);
        genrePanel.add(genreComboBox);

        loadBooksButton = new JButton("Load Books");
        genrePanel.add(loadBooksButton);

        // Add genre panel to the top of the frame
        add(genrePanel, BorderLayout.NORTH);

        // Add table to display books
        booksTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(booksTable);
        add(tableScrollPane, BorderLayout.CENTER);

        // Button click event to load books based on selected genre
        loadBooksButton.addActionListener(e -> loadBooksByGenre());
    }

    // Method to load genres from the database and populate the JComboBox
    private void loadGenres() {
        try {
            String query = "SELECT DISTINCT genre FROM library.book"; // Query to get all unique genres
            PreparedStatement pst = c.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Add genres to the JComboBox
            while (rs.next()) {
                genreComboBox.addItem(rs.getString("genre"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading genres: " + ex.getMessage());
        }
    }

    // Method to load books based on the selected genre
    private void loadBooksByGenre() {
        String selectedGenre = (String) genreComboBox.getSelectedItem();
        if (selectedGenre == null) {
            JOptionPane.showMessageDialog(this, "Please select a genre.");
            return;
        }

        try {
            String query = "SELECT * FROM library.book WHERE genre = ?"; // Query to fetch books by genre
            PreparedStatement pst = c.prepareStatement(query);
            pst.setString(1, selectedGenre);
            ResultSet rs = pst.executeQuery();

            // Get metadata to know the column count
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create a vector to store the column names
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnName(i));
            }

            // Create a vector to store the row data
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            // Set up the table model with the column names and data
            booksTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching books: " + ex.getMessage());
        }
    }

    // Override the paintComponent method of the panel to set background image
    @Override
    public void paintComponents(Graphics g) {
        super.paintComponents(g); // Call the parent class's paintComponents method

        // Load the background image
        ImageIcon backgroundImage = new ImageIcon(getClass().getResource("/img/Genre.jpg")); // Your background image file
        Image img = backgroundImage.getImage(); // Convert to Image

        // Move the background image down by 100 pixels (or adjust as needed)
        int yOffset = 100; // You can adjust this value to fit your design needs

        // Draw the image stretched to the full size of the window, with the y offset
        g.drawImage(img, 0, yOffset, getWidth(), getHeight(), this);
    }

    // Main method to show the GenreHandler JFrame
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GenreHandler genreHandler = new GenreHandler();
            genreHandler.setVisible(true); // Open the genre selection window with the icon
        });
    }
}