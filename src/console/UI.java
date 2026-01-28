package console;

import models.Bank;
import models.BankAccount;

import java.math.BigDecimal;
import java.util.Scanner;

import static utility.Validation.validEmail;
import static utility.Validation.validPhoneNumber;

public class UI {
    private Bank bank;
    private Scanner scanner;

    public UI(Bank bank, Scanner scanner){
        this.bank=bank;
        this.scanner=scanner;
    }

    public void start(){
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

    void printMenu(){
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

    void createAccount(){
        String customerName;
        System.out.print("Enter the name of the customer: ");
        customerName = scanner.nextLine();
        String phoneNumber;
        do {
            System.out.print("Enter the phone number of the customer: ");
            phoneNumber = scanner.nextLine();
            if(!validPhoneNumber(phoneNumber)) System.out.println("INVALID PHONE NUMBER! Try again...");
        }while (!validPhoneNumber(phoneNumber));
        String email;

        do {
            System.out.print("Enter the email of the customer: ");
            email = scanner.nextLine();
            if(!validEmail(email)) System.out.println("INVALID EMAIL! Try again...");
        }while (!validEmail(email));

        System.out.println("Initial deposit: ");
        if (!scanner.hasNextBigDecimal()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine(); // clear invalid input
            return;
        }
        BigDecimal initialDeposit = scanner.nextBigDecimal();
        scanner.nextLine();
        try {
            BankAccount account = bank.createAccount(customerName,phoneNumber,email, initialDeposit);
            if (account != null) {
                loading("Creating new account");
                System.out.println("Account created! Account Number: " + account.getAccountNumber());
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
