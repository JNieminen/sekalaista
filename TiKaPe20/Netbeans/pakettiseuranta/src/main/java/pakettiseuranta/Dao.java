package pakettiseuranta;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Jarkko Nieminen
 */
public class Dao {
    private final String dataBase;
    
    public Dao(String db) {
        this.dataBase = "jdbc:sqlite:" + db;
    }
    
    public Connection connect() {
        try {
            Connection conn =  DriverManager.getConnection(dataBase);
            
            if (conn != null) {
                return conn;        
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
        
    }
    
    public void luoTaulut() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Poistetaan vanhat taulut jos niitä on
            pstmt = conn.prepareStatement("DROP TABLE IF EXISTS 'Paikat'");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("DROP TABLE IF EXISTS 'Asiakkaat'");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("DROP TABLE IF EXISTS 'Paketit'");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("DROP TABLE IF EXISTS 'Tapahtumat'");
            pstmt.executeUpdate();
            
            // Luodaan taulut
            pstmt = conn.prepareStatement(
                    "CREATE TABLE `Paikat` (\n" +
                    "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `nimi` TEXT UNIQUE\n" +
                    ")");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(
                    "CREATE TABLE `Asiakkaat` (\n" +
                    "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `nimi` TEXT UNIQUE\n" +
                    ");");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(
                    "CREATE TABLE `Paketit` (\n" +
                    "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `koodi` TEXT UNIQUE,\n" +
                    "  `asiakas_id` INTEGER NOT NULL\n" +
                    ");");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement(
                    "CREATE TABLE `Tapahtumat` (\n" +
                    "  `id` INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  `paketti_id` INTEGER NOT NULL,\n" +
                    "  `paikka_id` INTEGER NOT NULL,\n" +
                    "  `aika` TEXT NOT NULL,\n" +
                    "  `kuvaus` TEXT\n" +
                    ");");
            pstmt.executeUpdate();
            
            // Ja lopuksi ajetaan muutokset kantaan
            conn.commit();            
            
        } catch (SQLException sqle1) {
            
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }
    
    public void luoIndeksit() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Luodaan indeksit
            pstmt = conn.prepareStatement("CREATE INDEX idx_asiakas ON Asiakkaat (nimi);");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("CREATE INDEX idx_paketti ON Paketit (koodi);");
            pstmt.executeUpdate();
            pstmt = conn.prepareStatement("CREATE INDEX idx_paikka ON Paikat (nimi);");
            pstmt.executeUpdate();
            
            // Ja lopuksi ajetaan muutokset kantaan
            conn.commit();            
            
        } catch (SQLException sqle1) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }                
    }
    
    public boolean onkoTauluOlemassa(String taulu) {
        String kysely = "SELECT name FROM sqlite_master WHERE type = 'table' AND name = ?";

        return onkoKysely(kysely, taulu);
    }
    
    public boolean onkoPaikkaOlemassa(String paikka) {
        String kysely = "SELECT nimi FROM Paikat WHERE nimi = ?";

        return onkoKysely(kysely, paikka);
    }

    public boolean onkoAsiakasOlemassa(String asiakas) {
        String kysely = "SELECT nimi FROM Asiakkaat WHERE nimi = ?";

        return onkoKysely(kysely, asiakas);
    }
    
    public boolean onkoPakettiOlemassa(String koodi) {
        String kysely = "SELECT koodi FROM Paketit WHERE koodi = ?";

        return onkoKysely(kysely, koodi);
    }

    private boolean onkoKysely(String kysely, String hakutermi) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean loytyi = false;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return false;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, hakutermi);
            rs = pstmt.executeQuery();

            conn.commit();
            
            loytyi = rs.next();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return loytyi;
        
    }
    
    public void lisaaPaikka(Paikka paikka) {
        String kysely = "INSERT INTO Paikat(nimi) VALUES(?)";
        
        lisaysKysely(kysely, paikka.getNimi());
    }

    public void lisaaAsiakas(Asiakas asiakas) {
        String kysely = "INSERT INTO Asiakkaat(nimi) VALUES(?)";
        
        lisaysKysely(kysely, asiakas.getNimi());
    }

    public void lisaaPaketti(Paketti paketti) {
        String kysely = "INSERT INTO Paketit(koodi, asiakas_id) VALUES(?,?)";
        
        lisaysKysely(kysely, paketti.getKoodi(), paketti.getAsiakasId());
    }

    void lisaaTapahtuma(Tapahtuma tapahtuma) {
        String kysely = "INSERT INTO Tapahtumat(paketti_id, paikka_id, aika, kuvaus) VALUES(?,?,?,?)";
        
        // pakettiID, paikaID, aika, kuvaus
        lisaysKysely(kysely, tapahtuma.getPakettiId(), tapahtuma.getPaikkaId(), tapahtuma.getAika(), tapahtuma.getKuvaus());
        
    }
    
    private void lisaysKysely(String kysely, String tieto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, tieto);
            pstmt.executeUpdate();
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }

    private void lisaysKysely(String kysely, String tieto1, int tieto2) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, tieto1);
            pstmt.setInt(2, tieto2);
            pstmt.executeUpdate();
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }

    private void lisaysKysely(String kysely, int tieto1, int tieto2, String tieto3, String tieto4) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setInt(1, tieto1);
            pstmt.setInt(2, tieto2);
            pstmt.setString(3, tieto3);
            pstmt.setString(4, tieto4);
            pstmt.executeUpdate();
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }
    
    public Asiakas haeAsiakas(String nimi) {
        String kysely = "SELECT * FROM Asiakkaat WHERE nimi = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Asiakas asiakas = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, nimi);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            if (rs.next()) {
                asiakas = new Asiakas(rs.getString("nimi"));
                asiakas.setId(rs.getInt("id"));
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return asiakas;
    }

    public Paketti haePaketti(String koodi) {
        String kysely = "SELECT * FROM Paketit WHERE koodi = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Paketti paketti = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, koodi);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            if (rs.next()) {
                paketti = new Paketti(rs.getString("koodi"), rs.getInt("asiakas_id"));
                paketti.setId(rs.getInt("id"));
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return paketti;
    }

    public Paikka haePaikka(String nimi) {
        String kysely = "SELECT * FROM Paikat WHERE nimi = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        Paikka paikka = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, nimi);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            if (rs.next()) {
                paikka = new Paikka(rs.getString("nimi"));
                paikka.setId(rs.getInt("id"));
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return paikka;
    }

    public String haePaikanNimi(int paikkaId) {
        String kysely = "SELECT * FROM Paikat WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String nimi = null;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setInt(1, paikkaId);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            if (rs.next()) {
                nimi = rs.getString("nimi");
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return nimi;
    }

    public ArrayList<Tapahtuma> haePaketinTapahtumat(String koodi) {
        String kysely = "SELECT T.* FROM Tapahtumat T LEFT JOIN Paketit P ON T.paketti_id = P.id WHERE P.koodi = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        ArrayList<Tapahtuma> tapahtumat = new ArrayList<>();
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, koodi);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            while (rs.next()) {
                Tapahtuma tapahtuma = new Tapahtuma(rs.getInt("paketti_id"), rs.getInt("paikka_id"));
                tapahtuma.setId(rs.getInt("id"));
                tapahtuma.setAika(rs.getString("aika"));
                tapahtuma.setKuvaus(rs.getString("kuvaus"));
                tapahtumat.add(tapahtuma);
                
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return tapahtumat;
    }

    public ArrayList<Paketti> haeAsiakkaanPaketit(String nimi) {
        String kysely = "SELECT P.* FROM Asiakkaat A LEFT JOIN Paketit P ON A.id = P.asiakas_id WHERE A.nimi = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        ArrayList<Paketti> paketit = new ArrayList<>();
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return null;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setString(1, nimi);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            while (rs.next()) {
                Paketti paketti = new Paketti(rs.getString("koodi"), rs.getInt("asiakas_id"));
                paketti.setId(rs.getInt("id"));
                paketit.add(paketti);
                
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return paketit;
     }

    public int haePaikanTapahtumat(int paikkaId, String paiva) {
        paiva = paiva + "%";
        String kysely = "SELECT COUNT(*) FROM Tapahtumat WHERE paikka_id = ? AND aika LIKE ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        int maara = 0;
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return maara;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Tehdään kysely
            pstmt = conn.prepareStatement(kysely);
            pstmt.setInt(1, paikkaId);
            pstmt.setString(2, paiva);
            rs = pstmt.executeQuery();
            
            // Ja ajetaan kysely
            conn.commit();
            
            // Kaivetaan tiedot kannan vastauksesta
            if (rs.next()) {
                maara = rs.getInt(1);                
            }
        } catch (SQLException sqle) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle.getMessage());
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
        
        return maara;
    }

    void luoTestiPaikat(ArrayList<String> paikat) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        String kysely = "INSERT INTO Paikat(nimi) VALUES(?)";
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Lisätään paikat
            pstmt = conn.prepareStatement(kysely);
            
            for (String paikka: paikat) {
                pstmt.setString(1, paikka);
                pstmt.executeUpdate();
            }
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }

    }
    
    void luoTestiAsiakkaat(ArrayList<String> asiakkaat) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        String kysely = "INSERT INTO Asiakkaat(nimi) VALUES(?)";
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Lisätään asiakkaat            
            pstmt = conn.prepareStatement(kysely);
            
            for (String asiakas: asiakkaat) {
                pstmt.setString(1, asiakas);
                pstmt.executeUpdate();
            }
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }

    }

    void luoTestiPaketit(ArrayList<Paketti> paketit) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        String kysely = "INSERT INTO Paketit(koodi,asiakas_id) VALUES(?,?)";
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Lisätään paketit
            pstmt = conn.prepareStatement(kysely);
            
            for (Paketti paketti: paketit) {
                pstmt.setString(1, paketti.getKoodi());
                pstmt.setInt(2, paketti.getAsiakasId());
                pstmt.executeUpdate();
            }
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }

    void luoTestiTapahtumat(ArrayList<Tapahtuma> tapahtumat) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        String kysely = "INSERT INTO Tapahtumat(paketti_id,paikka_id, aika, kuvaus) VALUES(?,?,?,?)";
        
        try {
            // Yhidstä kantaan
            conn = this.connect();
            if (conn == null)
                return;
            
            // Auto-commit pois päältä
            conn.setAutoCommit(false);
            
            // Lisätään paketit
            pstmt = conn.prepareStatement(kysely);
            
            for (Tapahtuma tapahtuma: tapahtumat) {
                pstmt.setInt(1, tapahtuma.getPakettiId());
                pstmt.setInt(2, tapahtuma.getPaikkaId());
                pstmt.setString(3, tapahtuma.getAika());
                pstmt.setString(4, tapahtuma.getKuvaus());
                pstmt.executeUpdate();
            }
            
            // Ja ajetaan kysely
            conn.commit();
                
        } catch (SQLException sqle1) {      
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
            System.out.println(sqle1.getMessage());
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException sqle2) {
                System.out.println(sqle2.getMessage());
            }
        }
    }
}
