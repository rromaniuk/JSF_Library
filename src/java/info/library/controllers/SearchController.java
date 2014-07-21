/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package info.library.controllers;

import info.library.beans.Book;
import info.library.db.Database;
import info.library.enums.SearchType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Roma
 */
@ManagedBean(eager=true)
@RequestScoped
public class SearchController {

    /**
     * @param aSearchList the searchList to set
     */
    public static void setSearchList(Map <String,SearchType> aSearchList) {
        searchList = aSearchList;
    }
    private SearchType searchType; // stores type of search
    private static Map <String,SearchType> searchList = new HashMap<String, SearchType>(); //stores types of search
    private ArrayList <Book> currentBookList;
    private String searchString;// stores typed search string from search field
    private ArrayList <Integer> pageNumbers =new ArrayList<Integer>();
    private String currentSql;
    private long totalBooksCount;
    private long selectedPageNumber=1;
    private char selectedLetter;
    private int booksOnPage=2;
    private int selectedGenreId;
    
    
    
    public SearchController(){
        ResourceBundle bundle = ResourceBundle.getBundle("info.library.nls.messages",FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author_name"), searchType.AUTHOR);
        searchList.put(bundle.getString("book_name"), searchType.TITLE);
        fillAllBooks();
    }

    private void fillBooksBySQL(String sql) {
        StringBuilder sqlBuilder = new StringBuilder(sql);
        currentSql = sql;
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;

        try {
            try {
                conn = Database.getConnection();
            } catch (Exception ex) {
                Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery(sqlBuilder.toString());
            rs.last();
            totalBooksCount =rs.getRow();
            fillPageNumbers(totalBooksCount, booksOnPage);
             if (totalBooksCount>booksOnPage){
                 sqlBuilder.append (" limit ").append (selectedPageNumber*booksOnPage).append (",").append(booksOnPage);
             }
             rs=stmt.executeQuery(sqlBuilder.toString()); 
            currentBookList = new ArrayList<Book>();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getLong("id"));
                book.setName(rs.getString("name"));
                book.setGenre(rs.getString("genre"));
                book.setIsbn(rs.getString("isbn"));
                book.setAuthor(rs.getString("author"));
                book.setPageCount(rs.getInt("page_count"));
                book.setPublishDate(rs.getInt("publish_year"));
                book.setPublisher(rs.getString("publisher"));
                book.setDescription(rs.getString("descr"));
                currentBookList.add(book);
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        
    }
    
    private void fillAllBooks(){
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, b.descr," 
  +"a.fio as author, g.name as genre, b.image from book b inner join author a on b.author_id=a.id "
  + "inner join genre g on b.genre_id=g.id inner join publisher p on b.publisher_id=p.id order by b.name");
    }
    public void fillBooksByGenre(){
        Map <String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
       selectedGenreId = Integer.valueOf(params.get("genre_id"));
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.descr, b.image from book.b"
               +"inner join author a on b.author_id=a.id "
               +"inner join genre g on b.genre_id=g.id "
               +"inner join publisher p on b.publisher_id=p.id "
               +"where genre_id="+selectedGenreId+"order by b.name");
        selectedGenreId=-1;
        selectedPageNumber=-1;
    }
    
    public void fillBooksBySearch(){
    if (searchString.trim().length()==0){
        fillAllBooks();
        return;
    }
    StringBuilder sql = new StringBuilder("select b.descr, b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, g.name as genre, b.image from book b "
                + "inner join author a on b.author_id=a.id "
                + "inner join genre g on b.genre_id=g.id "
                + "inner join publisher p on b.publisher_id=p.id ");
    
    
    if (searchType==SearchType.AUTHOR){
        sql.append("where lower (a.fio) like '%" + searchString.toLowerCase()+"%' order by b.name");
        }
   else if (searchType==SearchType.TITLE){
        sql.append ("where lower (b.name) like '%" + searchString.toLowerCase()+"%' order by b.name");
    }
        fillBooksBySQL(sql.toString());
        
        selectedLetter=' ';
        selectedGenreId=-1;
        selectedPageNumber=-1;
    
    }
    
