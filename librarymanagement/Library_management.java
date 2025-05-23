package librarymanagement;
import java.util.*;
import java.time.LocalDate;

class Book {
    int id;
    String title;
    String author;
    boolean isAvailable;
    String borrowedBy;
    LocalDate returnDate;

    Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.borrowedBy = null;
        this.returnDate = null;
    }

    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Borrowed by " + borrowedBy;
        String returnInfo = !isAvailable ? ", Return by: " + returnDate : "";
        return id + ". " + title + " by " + author + " - " + status + returnInfo;
    }
}

class Library {
    private List<Book> books = new ArrayList<>();
    private int bookIdCounter = 1;

    public void addBook(String title, String author) {
        books.add(new Book(bookIdCounter++, title, author));
        System.out.println("Book added successfully.");
    }

    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in library.");
            return;
        }
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public void borrowBook(int id, String userName) {
        for (Book book : books) {
            if (book.id == id) {
                if (book.isAvailable) {
                    book.isAvailable = false;
                    book.borrowedBy = userName;
                    book.returnDate = LocalDate.now().plusDays(14); // 2 weeks return policy
                    System.out.println("Book borrowed successfully by " + userName);
                } else {
                    System.out.println("Book is already borrowed.");
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }

    public void returnBook(int id, String userName) {
        for (Book book : books) {
            if (book.id == id) {
                if (!book.isAvailable && userName.equals(book.borrowedBy)) {
                    book.isAvailable = true;
                    System.out.println("Book returned by " + userName + " on " + LocalDate.now());
                    book.borrowedBy = null;
                    book.returnDate = null;
                } else {
                    System.out.println("Book not borrowed by you or already available.");
                }
                return;
            }
        }
        System.out.println("Book not found.");
    }
}

public class Library_management {
    static Scanner scanner = new Scanner(System.in);
    static Library library = new Library();

    public static void main(String[] args) {
        System.out.println("Welcome to the Library System!");
        while (true) {
            System.out.print("\nLogin as (1) Admin (2) User (0) Exit: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) adminMenu();
            else if (choice == 2) userMenu();
            else if (choice == 0) break;
            else System.out.println("Invalid choice.");
        }
    }

    public static void adminMenu() {
        while (true) {
            System.out.print("\nAdmin Menu: (1) Add Book (2) View Books (0) Logout: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                System.out.print("Enter book title: ");
                String title = scanner.nextLine();
                System.out.print("Enter author name: ");
                String author = scanner.nextLine();
                library.addBook(title, author);
            } else if (choice == 2) {
                library.viewBooks();
            } else if (choice == 0) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    public static void userMenu() {
        System.out.print("Enter your name: ");
        String userName = scanner.nextLine();

        while (true) {
            System.out.print("\nUser Menu: (1) View Books (2) Borrow Book (3) Return Book (0) Logout: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                library.viewBooks();
            } else if (choice == 2) {
                System.out.print("Enter book ID to borrow: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                library.borrowBook(id, userName);
            } else if (choice == 3) {
                System.out.print("Enter book ID to return: ");
                int id = scanner.nextInt();
                scanner.nextLine();
                library.returnBook(id, userName);
            } else if (choice == 0) {
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }
}