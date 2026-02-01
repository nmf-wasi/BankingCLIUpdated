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
                case 4 -> showBalance();
                case 5 -> showTransactions();
                case 6 -> analyticsMenu();
                case 7 -> {
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
        System.out.println("Account Number: ");
        String accNum = scanner.nextLine();
        Optional<BankAccount> account = bank.findByAccountNumber(accNum);
        if (account.isEmpty()) {
            System.out.println("Account not found!");
            return;
        }

        BigDecimal amount;
        System.out.println("Deposit amount: ");
        if (!scanner.hasNextDouble()) {
            System.out.println("Invalid amount!");
            scanner.nextLine();
            return;
        }
        do {
            System.out.print("Deposit amount: ");
            amount = scanner.nextBigDecimal();
            if (!validAmount(amount)) System.out.println("INVALID AMOUNT! Try again...");
        } while (!validAmount(amount));

        String message;
        String choice;
        System.out.print("Would you like to add a message? (Y/N): ");
        choice = scanner.nextLine();
        char choiceChar = Character.toUpperCase(choice.charAt(0));
        if (choiceChar == 'Y') {
            System.out.print("Enter message: ");
            message = scanner.nextLine();
        } else {
            message = "";
        }
        bank.deposit(accNum,amount,message);

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
