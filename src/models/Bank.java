package models;

import transactions.Transaction;
import transactions.TransactionStatus;
import transactions.TransactionStore;
import transactions.TransactionType;

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
import static utility.Validation.validAmount;
import static utility.WritingCsv.appendToFile;

public class Bank {
    ArrayList<BankAccount> accounts = new ArrayList<>();
    private final BigDecimal minimumBalance = BigDecimal.valueOf(100);

    public BankAccount createAccount(String customerName,
                                     String phoneNumber,
                                     String customerEmail,
                                     BigDecimal initialDeposit) {
        String accountNumber = setAccountNumber();
        if (findByAccountNumber(accountNumber).isPresent()) {
            System.out.println("Account already exists!");
            return null;
        }
        BankAccount bankAccount = new BankAccount(customerName, accountNumber, phoneNumber, customerEmail, initialDeposit);
        accounts.add(bankAccount);
        appendToFile(bankAccount);
        return bankAccount;
    }


    public boolean deposit(String accountNumber,
                           BigDecimal amount,
                           String message) {

        if (!validAmount(amount)) {
            System.out.println("You have to deposit more than $0!");
            return false;
        }
        Optional<BankAccount> bankAccount = findByAccountNumber(accountNumber);
        if (bankAccount.isEmpty()) {
            System.out.println("Couldn't find account: " + accountNumber);
            return false;
        }
        BankAccount account = bankAccount.get(); //get the acc obj from the Optional
        Transaction transaction = new Transaction(
                accountNumber, amount,
                TransactionType.DEPOSIT,
                TransactionStatus.SUCCESS,
                message);

        account.addTransaction(transaction);
        TransactionStore.appendTransaction(account, transaction);
        System.out.println("Deposited " + amount + " to account: " + accountNumber + "!");
        System.out.println("New balance: " + getBalance(account));
        return true;
    }

    public boolean withdraw(String accountNumber,
                            BigDecimal amount,
                            String message) {
        if (!validAmount(amount)) {
            System.out.println("You can't withdraw less than $0!");
            return false;
        }
        Optional<BankAccount> bankAccount = findByAccountNumber(accountNumber);
        if (bankAccount.isEmpty()) {
            System.out.println("Couldn't find account: " + accountNumber);
            return false;
        }
        BankAccount account = bankAccount.get();
        BigDecimal availableBalance = getBalance(account).subtract(account.getMinimumBalance());
        if (availableBalance.compareTo(amount) < 0) {
            System.out.println("Not enough balance!");
            return false;
        }
        Transaction transaction = new Transaction(accountNumber, amount,
                TransactionType.WITHDRAW,
                TransactionStatus.SUCCESS,
                message);
        account.addTransaction(transaction);
        TransactionStore.appendTransaction(account, transaction);
        System.out.println("Withdrew " + amount + " from account: " + accountNumber + "!");
        System.out.println("New balance: $" + getBalance(account));
        return true;
    }

    public boolean transferMoney(BankAccount senderAccount,
                                 BankAccount receiverAccount,
                                 BigDecimal amount, String message) {
        boolean success=true;
        // deposit on  receiver acc withdraw from sender acc, jobs done!
        BigDecimal senderBalance = getBalance(senderAccount);
        TransactionStatus transactionStatus = TransactionStatus.SUCCESS;
        if ((senderBalance.add(senderAccount.getMinimumBalance().negate())).compareTo(amount) < 0) {
            transactionStatus = TransactionStatus.FAILURE;
            System.out.println("Insufficient balance in sender account!");
            success=false;
        }
        Transaction transaction =
                new Transaction(senderAccount.getAccountNumber(),
                        receiverAccount.getAccountNumber(),
                        amount,
                        TransactionType.TRANSFER_OUT,
                        transactionStatus,
                        message);
        senderAccount.addTransaction(transaction);
        if (transactionStatus != TransactionStatus.FAILURE) {
            Transaction receiverTransaction = new Transaction(
                    receiverAccount.getAccountNumber(),
                    senderAccount.getAccountNumber(),
                    amount,
                    TransactionType.TRANSFER_IN,
                    transactionStatus, message);
            receiverAccount.addTransaction(receiverTransaction);
            TransactionStore.appendTransaction(receiverAccount, receiverTransaction);
            System.out.println("✓ Transfer successful!");
            System.out.println("  From: " + senderAccount.getAccountNumber() + " (New balance: $" + getBalance(senderAccount) + ")");
            System.out.println("  To: " + receiverAccount.getAccountNumber() + " (New balance: $" + getBalance(receiverAccount) + ")");
        }
        TransactionStore.appendTransaction(senderAccount, transaction);
        return success;

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


    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return accounts.stream()
                .filter(acc -> acc.getAccountNumber()
                        .equals(accountNumber)
                ).findFirst();
    }


    public static BigDecimal getBalance(BankAccount bankAccount) {
        return bankAccount.getTransactions().stream()
                .filter(transaction -> transaction.getStatus() == TransactionStatus.SUCCESS)
                .map(transaction ->
                {
                    switch (transaction.getType()) {
                        case DEPOSIT:
                        case TRANSFER_IN:
                            return transaction.getAmount();
                        case WITHDRAW:
                        case TRANSFER_OUT:
                            return transaction.getAmount().negate();
                        default:
                            return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
