package info.library.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static Connection conn;
   
   
    
    
     public static Connection getConnection() throws Exception {
       try{
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "admin");
            return conn;
        }catch(SQLException e){
            throw new Exception(e.getMessage());
        }

       
    }

    
}
