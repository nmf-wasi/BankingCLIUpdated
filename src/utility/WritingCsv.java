package utility;

import models.BankAccount;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static utility.CsvUtil.bankAccountTextToCSV;

public class WritingCsv {
    public static void appendToFile(BankAccount bankAccount){
        String filePath="src/repo/bankAccounts.csv";
        File dataDir=new File(filePath);
        if(!dataDir.exists()) dataDir.mkdir();
        try(FileWriter fileWriter=new FileWriter(filePath,true)){
        fileWriter.write(bankAccountTextToCSV(bankAccount));
        }catch (IOException e){
            System.out.println("Couldn't save new account to existing csv file!");
            e.printStackTrace();
        }
    }
}
