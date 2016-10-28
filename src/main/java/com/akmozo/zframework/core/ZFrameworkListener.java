package com.akmozo.zframework.core;

import com.akmozo.zframework.factory.ActionsFactory;
import com.akmozo.zframework.factory.FormsFactory;
import com.akmozo.zframework.xml.XMLAction;
import com.akmozo.zframework.xml.XMLForm;
import com.akmozo.zframework.xml.XMLPage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ZFrameworkListener implements ServletContextListener {

    ActionsFactory actionsFactory;
    FormsFactory formsFactory;

    List<XMLAction> actionsList;

    Map<String, XMLAction> actionsMap;
    Map<String, XMLForm> formsMap;

    @Override
    public void contextInitialized(ServletContextEvent paramServletContextEvent) {

        if ((ActionsFactory) paramServletContextEvent.getServletContext().getAttribute("actionsFactory") != null) {
            return;
        }

        actionsFactory = new ActionsFactory();
        formsFactory = new FormsFactory();

        actionsList = new ArrayList<>();
        actionsMap = new HashMap<>();
        formsMap = new HashMap<>();

        try {

            InputStream xmlConfigStream = paramServletContextEvent.getServletContext().getResourceAsStream("/WEB-INF/zframework.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            factory.setIgnoringComments(true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlConfigStream);

            Element racine = document.getDocumentElement();
            // get forms
            NodeList forms = racine.getElementsByTagName("form");
            for (int i = 0; i < forms.getLength(); i++) {

                Node form = forms.item(i);
                NodeList elements = form.getChildNodes();

                XMLForm xmlForm = new XMLForm();

                for (int j = 0; j < elements.getLength(); j++) {

                    Node formElement = elements.item(j);

                    if (formElement.getNodeName().equals("form-class")) {
                        xmlForm.setFormClass(formElement.getTextContent());
                    }
                    if (formElement.getNodeName().equals("form-name")) {
                        xmlForm.setName(formElement.getTextContent());
                    }
                    if (formElement.getNodeName().equals("form-origin")) {
                        xmlForm.setOrigin(formElement.getTextContent());
                    }

                    try {
                        if(xmlForm != null && xmlForm.getFormClass() != null){
                            Class.forName(xmlForm.getFormClass());
                            formsMap.put(xmlForm.getName(), xmlForm);
                        }
                    } catch (ClassNotFoundException ex) {
                        xmlForm = null;
                    }
                }

            }

            // get actions
            NodeList actions = racine.getElementsByTagName("action");
            for (int i = 0; i < actions.getLength(); i++) {

                Node action = actions.item(i);
                NodeList elements = action.getChildNodes();

                XMLAction xmlAction = new XMLAction();
                List<XMLPage> xmlPages = new ArrayList<>();

                for (int j = 0; j < elements.getLength(); j++) {

                    Node actionElement = elements.item(j);
                    if (actionElement.getNodeName().equals("action-class")) {
                        xmlAction.setClassName(actionElement.getTextContent());
                    }
                    if (actionElement.getNodeName().equals("url-pattern")) {
                        xmlAction.setUrl(actionElement.getTextContent());
                    }
                    if (actionElement.getNodeName().equals("pages")) {
                        NodeList pages = actionElement.getChildNodes();
                        for (int p = 0; p < pages.getLength(); p++) {

                            Node page = pages.item(p);
                            Element e = (Element) page;

                            XMLPage xmlPage = new XMLPage(e.getAttribute("name"), e.getAttribute("value"));
                            xmlPages.add(xmlPage);
                        }
                        xmlAction.setPages(xmlPages);
                    }

                    if (actionElement.getNodeName().equals("form-name")) {
                        xmlAction.setForm(formsMap.get(actionElement.getTextContent()));
                    }
                }

                if(xmlAction.getClassName()!= null){
                    try {
                        Class.forName(xmlAction.getClassName());
                        actionsList.add(xmlAction);
                        actionsMap.put(xmlAction.getUrl(), xmlAction);
                    } catch (ClassNotFoundException ex) {
                    }
                }

            }

            paramServletContextEvent.getServletContext().setAttribute("actionsList", actionsList);
            paramServletContextEvent.getServletContext().setAttribute("actionsMap", actionsMap);
            paramServletContextEvent.getServletContext().setAttribute("formsMap", formsMap);

            actionsList = null;
            actionsMap = null;
            formsMap = null;

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(ZFrameworkListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent paramSce) {
    }

}