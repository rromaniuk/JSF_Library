package info.library.beans;

import info.library.db.Database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

@ManagedBean
@ApplicationScoped
public class GenresFromDB {

    private ArrayList<Genre> genreList = new ArrayList<Genre>();

    private ArrayList<Genre> getGenres() {
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        try {
            try {
                conn = Database.getConnection();
            } catch (Exception ex) {
                Logger.getLogger(GenresFromDB.class.getName()).log(Level.SEVERE, null, ex);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select * from library.genre order by 'name'");
            while (rs.next()) {
                System.out.println(rs.getString("name"));
                Genre genre = new Genre();
                genre.setName(rs.getString("name"));
                genre.setId(rs.getLong("id"));
                genreList.add(genre);
            }

        } catch (SQLException ex) {
            Logger.getLogger(GenresFromDB.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(GenresFromDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return genreList;
    }

    public ArrayList<Genre> getGenreList() {
        if (!genreList.isEmpty()) {
            return genreList;
        } else {
            return getGenres();
        }
    }
}
