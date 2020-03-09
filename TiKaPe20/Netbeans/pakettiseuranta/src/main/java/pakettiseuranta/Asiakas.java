package pakettiseuranta;

/**
 *
 * @author Jarkko Nieminen
 */
public class Asiakas {
    private int id;
    private String nimi;
    
    public Asiakas(String nimi) {
        this.nimi = nimi;
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
     * @return the nimi
     */
    public String getNimi() {
        return nimi;
    }
}
