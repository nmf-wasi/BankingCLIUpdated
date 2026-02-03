package console;

import models.Bank;
import models.BankAccount;
import transactions.TransactionAnalytics;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Scanner;

import static utility.Validation.*;

public class UI {
    private Bank bank;
    private Scanner scanner;

    public UI(Bank bank, Scanner scanner) {
        this.bank = bank;
        this.scanner = scanner;
    }

    public void start() {
        while (true) {
            printMenu();
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
                return;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transferMoney();
                case 5 -> showBalance();
                case 6 -> showTransactions();
                case 7 -> analyticsMenu();
                case 8 -> {
                    exit();
                    return;
                }
                default -> System.out.println("Invalid Choice!");
            }
        }
    }

    void printMenu() {
        System.out.println("""
                Select an option to continue:
                1: Create an Account
                2: Deposit
                3: Withdraw
                4: Transfer Money
                5: Show Balance
                6: Show Transaction List
                7: Analytics
                8: Exit
                """);
    }

    void createAccount() {
        String customerName;
        System.out.print("Enter the name of the customer: ");
        customerName = scanner.nextLine();
        String phoneNumber;
        do {
            System.out.print("Enter the phone number of the customer: ");
            phoneNumber = scanner.nextLine();
            if (!validPhoneNumber(phoneNumber)) System.out.println("INVALID PHONE NUMBER! Try again...");
        } while (!validPhoneNumber(phoneNumber));
        String email;

        do {
            System.out.print("Enter the email of the customer: ");
            email = scanner.nextLine();
            if (!validEmail(email)) System.out.println("INVALID EMAIL! Try again...");
        } while (!validEmail(email));

        System.out.println("Initial deposit: ");
        if (!scanner.hasNextBigDecimal()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // clear invalid input
            return;
        }
        BigDecimal initialDeposit = scanner.nextBigDecimal();
        scanner.nextLine();
        try {
            BankAccount account = bank.createAccount(customerName, phoneNumber, email, initialDeposit);
            if (account != null) {
                loading("Creating new account");
                System.out.println("Account created! Account Number: " + account.getAccountNumber());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deposit() {
        System.out.print("Enter your account number: ");
        String accNum = scanner.nextLine();
        Optional<BankAccount> account = bank.findByAccountNumber(accNum);
        if (account.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        BigDecimal amount=getValidDecimal("Enter the amount of money you want to deposit: ");
        String message=getOptionalMessage();
        bank.deposit(accNum, amount, message);
    }

    private void withdraw() {
        System.out.print("Enter your account number: ");
        String accNumber = scanner.nextLine();
        Optional<BankAccount> bankAccount = bank.findByAccountNumber(accNumber);
        if (bankAccount.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        BigDecimal amount=getValidDecimal("Enter the amount of money you want to withdraw: ");
        String message=getOptionalMessage();
        bank.withdraw(accNumber, amount, message);
    }

    private void transferMoney(){
        System.out.print("Enter your account number: ");
        String senderAccNumber = scanner.nextLine();
        Optional<BankAccount> senderAccount = bank.findByAccountNumber(senderAccNumber);
        if (senderAccount.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        System.out.print("Enter receiver account number: ");
        String receiverAccNumber = scanner.nextLine();
        Optional<BankAccount> receiverAccount = bank.findByAccountNumber(receiverAccNumber);
        if (receiverAccount.isEmpty()) {
            System.out.println("Receiver account not found!");
            return;
        }
        BigDecimal amount=getValidDecimal("Enter deposit amount: ");
        String message=getOptionalMessage();
        bank.transferMoney(senderAccount.get(),receiverAccount.get(),amount,message);
    }

    private void showBalance() {
        System.out.println("Account Number: ");
        String accNum = scanner.nextLine();
        Optional<BankAccount> account = bank.findByAccountNumber(accNum);
        if (account.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        System.out.println("Current Balance: " + Bank.getBalance(account.get()));
    }

    private void showTransactions() {
        System.out.println("Account Number: ");
        String accNum = scanner.nextLine();
        Optional<BankAccount> account = bank.findByAccountNumber(accNum);
        if (account.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        account.get().showTransactions();
    }

    private void analyticsMenu() {
        System.out.println("Account Number: ");
        String accountNumber = scanner.nextLine();

        Optional<BankAccount> bankAccount = bank.findByAccountNumber(accountNumber);
        if (bankAccount.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }
        TransactionAnalytics analytics = bankAccount.get().getAnalytics();
        while (true) {
            System.out.println("""
                    Analytics Options:
                    1: Total Deposited
                    2: Total Withdrawn
                    3: Failed Transactions
                    4: Recent Transactions
                    5: Transactions Per Day
                    6: Group By Type
                    7: Back to Main Menu
                    """);
            if (!scanner.hasNextInt()) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
                return;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1 -> {
                    loading("Loading total deposit");
                    System.out.printf("Total Deposit: $%.2f\n", analytics.totalDeposited());
                }
                case 2 -> {
                    loading("Loading total withdrawn");
                    System.out.println("Total Withdrawn: $" + analytics.totalWithdrawn());
                }
                case 3 -> {
                    loading("Printing Failed Transactions");
                    analytics.printFailedTransactions();
                }
                case 4 -> {
                    loading("Printing Recent Transactions");
                    analytics.recentTransactions();
                }
                case 5 -> {
                    loading("Printing Transactions Per day");
                    analytics.transactionsPerDay()
                            .forEach((date, txs) ->
                                    System.out.println(date + " -> " + txs.size() + " transactions"));
                }
                case 6 -> {
                    loading("Printing transactions in grouped by transaction type view");
                    analytics.groupByType()
                            .forEach((type, transaction) ->
                                    System.out.println(type + " -> " + transaction));
                }
                case 7 -> {
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void exit() {
        System.out.println("Exiting...");
    }

    private BigDecimal getValidDecimal(String prompt){
        BigDecimal amount;
        while(true){
            System.out.println(prompt);
            if(!scanner.hasNextBigDecimal()){
                System.out.println("INVALID INPUT! PLEASE ENTER A NUMBER!");
                scanner.nextLine();
                continue;
            }
            amount=scanner.nextBigDecimal();
            scanner.nextLine();
            if(validAmount(amount)) return amount;
            System.out.println("Amount must be greater than $0!");
        }
    }

    private  String getOptionalMessage(){
        System.out.print("Add a message? (Y/N): ");
        String choice =scanner.nextLine().trim();
        if(choice.isEmpty()|| Character.toUpperCase(choice.charAt(0))=='Y'){
            System.out.println("Enter message: ");
            return scanner.nextLine();
        }
        return "";
    }

    private void loading(String message) {
        System.out.print(message);
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println();
    }
}


//TODO ✅ Implement proper ID generation in setAccountNumber() - generate unique account numbers (UUID or incremental)
//
//TODO Medium Priority (Important):
//
//TODO ✅ Add transaction validation - Check for duplicate transaction IDs when loading
//TODO ✅ Implement CSV parsing for transactions - Create a method to convert CSV string back to Transaction object
//TODO ✅ Add error handling for file operations throughout the application
//TODO ✅ Transaction filtering - Add methods to filter transactions by date, type, or status
//TODO ✅ Account search improvements - Add methods to search by customer name, email, or phone
//
//TODO Low Priority (Nice to Have):
//
//TODO         ✅ Add transaction history display options - Filter by date range, type, etc.
// TODO        ✅ Implement analytics features:
//
        //TODO Total deposits/withdrawals
        //TODO Average transaction amount
        //TODO Most frequent transaction type
        //TODO Monthly summaries
//
//
//TODO ✅ Add input validation improvements - More robust error messages
//TODO ✅ Add account deletion feature
//TODO ✅ Add transaction reversal/cancellation feature
//TODO ✅ Implement password/PIN security for accounts
//TODO ✅ Add transaction limits (daily/monthly)
//TODO ✅ Better formatting for transaction display (use tables)
//TODO ✅ Add logging for debugging purposes
//
//TODO Code Quality Improvements:
//
// TODO        ✅ Consistent error handling - Standardize how errors are displayed to users
//TODO ✅ Extract magic numbers - Move hardcoded values like minimum balance to constants
//TODO ✅ Add JavaDoc comments to public methods
//TODO ✅ Unit tests - Add JUnit tests for Bank and BankAccount classes
//TODO ✅ Separate concerns - Consider moving CSV operations to a dedicated repository class