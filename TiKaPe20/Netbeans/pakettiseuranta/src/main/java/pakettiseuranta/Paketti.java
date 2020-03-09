package pakettiseuranta;

/**
 *
 * @author Jarkko Nieminen
 */
public class Paketti {
    private int id;
    private String koodi;
    private int asiakasId;
    
    public Paketti(String koodi, int asiakasId) {
        this.koodi = koodi;
        this.asiakasId = asiakasId;
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
     * @return the koodi
     */
    public String getKoodi() {
        return koodi;
    }

    /**
     * @return the asiakasId
     */
    public int getAsiakasId() {
        return asiakasId;
    }
}
