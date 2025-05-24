//Andrey Torgashinov
import java.util.*;
import java.util.Locale;


public class Main {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        Proxy bankingSystem = new Proxy(SimplifiedBankingSystem.getInstance());
        int n = s.nextInt();
        for (int i = 0; i < n; i++) {
            try {
                String command = s.next();
                if (Objects.equals(command, "Create")) {
                    s.next();
                    String type = s.next();
                    String accountName = s.next();
                    float initialDeposit = Float.parseFloat(s.next());
                    bankingSystem.createAccount(type, accountName, initialDeposit);
                } else if (Objects.equals(command, "Deposit")) {
                    String accountName = s.next();
                    float depositAmount = Float.parseFloat(s.next());
                    bankingSystem.deposit(accountName, depositAmount);
                } else if (Objects.equals(command, "Withdraw")) {
                    String accountName = s.next();
                    float withdrawalAmount = Float.parseFloat(s.next());
                    bankingSystem.withdraw(accountName, withdrawalAmount);
                } else if (Objects.equals(command, "Transfer")) {
                    String fromAccountName = s.next();
                    String toAccountName = s.next();
                    float transferAmount = Float.parseFloat(s.next());
                    bankingSystem.transfer(fromAccountName, toAccountName, transferAmount);
                } else if (Objects.equals(command, "View")) {
                    String accountName = s.next();
                    bankingSystem.viewAccount(accountName);
                } else if (Objects.equals(command, "Deactivate")) {
                    String accountName = s.next();
                    bankingSystem.deactivate(accountName);
                } else if (Objects.equals(command, "Activate")) {
                    String accountName = s.next();
                    bankingSystem.activate(accountName);
                }
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
            }
        }
    }
}


/**
 * Interface that describes behavior of all Banking Systems
 * */
