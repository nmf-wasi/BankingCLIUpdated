package transactions;

import models.BankAccount;

import java.io.*;

import static utility.CsvUtil.csvToTransaction;
import static utility.CsvUtil.transactionToCSV;

public class TransactionStore {


    public static void loadTransactions(BankAccount bankAccount){
        String filePath = "src/repo/transactions.csv";
        File file = new File(filePath);

        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                csvToTransaction(line).ifPresent(transaction -> {
                    String acc = bankAccount.getAccountNumber();

                    if (transaction.getFromAcc().equals(acc)
                            || transaction.getToAcc().orElse("").equals(acc)) {

                        bankAccount.addTransaction(transaction);
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Couldn't load transactions from csv!");
        }
    }

    public static void appendTransaction(BankAccount bankAccount, Transaction transaction) {
        //open file ->
        // write down on append mode (use obj to csv)
        String filePath = "src/repo/transactions.csv";
        File dataDir = new File(filePath);
        if (!dataDir.exists()) dataDir.mkdir();
        try(FileWriter fileWriter=new FileWriter(filePath,true)){
            fileWriter.write(transactionToCSV(transaction));
        }catch (IOException e){
            System.out.println("Couldn't save transaction to existing csv file!");
            e.printStackTrace();
        }



    }
    }
