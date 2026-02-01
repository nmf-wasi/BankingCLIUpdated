package console;

import models.Bank;
import models.BankAccount;

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
                4: Show Balance
                5: Show Transaction List
                6: Analytics
                7: Exit
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
        bank.deposit(accNumber, amount, message);
    }

    private void transferMoney(){
        System.out.print("Enter your account number: ");
        String accNumber = scanner.nextLine();
        Optional<BankAccount> bankAccount = bank.findByAccountNumber(accNumber);
        if (bankAccount.isEmpty()) {
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
        bank.transferMoney(accNumber,receiverAccount,amount,message);
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