interface BankingSystem {
    /**
     * Method that describes creation of a new Account
     * @param type The type of Account
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit of Account
     */
    void createAccount(String type, String accountName, float initialDeposit);
    /**
     * Method that describes deposit on Account
     * @param accountName The name of Account
     * @param depositAmount The amount of deposit
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    void deposit(String accountName, float depositAmount) throws NonExistentAccount;
    /**
     * Method that describes withdraw from Account
     * @param accountName The name of Account
     * @param withdrawalAmount The amount of withdraw
     * @return The amount of fee of the bank and the money that the client gets
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws InsufficientFundsException Exception is thrown if Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if Account is deactivated
     */
    Transaction withdraw(String accountName, float withdrawalAmount) throws NonExistentAccount, InsufficientFundsException, InactiveWithdrawalException;
    /**
     * Method that describes transfer from one Account to other
     * @param fromAccountName Account from which transfer is made
     * @param toAccountName Account to which transfer is made
     * @param transferAmount The amount of transfer
     * @return The amount of fee of the bank and the money that the client gets
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws InsufficientFundsException Exception is thrown if withdrawal Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if withdrawal Account is deactivated
     */
    Transaction transfer(String fromAccountName, String toAccountName, float transferAmount) throws NonExistentAccount, InsufficientFundsException, InactiveWithdrawalException;
    /**
     * Method that describes activation of Account
     * @param accountName The name of Account
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws ActivationException Exception is thrown if Account is already activated
     */
    void activate(String accountName) throws NonExistentAccount, ActivationException;
    /**
     * Method that describes deactivation of Account
     * @param accountName The name of Account
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws DeactivationException Exception is thrown if Account is already deactivated
     */
    void deactivate(String accountName) throws NonExistentAccount, DeactivationException;
    /**
     * Method that finds Account by its name
     * @param accountName The name of Account
     * @return Account found
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    Account getAccount(String accountName) throws NonExistentAccount;
    /**
     * Method that finds the set of operations made by Account
     * @param accountName The name of Account
     * @return The array of operations made
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    ArrayList<String> viewAccount(String accountName) throws NonExistentAccount;
}


/**
 * Proxy that provides remote access to BankingSystem database and handles console output
 */
class Proxy implements BankingSystem {
    /**
     * BankingSystem implementation instance
     */
    private final BankingSystem bankingSystem;
    /**
     * Method that returns String representation of a float number
     * @param number Method that returns String representation of a float number
     * @return String representation with three digits after point
     */
    private String printFloat(float number) {
        Locale.setDefault(Locale.US);
        return String.format("%.3f", number);
    }
    /**
     * Method that returns String representation of a float number
     * @param number Method that returns String representation of a float number
     * @return String representation with one digit after point
     */
    private String printPercent(float number) {
        Locale.setDefault(Locale.US);
        return String.format("%.1f", number);
    }
    /**
     * Method that finds Account by its name
     * @param accountName The name of Account
     * @return Account found
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    public Account getAccount(String accountName) throws NonExistentAccount {
        Account account = bankingSystem.getAccount(accountName);
        if (account == null) {
            throw new NonExistentAccount(accountName);
        }
        return account;
    }
    /**
     * Method that redirects creation of a new Account to a bankingSystem and outputs a message
     * @param type The type of Account
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit of Account
     */
    public void createAccount(String type, String accountName, float initialDeposit) {
        bankingSystem.createAccount(type, accountName, initialDeposit);
        System.out.printf("A new %s account created for %s with an initial balance of $%s.\n",
                type, accountName, printFloat(initialDeposit));
    }
    /**
     * Method that redirects deposit on Account to a bankingSystem and outputs a message
     * @param accountName The name of Account
     * @param depositAmount The amount of deposit
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    public void deposit(String accountName, float depositAmount) throws NonExistentAccount {
        Account account = getAccount(accountName);
        bankingSystem.deposit(accountName, depositAmount);
        System.out.printf("%s successfully deposited $%s. New Balance: $%s.\n",
                accountName, printFloat(depositAmount), printFloat(account.getBalance()));
    }
    /**
     * Method that redirects withdraw from Account to a bankingSystem and outputs a message
     * @param accountName The name of Account
     * @param withdrawalAmount The amount of withdraw
     * @return The amount of fee of the bank and the money that the client gets
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws InsufficientFundsException Exception is thrown if Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if Account is deactivated
     */
    public Transaction withdraw(String accountName, float withdrawalAmount) throws NonExistentAccount, InsufficientFundsException, InactiveWithdrawalException {
        Account account = getAccount(accountName);
        Transaction transaction = bankingSystem.withdraw(accountName, withdrawalAmount);
        System.out.printf("%s successfully withdrew $%s. New Balance: $%s. Transaction Fee: $%s (%s",
                accountName, printFloat(transaction.clientMoney), printFloat(account.getBalance()),
                printFloat(transaction.bankFee), printPercent(account.getFee()));
        System.out.print("%) in the system.\n");
        return transaction;
    }
    /**
     * Method that redirects transfer from one Account to other to a bankingSystem and outputs a message
     * @param fromAccountName Account from which transfer is made
     * @param toAccountName Account to which transfer is made
     * @param transferAmount The amount of transfer
     * @return The amount of fee of the bank and the money that the client gets
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws InsufficientFundsException Exception is thrown if withdrawal Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if withdrawal Account is deactivated
     */
    public Transaction transfer(String fromAccountName, String toAccountName, float transferAmount) throws NonExistentAccount, InsufficientFundsException, InactiveWithdrawalException {
        Account from = getAccount(fromAccountName);
        getAccount(toAccountName);
        Transaction transaction = bankingSystem.transfer(fromAccountName, toAccountName, transferAmount);
        System.out.printf("%s successfully transferred $%s to %s. New Balance: $%s. Transaction Fee: $%s (%s",
                fromAccountName, printFloat(transaction.clientMoney), toAccountName, printFloat(from.getBalance()),
                printFloat(transaction.bankFee), printPercent(from.getFee()));
        System.out.print("%) in the system.\n");
        return transaction;
    }
    /**
     * Method that redirects activation of Account to a bankingSystem and outputs a message
     * @param accountName The name of Account
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws ActivationException Exception is thrown if Account is already activated
     */
    public void activate(String accountName) throws NonExistentAccount, ActivationException {
        Account account = getAccount(accountName);
        bankingSystem.activate(accountName);
        System.out.printf("%s's account is now activated.\n", account.getAccountName());
    }
    /**
     * Method that redirects deactivation of Account to a bankingSystem and outputs a message
     * @param accountName The name of Account
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     * @throws DeactivationException Exception is thrown if Account is already deactivated
     */
    public void deactivate(String accountName) throws NonExistentAccount, DeactivationException {
        Account account = getAccount(accountName);
        bankingSystem.deactivate(accountName);
        System.out.printf("%s's account is now deactivated.\n", account.getAccountName());
    }
    /**
     * Method that gets the array of operations made by Account and outputs them to console
     * @param accountName The name of Account
     * @return The array of operations made
     * @throws NonExistentAccount Exception is thrown if Account does not exist
     */
    public ArrayList<String> viewAccount(String accountName) throws NonExistentAccount {
        Account account = getAccount(accountName);
        ArrayList<String> transactions = bankingSystem.viewAccount(accountName);
        System.out.printf("%s's Account: Type: %s, Balance: $%s, State: %s, Transactions: [",
                account.getAccountName(), account.getType(), printFloat(account.getBalance()), account.getState());
        for (int i = 0; i < transactions.size(); i++) {
            if (i != transactions.size() - 1) {
                System.out.printf("%s, ", transactions.get(i));
            }
            else {
                System.out.printf("%s].\n", transactions.get(i));
            }
        }
        return transactions;
    }
    /**
     * Proxy constructor
     * @param bankingSystem BankingSystem implementation
     */
    Proxy(BankingSystem bankingSystem) {
        this.bankingSystem = bankingSystem;
    }
}


/**
 * BankingSystem implementation
 */
class SimplifiedBankingSystem implements BankingSystem {
    /**
     * Map that connects Accounts with their names
     */
    private final Map<String, Account> accounts;
    /**
     * Map that contains actions made by Accounts
     */
    private final Map<String, ArrayList<String>> log;
    /**
     * Method that returns String representation of a float number
     * @param number Method that returns String representation of a float number
     * @return String representation with three digits after point
     */
    private String toFloat(float number) {
        Locale.setDefault(Locale.US);
        return String.format("%.3f", number);
    }
    /**
     * Singleton instance of BankingSystem
     */
    private static SimplifiedBankingSystem instance;
    /**
     * Method that creates a new Account
     * @param type The type of Account
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit of Account
     */
    public void createAccount(String type, String accountName, float initialDeposit) {
        AccountDirector accountDirector = new AccountDirector();
        Account account = accountDirector.build(type, accountName, initialDeposit);
        accounts.put(accountName, account);
        log.put(accountName, new ArrayList<>());
        log.get(accountName).add("Initial Deposit $" + toFloat(initialDeposit));
    }
    /**
     * Method that makes deposit on Account
     * @param accountName The name of Account
     * @param depositAmount The amount of deposit
     */
    public void deposit(String accountName, float depositAmount) {
        Account account = getAccount(accountName);
        account.deposit(depositAmount);
        log.get(accountName).add("Deposit $" + toFloat(depositAmount));
    }
    /**
     * Method that makes withdraw from Account
     * @param accountName The name of Account
     * @param withdrawalAmount The amount of withdraw
     * @return The amount of fee of the bank and the money that the client gets
     * @throws InsufficientFundsException Exception is thrown if Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if Account is deactivated
     */
    public Transaction withdraw(String accountName, float withdrawalAmount) throws InsufficientFundsException, InactiveWithdrawalException {
        Account account = getAccount(accountName);
        Transaction transaction = account.withdraw(withdrawalAmount);
        log.get(accountName).add("Withdrawal $" + toFloat(withdrawalAmount));
        return transaction;
    }
    /**
     * Method that makes transfer from one Account to other
     * @param fromAccountName Account from which transfer is made
     * @param toAccountName Account to which transfer is made
     * @param transferAmount The amount of transfer
     * @return The amount of fee of the bank and the money that the client gets
     * @throws InsufficientFundsException Exception is thrown if withdrawal Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if withdrawal Account is deactivated
     */
    public Transaction transfer(String fromAccountName, String toAccountName, float transferAmount) throws InsufficientFundsException, InactiveWithdrawalException {
        Account from = getAccount(fromAccountName);
        Account to = getAccount(toAccountName);
        Transaction transaction = from.withdraw(transferAmount);
        to.deposit(transaction.clientMoney);
        log.get(fromAccountName).add("Transfer $" + toFloat(transferAmount));
        return transaction;
    }
    /**
     * Method that describes activation of Account
     * @param accountName The name of Account
     * @throws ActivationException Exception is thrown if Account is already activated
     */
    public void activate(String accountName) throws ActivationException {
        Account account = getAccount(accountName);
        account.activate();
    }
    /**
     * Method that deactivates Account
     * @param accountName The name of Account
     * @throws DeactivationException Exception is thrown if Account is already deactivated
     */
    public void deactivate(String accountName) throws DeactivationException {
        Account account = getAccount(accountName);
        account.deactivate();
    }
    /**
     * Method that returns the array of operations made by Account
     * @param accountName The name of Account
     * @return The array of operations made
     */
    public ArrayList<String> viewAccount(String accountName) {
        return log.get(accountName);
    }
    /**
     * Method that returns singleton instance or initializes it if the instance has not been created yet
     * @return Singleton instance
     */
    public static SimplifiedBankingSystem getInstance() {
        if (instance == null) {
            instance = new SimplifiedBankingSystem();
        }
        return instance;
    }
    /**
     * Method that finds Account by its name
     * @param accountName The name of Account
     * @return Account found
     */
    public Account getAccount(String accountName) {
        return this.accounts.get(accountName);
    }
    /**
     * Private SimplifiedBankingSystem constructor
     */
    private SimplifiedBankingSystem() {
        this.accounts = new HashMap<>();
        this.log = new HashMap<>();
    }
}


/**
 * State abstract class
 */
abstract class State {
    /**
     * Account that owns the State
     */
    protected Account account;
    /**
     * Method that describes activation of Account
     * @throws ActivationException Exception is thrown if Account is already activated
     */
    abstract public void activate() throws ActivationException;
    /**
     * Method that describes deactivation of Account
     * @throws DeactivationException Exception is thrown if Account is already deactivated
     */
    abstract public void deactivate() throws DeactivationException;
    /**
     * Method that describes withdraw operation over Account
     * @param withdrawalAmount The amount of withdraw
     * @return The number of money obtained by a client
     * @throws InsufficientFundsException Exception is thrown if withdrawal Account has insufficient money
     */
    abstract public float withdraw(float withdrawalAmount) throws InactiveWithdrawalException, InsufficientFundsException;
    /**
     * Method that describes deposit operation over Account
     * @param depositAmount The amount of deposit
     */
    abstract public void deposit(float depositAmount);

