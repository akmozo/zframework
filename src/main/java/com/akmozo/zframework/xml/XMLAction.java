package com.akmozo.zframework.xml;

import java.util.List;

public class XMLAction {
    
    private String className;
    private String url;
    private XMLForm form;
    private List<XMLPage> pages;

    public XMLAction() {
    }
    
    public XMLAction(String paramName, String paramUrl) {
        this.className = paramName;
        this.url = paramUrl;
    }
    
    public XMLAction(String paramName, String paramUrl, List<XMLPage> paramRetour) {
        this.className = paramName;
        this.url = paramUrl;
        this.pages = paramRetour;
    }

    public XMLAction(String paramName, String paramUrl, XMLForm paramForm, List<XMLPage> paramRetour) {
        this.className = paramName;
        this.url = paramUrl;
        this.form = paramForm;
        this.pages = paramRetour;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String paramClassName) {
        this.className = paramClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String paramUrl) {
        this.url = paramUrl;
    }

    public XMLForm getForm() {
        return form;
    }

    public void setForm(XMLForm paramForm) {
        this.form = paramForm;
    }

    public List<XMLPage> getPages() {
        return pages;
    }

    public void setPages(List<XMLPage> paramPages) {
        this.pages = paramPages;
    }

}
