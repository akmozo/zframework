package com.akmozo.zframework.core;

import com.akmozo.zframework.action.Action;
import com.akmozo.zframework.factory.ActionsFactory;
import com.akmozo.zframework.factory.FormsFactory;
import com.akmozo.zframework.form.ActionForm;
import com.akmozo.zframework.xml.XMLAction;
import com.akmozo.zframework.xml.XMLForm;
import com.akmozo.zframework.xml.XMLPage;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.beanutils.BeanUtils;

public class ZFramework extends HttpServlet {

    List<String> errors = new ArrayList<>();

    Map<String, XMLAction> actionsMap;
    Map<String, XMLForm> formsMap;

    List<XMLAction> actionsList;

    @Override
    public void init() throws ServletException {

        actionsList = (List) getServletContext().getAttribute("actionsList");
        actionsMap = (Map) getServletContext().getAttribute("actionsMap");
        formsMap = (Map) getServletContext().getAttribute("formsMap");

        actionsList.stream().forEach((XMLAction xmlAction) -> {
            try {
                ActionsFactory.addClass(xmlAction.getUrl(), xmlAction.getClassName());
                if (xmlAction.getForm() != null) {
                    FormsFactory.addForm(xmlAction.getForm().getName(), xmlAction.getForm().getFormClass());
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) { 
                
                actionsMap.remove(xmlAction.getUrl());
                
                if(ex instanceof ClassNotFoundException){                    
                    errors.add("The action with the url-pattern <b>" + xmlAction.getUrl() + "</b> "
                            +"will not be activated because the class <b>" + xmlAction.getClassName() + "</b> did not exist.");                    
                }                
                if(ex instanceof InstantiationException){                    
                    errors.add("The action with the url-pattern <b> " + xmlAction.getUrl() + "</b> "
                            +"will not be activated because the class <b>" + xmlAction.getClassName() + "</b> can not be instantiated.");                    
                } 
                if(ex instanceof IllegalAccessException){                    
                    errors.add("The action with the url-pattern <b>" + xmlAction.getUrl() + "</b> "
                            +"will not be activated because we don't have access to the definition of the class <b>" + xmlAction.getClassName() + "</b>.");                    
                }
            }
        });
        actionsList = null;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ActionServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ActionServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private void showErrors(List<String> errors, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>You have some errors</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Errors : </h1>");
            errors.stream().forEach((error) -> {
                out.println("<p>" + error + "</p>");
            });
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!errors.isEmpty()) {
            showErrors(errors, response);
            errors.removeAll(errors);
            return;
        }
        
        String path = request.getServletPath();
        String parts[] = path.split("/");
        String url = "/" + parts[parts.length - 1];

        Action action = (Action) ActionsFactory.getActionByURL(url);

        XMLAction xmlAction = actionsMap.get(url);
        
        if(xmlAction == null){        
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String nextPage = "", defaultPage = "";

        XMLForm xmlForm = xmlAction.getForm();
        boolean formOK = false;

        if (xmlForm != null) {

            ActionForm actionForm = (ActionForm) FormsFactory.getFormByName(xmlForm.getName());

            try {
                BeanUtils.populate(actionForm, request.getParameterMap());
            } catch (IllegalAccessException | InvocationTargetException ex) {
                Logger.getLogger(ZFramework.class.getName()).log(Level.SEVERE, null, ex);
            }

            formOK = actionForm.validateForm();
            if (!formOK) {
                nextPage = xmlForm.getOrigin();
                response.sendRedirect(request.getContextPath() + nextPage);
            }
        }

        // our form is ok or we haven't a form
        if ((xmlForm != null && formOK) || xmlForm == null) {
            if(xmlAction.getPages() != null){
                for (XMLPage page : xmlAction.getPages()) {
                    if ("default".equals(page.getName())) {
                        defaultPage = page.getUrl();
                    }
                    if (action.execute(request).equals(page.getName())) {
                        nextPage = page.getUrl();
                    }
                }
                if (nextPage.isEmpty() && !defaultPage.isEmpty()) {
                    nextPage = defaultPage;
                }
                if (!nextPage.isEmpty()) {
                    RequestDispatcher requestDispatcher = request.getRequestDispatcher(nextPage);
                    requestDispatcher.forward(request, response);
                }
            }
        }

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
