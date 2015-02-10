package com.msf.form;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
        // TODO Auto-generated method stub
        String username = request.getParameter("user");
        String password = request.getParameter("pass");
        //PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(true);
        boolean isLoginSuccess = false;

        // User is admin
        if(username.toLowerCase().trim().equals("admin")
                && password.toLowerCase().trim().equals("admin")){
            session.setAttribute("userType", "isAdmin");
            isLoginSuccess = true;
        }

        // User is msfuser
        if(username.toLowerCase().trim().equals("msfuser")
                && password.toLowerCase().trim().equals("msfuser")){
            session.setAttribute("userType", "isUser");
            isLoginSuccess = true;
        }

            /*
            try{
                String loged= session.getAttribute("username").toString();
                if(loged.compareToIgnoreCase(username)==0)
                {
                    session.setAttribute("Error","This user is logged in!");
                    response.sendRedirect("index.jsp");
                }else
                {
                    session.setAttribute("username", username);  
                    response.sendRedirect("country.jsp");
                }
            }catch(Exception e){
                response.sendRedirect("index.jsp");
            }
            */

        if(isLoginSuccess){
            // Set user name for session
            session.setAttribute("username", username);
            // Get context path
            String backURL = (String) session.getAttribute("backURL");
            if(backURL != null && backURL.length() != 0){
                response.sendRedirect(backURL);
            }else{
                response.sendRedirect("dataStorage.jsp");
            }
        }else{
            session.setAttribute("Error","Invalid username and password!");
            response.sendRedirect("login.jsp");
       }
    }
}
