package pakettiseuranta;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jarkko Nieminen
 */
public class Tapahtuma {
    private int id;
    private int pakettiId;
    private int paikkaId;
    private String aika;
    private String kuvaus;
    
    public Tapahtuma(int pakettiId, int paikkaId) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        this.pakettiId = pakettiId;
        this.paikkaId = paikkaId;
        this.aika = sdf.format(new Date());
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the pakettiId
     */
    public int getPakettiId() {
        return pakettiId;
    }

    /**
     * @return the paikkaId
     */
    public int getPaikkaId() {
        return paikkaId;
    }

    /**
     * @return the aika
     */
    public String getAika() {
        return aika;
    }

    /**
     * @return the kuvaus
     */
    public String getKuvaus() {
        return kuvaus;
    }

    /**
     * @param kuvaus the kuvaus to set
     */
    public void setKuvaus(String kuvaus) {
        this.kuvaus = kuvaus;
    }

    /**
     * @param aika the aika to set
     */
    public void setAika(String aika) {
        this.aika = aika;
    }

}