    public void fillBooksByLetters(){
        Map <String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letters").charAt(0);
        fillBooksBySQL("select b.id,b.name,b.isbn,b.page_count,b.publish_year, p.name as publisher, a.fio as author, gnr.name as genre, b.descr, b.image from book.b"
               +"inner join author a on b.author_id=a.id "
               +"inner join genre gnr on b.genre_id=gnr.id "
               +"inner join publisher p on b.publisher_id=p.id "
               +"where genre_id="+selectedLetter+"order by b.name");
        selectedGenreId=-1;
        selectedPageNumber=-1;
        
    }
    
    public void selectPage(){
         Map <String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
         selectedPageNumber= Integer.valueOf(params.get("page_number"));
         fillBooksBySQL(currentSql);
         
    }
    
    public void fillPageNumbers (long totalBooksCount, int booksCountOnPage){
        int pageCount =booksCountOnPage>0 ? (int) (totalBooksCount/booksCountOnPage):0;
        pageNumbers.clear();
        for (int i=1; i<=pageCount;i++){
            pageNumbers.add(i);
        }
    }
    public Character[] getLetters(){
        Character[] letters = new Character[33];
        letters[0]= 'А';
        letters[1]= 'Б';
        letters[2]= 'В';
        letters[3]= 'Г';
        letters[4]= 'Д';
        letters[5]= 'Е';
        letters[6]= 'Ё';
        letters[7]= 'Ж';
        letters[8]= 'З';
        letters[9]= 'И';
        letters[10]= 'Й';
        letters[11]= 'К';
        letters[12]= 'Л';
        letters[13]= 'М';
        letters[14]= 'Н';
        letters[15]= 'О';
        letters[16]= 'П';
        letters[17]= 'Р';
        letters[18]= 'С';
        letters[19]= 'Т';
        letters[20]= 'У';
        letters[21]= 'Ф';
        letters[22]= 'Х';
        letters[23]= 'Ц';
        letters[24]= 'Ч';
        letters[25]= 'Ш';
        letters[26]= 'Щ';
        letters[27]= 'Ъ';
        letters[28]= 'Ы';
        letters[29]= 'Ь';
        letters[30]= 'Э';
        letters[31]= 'Ю';
        letters[32]= 'Я';
        
        return letters;
    }
    public byte[] getImage(int id){
        
        Statement stmt = null;
        ResultSet rs = null;
        Connection conn = null;
        byte [] image = null;
        try {
            try {
                conn = Database.getConnection();
            } catch (Exception ex) {
                Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery("select image from book where id="+id);
            while (rs.next()) {
               image = rs.getBytes("image");
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(GenreListController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return image;
    } 
    
    public SearchType getSearchType(){
        return searchType;
    }
    /**
     * @param searchType the searchType to set
     */
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
    
    public Map <String, SearchType> getSearchList(){
        return searchList;
    }

    

    /**
     * @return the currentBookList
     */
    public ArrayList <Book> getCurrentBookList() {
        return currentBookList;
    }

    /**
     * @param currentBookList the currentBookList to set
     */
    public void setCurrentBookList(ArrayList <Book> currentBookList) {
        this.currentBookList = currentBookList;
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return searchString;
    }

    /**
     * @param searchString the searchString to set
     */
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    /**
     * @return the selectedPageNumber
     */
    public long getSelectedPageNumber() {
        return selectedPageNumber;
    }

    /**
     * @param selectedPageNumber the selectedPageNumber to set
     */
    public void setSelectedPageNumber(int selectedPageNumber) {
        this.selectedPageNumber = selectedPageNumber;
    }

    /**
     * @return the pageNumbers
     */
    public ArrayList <Integer> getPageNumbers() {
        return pageNumbers;
    }

    /**
     * @param pageNumbers the pageNumbers to set
     */
    public void setPageNumbers(ArrayList <Integer> pageNumbers) {
        this.pageNumbers = pageNumbers;
    }

    public char getSelectedLetter(){
        return selectedLetter;
    }
    public void setSelectedLetter(char selectedLetter){
        this.selectedLetter=selectedLetter;
    }
    
}   
