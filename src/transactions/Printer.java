package transactions;

import java.util.ArrayList;

public class Printer{
    public static void printTransactions(ArrayList<Transaction> transactions){
            transactions.forEach(System.out::println);
        }
}
