package com.msf.form;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 * Servlet implementation class GenerateJar
 */
@WebServlet("/GenerateJar")
public class GenerateJar extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Logger logger = java.util.logging.Logger.getLogger(GenerateJar.class.getName());
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GenerateJar() {
        super();
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
    }
    
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request,
                            HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession(true);
        // PrintWriter out = response.getWriter();
        
        logger.info("Start to generate Apps");
        String strType = session.getAttribute("typeForm").toString();
        String strCode = session.getAttribute("CountryCode").toString();
        String strName = session.getAttribute("CountryName").toString();
        String strApp = strType + "_" + strCode;
        if ("".compareToIgnoreCase(session.getAttribute("username").toString().trim()) != 0) {
            String folder = session.getAttribute("selectedForm").toString();
            String strDeployPath = session.getAttribute("deployPath").toString();
            logger.info("DeployPath = " + strDeployPath);
            
            String realPath = getServletContext().getRealPath("/");
            String urlServer = CommonCode.readFileProp(realPath, "msfform.properties", "urlServer");
            String urlApp = urlServer + "/ftpmsf/" + strApp;
            
            File dirSh = new File(strDeployPath + "script/");
            File dirSrc = new File(strDeployPath + "commonFiles/");
            File dirDes = new File(strDeployPath + "ftpmsf/" + strApp + "/");
            
            if (!dirDes.exists()) {
                dirDes.mkdirs();
            } else {
                FileUtils.cleanDirectory(dirDes);
            }
            
            ArrayList<String> strOld = new ArrayList<String>();
            ArrayList<String> strNew = new ArrayList<String>();
            
            if ("f1".compareToIgnoreCase(folder) == 0
                    || "f2".compareToIgnoreCase(folder) == 0
                    || "f3".compareToIgnoreCase(folder) == 0
                    || "f4".compareToIgnoreCase(folder) == 0
                    || "f5".compareToIgnoreCase(folder) == 0
                    || "f6".compareToIgnoreCase(folder) == 0
                    || "f7".compareToIgnoreCase(folder) == 0) {
                strOld.add("https://ftp.ocg.msf.org/webstart/CNCD_LB");
                strOld.add("http://ftp.ocg.msf.org/webstart/CNCD_LB");
                strOld.add("Lebanon - CNCD");
                strOld.add("CNDC Lebanon");
                strOld.add("CNCD_LB");
                strOld.add("Lebanon");
                
                strNew.add("http://" + urlApp);
                strNew.add("http://" + urlApp);
                strNew.add(strName + " - " + strType);
                strNew.add(strType + " " + strName);
                strNew.add(strApp);
                strNew.add(strName);
                
                CommonCode.copyDir(dirSrc, dirDes);
                CommonCode.replaceString(dirDes, strOld, strNew);
                CommonCode.copyFile("ncdform.sh", dirDes, dirSh);
                
                logger.info("Start to run the ncdform.sh");
                Runtime.getRuntime().exec("sh ncdform.sh", null, dirSh);
                
                try {
                    boolean x1 = true;
                    int i = 1;
                    while (x1 && i < 50) {
                        if (CommonCode.checkFile(dirDes, strApp + "EntryForm3.0.jar.pack.gz")) {
                            logger.info("---------------------------------------------------------------");
                            logger.info("::::::: All files have been generated successfully after " + i * 5 + " seconds in " + urlApp);
                            logger.info("---------------------------------------------------------------");
                            break;
                        } else {
                            logger.info("Waiting to generate files ...... counter = " + i);
                            Thread.sleep(5000);// 30 sec
                        }
                        i++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            if ("f8".compareToIgnoreCase(folder) == 0
                    || "f9".compareToIgnoreCase(folder) == 0
                    || "f10".compareToIgnoreCase(folder) == 0) {
                strOld.add("https://ftp.ocg.msf.org/webstart/AGG_OPD_LB");
                strOld.add("http://ftp.ocg.msf.org/webstart/AGG_OPD_LB");
                strOld.add("Lebanon - AGG OPD");
                strOld.add("AGG OPD Lebanon");
                strOld.add("AGG_OPD_LB");
                strOld.add("Lebanon");
                
                strNew.add("http://" + urlApp);
                strNew.add("http://" + urlApp);
                strNew.add(strName + " - " + strType);
                strNew.add(strType + " " + strName);
                strNew.add(strApp);
                strNew.add(strName);
                
                CommonCode.copyDir(dirSrc, dirDes);
                CommonCode.replaceString(dirDes, strOld, strNew);
                CommonCode.copyFile("ncdform.sh", dirDes, dirSh);
                
                logger.info("Start to run the ncdform.sh");
                Runtime.getRuntime().exec("sh ncdform.sh", null, dirSh);
                logger.info("Finished running the ncdform.sh");
                
                try {
                    boolean x1 = true;
                    int i = 1;
                    while (x1 && i < 50) {
                        if (CommonCode.checkFile(dirDes, strApp + "EntryForm3.0.jar.pack.gz")) {
                            logger.info("---------------------------------------------------------------");
                            logger.info("::::::: All files have been generated successfully after " + i * 5 + " seconds in: ");
                            logger.info("::::::: " + urlApp);
                            logger.info("---------------------------------------------------------------");
                            break;
                        } else {
                            logger.info("Waiting to generate files ...... counter = " + i);
                            Thread.sleep(5000);// 30 sec
                        }
                        i++;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            session.setAttribute("urlJavaForm", "http://" + urlApp + "/instruction2_en.html");
            response.sendRedirect("writePdf.jsp");
            
        } else {
            response.sendRedirect("index.jsp");
        }
    }
}
