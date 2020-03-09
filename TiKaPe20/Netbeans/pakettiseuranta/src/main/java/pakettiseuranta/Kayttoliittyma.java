package pakettiseuranta;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Jarkko Nieminen
 */
public class Kayttoliittyma {
    private final Scanner lukija;
    private final Dao dao;
    
    public Kayttoliittyma(Scanner lukija, Dao dao) {
        this.lukija = lukija;
        this.dao = dao;
    }

    public void start() {
        // TODO: 

        String syote = null;

        tulostaValikko();

        while (true) {
            System.out.print("> ");
            syote = lukija.nextLine();
            syote = syote.toLowerCase();
            
            if (syote.equals("x"))
                return;
            
            switch (syote) {
                case "1":
                    luoTaulut();
                    break;
                case "2":
                    lisaaPaikka();
                    break;
                case "3":
                    lisaaAsiakas();
                    break;
                case "4":
                    lisaaPaketti();
                    break;
                case "5":
                    lisaaTapahtuma();
                    break;
                case "6":
                    haePaketinTapahtumat();
                    break;
                case "7":
                    haeAsiakkaanPaketit();
                    break;
                case "8":
                    haePaikanTapahtumat();
                    break;
                case "9":
                    tehokkuusTesti();
                    break;
                case "t":
                    tulostaValikko();
                    break;
                default:
                    System.out.println("Tuntematon komento.");
            }
        }
    }
    
    public void tulostaValikko() {
        System.out.println("*** Pakettiseuranta ***");
        System.out.println("");
        System.out.println("1. Alusta tietokanta");
        System.out.println("2. Lisää paikka");
        System.out.println("3. Lisää asiakas");
        System.out.println("4. Lisää paketti asiakkaalle");
        System.out.println("5. Lisää tapahtuma paketille");
        System.out.println("6. Hae paketin tapahtumat");
        System.out.println("7. Hae asiakkaan paketit");
        System.out.println("8. Hae paikan tapahtumat");
        System.out.println("9. Tietokannan tehokkuustesti");
        System.out.println("t. Tulosta valikko");
        System.out.println("x. Lopeta");
        System.out.println("");
        
    }
    
    public void luoTaulut() {
        if (dao.onkoTauluOlemassa("Paketit")) {
            System.out.println("VAROITUS!");
            System.out.println("Tämä alustaa tietokannan ja poistaa kaiken tiedon,");
            System.out.print("oletko varma että haluat jatkaa (k/e)? ");
            String syote = lukija.nextLine().toLowerCase();
            
            if (!syote.equals("k")) {
                System.out.println("Tietokantaa ei muutettu");
                return;
            }
        }
        
        dao.luoTaulut();
        dao.luoIndeksit();
        System.out.println("Tietokanta alustettu");
    }
    
    public void lisaaPaikka() {
        System.out.print("Anna paikan nimi: ");
        String syote = lukija.nextLine();
        
        if (dao.onkoPaikkaOlemassa(syote)) {
            System.out.println("Virhe: Paikka on jo olemassa");
            return;
        }
        
        Paikka paikka = new Paikka(syote);
        
        dao.lisaaPaikka(paikka);
        
        System.out.println("Paikka lisätty");
        
    }
    
    public void lisaaAsiakas() {
        System.out.print("Anna asiakkaan nimi: ");
        String syote = lukija.nextLine();
        
        if (dao.onkoAsiakasOlemassa(syote)) {
            System.out.println("Virhe: Asiakas on jo olemassa");
            return;
        }
        
        Asiakas asiakas = new Asiakas(syote);
        
        dao.lisaaAsiakas(asiakas);
        
        System.out.println("Asiakas lisätty");
        
    }
    
    public void lisaaPaketti() {
        System.out.print("Anna paketin seurantakoodi: ");
        String koodi = lukija.nextLine();
        
        if (dao.onkoPakettiOlemassa(koodi)) {
            System.out.println("Virhe: Paketti on jo olemassa");
            return;
        }

        System.out.print("Anna asiakkaan nimi: ");
        String nimi = lukija.nextLine();
        
        Asiakas asiakas = dao.haeAsiakas(nimi);
        
        if (asiakas == null) {
            System.out.println("Virhe: Asiakasta ei ole olemassa");
            return;
        }
        
        Paketti paketti = new Paketti(koodi, asiakas.getId());
        
        dao.lisaaPaketti(paketti);
        
        System.out.println("Paketti lisätty");
        
    }

    public void lisaaTapahtuma() {
        
        System.out.print("Anna paketin seurantakoodi: ");
        String koodi = lukija.nextLine();
        
        Paketti paketti = dao.haePaketti(koodi);
        
        if (paketti == null) {
            System.out.println("Virhe: Pakettia ei ole olemassa");
            return;
        }

        System.out.print("Anna paikan nimi: ");
        String nimi = lukija.nextLine();
        
        Paikka paikka = dao.haePaikka(nimi);
        
        if (paikka == null) {
            System.out.println("Virhe: Paikkaa ei ole olemassa");
            return;
        }

        System.out.print("Anna tapahtuman kuvaus: ");
        String kuvaus = lukija.nextLine();
        
        Tapahtuma tapahtuma = new Tapahtuma(paketti.getId(), paikka.getId());
        tapahtuma.setKuvaus(kuvaus);
        
        dao.lisaaTapahtuma(tapahtuma);
        
        System.out.println("Tapahtuma lisätty");
        
    }

