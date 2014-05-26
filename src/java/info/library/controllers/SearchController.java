/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package info.library.controllers;

import info.library.enums.SearchType;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Roma
 */
@ManagedBean
@RequestScoped
public class SearchController {
    private SearchType searchType;
    private static Map <String,SearchType> searchList = new HashMap<String, SearchType>();
    
    public SearchController(){
        ResourceBundle bundle = ResourceBundle.getBundle("info.library.nls.messages",FacesContext.getCurrentInstance().getViewRoot().getLocale());
        searchList.put(bundle.getString("author_name"), searchType.AUTHOR);
        searchList.put(bundle.getString("book_name"), searchType.TITLE);
    }

    public SearchType getSearchType(){
        return searchType;
    }
    public Map getSearchList(){
        return searchList;
    }

}   
