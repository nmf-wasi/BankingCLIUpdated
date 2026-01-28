package models;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

import static transactions.TransactionStore.loadTransactions;
import static utility.CsvUtil.bankAccountCSVtoText;
import static utility.IDGenerator.setAccountNumber;
import static utility.WritingCsv.appendToFile;

public class Bank {
    ArrayList<BankAccount>accounts=new ArrayList<>();

    public BankAccount createAccount(String customerName,
                                     String phoneNumber,
                                     String customerEmail,
                                     BigDecimal initialDeposit){
        String accountNumber=setAccountNumber();
        if(findAccount(accountNumber).isPresent()){
            System.out.println("Account already exists!");
            return null;
        }
        BankAccount bankAccount=new BankAccount(customerName,accountNumber,phoneNumber,customerEmail, initialDeposit);
        accounts.add(bankAccount);
        appendToFile(bankAccount);
        return bankAccount;
    }

    public void loadAccounts() {
        accounts.clear();
        Path filePath = Paths.get("src/repo/bankAccounts.csv");

        try { // Ensure parent directory exists
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            System.out.println("Could not create data directory");
            return;
        }

        if (!Files.exists(filePath)) { // file doesn't exist, nothing to load
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;

            while ((line = reader.readLine()) != null) {
                bankAccountCSVtoText(line).ifPresent(account -> {
                    accounts.add(account);
                    loadTransactions(account);
                });
            }

        } catch (IOException e) {
            System.out.println("Couldn't load accounts from csv!");
        }
    }

    public Optional<BankAccount> findAccount(String accountNumber){
        return accounts.stream()
                .filter(acc-> acc.getAccountNumber()
                        .equals(accountNumber)
                ).findFirst();
    }
}
