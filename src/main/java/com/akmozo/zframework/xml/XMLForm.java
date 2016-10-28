package com.akmozo.zframework.xml;

public class XMLForm {

    private String formClass;
    private String name;
    private String origin;

    public XMLForm() {
    }

    public XMLForm(String paramFormClass, String paramName) {
        this.formClass = paramFormClass;
        this.name = paramName;
    }

    public XMLForm(String paramFormClass, String paramName, String paramOrgin) {
        this.formClass = paramFormClass;
        this.name = paramName;
        this.origin = paramOrgin;
    }

    public String getFormClass() {
        return formClass;
    }

    public void setFormClass(String paramFormClass) {
        this.formClass = paramFormClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String paramName) {
        this.name = paramName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String paramOrigin) {
        this.origin = paramOrigin;
    }
    
}
