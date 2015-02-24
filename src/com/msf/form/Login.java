package com.msf.form;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

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
        Map<String, String[]> userInfo = request.getParameterMap();
        String username = Arrays.asList(userInfo.get("user")).get(0);
        String password = Arrays.asList(userInfo.get("pass")).get(0);

        boolean isLoginSuccess = false;
        HttpSession session = request.getSession(true);

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

        if(isLoginSuccess){
            // Set user name for session
            session.setAttribute("username", username);
            // Get context path
            String backURL = (String) session.getAttribute("backURL");
            if(backURL != null && backURL.length() != 0){
                response.getWriter().write(backURL);
                //response.sendRedirect(backURL);
            }else{
                response.getWriter().write("dataStorage.jsp");
                //response.sendRedirect("dataStorage.jsp");
            }
            response.setStatus(200);
        }else{
            response.setStatus(400);
        }
    }
}
