package utility;

import java.time.LocalDate;

public class IDGenerator {
    public static String setAccountNumber(int size){
        return LocalDate.now().getYear()+ "ABL05"+ String.format("%08d",size+1);
    }
}
