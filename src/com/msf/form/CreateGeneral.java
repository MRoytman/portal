package com.msf.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Get app folder for edit form
        String appFolder = request.getParameter("appFolder");
        if(appFolder != null && appFolder.length() != 0){// Edit form
            // CNCD_LB => form type is CNCD, country code is LB
            String strTypeForm = appFolder.split("_")[0];
            String strCountryCode = appFolder.split("_")[1];
            String strCountryName = getCountryNameWithCode(session, strCountryCode);

            // Set session when user edit form
            session.setAttribute("typeForm", strTypeForm);
            session.setAttribute("CountryCode", strCountryCode);
            session.setAttribute("CountryName", strCountryName);

            // Set session for project list
            String dirPropertiesFile = dirResources + "defaultProperties.properties";
            getProjectsAndSetSession(session, dirPropertiesFile);
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

    /**
     * Get country name with code country
     *
     * @author thaovd
     * @param session
     * @param codeCountry
     * @return
     * @throws IOException
     */
    private String getCountryNameWithCode(HttpSession session, String codeCountry) throws IOException{
        BufferedReader rdCountry = new BufferedReader(new FileReader(session.getServletContext().getRealPath("/list_countries.csv")));
        String lineCountry;
        String[] strSplitCountry;
        String countryName = "";
        while((lineCountry = rdCountry.readLine()) != null){
            // Example: AF   AFG Afghanistan Afghanistan Afganist�n  Asia    Southern Asia
            strSplitCountry = lineCountry.split("\\t");
            if(codeCountry.equals(strSplitCountry[0])){ // Example: Code country is  AF
                countryName = strSplitCountry[2];// Example: Country name is Afghanistan
                break;
            }
        }
        rdCountry.close();

        return countryName;
    }

    /**
     * Get projectName and projectCode, set session
     * 
     * @author thaovd
     * @param session
     * @param path
     * @throws IOException
     */
    private void getProjectsAndSetSession(HttpSession session, String path) throws IOException{
        if(path == null && path.length() == 0){
            return;
        }
        
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = null;
        BufferedReader br = null;
        isr = new InputStreamReader(fis , "UTF-8");
        br = new BufferedReader(isr);
        String line;
        List<String> projectNameList = new ArrayList<String>();
        List<String> projectCodeList = new ArrayList<String>();

        while((line = br.readLine()) != null){
            if(line.startsWith("CountryCode")){
                String[] arrProjectInfos = line.split("|");
                projectNameList.add(arrProjectInfos[1].replace("\\ ", " "));
                projectCodeList.add(arrProjectInfos[2]);
            }
        }

        // Set session of projectName and projectCode
        session.setAttribute("listProjectNameSS", projectNameList);
        session.setAttribute("listProjectCodeSS", projectCodeList);
    }
}
