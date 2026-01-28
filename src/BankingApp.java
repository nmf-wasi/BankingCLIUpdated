import console.UI;
import models.Bank;

import java.util.Scanner;

public class BankingApp {
    public static void main(String[] args){
        Bank bank=new Bank();
        bank.loadAccounts();
        System.out.println("Welcome to ABL!");
        Scanner scanner = new Scanner(System.in);
        UI ui=new UI(bank,scanner);
        ui.start();
        scanner.close();
    }
}
