package com.msf.form;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CreateGeneral
 */
@WebServlet("/CreateGeneral")
public class CreateGeneral extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateGeneral() {
        super();
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        
        // Get direction of resources folder for edit form
        String dirResources = request.getParameter("dirResources");
        if(dirResources != null && dirResources.length() != 0){
            session.setAttribute("dirResources", dirResources);
        }
        
        String folder = request.getParameter("selectForm");
        if (folder != null && folder.length() != 0) {
            session.setAttribute("selectedForm", folder);
            
            String strType = request.getParameter(folder);
            session.setAttribute("typeForm", strType);
            
            // *****************DEPLOY PATH*****************
            String realPath = getServletContext().getRealPath("/");
            session.setAttribute("deployPath", realPath + "deploy" + folder + "/");// set deployPath to session
            session.setAttribute("resourcePath", realPath + "deploy" + folder + "/resources/");// set deployPath to session
            
            File dirResource = new File(session.getAttribute("resourcePath").toString());
            File dirGeneralFiles = new File(realPath + folder + "/Files_general");
            File dirtmpXML = new File(realPath + "/tmpXML/");
            
            CommonCode.copyDir(dirtmpXML, dirResource);
            
            // PATIENT LEVEL
            if ("f1".compareToIgnoreCase(folder) == 0
                    || "f2".compareToIgnoreCase(folder) == 0
                    || "f3".compareToIgnoreCase(folder) == 0
                    || "f4".compareToIgnoreCase(folder) == 0
                    || "f5".compareToIgnoreCase(folder) == 0
                    || "f6".compareToIgnoreCase(folder) == 0
                    || "f7".compareToIgnoreCase(folder) == 0) {
                session.setAttribute("srcPath", realPath + folder + "/");// for multi language
                CommonCode.copyDir(dirGeneralFiles, dirResource);
            }
            
            // AGGREGATE LEVEL
            if ("f8".compareToIgnoreCase(folder) == 0
                    || "f9".compareToIgnoreCase(folder) == 0
                    || "f10".compareToIgnoreCase(folder) == 0) {
                CommonCode.copyDir(dirGeneralFiles, dirResource);
                File dirSectionsFiles = new File(realPath + folder + "/Files_sections");
                CommonCode.copyDir(dirSectionsFiles, dirResource);
            }
            response.sendRedirect("./" + folder + "/" + folder + ".jsp");
        } else {
            response.sendRedirect("f0.jsp");
        }
    }
}
