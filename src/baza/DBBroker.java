/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baza;

import java.sql.PreparedStatement;
import com.mysql.cj.protocol.Resultset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Autor;
import model.Knjiga;
import model.Zanr;

/**
 *
 * @author vlada
 */
public class DBBroker {

    public List<Knjiga> ucitajKnjigeIzBaze() {
          List<Knjiga> lista = new ArrayList<>();
        try {
          
            String query = "SELECT *  FROM PS.knjiga K JOIN PS.autor A ON autorID = A.id;";
            Statement st = Konekcija.getInstance().getConnection().createStatement();
            ResultSet rs = (ResultSet) st.executeQuery(query);
            while(rs.next()){
                int id = rs.getInt("k.id");
                String naslov = rs.getString("k.naslov");
                int godIzdanja = rs.getInt("k.godinaIzdanja");
                String ISBN = rs.getString("k.ISBN");
                String zanr = rs.getString("k.Zanr");
      
                int idA = rs.getInt("a.id");
                String ime = rs.getString("a.ime");
                String prezime = rs.getString("a.prezime");
                String biografija = rs.getString("a.biografija");
                int godR = rs.getInt("a.godinaRodjenja");

                Autor a = new Autor(idA, ime, prezime, godR, biografija);
              
                
                Zanr z = Zanr.valueOf(zanr);
                
                Knjiga k = new Knjiga(id, naslov, a, ISBN, godIzdanja, z);
                lista.add(k);
                
                        System.out.println("naslov: " + naslov);
                        System.out.println("autor: " + a);
                        System.out.println("isbn: " + ISBN);
                        System.out.println("godina: " + godIzdanja);
                        System.out.println("zanr: " + zanr);
                
            }
 
            
            
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

        
        return lista;
    }

    public List<Autor> ucitajListuAutora() {
        List<Autor> lista = new ArrayList<>();
        try {
          
            String query = "SELECT *  FROM PS.autor a";
            Statement st = Konekcija.getInstance().getConnection().createStatement();
            ResultSet rs = (ResultSet) st.executeQuery(query);
            while(rs.next()){
                int idA = rs.getInt("a.id");
                String ime = rs.getString("a.ime");
                String prezime = rs.getString("a.prezime");
                String biografija = rs.getString("a.biografija");
                int godR = rs.getInt("a.godinaRodjenja");

                Autor a = new Autor(idA, ime, prezime, godR, biografija);
                lista.add(a);
            }   
        } catch (SQLException ex) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    public void obrisiKnjigu(int id) {
        try {
        String upit = "DELETE FROM PS.knjiga WHERE id=?";
        PreparedStatement ps = Konekcija.getInstance().getConnection().prepareStatement(upit);
        ps.setInt(1, id);
        ps.executeUpdate();
        Konekcija.getInstance().getConnection().commit();
    } catch (SQLException ex) {
        Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
    }
        
    }

    public void dodajKnjigu(Knjiga novaKnjiga) {
    try {
        String query = "INSERT INTO PS.knjiga (id, naslov, autorID, ISBN, godinaIzdanja, zanr) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement st = Konekcija.getInstance().getConnection().prepareStatement(query);
        st.setInt(1, novaKnjiga.getId());
        st.setString(2, novaKnjiga.getNaslov());
        st.setInt(3, novaKnjiga.getAutor().getId());  // Mora autorID, ne "autor"
        st.setString(4, novaKnjiga.getISBN());
        st.setInt(5, novaKnjiga.getGodinaIzdanja());
        st.setString(6, String.valueOf(novaKnjiga.getZanr()));
        st.executeUpdate();
        Konekcija.getInstance().getConnection().commit();
    } catch (SQLException ex) {
        Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        try {
            Konekcija.getInstance().getConnection().rollback(); // u slučaju greške
        } catch (SQLException e) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}

    public void azurirajKnjigu(Knjiga knjigaZaIzmenu) {
    try {
        String query = "UPDATE PS.knjiga SET naslov = ?, autorID = ?, ISBN = ?, godinaIzdanja = ?, zanr = ? WHERE id = ?";
        PreparedStatement st = Konekcija.getInstance().getConnection().prepareStatement(query);

        st.setString(1, knjigaZaIzmenu.getNaslov());
        st.setInt(2, knjigaZaIzmenu.getAutor().getId());
        st.setString(3, knjigaZaIzmenu.getISBN());
        st.setInt(4, knjigaZaIzmenu.getGodinaIzdanja());
        st.setString(5, knjigaZaIzmenu.getZanr().name());
        st.setInt(6, knjigaZaIzmenu.getId()); // Ovo ide u WHERE uslov

        st.executeUpdate();
        Konekcija.getInstance().getConnection().commit();
    } catch (SQLException ex) {
        Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, ex);
        try {
            Konekcija.getInstance().getConnection().rollback();
        } catch (SQLException e) {
            Logger.getLogger(DBBroker.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
    
    
    
}
