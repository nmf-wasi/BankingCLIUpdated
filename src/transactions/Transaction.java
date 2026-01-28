package transactions;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    String fromAcc;
    String toAcc;
    BigDecimal amount;
    private final LocalDate date;
    private  final TransactionType type; // deposit/ withdraw
    private TransactionStatus status;
    String message;

    public Transaction(String fromAcc, String toAcc,
                       BigDecimal amount, TransactionType type, TransactionStatus status, String message){
        this.fromAcc=fromAcc;
        this.toAcc=toAcc;
        this.amount=amount;
        this.date=LocalDate.now();
        this.type=type;
        this.status=status;
        this.message=message;

    }
    public Transaction(String fromAcc,
                       BigDecimal amount, TransactionType type, TransactionStatus status, String message){
        this(fromAcc,amount, type, status,message)

    }


}
