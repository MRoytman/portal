package com.msf.form;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.msf.form.data.CareCenter;
import com.msf.form.data.Project;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;


@WebServlet(name = "ProjectSelection", urlPatterns = {"/ProjectSelection"})
public class ProjectSelection extends HttpServlet {

	private static final long serialVersionUID = 4074669388737771335L;
	private static final String PROJECT_LIST_FILE = "projects_list_active.txt";
        private static final String CHARACTER_ENCODING = "UTF-8";
        private static final String[] FILE_HEADERS = {"Project Code", "Project Name", "Care Center Code", "Care Center Name"};
    
    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        String country = request.getParameter("country");
        if (country != null) {
            String[] strTokens = country.split(ApplicationConstants.SEPARATOR_VALUE);
            String countryCode = strTokens[0] ;
            String countryName = strTokens[1] ;
            // Put the selected country code and name to the session
            session.setAttribute(ApplicationConstants.COUNTRY_NAME, countryName);
            session.setAttribute(ApplicationConstants.COUNTRY_CODE, countryCode);
            
            List<Project> projectList = parseProjects(countryCode);
            Gson gson = new Gson();
            request.setAttribute("projectList", gson.toJson(projectList));
            if (projectList.isEmpty()) {
                session.setAttribute("existProject", "0");
            } else {
                session.setAttribute("existProject", "1");
            }
            
            String theJSP = "/project.jsp";
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(theJSP);
            dispatcher.forward(request,response);
        } else {
            response.sendRedirect("country.jsp");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("getCareCenters") != null) {
            HttpSession session = request.getSession(true);
            String countryCode = (String) session.getAttribute(ApplicationConstants.COUNTRY_CODE);
            String projectCode = request.getParameter("projectCode");
            List<Project> projectList = parseProjects(countryCode);
            for (Project project: projectList) {
                if (project.getProjectCode().equals(projectCode)) {
                    Gson gson = new Gson();
                    response.getOutputStream().print(gson.toJson(project.getCareCenters()));
                    break;
                }
            }
        } else {
            processRequest(request, response);
        }
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("saveCareCenters") != null) {
            request.setCharacterEncoding(CHARACTER_ENCODING);
            String jsonProjects = request.getParameter("projects");
            jsonProjects = URLEncoder.encode( jsonProjects, "ISO-8859-1" );
            jsonProjects = URLDecoder.decode( jsonProjects, CHARACTER_ENCODING );
            if (jsonProjects != null) {
    		Gson gson = new Gson();
    		Type collectionType = new TypeToken<List<Project>>(){}.getType();
    		List<Project> projectList = gson.fromJson(jsonProjects, collectionType);
                
                saveProjects(projectList);
                
                // Reload the project list from the file
                HttpSession session = request.getSession(true);
                String countryCode = (String) session.getAttribute(ApplicationConstants.COUNTRY_CODE);
                projectList = parseProjects(countryCode);
                request.setAttribute("projectList", gson.toJson(projectList));
                if (projectList.isEmpty()) {
                    session.setAttribute("existProject", "0");
                } else {
                    session.setAttribute("existProject", "1");
                }

                String theJSP = "/project.jsp";
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(theJSP);
                dispatcher.forward(request,response);
            } else {
                response.sendRedirect("country.jsp");
            }
        } else {
            processRequest(request, response);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Project Selection";
    }// </editor-fold>
    
    private List<Project> parseProjects(String countryCode) throws FileNotFoundException, IOException {
        // Retrieve the project list from a CSV file
        String projectFilePath = getServletContext().getRealPath("/project/" + PROJECT_LIST_FILE);
        File projectFile = new File(projectFilePath);
        if (!projectFile.exists() || projectFile.isDirectory()) {
            return null;
        } else {
            List<Project> projectList = new ArrayList<Project>();
            Map<String, Project> projectMap = new HashMap<String, Project>();
            //Reader in = new FileReader(projectFilePath);
            final Reader reader = new InputStreamReader(new FileInputStream(projectFilePath), CHARACTER_ENCODING);
            try {
                Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader(FILE_HEADERS).parse(reader);
                for (CSVRecord record : records) {
                    String projectCode = record.get("Project Code");
                    if (projectCode.startsWith(countryCode)){
                        if (projectMap.containsKey(projectCode)) {
                            // Add a Care Center to the project
                            String ccCode = record.get("Care Center Code");
                            if (ccCode != null && !ccCode.trim().isEmpty()) {
                                    projectMap.get(projectCode).getCareCenters().add(new CareCenter(ccCode, record.get("Care Center Name")));
                            }
                        } else {
                            Project aProject = new Project(projectCode, record.get("Project Name")); // create a project
                            String ccCode = record.get("Care Center Code");
                            if (ccCode != null && !ccCode.trim().isEmpty()) {
                                    aProject.getCareCenters().add(new CareCenter(ccCode, record.get("Care Center Name"))); // add a Care Center to the project
                            }
                            projectList.add(aProject);
                            projectMap.put(projectCode, aProject);
                        }
                    }
                }
            } finally {
                reader.close();
            }
            return projectList;
        }
    }
    
    private void saveProjects(List<Project> projects) throws FileNotFoundException, IOException {
        if (projects == null || projects.isEmpty()) {
            return;
        }
        
        String projectFilePath = getServletContext().getRealPath("/project/" + PROJECT_LIST_FILE);
        //Writer out = new FileWriter(projectFilePath);
        Writer writer = new OutputStreamWriter(new FileOutputStream(projectFilePath), CHARACTER_ENCODING);
        CSVFormat csvFormat = CSVFormat.EXCEL.withHeader(FILE_HEADERS);
        CSVPrinter printer = new CSVPrinter(writer, csvFormat);
        try {
            for (Project project: projects) {
                if (project.getCareCenters() == null || project.getCareCenters().isEmpty()) {
                    printer.print(project.getProjectCode());
                    printer.print(project.getProjectName());
                    printer.print("");
                    printer.print("");
                    printer.println();
                } else {
                    for (CareCenter cc: project.getCareCenters()) {
                        printer.print(project.getProjectCode());
                        printer.print(project.getProjectName());
                        printer.print(cc.getCareCenterCode());
                        printer.print(cc.getCareCenterName());
                        printer.println();
                    }
                }
            }
            printer.flush();
        } finally {
            printer.close();
            writer.close();
        }
    } 
}
