package com.msf.pdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.msf.form.data.FormType;


@WebServlet("/WritePDFServlet")
public class WritePDFServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    
    /** EncounterType file */
    private static final String STR_ENCOUNTERTYPE                   = "EncounterType";
    private static final String STR_RESOURCES_FOLDER                = "/resources/";
    private static final String STR_PDF_FOLDER                      = "/pdf/";
    private static final String STR_PROPERTIES_FILE_NAME            = "defaultProperties.properties";
    private static final String STR_TXT_FILE_TYPE                   = ".txt";
    private static final String STR_PDF_FILE_TYPE                   = ".pdf";
    private static final String STR_RADIO_BUTTON_IMAGE              = "ui_radio_button_uncheck.png";
    private static final String STR_LOGO_IMAGE                      = "head_logo_msf.gif";
    private static final String STR_CNCD_MEDICINE_FILE              = "cncd_medication.txt";
    
    private static final String STR_PATIENT                         = "Patient";
    private static final String STR_TITLE                           = "_title_";
    private static final String STR_MAIN_TITLE                      = "_main_title_";
    private static final String STR_ENCOUNTER                       = "Encounter";
    private static final String STR_COMBOVALUE                      = "comboValue";
    private static final String STR_COMMENTS                        = "Comments";
    private static final String STR_COUNTRY_DEPLOY_SHORT_NAME       = "countryDeployShortName";
    private static final String STR_ORIGIN_NATIONALITY              = "origin_nationality";
    private static final String STR_ORIGIN_NATIONALITY_OTHER_DETAIL = "origin_nationality_other_detail";
    private static final String STR_MEDICATION                      = "Medication";
    private static final String STR_PATIENT_INFORMATION             = "PATIENT INFORMATION";
    private static final String STR_DISEASE_INFORMATION             = "DISEASE INFORMATION";
    
    private static final String STR_FIELDS_ID_TYPE                  = "-FieldsIdType";
    private static final String STR_FIELDS_LABEL                    = "-FieldsLabel";
    
    private static final String STR_ENLISH_LANGUAGE                 = "en";
    private static final String STR_FRANCE_LANGUAGE                 = "fr";
    
    /** Properties PDF file */
    private static final Font largeBold = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
    private static final Font normalBold = new Font(Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD);
    private static final Font smallNormal = new Font(Font.FontFamily.TIMES_ROMAN, 8.5f, Font.NORMAL);
    
    /** Constant */
    public static final String ENCODE_UTF8 = "UTF-8";
    public static final String SPACE       = "\\s+";
    public static final String UNDERSCORE  = "_";
    
    private String patientNation;
    private String strAppFolder;
    private String pathResources;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public WritePDFServlet() {
        super();
    }
    
    /**
     * @see Servlet#init(ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
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
        HttpSession session = request.getSession(true);
        // Check user login
        if(!isEmpty(session.getAttribute("username").toString().trim())){
            String appFolder = (String) session.getAttribute("appFolder");
            
            if(appFolder != null && appFolder.length() != 0){// Edit form
                strAppFolder = appFolder;
            }else{// Add new form
                String strTypeForm = (String) session.getAttribute("typeForm");
                String strCountryCode = (String) session.getAttribute("CountryCode");
                strAppFolder = strTypeForm + UNDERSCORE + strCountryCode;
            }
            
            pathResources = strAppFolder + STR_RESOURCES_FOLDER;
            
            // Get path contain defaultProperties.properties file
            String pathPropertiesFile = getServletContext().getRealPath(pathResources + STR_PROPERTIES_FILE_NAME);
            // Replace MSFForm text to fptmsf text
            pathPropertiesFile = replaceMSFFormToFtpmsf(pathPropertiesFile);
            
            // Read file of resources folder and write PDF file
            readFilesAndWritePdfFile(pathPropertiesFile);
            
            response.sendRedirect("result.jsp");
        }else{
            response.sendRedirect("index.jsp");
        }
    }
    
    /**
     * Replace from {MSFForm} text to {ftpmsf} text of path
     * 
     * @param path
     * @return String
     */
    private String replaceMSFFormToFtpmsf(String path){
        if(isEmpty(path)) return null;
        return path.replace("MSFForm", "ftpmsf");
    }
    
    /**
     * Read file and create PDF file
     * 
     * @param path
     * @throws FileNotFoundException
     */
    private void readFilesAndWritePdfFile(String path) throws FileNotFoundException{
        readFilesAndWritePdfFile(path, ENCODE_UTF8);
    }
    
    /**
     * Read file and create PDF file
     * 
     * @param path
     * @param encode
     * @throws FileNotFoundException
     */
    private void readFilesAndWritePdfFile(String path, String encode) throws FileNotFoundException{
        if(!isFile(path)) return;
        
        ArrayList<HashMap<String, ArrayList<String>>> contents = new ArrayList<HashMap<String, ArrayList<String>>>();
        String pathFile = "";
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = null;
        BufferedReader br = null;
        try{
            isr = new InputStreamReader(fis, encode);
            br = new BufferedReader(isr);
            String line;
            boolean flag = false;
            
            while((line = br.readLine()) != null){
                // Get nation name and read Patient file
                if(line.startsWith(STR_COUNTRY_DEPLOY_SHORT_NAME)){
                    String[] arr = line.split(SPACE);
                    if(arr.length > 1){
                        patientNation = arr[1];
                    }else{
                        patientNation = "";
                    }
                    
                    // Read patient file
                    pathFile = replaceMSFFormToFtpmsf(getServletContext().getRealPath(pathResources + STR_PATIENT));
                    
                    // Add main title is PATIENT INFORMATION
                    HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
                    hashMap.put(STR_MAIN_TITLE + STR_PATIENT_INFORMATION, new ArrayList<String>());
                    contents.add(hashMap);
                    
                    // Read contents of PATIENT file
                    contents = readTextFile(pathFile, contents, true, ENCODE_UTF8);
                    continue;
                }
                
                // Read EncounterType finished
                if(isEmpty(line) && flag) break;
                
                // Line not EncounterType
                if(isEmpty(line) || !line.startsWith(STR_ENCOUNTERTYPE)) continue;
                
                // Get file name
                String fileName = createFileName(line);
                
                if(!flag){
                    // Add main title DISEASE INFORMATION
                    HashMap<String, ArrayList<String>> hashMap = new HashMap<String, ArrayList<String>>();
                    hashMap.put(STR_MAIN_TITLE + STR_DISEASE_INFORMATION, new ArrayList<String>());
                    contents.add(hashMap);
                }
                
                // Create direction of type file
                pathFile = replaceMSFFormToFtpmsf(getServletContext().getRealPath(pathResources + fileName));
                
                // Get content file
                contents = readTextFile(pathFile, contents, false, ENCODE_UTF8);
                
                flag = true;
            }
        }catch(Exception e){
        }finally{
            try{
                if(br != null) br.close();
                if(isr != null) isr.close();
                if(fis != null) fis.close();
            }catch(Exception e){}
        }
        
        // Write PDF file
        if(contents.size() != 0){
            String pdfFileName = strAppFolder + STR_PDF_FILE_TYPE;
            pathFile = replaceMSFFormToFtpmsf(getServletContext().getRealPath(strAppFolder + "/" + pdfFileName));
            creatPDFFile(contents, pathFile);
        }
    }
    
    /**
     * Check direction of file is correct
     * 
     * @param path
     * @return boolean
     */
    public boolean isFile(String path){
        if(isEmpty(path)) return false;
        File file = new File(path);
        return (file.exists() && file.isFile());
    }
    
    /**
     * Check  empty string
     *
     * @param str
     * @return boolean
     */
    private boolean isEmpty(String str){
        if(str == null || str.length() == 0) return true;
        return false;
    }
    
    /**
     * Create file name
     *
     * @param str
     * @return string file name
     */
    private String createFileName(String str){
        if(isEmpty(str)) return null;
        // Example: String original EncounterType-Epidemics_opd 1;
        //          Convert to EncounterEpidemics_opd-FieldsIdType
        str = str.replace("Type-", "");
        str = str.split(SPACE)[0];
        return str;
    }
    
    /**
     * Read content file
     * 
     * @param fis
     * @param encode
     * @return String
     * @throws FileNotFoundException 
     */
    public ArrayList<HashMap<String, ArrayList<String>>> readTextFile(
            String path,
            ArrayList<HashMap<String, ArrayList<String>>> oldContents,
            boolean isPatient,
            String encode) throws FileNotFoundException{
        // Creative path *.FieldsIdType.txt file, example: /WEB-INF/resources/Patient-FieldsIdType.txt
        String pathId = path + STR_FIELDS_ID_TYPE + STR_TXT_FILE_TYPE;
        // Creative path *.FieldsLabelType.txt file, example: /WEB-INF/resources/Patient-FieldsLabelType.txt
        String pathLabel = path + STR_FIELDS_LABEL + STR_TXT_FILE_TYPE;
        
        if(!isFile(pathId) || !isFile(pathLabel)) return oldContents;
        
        // Get key: {id} and value: {label} map
        HashMap<String, String> idLabelMap = createIdLabelMap(pathLabel, STR_ENLISH_LANGUAGE);
        
        HashMap<String, ArrayList<String>> comboValMap;
        
        FileInputStream fis = new FileInputStream(pathId);
        InputStreamReader isr = null;
        BufferedReader br = null;
        try{
            isr = new InputStreamReader(fis , encode);
            br = new BufferedReader(isr);
            String line;
            String strStartFile = STR_ENCOUNTER;
            
            if(isPatient){
                strStartFile = STR_PATIENT;
                // Add info
                String[] arr = {"Identification number",
                                "Firstname",
                                "Name",
                                ""};
                for(int i = 0; i < arr.length; i++){
                    comboValMap = new HashMap<String, ArrayList<String>>();
                    comboValMap.put(arr[i], new ArrayList<String>());
                    oldContents.add(comboValMap);
                }
            }
            
            while((line = br.readLine()) != null){
                comboValMap = new HashMap<String, ArrayList<String>>();
                String id = line.split(SPACE)[0];
                // Check read Patient file
                if(isPatient){
                    if(id.equals(STR_ORIGIN_NATIONALITY)){
                        ArrayList<String> strs = new ArrayList<String>();
                        strs.add(patientNation);
                        comboValMap.put(idLabelMap.get(id), strs);
                        oldContents.add(comboValMap);
                        continue;
                    }
                    
                    if(id.equals(STR_ORIGIN_NATIONALITY_OTHER_DETAIL)){
                        comboValMap.put(idLabelMap.get(id), new ArrayList<String>());
                        oldContents.add(comboValMap);
                        continue;
                    }
                }
                
                if(idLabelMap.containsKey(id)){
                    // Add five line empty for comments component
                    if(idLabelMap.get(id).equals(STR_COMMENTS)){
                        ArrayList<String> comboVals = new ArrayList<String>();
                        for(int i = 0; i < 5; i++){
                            comboVals.add("");
                        }
                        
                        comboValMap.put(idLabelMap.get(id), comboVals);
                        oldContents.add(comboValMap);
                        continue;
                    }
                    
                    // Get value from comboValue file
                    String pathComboFile = getServletContext().getRealPath(pathResources
                                                                            + strStartFile
                                                                            + "-"
                                                                            + id
                                                                            + "-"
                                                                            + STR_COMBOVALUE
                                                                            + STR_TXT_FILE_TYPE);
                    // Replace MSFForm text to fptmsf text
                    pathComboFile = replaceMSFFormToFtpmsf(pathComboFile);
                    ArrayList<String> comboVals = readComboFile(pathComboFile, STR_ENLISH_LANGUAGE);
                    comboValMap.put(idLabelMap.get(id), comboVals);
                    oldContents.add(comboValMap);
                }
            }
        }catch(Exception e){
        }finally{
            try{
                if(br != null) br.close();
                if(isr != null) isr.close();
                if(fis != null) fis.close();
            }catch(Exception e){}
        }
        return oldContents;
    }
    
    /**
     * Read comboValue file
     * 
     * @param path
     * @param language
     * @return
     * @throws FileNotFoundException
     */
    private ArrayList<String> readComboFile(String path, String language) throws FileNotFoundException {
        if(!isFile(path)) return new ArrayList<String>();
        
        ArrayList<String> comboValues = new ArrayList<String>();
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = null;
        BufferedReader br = null;
        try{
            isr = new InputStreamReader(fis , ENCODE_UTF8);
            br = new BufferedReader(isr);
            String line;
            
            while((line = br.readLine()) != null){
                if(!line.endsWith(language)){
                    continue;
                }
                
                String[] idLabels = line.split(SPACE);
                if(idLabels.length < 2) continue;
                String strCombo = line.substring(idLabels[0].length(), line.length() - language.length());
                comboValues.add(strCombo.trim().replace("--", "-"));
            }
        }catch(Exception e){
        }finally{
            try{
                if(br != null) br.close();
                if(isr != null) isr.close();
                if(fis != null) fis.close();
            }catch(Exception e){}
        }
        return comboValues;
    }
    
    /**
     * Create map key: {id}, value: {label}, language is en or fr
     * 
     * @param path
     * @param language
     * @return
     * @throws FileNotFoundException
     */
    private HashMap<String, String> createIdLabelMap(String path, String language) throws FileNotFoundException {
        if(!isFile(path)) return new HashMap<String, String>();
        
        HashMap<String, String> idLabelMap = new HashMap<String, String>();
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = null;
        BufferedReader br = null;
        try{
            isr = new InputStreamReader(fis , ENCODE_UTF8);
            br = new BufferedReader(isr);
            String line;
            
            while((line = br.readLine()) != null){
                if(!line.endsWith(language)) continue;
                
                // Get key: {id} value: {label}, if label empty value: {id}
                String[] idLabels = line.split(SPACE);
                if(idLabels.length < 2) continue;
                
                // Get label value: weight_kg Weight (kg) en -> Weight (kg)
                String label = line.substring(idLabels[0].length(), line.length() - language.length()).trim();
                if(label.length() == 0){
                     if (idLabels[0].startsWith(UNDERSCORE)) continue; // title empty
                    // Put key and value -> key: {id}, value: {id}
                    idLabelMap.put(idLabels[0], idLabels[0]);
                }else{
                    // CNCD form and Medication label
                    if(strAppFolder.startsWith(FormType.CNCD.getLabel()) && label.startsWith(STR_MEDICATION)){
                        if(label.split("\\s+").length > 1){// Medication 1...n
                            if(path.contains("ncd_fup")){
                                idLabelMap.put(idLabels[0], "ncd_fup" + label);
                                continue;
                            }
                            
                            if(path.contains("ncd_baseline")){
                                idLabelMap.put(idLabels[0], "ncd_baseline" + label);
                                continue;
                            }
                        }
                    }
                    
                    if(idLabels[0].startsWith(UNDERSCORE)) label = STR_TITLE + label; // title
                    // Put key and value -> key: {id}, value: {label}
                    idLabelMap.put(idLabels[0], label);
                }
            }
        }catch(Exception e){
        }finally{
            try{
                if(br != null) br.close();
                if(isr != null) isr.close();
                if(fis != null) fis.close();
            }catch(Exception e){}
        }
        return idLabelMap;
    }
    
    /**
     * Create PDF file
     *
     * @param content
     * @param path
     */
    private void creatPDFFile(ArrayList<HashMap<String, ArrayList<String>>> contentMaps, String path){
        if(contentMaps.size() == 0 || isEmpty(path)) return;
        
        try{
            // step 1
            Document document = new Document();
            document.setPageSize(PageSize.A4);
            document.setMargins(1f, 1f, 10f, 10f);
            document.setMarginMirroring(true);
            
            // step 2
            PdfWriter.getInstance(document, new FileOutputStream(path));
            
            // step 3
            document.open();
            
            // step 4
            String imageUrl = getServletContext().getRealPath(STR_PDF_FOLDER + STR_LOGO_IMAGE);
            Image image = Image.getInstance(imageUrl);
            //image.scaleAbsolute(100f, 100f);
            //image.setAbsolutePosition(5f, 700f);
            document.add(image);
            document.add(createTable(contentMaps));
            
            // step 5
            document.close();
            
            System.out.println("Done");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * Add content for PDF file
     *
     * @param document
     * @param content
     * @throws DocumentException
     * @throws IOException 
     * @throws MalformedURLException 
     */
    private PdfPTable createTable(ArrayList<HashMap<String, ArrayList<String>>> contentMaps)
            throws DocumentException, MalformedURLException, IOException {
        // a table with four columns
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(90);
        float[] relativeWidths = {1, 15, 1, 17};
        float heightRow = 14.5f;
        float titleHeightRow = 18f;
        table.setWidths(relativeWidths);
        // the cell object
        PdfPCell cell;
        // Direction radio button image
        String pathRadioImage = getServletContext().getRealPath(STR_PDF_FOLDER + STR_RADIO_BUTTON_IMAGE);
        
        ArrayList<String> ncdBaselineMedicationList = new ArrayList<String>();
        ArrayList<String> ncdFupMedicationList = new ArrayList<String>();
        if(strAppFolder.startsWith(FormType.CNCD.getLabel())){// Check CNCD form
            int medicationCount = 10;
            // Get medication list
            for(HashMap<String, ArrayList<String>> contentMap : contentMaps){
                for(int i = 1; i <= medicationCount; i++){
                    String ncdBaselineMedication = "ncd_baseline" + STR_MEDICATION + " " + i; // example: ncd_baselineMedication 1
                    // Get medication list of Baseline
                    if(contentMap.containsKey(ncdBaselineMedication)){
                        ncdBaselineMedicationList.add(ncdBaselineMedication);
                    }
                    
                    String ncdFupMedication = "ncd_fup" + STR_MEDICATION + " " + i; // example: ncd_fupMedication 1
                    // Get medication list of Follow
                    if(contentMap.containsKey(ncdFupMedication)){
                        ncdFupMedicationList.add(ncdFupMedication);
                    }
                }
            }
        }
        
        boolean isNcdBaseline = true;
        boolean isNcdFup = true;
        for(HashMap<String, ArrayList<String>> hashMap : contentMaps){
            for(Map.Entry<String, ArrayList<String>> contentSet : hashMap.entrySet()){
                String labelId = contentSet.getKey();
                ArrayList<String> comboVals = contentSet.getValue();
                int comboboxCount = comboVals.size();
                
                // CNCD: Baseline Encounter
                if(ncdBaselineMedicationList.size() > 0){
                    String header = "Prescribed medication";
                    if(labelId.equals(STR_TITLE + header)){
                        continue;
                    }
                    
                    if(labelId.startsWith("ncd_baseline" + STR_MEDICATION)
                            || labelId.contains("Nb per day")){// Check CNCD form
                        
                        if(!isNcdBaseline){// Medication was printed
                            continue;
                        }
                        
                        cell = getNCDMedicationTable(ncdBaselineMedicationList,
                                                    contentMaps,
                                                    STR_CNCD_MEDICINE_FILE,
                                                    header,
                                                    STR_ENLISH_LANGUAGE);
                        table.addCell(cell);
                        isNcdBaseline = false;
                        continue;
                    }
                }
                
                // CNCD: Follow Encounter
                if(ncdFupMedicationList.size() > 0){
                    String header = "If change, new medication:";
                    if(labelId.equals(STR_TITLE + header)){
                        continue;
                    }
                    
                    if(labelId.startsWith("ncd_fup" + STR_MEDICATION)
                            || labelId.contains("Nb per day")){// Check CNCD form
                        
                        if(!isNcdFup){// Medication was printed
                            continue;
                        }
                        
                        cell = getNCDMedicationTable(ncdFupMedicationList,
                                                    contentMaps,
                                                    STR_CNCD_MEDICINE_FILE,
                                                    header,
                                                    STR_ENLISH_LANGUAGE);
                        table.addCell(cell);
                        isNcdFup = false;
                        continue;
                    }
                }
                
                // Add main title label
                if(labelId.startsWith(STR_MAIN_TITLE)){
                    cell = new PdfPCell(new Paragraph(labelId.replace(STR_MAIN_TITLE, ""), largeBold));
                    cell.setFixedHeight(titleHeightRow);
                    cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    cell.setColspan(4);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    continue;
                }
                
                // Add title label
                if(labelId.startsWith(STR_TITLE)){
                    cell = new PdfPCell(new Paragraph(labelId.replace(STR_TITLE, ""), normalBold));
                    cell.setFixedHeight(heightRow);
                    cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    cell.setColspan(4);
                    //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                    continue;
                }
                
                cell = new PdfPCell(new Paragraph(labelId, normalBold));
                cell.setFixedHeight(heightRow);
                
                // Label not contains combobox
                if(comboboxCount == 0){
                    cell.setColspan(2);
                    table.addCell(cell);
                    cell = new PdfPCell(new Phrase(""));
                    cell.setColspan(2);
                    table.addCell(cell);
                    continue;
                }
                
                // Label contains a combobox
                if (comboboxCount == 1) {
                    cell.setColspan(2);
                    table.addCell(cell);
                    cell = new PdfPCell(new Paragraph(comboVals.get(0)));
                    //cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    cell.setColspan(2);
                    table.addCell(cell);
                    continue;
                }
                
                // Add label
                cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                cell.setColspan(4);
                table.addCell(cell);
                
                // Add value of comboBox
                for(int i = 0; i < comboboxCount; i++){
                    if(comboVals.get(i).equals("")){ // Value comboBox empty not add image 
                        cell = new PdfPCell(new Paragraph(comboVals.get(i)));
                    }else{ // Add radio button image
                        Image image = Image.getInstance(pathRadioImage);
                        image.setAbsolutePosition(20f, 20f);
                        cell = new PdfPCell(image, false);
                    }
                    
                    cell.setFixedHeight(heightRow);
                    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    cell.setBorderWidthTop(Rectangle.NO_BORDER);
                    
                    if((i != comboboxCount - 2) && (i != comboboxCount - 1)){
                        cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    }
                    
                    if((comboboxCount % 2 != 0) && (i == comboboxCount - 2)){
                        cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    }
                    
                    if(i % 2 != 0){
                        cell.setBorderWidthLeft(Rectangle.NO_BORDER);
                    }
                    
                    cell.setBorderWidthRight(Rectangle.NO_BORDER);
                    table.addCell(cell);
                    
                    // Add text of comboBox for column
                    cell = new PdfPCell(new Paragraph(comboVals.get(i), smallNormal));
                    cell.setBorderWidthTop(Rectangle.NO_BORDER);
                    
                    if((i != comboboxCount - 2) && (i != comboboxCount - 1)){
                        cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    }
                    
                    if((comboboxCount % 2 != 0) && (i == comboboxCount - 2)){
                        cell.setBorderWidthBottom(Rectangle.NO_BORDER);
                    }
                    
                    if(i % 2 == 0){
                        cell.setBorderWidthRight(Rectangle.NO_BORDER);
                    }
                    
                    cell.setBorderWidthLeft(Rectangle.NO_BORDER);
                    //cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
                    table.addCell(cell);
                }
                
                // Add empty column, if comboboxCount is odd
                if(comboboxCount % 2 != 0){
                    cell = new PdfPCell(new Phrase(""));
                    cell.setBorderWidthLeft(Rectangle.NO_BORDER);
                    cell.setBorderWidthTop(Rectangle.NO_BORDER);
                    cell.setColspan(2);
                    table.addCell(cell);
                }
            }
        }
        return table;
    }
    
    /**
     * Get medicine list of CNCD form
     * 
     * @author thaovd
     * @param pathMedicine
     * @param language
     * @return
     * @throws IOException
     */
    private ArrayList<String> getMedicineList(String pathMedicine, String language) throws IOException{
        BufferedReader buff = new BufferedReader(new FileReader(getServletContext().getRealPath(pathMedicine)));
        String line;
        ArrayList<String> medicineList = new ArrayList<String>();
        medicineList.add(STR_MEDICATION + " name");
        
        while((line = buff.readLine()) != null){
            if(line.endsWith(language)){
                // Remove en or fr characters of end line
                line = line.substring(0, line.length() - language.length()).trim();
                // Get medicine name
                String medicine = line.split("\t")[1].trim();
                medicineList.add(medicine.replace("--", "-"));
                continue;
            }
        }
        buff.close();
        
        return medicineList;
    }
    
    /**
     * Get CNCD medication table
     * 
     * @author thaovd
     * @param ncdMedicineList
     * @param pathMedicine
     * @param language
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    private PdfPCell getNCDMedicationTable(ArrayList<String> ncdMedicineList,
                                            ArrayList<HashMap<String, ArrayList<String>>> contentMaps,
                                            String pathMedicine,
                                            String header,
                                            String language) throws IOException, DocumentException{
        int n = ncdMedicineList.size();
        PdfPTable medicationTable = new PdfPTable(n + 1);
        medicationTable.setWidthPercentage(90);
        float[] columnWidths = new float[n + 1];
        
        // Set size of medicine column
        float medicineColumnWidth = 60f;
        
        // Set size of medication column
        float medicationColumnWidth = 10f;
        
        // Add column size of medication table
        for(int i = 0; i <= n; i++){
            if(i == 0){
                // Set column size of title medicine
                columnWidths[i] = medicineColumnWidth;
                continue;
            }
            columnWidths[i] = medicationColumnWidth;
        }
        medicationTable.setWidths(columnWidths);
        
        // Add header
        PdfPCell cell = new PdfPCell(new Paragraph(header, normalBold));
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(n + 1);
        medicationTable.addCell(cell);
        
        // Get medicine list
        ArrayList<String > medicineList = getMedicineList(pathMedicine, language);
        
        int medicineCount = medicineList.size();
        String medicineName = "";
        String medicationName = "";
        Font font;
        
        for(int i = 0; i < medicineCount; i++){
            for(int j = 0; j <= n; j++){
                if(i == 0){ // Header of medication table
                    font = normalBold;
                    if(j > 0){// Begin header of M1 => M10
                        String num = ncdMedicineList.get(j - 1).split("\\s+")[1]; // Medication 1 => 1
                        medicationName = "M" + num;
                    }
                }else{
                    font = smallNormal;
                    if(j > 0){ // Begin value of M1 => M10
                        if(isExistedMedicine(medicineList.get(i), ncdMedicineList.get(j - 1), contentMaps)){
                            medicationName = "";
                        }else{
                            medicationName = "n/a";
                        }
                    }
                }
                medicineName = medicineList.get(i);
                
                if(j == 0){// Medicine name
                    medicationTable.addCell(new Paragraph(medicineName, font));
                }else{
                    cell = new PdfPCell(new Paragraph(medicationName, font));
                    //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    medicationTable.addCell(cell);
                }
            }
        }
        
        cell = new PdfPCell(medicationTable);
        cell.setRowspan(medicineCount);
        cell.setBorderWidthBottom(Rectangle.NO_BORDER);
        cell.setColspan(4);
        //cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        
        return cell;
    }
    
    /**
     * Check text existed in combobox list
     * 
     * @author thaovd
     * @param medicine
     * @param contentMaps
     * @return
     */
    private boolean isExistedMedicine(String medicine, String medicationKey, ArrayList<HashMap<String, ArrayList<String>>> contentMaps){
        for(HashMap<String, ArrayList<String>> contentMap : contentMaps){
            for(Map.Entry<String, ArrayList<String>> contentSet : contentMap.entrySet()){
                if(contentMap.containsKey(medicationKey)){// medication key existed in map
                    // Get all values of combobox medication
                    ArrayList<String> comboVals = contentSet.getValue();
                    for(String tempMedicine : comboVals){
                        if(medicine.toLowerCase().equals(tempMedicine.toLowerCase())){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}