package com.msf.form;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
/**
 * Servlet implementation class CreateForm
 */
@WebServlet(
		urlPatterns = { "/CreateForm" }, 
		initParams = { 
				@WebInitParam(name = "lstName", value = ""), 
				@WebInitParam(name = "lstType", value = "")
		})
public class CreateForm extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateForm() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	PrintWriter out;
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		out=response.getWriter();
        HttpSession session = request.getSession(true);
		File dirResource = new File(session.getAttribute("resourcePath").toString());
		if (!dirResource.exists()) {
			dirResource.mkdirs();
		}

        String folder=session.getAttribute("selectedForm").toString();
        if("f1".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF1.generateResource(strField, request);
        }
        if("f2".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF2.generateResource(strField, request);
        }
        if("f3".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF3.generateResource(strField, request);
        }
        if("f4".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF4.generateResource(strField, request);
        }
        if("f5".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF5.generateResource(strField, request);
        }
        if("f6".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF6.generateResource(strField, request);
        }
        if("f7".compareToIgnoreCase(folder)==0)
        {
    		String[] strField= request.getParameter("lstFiledName").split("<msf_split>");
        	 CreateFormF7.generateResource(strField, request);
        }
        if("f8".compareToIgnoreCase(folder)==0)
        {
        	CreateFormF8.generateResource(request);
        }
        if("f10".compareToIgnoreCase(folder)==0)
        {
        	CreateFormF10.generateResource(request);
        }
        response.sendRedirect(folder+"/summary.jsp");
	}
	
	
}
