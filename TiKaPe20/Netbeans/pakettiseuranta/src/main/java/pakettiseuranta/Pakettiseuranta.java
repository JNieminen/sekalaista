package pakettiseuranta;

import java.util.Scanner;

/**
 *
 * @author Jarkko Nieminen
 */
public class Pakettiseuranta {
    private static final String KANTA = "pakettiseuranta.db";
    
    public static void main(String[] args) {
        Scanner lukija = new Scanner(System.in);
        Dao dao = new Dao(KANTA);
        
        Kayttoliittyma kali = new Kayttoliittyma(lukija, dao);
        kali.start();

    }

}
