package com.msf.form;
import java.io.File;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

public class CreateFormF10 {
	//static PrintWriter out;
	public static void  generateResource(HttpServletRequest request)
	{
        HttpSession session = request.getSession(true);
		File dirResource = new File(session.getAttribute("resourcePath").toString());
		if (!dirResource.exists()) {
			dirResource.mkdirs();
		}
		try{
			session.setAttribute("section1",request.getParameter("section1").toString());
			session.setAttribute("section2",request.getParameter("section2").toString());
			generateProperties(dirResource,session);
			generateSection(dirResource,session,request);
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void generateProperties(File dirResource, HttpSession session)
	{
        String strName=session.getAttribute("CountryName").toString().trim();
        String strCode=session.getAttribute("CountryCode").toString().trim();
        String strType=session.getAttribute("typeForm").toString().trim();
        String[] projectNameSS = session.getAttribute("listProjectNameSS").toString().split("<msf_split>");
        String[] projectCodeSS = session.getAttribute("listProjectCodeSS").toString().split("<msf_split>");
        StringBuilder strFile= new StringBuilder("// FIELDS BELOW ARE PART OF THE FIELD CONFIGURATION................");
        strFile.append("\napplicationTitle PHC "+strType+" "+strCode+" EntryForm 2.5 MSF OCG 2014");
        strFile.append("\napplicationTitleAdmin PHC "+strName+" Admin 1.5 MSF OCG 2014");
        strFile.append("\napplicationShortName "+strType+"_"+strCode+"\ncountryDeployShortName "+strCode+"\n\n");
        
        strFile.append("\n\n// CODES PROJETS");
        for(int i=0;i<projectCodeSS.length;i++)
        {
        	if("".compareToIgnoreCase(projectNameSS[i].trim())!=0)
        		strFile.append("\nCountryCode-"+strCode+"|"+projectNameSS[i].replace(" ", "\\ ")+"|"+projectCodeSS[i]);
        }
        
        strFile.append("\n\n// AGGREGATED THEMES FORMAT\nAggregatedTheme-nutrition_agg	1	nutrition");

        strFile.append("\n\n// USER PWD");
        strFile.append("\nadminUser adminUser\nadminUserPwd ocgncdlbadmin\nmsfUser msfuser\nmsfUserPwd msfuser");
        
        strFile.append("\n\n// FIELDS BELOW ARE USED FOR DEVELOPMENT PURPOSES\nallCountriesFileName list_countries.csv");
        strFile.append("\nworkspaceresource H:\\\\devel\\\\workspace64Bits\\\\"+strType+"_"+strCode+"\\\\src\\\\main\\\\resources\\\\");
        strFile.append("\nworkspaceresourceCommnon H:\\\\devel\\\\workspace64Bits\\\\AggregatedApps\\\\src\\\\main\\\\resources\\\\");
        
        strFile.append("\n\n// devel: network or not\nmixedModeDB false\nmsfApplicationDir C:\\\\MSFMedAppData\n\n// TEST DB\ntestModeDB false");
        
        
        CommonCode.doWrite(dirResource.getPath(), "defaultProperties.properties", strFile.toString());
	}
	
	public static void generateSection(File dirResource, HttpSession session, HttpServletRequest request)
	{

        StringBuilder strId= new StringBuilder();
        StringBuilder strLabel=new StringBuilder();
        
        int counter = 1;
        
        if("1".compareToIgnoreCase(session.getAttribute("section1").toString().trim())==0){
        	strId.append("opd_atfc	" + counter + "\n");
        	strLabel.append("opd_atfc	ATFC	en\n");
        	strLabel.append("opd_atfc	CNTA	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section2").toString().trim())==0){
        	strId.append("ipd_itfc	" + counter + "\n");
        	strLabel.append("ipd_itfc	ITFC	en\n");
        	strLabel.append("ipd_itfc	CNTH	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        }
 
        CommonCode.doWrite(dirResource.getPath(), "AggregatedTheme-nutrition-SectionIds.txt", strId.toString());
        CommonCode.doWrite(dirResource.getPath(), "AggregatedTheme-nutrition-SectionLabels.txt", strLabel.toString());
        
	}
}
