package com.msf.form;

import java.io.File;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CreateFormF8 {
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
			session.setAttribute("section3",request.getParameter("section3").toString());
			session.setAttribute("section4",request.getParameter("section4").toString());
			session.setAttribute("section5",request.getParameter("section5").toString());
			session.setAttribute("section6",request.getParameter("section6").toString());
			session.setAttribute("section7",request.getParameter("section7").toString());
			session.setAttribute("section8",request.getParameter("section8").toString());
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
        
        strFile.append("\n\n// AGGREGATED THEMES FORMAT\nAggregatedTheme-opd_phc	1	phc");

        strFile.append("\n\n// USER PWD");
        strFile.append("\nadminUser adminUser\nadminUserPwd ocgncdlbadmin\nmsfuser msfUser\nmsfUserPwd msfuser");
        
        strFile.append("\n\n// FIELDS BELOW ARE USED FOR DEVELOPMENT PURPOSES\nallCountriesFileName list_countries.csv");
        strFile.append("\nworkspaceresource H:\\\\devel\\\\workspace64Bits\\\\"+strType+"_"+strCode+"\\\\src\\\\main\\\\resources\\\\");
        strFile.append("\nworkspaceresourceCommnon H:\\\\devel\\\\workspace64Bits\\\\AggregatedApps\\\\src\\\\main\\\\resources\\\\");
        
        strFile.append("\n\n// devel: network or not\nmixedModeDB false\nmsfApplicationDir D:\\\\MSFMedAppData\n\n// TEST DB\ntestModeDB false");
        
        CommonCode.doWrite(dirResource.getPath(), "defaultProperties.properties", strFile.toString());
	}
	
	public static void generateSection(File dirResource, HttpSession session, HttpServletRequest request)
	{
        StringBuilder strId= new StringBuilder();
        StringBuilder strLabel=new StringBuilder();
        
        int counter = 1;
        
        if("1".compareToIgnoreCase(session.getAttribute("section1").toString().trim())==0){
        	strId.append("opd_parameters	" + counter + "\n");
        	strLabel.append("opd_parameters	General parameters	en\n");
        	strLabel.append("opd_parameters	Paramètres généraux	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section2").toString().trim())==0){
        	strId.append("opd_morbidity	" + counter + "\n");
        	strLabel.append("opd_morbidity	Morbidity	en\n");
        	strLabel.append("opd_morbidity	Morbidité	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section3").toString().trim())==0){
        	strId.append("opd_consultations_referrals	" + counter + "\n");
        	strLabel.append("opd_consultations_referrals	Consultations & referrals	en\n");
        	strLabel.append("opd_consultations_referrals	Consultations et références	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section4").toString().trim())==0){
        	strId.append("opd_dressing_injections	" + counter + "\n");
        	strLabel.append("opd_dressing_injections	Dressing & injections	en\n");
        	strLabel.append("opd_dressing_injections	Pansements et injections	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section5").toString().trim())==0){
        	strId.append("opd_nutrition_screening	" + counter + "\n");
        	strLabel.append("opd_nutrition_screening	Malnutrition screening	en\n");
        	strLabel.append("opd_nutrition_screening	Screening malnutrition	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section6").toString().trim())==0){
        	strId.append("opd_laboratory	" + counter + "\n");
        	strLabel.append("opd_laboratory	Laboratory	en\n");
        	strLabel.append("opd_laboratory	Laboratoire	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section7").toString().trim())==0){
        	strId.append("opd_vaccination	" + counter + "\n");
        	strLabel.append("opd_vaccination	Vaccination	en\n");
        	strLabel.append("opd_vaccination	Vaccination	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
        if("1".compareToIgnoreCase(session.getAttribute("section8").toString().trim())==0){
        	strId.append("opd_reproductive	" + counter + "\n");
        	strLabel.append("opd_reproductive	Reproductive health	en\n");
        	strLabel.append("opd_reproductive	Santé reproductive	fr\n");
        	CommonCode.writeFileSection(dirResource, request, Integer.toString(counter));
        	counter++;
        }
       
        CommonCode.doWrite(dirResource.getPath(), "AggregatedTheme-phc-SectionIds.txt", strId.toString());
        CommonCode.doWrite(dirResource.getPath(), "AggregatedTheme-phc-SectionLabels.txt", strLabel.toString());
        
	}
	
}
