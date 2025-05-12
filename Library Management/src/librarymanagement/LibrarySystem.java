//package librarymanagement;
//
//import java.util.Scanner;
//
//public class Library_Management {
//	protected static final Scanner sc=new Scanner(System.in);
//	protected static final Library library=new Library();
//
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		while(true) {
//			System.out.println("\n ----------LIBRARY MANAGEMENT SYSTEM----------");
//			System.out.println("1.Add Book");
//			System.out.println("2.View Book");
//			System.out.println("3.Issue Book");
//			System.out.println("4.Return Book");
//			System.out.println("5.Exit");
//			System.out.println();
//			System.out.println("Enter your Choice: ");
//			int choice =sc.nextInt();
//			sc.nextLine();
//			switch(choice) {
//			case 1:
//				addBook();
//				break;
//			case 2:
//				library.viewBooks();
//				break;
//			case 3:
//				issueBook();	
//				break;
//			case 4:
//				returnBook();
//				break;
//			case 5:{
//				System.out.println("you are exit from the program");
//				sc.close();
//				System.exit(0);
//			}
//			default:
//				System.out.println("Invalid choice. tryagain");
//			
//			}
//		}
//
//	}
//
//
//	public static void addBook(){
//		System.out.println("Enter your Book id: ");
//		int id=sc.nextInt();
//		sc.nextLine();
//		System.out.println("Enter Book Title: ");
//		String title=sc.nextLine();
//		System.out.println("Enter Book Author");
//		String author=sc.nextLine();
//		
//		Book book=new Book(id, author, title);
//		library.addBook(book);
//	}
//	private static void issueBook() {
//		System.out.println("Enter Book Id to issue: ");
//		int id=sc.nextInt();
//		sc.nextLine();
//		library.issueBook(id);
//	}
//	private static void returnBook() {
//		System.out.println("Enter Book Id to return: ");
//		int id=sc.nextInt();
//		sc.nextLine();
//		library.returnBook(id);;
//	}
//}
package librarymanagement;

import java.sql.*;
import java.util.Scanner;

public class LibrarySystem {
    static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    static final String USER = "root";
    static final String PASS = "your_mysql_password";
    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            while (true) {
                System.out.println("\n1. Admin Login\n2. User Login\n3. Register as User\n4. Exit");
                int choice = sc.nextInt(); sc.nextLine();

                switch (choice) {
                    case 1: adminLogin(); break;
                    case 2: userLogin(); break;
                    case 3: registerUser(); break;
                    case 4: System.exit(0);
                    default: System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void adminLogin() throws SQLException {
        System.out.print("Admin username: ");
        String uname = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String sql = "SELECT * FROM admins WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uname);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                adminMenu();
            } else {
                System.out.println("Invalid admin credentials.");
            }
        }
    }

    static void adminMenu() throws SQLException {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Add Book\n2. View All Books\n3. View Issued Books\n4. Logout");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Title: ");
                    String title = sc.nextLine();
                    System.out.print("Author: ");
                    String author = sc.nextLine();
                    System.out.print("Quantity: ");
                    int qty = sc.nextInt(); sc.nextLine();
                    String sql = "INSERT INTO books (title, author, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, title);
                        stmt.setString(2, author);
                        stmt.setInt(3, qty);
                        stmt.executeUpdate();
                        System.out.println("Book added.");
                    }
                    break;
                case 2:
                    viewBooks();
                    break;
                case 3:
                    String issuedSql = "SELECT ib.id, b.title, u.username, ib.issue_date FROM issued_books ib " +
                                       "JOIN books b ON ib.book_id = b.id " +
                                       "JOIN users u ON ib.user_id = u.id";
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery(issuedSql)) {
                        while (rs.next()) {
                            System.out.printf("IssueID: %d | Book: %s | User: %s | Date: %s\n",
                                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4));
                        }
                    }
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    static void registerUser() throws SQLException {
        System.out.print("New username: ");
        String uname = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uname);
            stmt.setString(2, pass);
            stmt.executeUpdate();
            System.out.println("User registered.");
        }
    }

    static void userLogin() throws SQLException {
        System.out.print("Username: ");
        String uname = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        String sql = "SELECT * FROM users WHERE username=? AND password=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uname);
            stmt.setString(2, pass);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                userMenu(userId);
            } else {
                System.out.println("Invalid user credentials.");
            }
        }
    }

    static void userMenu(int userId) throws SQLException {
        while (true) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View Books\n2. Issue Book\n3. Return Book\n4. Logout");
            int choice = sc.nextInt(); sc.nextLine();

            switch (choice) {
                case 1: viewBooks(); break;
                case 2:
                    System.out.print("Enter Book ID to issue: ");
                    int bookId = sc.nextInt(); sc.nextLine();
                    String checkSql = "SELECT quantity FROM books WHERE id=?";
                    try (PreparedStatement stmt = conn.prepareStatement(checkSql)) {
                        stmt.setInt(1, bookId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next() && rs.getInt("quantity") > 0) {
                            conn.setAutoCommit(false);
                            try {
                                String issueSql = "INSERT INTO issued_books (book_id, user_id, issue_date) VALUES (?, ?, CURDATE())";
                                try (PreparedStatement issueStmt = conn.prepareStatement(issueSql)) {
                                    issueStmt.setInt(1, bookId);
                                    issueStmt.setInt(2, userId);
                                    issueStmt.executeUpdate();
                                }
                                String updateQty = "UPDATE books SET quantity = quantity - 1 WHERE id=?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateQty)) {
                                    updateStmt.setInt(1, bookId);
                                    updateStmt.executeUpdate();
                                }
                                conn.commit();
                                System.out.println("Book issued.");
                            } catch (SQLException e) {
                                conn.rollback();
                                throw e;
                            } finally {
                                conn.setAutoCommit(true);
                            }
                        } else {
                            System.out.println("Book not available.");
                        }
                    }
                    break;
                case 3:
                    System.out.print("Enter Book ID to return: ");
                    int retBookId = sc.nextInt(); sc.nextLine();
                    String deleteSql = "DELETE FROM issued_books WHERE book_id=? AND user_id=? LIMIT 1";
                    try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                        stmt.setInt(1, retBookId);
                        stmt.setInt(2, userId);
                        int rows = stmt.executeUpdate();
                        if (rows > 0) {
                            String updateQty = "UPDATE books SET quantity = quantity + 1 WHERE id=?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQty)) {
                                updateStmt.setInt(1, retBookId);
                                updateStmt.executeUpdate();
                            }
                            System.out.println("Book returned.");
                        } else {
                            System.out.println("No such issued book found.");
                        }
                    }
                    break;
                case 4: return;
                default: System.out.println("Invalid option.");
            }
        }
    }

    static void viewBooks() throws SQLException {
        String sql = "SELECT * FROM books";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nAvailable Books:");
            while (rs.next()) {
                System.out.printf("ID: %d | %s by %s | Qty: %d\n",
                        rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getInt("quantity"));
            }
        }
    }
}