    public void haePaketinTapahtumat() {
        System.out.print("Anna paketin seurantakoodi: ");
        String koodi = lukija.nextLine();
        
        if (!dao.onkoPakettiOlemassa(koodi)) {
            System.out.println("Virhe: Pakettia ei ole olemassa");
            return;
        }
        
        ArrayList<Tapahtuma> tapahtumat = dao.haePaketinTapahtumat(koodi);
        
        for (Tapahtuma tapahtuma : tapahtumat) {
            String paikanNimi = dao.haePaikanNimi(tapahtuma.getPaikkaId());
            System.out.println(tapahtuma.getAika() + ", " + paikanNimi + ", " + tapahtuma.getKuvaus());
        }
    }
    
    public void haeAsiakkaanPaketit() {
        System.out.print("Anna asiakkaan nimi: ");
        String nimi = lukija.nextLine();
        
        if (!dao.onkoAsiakasOlemassa(nimi)) {
            System.out.println("Virhe: Asiakasta ei ole olemassa");
            return;
        }
                
        ArrayList<Paketti> paketit = dao.haeAsiakkaanPaketit(nimi);
        
        for (Paketti paketti : paketit) {
            ArrayList<Tapahtuma> tapahtumat = dao.haePaketinTapahtumat(paketti.getKoodi());
            System.out.println(paketti.getKoodi() + ": " + tapahtumat.size() + " tapahtumaa");
        }
    }
    
    public void haePaikanTapahtumat() {
        System.out.print("Anna paikan nimi: ");
        String nimi = lukija.nextLine();
        
        Paikka paikka = dao.haePaikka(nimi);
        
        if (paikka == null) {
            System.out.println("Virhe: Paikkaa ei ole olemassa");
            return;
        }
        
        System.out.print("Anna päivämäärä (pp.kk.vvvv): ");
        String paiva = lukija.nextLine();
        
        String[] temp = paiva.split("\\.");
        
        // Muokataan päivämäärä tarvittaessa muotoon jollaisena se on tallennetu kantaan
        if (temp[0].length() == 1)
            temp[0] = "0" + temp[0];
        if (temp[1].length() == 1)
            temp[1] = "0" + temp[1];
        
        paiva = String.join(".", temp);
        
        int maara = dao.haePaikanTapahtumat(paikka.getId(), paiva);
        
        if (maara > 0) {
            System.out.println("Tapahtumien määrä: " + maara);
        } else {
            System.out.println("Paikassa ei tapahtumia annettuna päivänä");
        }
    }
    
    public void tehokkuusTesti() {
        System.out.println("* Tietokannan tehokkuustesti *");
        
        
        System.out.println("\nIlman indeksejä:");
        dao.luoTaulut();
        testaa();
        
        System.out.println("\nIndeksien kanssa:");
        dao.luoTaulut();
        dao.luoIndeksit();
        testaa();
        
    }
    
    private void testaa() {
        ArrayList<String> asiakasNimet = new ArrayList<>();
        ArrayList<String> paikanNimet = new ArrayList<>();
        ArrayList<String> pakettiKoodit = new ArrayList();
        ArrayList<Paketti> paketit = new ArrayList<>();
        ArrayList<Tapahtuma> tapahtumat = new ArrayList<>();
        
        long alku, loppu;
        double kesto;
        Random rng = new Random();
        
        // Testit
        // Vaihe 1 - Paikat
        alku = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String paikka = "P" + (i + 1);
            paikanNimet.add(paikka);
        }
        dao.luoTestiPaikat(paikanNimet);
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 1 kesto: " + kesto + "s");
        
        // Vaihe 2 - Asiakkaat
        alku = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String asiakas = "A" + (i + 1);
            asiakasNimet.add(asiakas);
        }
        dao.luoTestiAsiakkaat(asiakasNimet);
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 2 kesto: " + kesto + "s");

        // Vaihe 3 - Paketit
        alku = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String koodi = String.format("PA%04d", i + 1);
            pakettiKoodit.add(koodi);
            Asiakas asiakas = dao.haeAsiakas(asiakasNimet.get(rng.nextInt(1000)));
            Paketti paketti = new Paketti(koodi, asiakas.getId());
            paketit.add(paketti);
        }
        dao.luoTestiPaketit(paketit);
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 3 kesto: " + kesto + "s");

        // Vaihe 4 - Tapahtumat
        alku = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            Paketti paketti = dao.haePaketti(pakettiKoodit.get(rng.nextInt(1000)));
//            Paketti paketti = paketit.get(rng.nextInt(1000));
            Paikka paikka = dao.haePaikka(paikanNimet.get(rng.nextInt(1000)));
            String kuvaus = String.format("Tapahtuma %d", i + 1);
            Tapahtuma tapahtuma = new Tapahtuma(paketti.getId(), paikka.getId());
            tapahtuma.setKuvaus(kuvaus);
            tapahtumat.add(tapahtuma);
        }
//        alku = System.nanoTime();
        dao.luoTestiTapahtumat(tapahtumat);
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 4 kesto: " + kesto + "s");
        
        // Vaihe 5 - Asiakkaan paketit
        alku = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String nimi = asiakasNimet.get(rng.nextInt(1000));
            ArrayList<Paketti> asiakkaanPaketit = dao.haeAsiakkaanPaketit(nimi);
            int maara = asiakkaanPaketit.size();
        }
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 5 kesto: " + kesto + "s");

        // Vaihe 6 - Paketin tapahtumat
        alku = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            String koodi = pakettiKoodit.get(rng.nextInt(1000));
            ArrayList<Tapahtuma> paketinTapahtumat = dao.haePaketinTapahtumat(koodi);
            int maara = paketinTapahtumat.size();
        }
        loppu = System.nanoTime();
        kesto = 1.0 * (loppu - alku) / 1000000000;
        System.out.println("Vaihe 6 kesto: " + kesto + "s");
    }
}
