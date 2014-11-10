/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.library.beans;

import java.io.Serializable;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author Roma
 */
@ManagedBean
@SessionScoped
public class LocalChanger implements Serializable{
    
    private Locale currentLocale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
    public LocalChanger(){
        
    }
    
    public void changeLocale (String localeCode){
        currentLocale = new Locale (localeCode);
                
    }
public Locale getCurrentLocale(){
    return currentLocale;
}


}

