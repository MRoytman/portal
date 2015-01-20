package com.msf.gui;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
public class HeaderFooter {
	public void printHeader(HttpServletRequest request,HttpServletResponse response)
	{
		ServletContext sc = request.getServletContext();
		try {
			sc.getRequestDispatcher("/headerForServlet.jsp").include(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void printFooter(HttpServletRequest request,HttpServletResponse response)
	{
		ServletContext sc = request.getServletContext();
		try {
			sc.getRequestDispatcher("/footerForServlet.jsp").include(request, response);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getAppName(HttpServletRequest request)
	{
		HttpSession session= request.getSession(true);
        String strCode=session.getAttribute("CountryCode").toString().trim();
        String strType=session.getAttribute("typeForm").toString().trim();
        return strType+"_"+strCode;
	}
}