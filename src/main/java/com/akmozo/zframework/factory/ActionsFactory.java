package com.akmozo.zframework.factory;

import com.akmozo.zframework.action.Action;
import java.util.HashMap;
import java.util.Map;

public class ActionsFactory {
    
    static Map<String, Action> classes = new HashMap<>();
    
    public static void addClass(String urlPattern, String classPath) throws ClassNotFoundException, InstantiationException, IllegalAccessException{         
        classes.put(urlPattern, (Action) Class.forName(classPath).newInstance());
    }
    
    public static Object getActionByURL(String urlPattern){        
        return classes.get(urlPattern);
    }

}
