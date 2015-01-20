package com.msf.form;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msf.form.data.CareCenter;
import com.msf.form.data.Project;

/**
 * Servlet implementation class CreateConfigXML
 */
@WebServlet("/CreateConfigXML")
public class CreateConfigXML extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateConfigXML() {
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
		String jsonProjects = request.getParameter("projects");
        PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(true);
		String countryCode = (String) session.getAttribute(ApplicationConstants.COUNTRY_CODE);
		StringBuilder strXML=new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		strXML.append("\n<entryFormConfig>\n\t<selectedCountries>\n\t\t<code>"+countryCode+"</code>");
        StringBuilder listProjectNameSS = new StringBuilder("");
        StringBuilder listProjectCodeSS = new StringBuilder("");
        if (jsonProjects != null) {
    		Gson gson = new Gson();
    		Type collectionType = new TypeToken<List<Project>>(){}.getType();
    		List<Project> projectList = gson.fromJson(jsonProjects, collectionType);
    		
			for(Project project: projectList)
			{
				strXML.append("\n\t\t<projects>");
				if (project.getCareCenters() != null) {
					for (CareCenter cc: project.getCareCenters()) {
						strXML.append("\n\t\t\t<careCenters>\n\t\t\t\t<code>" + cc.getCareCenterCode() + "</code>");
						strXML.append("\n\t\t\t\t<name>" + cc.getCareCenterName() + "</name>\n\t\t\t\t<retired>false</retired>\n\t\t\t</careCenters>");
					}
				}
				strXML.append("\n\t\t\t<code>"+project.getProjectCode()+"</code>" + "\n\t\t\t<name>"+project.getProjectName()+"</name>\n\t\t\t<retired>false</retired>\n\t\t</projects>");
				listProjectNameSS.append(project.getProjectName()).append("<msf_split>");
				listProjectCodeSS.append(project.getProjectCode()).append("<msf_split>");
			}
			//String countryName = (String) session.getAttribute(ApplicationConstants.COUNTRY_NAME);
			//strXML.append("\n\t\t<name>"+ countryName +"</name>");
			strXML.append("\n\t\t<retired>false</retired>\n\t</selectedCountries>\n</entryFormConfig>");
			out.print(strXML.toString());
			
			session.setAttribute("listProjectNameSS", listProjectNameSS);
			session.setAttribute("listProjectCodeSS", listProjectCodeSS);
			
			
			String realPath = getServletContext().getRealPath("/");
			

			
			File dirtmpXML = new File(realPath+"/tmpXML");
			
			if (!dirtmpXML.exists()) {
				dirtmpXML.mkdirs();
			}else
			{
				FileUtils.cleanDirectory(dirtmpXML); 
			}
			
			CommonCode.doWrite(dirtmpXML.getPath(), "entryFormConfig.xml", strXML.toString());
			
			out.print("\nFile exist: "+session.getAttribute("existProject").toString());
			
			if("1".compareToIgnoreCase(session.getAttribute("existProject").toString())==0)
			{
				try{
					File dirSrc=new File(realPath+"project/");
					CommonCode.copyFile("projects_list_active.txt", dirSrc, dirtmpXML);
					CommonCode.copyFile("liste_aires_de_sante_"+countryCode+".txt", dirSrc, dirtmpXML);
				}catch(Exception e)
				{
					e.printStackTrace();
				}
			}
            response.sendRedirect("f0.jsp");
			
        }
        else {
            out.println("Invalid parameter");
            response.sendRedirect("index.jsp");
       }
	}



}
