package utility;

import models.BankAccount;
import transactions.Transaction;

import java.util.Optional;

public class CsvUtil {
    public static  String bankAccountTextToCSV(BankAccount bankAccount){
        String line=String.join(",",
                bankAccount.getAccountNumber(),
                bankAccount.getCustomerName(),
                bankAccount.getCustomerPhoneNumber(),
                bankAccount.getCustomerEmail()
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
        return String.join((CharSequence) ",",
            transaction.getTransactionID(),
                transaction.getFromAcc(),
                transaction.getToAcc().orElse(""),
                transaction.getAmount().toString(),
                transaction.getDate().toString(),
                transaction.getType().toString(),
                transaction.getStatus().toString(),
                escapeCsv(transaction.getMessage())
                );
    }

    public static String escapeCsv(String value) {
        if (value == null || value.isEmpty()) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

}
