package models;

import transactions.Transaction;
import transactions.TransactionStatus;
import transactions.TransactionType;
import java.math.BigDecimal;
import java.util.ArrayList;


public class BankAccount {
    private final String customerName;
    private final String accountNumber;
    private final String customerPhoneNumber;
    private final String customerEmail;
    private final BigDecimal minimumBalance = BigDecimal.valueOf(100);
    private ArrayList<Transaction> transactions = new ArrayList<>();

    public BankAccount(String customerName,
                       String accountNumber,
                       String customerPhoneNumber,
                       String customerEmail,
                       BigDecimal initialDeposit) {
        if (initialDeposit.compareTo(minimumBalance) < 0) {
            throw new IllegalArgumentException(
                    "You have to deposit minimum $" + minimumBalance + " to open an account!"
            );
        }
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerEmail = customerEmail;
        Transaction transaction =
                new Transaction(
                        getAccountNumber(),
                        initialDeposit,
                        TransactionType.DEPOSIT,
                        TransactionStatus.SUCCESS,
                        "initial deposit for opening account");
        transactions.add(transaction);

    }

    public BankAccount(String customerName,
                       String accountNumber,
                       String customerPhoneNumber,
                       String customerEmail) {
        this.customerName = customerName;
        this.accountNumber = accountNumber;
        this.customerPhoneNumber = customerPhoneNumber;
        this.customerEmail = customerEmail;

    }





    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }
    public BigDecimal getMinimumBalance() {return minimumBalance;}


}
