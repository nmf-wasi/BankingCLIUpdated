package utility;

import models.BankAccount;
import transactions.Transaction;
import transactions.TransactionStatus;
import transactions.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public class CsvUtil {
    public static  String bankAccountTextToCSV(BankAccount bankAccount){
        String line=String.join(",",
                bankAccount.getAccountNumber(),
                bankAccount.getCustomerName(),
                bankAccount.getCustomerPhoneNumber(),
                bankAccount.getCustomerEmail()+"\n"
        );
        return line;
    }

    public static Optional<BankAccount> bankAccountCSVtoText(String line){
        if(line.isEmpty()) return Optional.empty();
        String []fields=line.split(",", -1);
        if(fields.length<4) {
            System.out.println("Skipping invalid line: " + line);
            return Optional.empty();
        }
        String accountNumber = fields[0];
        String customerName = fields[1];
        String phoneNumber = fields[2];
        String customerEmail = fields[3];
        return Optional.of(new BankAccount(customerName,accountNumber,phoneNumber,customerEmail));
    }


    public static String transactionToCSV(Transaction transaction){
        return String.join(",",
            transaction.getTransactionID(),
                transaction.getFromAcc(),
                transaction.getToAcc().orElse(""),
                transaction.getAmount().toString(),
                transaction.getDate().toString(),
                transaction.getType().toString(),
                transaction.getStatus().toString(),
                escapeCsv(transaction.getMessage())+"\n"
                );
    }

    public static Optional<Transaction> csvToTransaction(String line) {
        if (line == null || line.isBlank()) return Optional.empty();

        // Split into max 8 parts (7 commas)
        String[] fields = line.split(",", 8);
        if (fields.length < 8) {
            System.out.println("Skipping malformed transaction line: " + line);
            return Optional.empty();
        }

        try {
            String transactionId = fields[0];
            String fromAcc = fields[1];
            String toAccRaw = fields[2];
            BigDecimal amount = new BigDecimal(fields[3]);
            LocalDate date = LocalDate.parse(fields[4]);
            TransactionType type = TransactionType.valueOf(fields[5]);
            TransactionStatus status = TransactionStatus.valueOf(fields[6]);

            String message = unescapeCsv(fields[7].trim());

            return Optional.of(
                    new Transaction(
                            transactionId,
                            fromAcc,
                            amount,
                            date,
                            type,
                            status,
                            message,
                            toAccRaw.isEmpty() ? Optional.empty() : Optional.of(toAccRaw)
                    )
            );
        } catch (Exception e) {
            System.out.println("Skipping invalid transaction line: " + line);
            return Optional.empty();
        }
    }

    private static String unescapeCsv(String value) {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    public static String escapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}
