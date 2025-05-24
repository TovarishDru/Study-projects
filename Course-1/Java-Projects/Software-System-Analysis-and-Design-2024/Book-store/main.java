//Andrey Torgashinov
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BookStore bookStore = new BookStore();
        CommandProcessor processor = new CommandProcessor(bookStore);
        boolean inputFlow = true;
        while (inputFlow) {
            String command = scanner.nextLine();
            inputFlow = processor.processCommand(command);
        }
    }
}


/**
 * Facade class for command processing
 */
class CommandProcessor {
    /**
     * BookStore with which the Processor interacts
     */
    private final BookStore bookStore;
    /**
     * Method that processes the given command
     * @param command Command to be processed
     * @return True if input is continued, False otherwise
     */
    public boolean processCommand(String command) {
        String[] parsed = command.split(" ");
        try{
            switch (parsed[0]) {
                case "createBook":
                    bookStore.createBook(parsed[1], parsed[2], parsed[3]);
                    break;
                case "createUser":
                    bookStore.createUser(parsed[1], parsed[2]);
                    break;
                case "subscribe":
                    bookStore.subscribe(parsed[1]);
                    break;
                case "unsubscribe":
                    bookStore.unsubscribe(parsed[1]);
                    break;
                case "updatePrice":
                    bookStore.updatePrice(parsed[1], parsed[2]);
                    break;
                case "readBook":
                    bookStore.readBook(parsed[1], parsed[2]);
                    break;
                case "listenBook":
                    bookStore.listenBook(parsed[1], parsed[2]);
                    break;
                case "end":
                    return false;
            }
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
        return true;
    }
    /**
     * CommandProcessor constructor
     * @param bookStore BookStore with which the Processor interacts
     */
    CommandProcessor(BookStore bookStore) {
        this.bookStore = bookStore;
    }
}


/**
 * BookStore class
 */
class BookStore {
    /**
     * Array of books
     */
    private ArrayList<Book> books;
    /**
     * Array of users
     */
    private ArrayList<User> users;
    /**
     * Array of subscribers to be notified
     */
    private ArrayList<User> subscribers;
    /**
     * Method that send a notifying message to all subscribers
     * @param message Message to be sent
     *
     */
    private void notifySubscribers(String message) {
        for (User user : subscribers) {
            user.getNotification(message);
        }
    }
    /**
     * Method that finds a Book by a name
     * @param bookName The name of a Book
     * @return Book instance found, or null otherwise
     */
    public Book getBook(String bookName) {
        for (Book book : this.books) {
            if (book.getTitle().equals(bookName)) {
                return book;
            }
        }
        return null;
    }
    /**
     * Method that finds a User by a name
     * @param userName The name of a User
     * @return User instance found, or null otherwise
     */
    public User getUser(String userName) {
        for (User user : this.users) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }
    /**
     * Method that finds a Subscriber by a name
     * @param userName The name of a Subscriber
     * @return User instance found, or null otherwise
     */
    public User getSubscriber(String userName) {
        for (User user : this.subscribers) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }
    /**
     * Method that creates a Book
     * @param title Title of a Book
     * @param author Author of a Book
     * @param price Price of a Book
     */
    public void createBook(String title, String author, String price) {
        if (getBook(title) != null) {
            throw new RuntimeException("Book already exists");

        }
        this.books.add(new Book(title, author, price));
    }
    /**
     * Method that creates a User
     * @param userType Type of User
     * @param userName Name of User
     */
    public void createUser(String userType, String userName) {
        if (getUser(userName) != null) {
            throw new RuntimeException("User already exists");
        }
        switch (userType) {
            case "standard":
                this.users.add(new StandardUser(userName));
                break;
            case "premium":
                this.users.add(new PremiumUser(userName));
                break;
            default:
                throw new RuntimeException("Type error");
        }
    }
    /**
     * Method that subscribes a User to notifications
     * @param userName Name of User
     */
    public void subscribe(String userName) {
        if (getSubscriber(userName) != null) {
            throw new RuntimeException("User already subscribed");
        }
        this.subscribers.add(getUser(userName));
    }
    /**
     * Method that unsubscribes a User to notifications
     * @param userName Name of User
     */
    public void unsubscribe(String userName) {
        if (getSubscriber(userName) == null) {
            throw new RuntimeException("User is not subscribed");
        }
        this.subscribers.remove(getUser((userName)));
    }
    /**
     * Method that updates a price of a Book
     * @param title Title of a Book
     * @param newPrice The new price
     */
    public void updatePrice(String title, String newPrice) {
        if (this.getBook(title) == null) {
            throw new RuntimeException("Book does not exist");
        }
        getBook(title).setPrice(newPrice);
        notifySubscribers("price update for " + title + " to " + newPrice);
    }
    /**
     * Method that makes User to read a Book
     * @param userName Name of User
     * @param title Title of Book
     */
    public void readBook(String userName, String title) {
        if (getUser(userName) == null) {
            throw new RuntimeException("User does not exist");
        }
        if (this.getBook(title) == null) {
            throw new RuntimeException("Book does not exist");
        }
        getUser(userName).read(getBook(title));
    }
    /**
     * Method that makes User to listen a Book
     * @param userName Name of User
     * @param title Title of Book
     */
    public void listenBook(String userName, String title) {
        if (getUser(userName) == null) {
            throw new RuntimeException("User does not exist");
        }
        if (this.getBook(title) == null) {
            throw new RuntimeException("Book does not exist");
        }
        getUser(userName).listen(getBook(title));
    }
    /**
     * BookStore constructor
     */
    BookStore() {
        this.books = new ArrayList<Book>();
        this.users = new ArrayList<User>();
        this.subscribers = new ArrayList<User>();
    }
}


/**
 * Book class
 */
class Book {
    /**
     * Title of Book
     */
    private String title;
    /**
     * Author of Book
     */
    private String author;
    /**
     * Price of Book
     */
    private String price;
    /**
     * Author getter
     * @return Author of Book
     */
    public String getAuthor() {
        return this.author;
    }
    /**
     * Price setter
     * @param price New price of Book
     */
    public void setPrice(String price) {
        this.price = price;
    }
    /**
     * Title getter
     * @return Title of Book
     */
    public String getTitle() {
        return this.title;
    }
    /**
     * Book constructor
     * @param title Title of Book
     * @param author Author of Book
     * @param price Price of Book
     */
    Book(String title, String author, String price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }
}


/**
 * User abstract class
 */
abstract class User {
    /**
     * Name of User
     */
    private final String userName;
    /**
     * Abstract method for listening a Book
     * @param book Book to be listened
     */
    abstract void listen(Book book);
    /**
     * Abstract method for reading a Book
     * @param book Book to be read
     */
    abstract void read(Book book);
    /**
     * Name getter
     * @return Name of User
     */
    public String getName() {
        return this.userName;
    }
    /**
     * Method that sends a notification to User
     * @param message Message of notification
     */
    public void getNotification(String message) {
        System.out.println(this.userName + " notified about " + message);
    }
    /**
     * User constructor
     * @param userName Name of User
     */
    User(String userName) {
        this.userName = userName;
    }
}


/**
 * Standard implementation of User
 */
class StandardUser extends User {
    /**
     * Method that makes User to read a Book
     * @param book Book to be read
     */
    public void read(Book book) {
        System.out.println(this.getName() + " reading " + book.getTitle() + " by " + book.getAuthor());
    }
    /**
     * Cannot listen Books. Throws an Exception
     * @param book Book to be listened
     */
    public void listen(Book book) {
        throw new RuntimeException("No access");
    }
    /**
     * StandardUser constructor
     * @param userName Name of User
     */
    StandardUser(String userName) {
        super(userName);
    }
}


/**
 * Premium implementation of User
 */
class PremiumUser extends User {
    /**
     * Method that makes User to listen a Book
     * @param book Book to be listened
     */
    public void listen(Book book) {
        System.out.println(this.getName() + " listening " + book.getTitle() + " by " + book.getAuthor());
    }
    /**
     * Method that makes User to read a Book
     * @param book Book to be read
     */
    public void read(Book book) {
        System.out.println(this.getName() + " reading " + book.getTitle() + " by " + book.getAuthor());
    }
    /**
     * PremiumUser constructor
     * @param userName Name of User
     */
    PremiumUser(String userName) {
        super(userName);
    }
}