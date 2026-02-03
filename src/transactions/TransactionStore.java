package transactions;

import models.BankAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static utility.CsvUtil.transactionToCSV;

public class TransactionStore {


    public static void loadTransactions(BankAccount bankAccount){
        // open file -> read from there (use csv to obj)
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
