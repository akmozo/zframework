package com.akmozo.zframework.xml;

public class XMLPage {

    private String name;
    private String url;

    public XMLPage() {
    }

    public XMLPage(String paramName, String paramUrl) {
        this.name = paramName;
        this.url = paramUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String paramName) {
        this.name = paramName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String paramUrl) {
        this.url = paramUrl;
    }
    
}
