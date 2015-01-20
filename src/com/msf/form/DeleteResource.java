package com.msf.form;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 * Servlet implementation class DeleteResource
 */
@WebServlet("/DeleteResource")
public class DeleteResource extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteResource() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dirResource = request.getParameter("deleteForm");
        
        // Delete last resource
        if (dirResource != null && dirResource.length() != 0) {
            File resourceFile = new File(dirResource);
            if (resourceFile.exists()) {
                FileUtils.deleteDirectory(resourceFile);
            }
        }
        
        response.sendRedirect("modify.jsp");
    }
}
