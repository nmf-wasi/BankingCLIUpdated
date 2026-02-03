package transactions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionAnalytics {
    private final List<Transaction> transactions;

    public TransactionAnalytics(List<Transaction> transactions) {
        this.transactions = List.copyOf(transactions);
    }

    public void printFailedTransactions() {
        transactions.stream().filter(t -> t.getStatus().equals(TransactionStatus.FAILURE)).forEach(System.out::println);
    }

    public void recentTransactions() {
        transactions.stream().sorted().limit(5).toList().forEach(System.out::println);
    }

    public BigDecimal totalDeposited() {
        return transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .filter(t -> t.getType().equals(TransactionType.DEPOSIT))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal totalWithdrawn() {
        return transactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .filter(t -> t.getType().equals(TransactionType.WITHDRAW))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Map<LocalDate, List<Transaction>> transactionsPerDay() {
        return transactions.stream().collect(Collectors.groupingBy(Transaction::getDate));
    }

    public Map<TransactionType, List<Transaction>> groupByType() {
        return transactions.stream().collect(Collectors.groupingBy(Transaction::getType));
    }

}

