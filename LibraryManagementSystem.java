import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Book implements Serializable {
    String bookID, title, author, genre, status;

    public Book(String bookID, String title, String author, String genre, String status) {
        this.bookID = bookID;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.status = status;
    }
}

public class LibraryManagementSystem {
    private final JFrame frame;
    private final JTable table;
    private final DefaultTableModel model;
    private final HashMap<String, Book> books;
    private static final String FILE_NAME = "library_data.ser";

    public LibraryManagementSystem() {
        books = loadBooks();
        frame = new JFrame("Library Management System");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"Book ID", "Title", "Author", "Genre", "Status"}, 0);
        table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        JButton addButton = new JButton("Add Book");
        JButton searchButton = new JButton("Search Book");
        JButton updateButton = new JButton("Update Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton exitButton = new JButton("Exit");

        panel.add(addButton);
        panel.add(searchButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(exitButton);
        frame.add(panel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addBook());
        searchButton.addActionListener(e -> searchBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        exitButton.addActionListener(e -> {
            saveBooks();
            System.exit(0);
        });

        refreshTable();
        frame.setVisible(true);
    }

    private void addBook() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Checked Out"});

        Object[] fields = {"Book ID:", idField, "Title:", titleField, "Author:", authorField, "Genre:", genreField, "Status:", statusBox};
        int result = JOptionPane.showConfirmDialog(frame, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String genre = genreField.getText().trim();
            String status = (String) statusBox.getSelectedItem();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty() || books.containsKey(id)) {
                JOptionPane.showMessageDialog(frame, "Invalid input or duplicate Book ID.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Book book = new Book(id, title, author, genre, status);
            books.put(id, book);
            saveBooks();
            refreshTable();
        }
    }

    private void searchBook() {
        String searchKey = JOptionPane.showInputDialog(frame, "Enter Book ID or Title:");
        if (searchKey == null || searchKey.trim().isEmpty()) return;

        for (Book book : books.values()) {
            if (book.bookID.equalsIgnoreCase(searchKey) || book.title.equalsIgnoreCase(searchKey)) {
                JOptionPane.showMessageDialog(frame, "Book Found:\nID: " + book.bookID + "\nTitle: " + book.title + "\nAuthor: " + book.author + "\nGenre: " + book.genre + "\nStatus: " + book.status);
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Book not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBook() {
        String bookID = JOptionPane.showInputDialog(frame, "Enter Book ID to Update:");
        if (bookID == null || bookID.trim().isEmpty() || !books.containsKey(bookID)) {
            JOptionPane.showMessageDialog(frame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = books.get(bookID);
        JTextField titleField = new JTextField(book.title);
        JTextField authorField = new JTextField(book.author);
        JTextField genreField = new JTextField(book.genre);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Checked Out"});
        statusBox.setSelectedItem(book.status);

        Object[] fields = {"Title:", titleField, "Author:", authorField, "Genre:", genreField, "Status:", statusBox};
        int result = JOptionPane.showConfirmDialog(frame, fields, "Update Book", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            book.title = titleField.getText().trim();
            book.author = authorField.getText().trim();
            book.genre = genreField.getText().trim();
            book.status = (String) statusBox.getSelectedItem();
            saveBooks();
            refreshTable();
        }
    }

    private void deleteBook() {
        String bookID = JOptionPane.showInputDialog(frame, "Enter Book ID to Delete:");
        if (bookID == null || bookID.trim().isEmpty() || !books.containsKey(bookID)) {
            JOptionPane.showMessageDialog(frame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        books.remove(bookID);
        saveBooks();
        refreshTable();
    }

    private void refreshTable() {
        model.setRowCount(0);
        for (Book book : books.values()) {
            model.addRow(new Object[]{book.bookID, book.title, book.author, book.genre, book.status});
        }
    }

    private void saveBooks() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(books);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Book> loadBooks() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (HashMap<String, Book>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LibraryManagementSystem::new);
    }
}
