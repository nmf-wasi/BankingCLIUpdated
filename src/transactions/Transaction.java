package transactions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class Transaction implements Comparable<Transaction>{
    private final String transactionID;
    private final String fromAcc;
    private final Optional<String> toAcc;
    private final BigDecimal amount;
    private final LocalDate date;
    private final TransactionType type; // deposit/ withdraw
    private final TransactionStatus status;
    private final String message;


    public Transaction(String transactionID,
                       String fromAcc,
                       BigDecimal amount,
                       LocalDate date,
                       TransactionType type,
                       TransactionStatus status,
                       String message,
                       Optional<String> toAcc) {
        this.transactionID = transactionID;
        this.fromAcc = fromAcc;
        this.toAcc = toAcc;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.status = status;
        this.message = message;
    }

    //for deposit, withdraw
    public Transaction(String accountNumber,
                       BigDecimal amount,
                       TransactionType type,
                       TransactionStatus status,
                       String message) {
        this(UUID.randomUUID().toString(), accountNumber, amount, LocalDate.now(), type, status, message, Optional.empty());

    }

    //for transfer
    public Transaction(String accountNumber, String relatedAccountNumber,
                       BigDecimal amount,
                       TransactionType type,
                       TransactionStatus status,
                       String message) {
        this(UUID.randomUUID().toString(), accountNumber, amount, LocalDate.now(), type, status, message, Optional.of(relatedAccountNumber));
    }

    @Override
    public String toString() {
        return "Transaction ID: "+transactionID+
                (toAcc.isEmpty()? "Account: "+fromAcc :"From Account: "+fromAcc+" To Account: "+toAcc)
                +"Date: "+date
                +"Transaction Type: "+type
                +"Transaction Status : "+status
                +"Message: "+message;
    }

    public String getMessage(){return message;}
    public LocalDate getDate(){return date;}
    public Optional<String> getToAcc(){return toAcc;}
    public String getFromAcc(){return fromAcc;}
    public String getTransactionID(){return transactionID;}
    public TransactionStatus getStatus(){ return status;}
    public TransactionType getType(){return type;}
    public BigDecimal getAmount(){return amount;}

    @Override
    public int compareTo(Transaction o) {
        return o.getDate().compareTo(this.getDate()); // naturally how it should be sorted
    }

}
