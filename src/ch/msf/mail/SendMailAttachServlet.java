package ch.msf.mail;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * A servlet that takes message details from user and send it as a new e-mail
 * through an SMTP server. The e-mail message may contain attachments which
 * are the files uploaded from client.
 *
 * @author www.codejava.net
 *
 */
@WebServlet("/SendMailAttachServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2,     // 2MB
                maxFileSize = 1024 * 1024 * 10,         // 10MB
                maxRequestSize = 1024 * 1024 * 50)        // 50MB
public class SendMailAttachServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /** Properties of mail */
    private static final String STR_RECIPIENT = "recipient";
    private static final String STR_YOUR_MISSION_PROJECT = "yourMissionProject";
    private static final String STR_YOUR_POSITION = "yourPostion";
    private static final String STR_SUBJECT = "subject";
    private static final String STR_CONTENT = "content";

    /** String config of mail */
    private static final String STR_HOST = "host";
    private static final String STR_PORT = "port";
    private static final String STR_USER = "user";
    private static final String STR_PASSWORD = "password";

    private String host;
    private String port;
    private String user;
    private String pass;

    public void init() {
        // reads SMTP server setting from web.xml file
        ServletContext context = getServletContext();
        host = context.getInitParameter(STR_HOST);
        port = context.getInitParameter(STR_PORT);
        user = context.getInitParameter(STR_USER);
        pass = context.getInitParameter(STR_PASSWORD);
    }

    /**
     * handles form submission
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        List<File> uploadedFiles = saveUploadedFiles(request);

        // Get info from form
        String recipient = request.getParameter(STR_RECIPIENT);
        String yourMissionProject = request.getParameter(STR_YOUR_MISSION_PROJECT);
        String yourPosition = request.getParameter(STR_YOUR_POSITION);
        String subject = request.getParameter(STR_SUBJECT);
        String content = request.getParameter(STR_CONTENT);

/*        // Set parameter
        request.setAttribute(STR_RECIPIENT, recipient);
        request.setAttribute(STR_SUBJECT, subject);
        request.setAttribute(STR_CONTENT, content);

        // Validate
        String errorMessage = "";
        if("".equals(recipient)){
            errorMessage = "<font color='red'><b>You have entered incorrect email</b></font>";
            request.setAttribute("errorMessage", errorMessage);
            this.showError(request, response);
            return;
        }
        if("".equals(subject)){
            errorMessage = "<font color='red'><b>Subject not empty</b></font>";
            request.setAttribute("errorMessage", errorMessage);
            this.showError(request, response);
            return;
        }
        if("".equals(content)){
            errorMessage = "<font color='red'><b>Content not empty</b></font>";
            request.setAttribute("errorMessage", errorMessage);
            this.showError(request, response);
            return;
        }*/

        String resultMessage = "";
        String message = "Mission/project: " + yourMissionProject + ", Position: " + yourPosition + "<br>" + content;
        try {
            EmailUtility.sendEmailWithAttachment(
                    host,
                    port,
                    user,
                    pass,
                    recipient,
                    subject,
                    message,
                    uploadedFiles);
            resultMessage = "The e-mail was sent successfully to: " + recipient;
        } catch (Exception ex) {
            ex.printStackTrace();
            resultMessage = "There were an error: " + ex.getMessage();
        } finally {
            deleteUploadFiles(uploadedFiles);
            request.setAttribute("message", resultMessage);
            getServletContext().getRequestDispatcher("/emailResult.jsp").forward(
                    request, response);
        }
    }

    /**
     * Saves files uploaded from the client and return a list of these files
     * which will be attached to the e-mail message.
     */
    private List<File> saveUploadedFiles(HttpServletRequest request)
            throws IllegalStateException, IOException, ServletException {
        List<File> listFiles = new ArrayList<File>();
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        Collection<Part> multiparts = request.getParts();
        if (multiparts.size() > 0) {
            for (Part part : request.getParts()) {
                // creates a file to be saved
                String fileName = extractFileName(part);
                if (fileName == null || fileName.equals("")) {
                    // not attachment part, continue
                    continue;
                }

                File saveFile = new File(fileName);
                System.out.println("saveFile: " + saveFile.getAbsolutePath());
                FileOutputStream outputStream = new FileOutputStream(saveFile);

                // saves uploaded file
                InputStream inputStream = part.getInputStream();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();

                listFiles.add(saveFile);
            }
        }
        return listFiles;
    }

    /**
     * Retrieves file name of a upload part from its HTTP header
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return null;
    }

    /**
     * Deletes all uploaded files, should be called after the e-mail was sent.
     */
    private void deleteUploadFiles(List<File> listFiles) {
        if (listFiles != null && listFiles.size() > 0) {
            for (File aFile : listFiles) {
                aFile.delete();
            }
        }
    }

    /*private void showError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        RequestDispatcher rd = request.getRequestDispatcher("EmailForm.jsp");
        rd.include(request, response);
    }*/
}