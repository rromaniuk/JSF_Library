package info.library.validators;

import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("ru.javabegin.training.web.validators.LoginValidator")
public class LoginValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        ResourceBundle bundle = ResourceBundle.getBundle("ru.javabegin.training.web.nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            try{
        
            String checkString = value.toString().trim().toLowerCase();
        if (checkString.length() < 5) {
            throw new IllegalArgumentException(bundle.getString("login_length_error"));
        }
        if (Character.isLetter(checkString.charAt(0))){
            throw new IllegalArgumentException(bundle.getString("login_letter_error"));
        }
         if (getIlligalNames().contains(checkString)) {
            throw new IllegalArgumentException(bundle.getString("login_username_error"));
        }
       
        
        
            } catch (IllegalArgumentException e) {
            FacesMessage message = new FacesMessage(e.getMessage());
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
            }
    }
    
    private ArrayList<String> getIlligalNames(){
        ArrayList <String> list = new ArrayList<String>();
        list.add("username");
        list.add("login");
        return list;
    }  
    
}