    /**
     * State constructor
     * @param account Account that owns the State
     */
    State(Account account) {
        this.account = account;
    }
}


/**
 * Active State
 */
class Active extends State {
    /**
     * Active State can not be activated
     * @throws ActivationException Throws exception
     */
    public void activate() throws ActivationException {
        throw new ActivationException(account.getAccountName());
    }
    /**
     * Changes state of Account to Deactivated
     */
    public void deactivate() {
        account.changeState(new Inactive(account));
    }
    /**
     * Method that implements withdraw operation over Account
     * @param withdrawalAmount The amount of withdraw
     * @return The number of money obtained by a client
     * @throws InsufficientFundsException Exception is thrown if withdrawal Account has insufficient money
     */
    public float withdraw(float withdrawalAmount) throws InsufficientFundsException {
        if (account.getBalance() < withdrawalAmount) {
            throw new InsufficientFundsException(account.getAccountName());
        }
        account.changeBalance(-withdrawalAmount);
        return withdrawalAmount * (1 - account.getFee() / 100);
    }
    /**
     * Method that implements deposit operation over Account
     * @param depositAmount The amount of deposit
     */
    public void deposit(float depositAmount) {
        account.changeBalance(depositAmount);
    }
    /**
     * Returns string representation of Active State
     * @return String representation
     */
    public String toString() {
        return "Active";
    }
    /**
     * Active State constructor
     * @param account Account that owns the State
     */
    Active(Account account) {
        super(account);
    }
}


/**
 * Inactive State
 */
class Inactive extends State {
    /**
     * Changes state of Account to Activated
     */
    public void activate() {
        account.changeState(new Active(account));
    }
    /**
     * Inactive State can not be deactivated
     * @throws DeactivationException Throws exception
     */
    public void deactivate() throws DeactivationException {
        throw new DeactivationException(account.getAccountName());
    }
    /**
     * Withdraw from Inactive Account is prohibited
     * @param withdrawalAmount The amount of withdraw
     * @throws InactiveWithdrawalException Throws exception
     */
    public float withdraw(float withdrawalAmount) throws InactiveWithdrawalException {
        throw new InactiveWithdrawalException(account.getAccountName());
    }
    /**
     * Method that implements deposit operation over Account
     * @param depositAmount The amount of deposit
     */
    public void deposit(float depositAmount) {
        account.changeBalance(depositAmount);
    }
    /**
     * Returns string representation of Inactive State
     * @return String representation
     */
    public String toString() {
        return "Inactive";
    }
    /**
     * Inactive State constructor
     * @param account Account that owns the State
     */
    Inactive(Account account) {
        super(account);
    }
}


/**
 * AccountBuilder interface
 */
interface Builder {
    /**
     * Method that describes instantiation of Account
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit
     */
    void createAccount(String accountName, float initialDeposit);
    /**
     * Method that describes Accounts' fee initialization
     */
    void setFee();
    /**
     * Method that describes Accounts' type initialization
     */
    void setType();
    /**
     * Method that describes the method returning resulting Account
     * @return Created Account
     */
    Account getResult();
}


/**
 * Builder for Savings Account
 */
class SavingsAccountBuilder implements Builder {
    /**
     * Resulting Account
     */
    private Account result;
    /**
     * Account instantiation
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit
     */
    public void createAccount(String accountName, float initialDeposit) {
        this.result = new Account(accountName, initialDeposit);
    }
    /**
     * Method that sets corresponding fee
     */
    public void setFee() {
        result.setFee(1.5f);
    }
    /**
     * Method that sets corresponding type
     */
    public void setType() {
        result.setType("Savings");
    }
    /**
     * Method that returns resulting Account
     * @return Created Account
     */
    public Account getResult() {
        return this.result;
    }
}


/**
 * Builder for Checking Account
 */
class CheckingAccountBuilder implements Builder {
    /**
     * Resulting Account
     */
    private Account result;
    /**
     * Account instantiation
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit
     */
    public void createAccount(String accountName, float initialDeposit) {
        this.result = new Account(accountName, initialDeposit);
    }
    /**
     * Method that sets corresponding fee
     */
    public void setFee() {
        result.setFee(2f);
    }
    /**
     * Method that sets corresponding type
     */
    public void setType() {
        result.setType("Checking");
    }
    /**
     * Method that returns resulting Account
     * @return Created Account
     */
    public Account getResult() {
        return this.result;
    }
}


/**
 * Builder for Business Account
 */
class BusinessAccountBuilder implements Builder {
    /**
     * Resulting Account
     */
    private Account result;
    /**
     * Account instantiation
     * @param accountName The name of Account
     * @param initialDeposit The initial deposit
     */
    public void createAccount(String accountName, float initialDeposit) {
        this.result = new Account(accountName, initialDeposit);
    }
    /**
     * Method that sets corresponding fee
     */
    public void setFee() {
        result.setFee(2.5f);
    }
    /**
     * Method that sets corresponding type
     */
    public void setType() {
        result.setType("Business");
    }
    /**
     * Method that returns resulting Account
     * @return Created Account
     */
    public Account getResult() {
        return this.result;
    }
}


/**
 * Director for Account Builders
 */
class AccountDirector {
    /**
     * Account Builder instance
     */
    private Builder builder;
    /**
     * Method that builds specific Account type
     * @param type Type of Account
     * @param accountName Name of Account
     * @param initialDeposit Initial deposit
     * @return Account built
     */
    Account build(String type, String accountName, float initialDeposit) {
        if (Objects.equals(type, "Savings")) {
            this.builder = new SavingsAccountBuilder();
        }
        else if (Objects.equals(type, "Checking")) {
            this.builder = new CheckingAccountBuilder();
        }
        else if (Objects.equals(type, "Business")) {
            this.builder = new BusinessAccountBuilder();
        }
        this.builder.createAccount(accountName, initialDeposit);
        this.builder.setFee();
        this.builder.setType();
        return this.builder.getResult();
    }
}


/**
 * Account class
 */
class Account {
    /**
     * State of Account
     */
    private State state;
    /**
     * Fee taken for this account operations
     */
    private float fee;
    /**
     * Name of Account
     */
    private final String accountName;
    /**
     * Balance of Account
     */
    private float balance;
    /**
     * Type of Account
     */
    String type;
    /**
     * Method that activates Account
     * @throws ActivationException Exception is thrown if Account is already activated
     */
    public void activate() throws ActivationException {
        state.activate();
    }
    /**
     * Account Fee setter
     * @param fee Fee to be set
     */
    public void setFee(float fee) {
        this.fee = fee;
    }
    /**
     * Method that deactivates Account
     * @throws DeactivationException Exception is thrown if Account is already deactivated
     */
    public void deactivate() throws DeactivationException {
        state.deactivate();
    }
    /**
     * Method that makes withdraw from Account
     * @param withdrawalAmount Amount of withdraw
     * @return The amount of fee of the bank and the money that the client gets
     * @throws InsufficientFundsException Exception is thrown if Account has insufficient money
     * @throws InactiveWithdrawalException Exception thrown if Account is deactivated
     */
    public Transaction withdraw(float withdrawalAmount) throws InsufficientFundsException, InactiveWithdrawalException {
        float clientMoney = this.state.withdraw(withdrawalAmount);
        return new Transaction(withdrawalAmount - clientMoney, clientMoney);
    }
    /**
     * Method that makes deposit on Account
     * @param depositAmount Amount of deposit
     */
    public void deposit(float depositAmount) {
        this.state.deposit(depositAmount);
    }
    /**
     * Account name getter
     * @return Name of Account
     */
    public String getAccountName() {
        return this.accountName;
    }
    /**
     * Account state setter
     * @param state State to be set
     */
    public void changeState(State state) {
        this.state = state;
    }
    /**
     * Method that changes balance of Account
     * @param value Difference to be applied
     */
    public void changeBalance(float value) {
        this.balance += value;
    }
    /**
     * Fee getter
     * @return Fee of Account
     */
    public float getFee() {
        return this.fee;
    }
    /**
     * Balance getter
     * @return Balance of Account
     */
    public float getBalance() {
        return this.balance;
    }
    /**
     * State getter
     * @return String representation of current Account state
     */
    public String getState() {
        return this.state.toString();
    }
    /**
     * Type getter
     * @return Type of Account
     */
    public String getType() {
        return this.type;
    }
    /**
     * Type setter
     * @param type Type of Account to be set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * Account constructor
     * @param accountName Name of Account
     * @param balance Initial balance of Account
     */
    Account(String accountName, float balance) {
        this.state = new Active(this);
        this.accountName = accountName;
        this.balance = balance;
    }
}


/**
 * Class for withdrawal transactions
 */
class Transaction {
    /**
     * Fee taken by bank
     */
    float bankFee;
    /**
     * Money that client obtains
     */
    float clientMoney;

    /**
     * Transaction constructor
     * @param bankFee Fee taken by bank
     * @param clientMoney Money that client obtains
     */
    Transaction(float bankFee, float clientMoney) {
        this.bankFee = bankFee;
        this.clientMoney = clientMoney;
    }
}


/**
 * Exception is thrown if Account does not exist
 */
class NonExistentAccount extends Exception {
    NonExistentAccount(String account) {
        super("Error: Account " + account + " does not exist.");
    }
}


/**
 * Exception thrown if Account is deactivated
 */
class InactiveWithdrawalException extends Exception {
    InactiveWithdrawalException(String account) {
        super("Error: Account " + account + " is inactive.");
    }
}


/**
 * Exception is thrown if Account has insufficient money
 */
class InsufficientFundsException extends Exception {
    InsufficientFundsException(String account) {
        super("Error: Insufficient funds for " + account + ".");
    }
}


/**
 * Exception is thrown if Account is already activated
 */
class ActivationException extends Exception {
    ActivationException(String account) {
        super("Error: Account " + account + " is already activated.");
    }
}


/**
 * Exception is thrown if Account is already deactivated
 */
class DeactivationException extends Exception {
    DeactivationException(String account) {
        super("Error: Account " + account + " is already deactivated.");
    }
}
