package com.akmozo.zframework.factory;

import com.akmozo.zframework.form.ActionForm;
import java.util.HashMap;
import java.util.Map;

public class FormsFactory {
    
    static Map<String, ActionForm> forms = new HashMap<>();
    
    public static void addForm(String formName, String formClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {         
        forms.put(formName, (ActionForm) Class.forName(formClass).newInstance());
    }
    
    public static Object getFormByName(String formName){        
        return forms.get(formName);
    }

}
