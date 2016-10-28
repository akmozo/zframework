package com.akmozo.zframework.action;

import javax.servlet.http.HttpServletRequest;

public interface Action {
    
    public String execute(HttpServletRequest request);
    
}
