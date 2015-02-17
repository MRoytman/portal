package com.msf.form;

import java.io.File;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CreateFormF1 {
	public static void  generateResource(String[] strField,HttpServletRequest request)
	{
        HttpSession session = request.getSession(true);
        String srcPath=session.getAttribute("srcPath").toString();//to get origin Encounter files
		File dirResource = new File(session.getAttribute("resourcePath").toString());
		if (!dirResource.exists()) {
			dirResource.mkdirs();
		}
		try{
			StringBuilder strOut=new StringBuilder();
			String strLang=request.getParameter("msf_language");
			StringBuilder strMsf_Combo=new StringBuilder();//<msf_combo> <msf_cb_Items>
			ArrayList<String> lstTypeEnKey=new ArrayList<String>();//<msf_typeEn>
			ArrayList<String> lstLabelEnKey=new ArrayList<String>();//<msf_lableEn>
			ArrayList<String> lstTypeEnVal=new ArrayList<String>();
			ArrayList<String> lstLabelEnVal=new ArrayList<String>();
			
			ArrayList<String> lstTypeEnValDis1=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis1=new ArrayList<String>();
			ArrayList<String> lstTypeEnKeyDis1=new ArrayList<String>();//<msf_typeEnDis>
			ArrayList<String> lstLabelEnKeyDis1=new ArrayList<String>();//<msf_lableEnDis>
			
			ArrayList<String> lstTypeEnValDis2=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis2=new ArrayList<String>();
			ArrayList<String> lstTypeEnKeyDis2=new ArrayList<String>();//<msf_typeEnDis>
			ArrayList<String> lstLabelEnKeyDis2=new ArrayList<String>();//<msf_lableEnDis>
			
			ArrayList<String> lstTypeEnValDis3=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis3=new ArrayList<String>();
			ArrayList<String> lstTypeEnKeyDis3=new ArrayList<String>();//<msf_typeEnDis>
			ArrayList<String> lstLabelEnKeyDis3=new ArrayList<String>();//<msf_lableEnDis>
			
			ArrayList<String> lstTypeEnValDis4=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis4=new ArrayList<String>();
			ArrayList<String> lstTypeEnKeyDis4=new ArrayList<String>();//<msf_typeEnDis>
			ArrayList<String> lstLabelEnKeyDis4=new ArrayList<String>();//<msf_lableEnDis>
			
			ArrayList<String> lstTypeEnValDis5=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis5=new ArrayList<String>();
			ArrayList<String> lstTypeEnKeyDis5=new ArrayList<String>();//<msf_typeEnDis>
			ArrayList<String> lstLabelEnKeyDis5=new ArrayList<String>();//<msf_lableEnDis>
			

			
			
			//***************SPLIT STRING********************
			for(int i=0;i<strField.length;i++)
			{
				if(strField[i].contains("<msf_typeEn>"))
				{
					lstTypeEnKey.add(strField[i]);
					lstTypeEnVal.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEn>"))
				{
					lstLabelEnKey.add(strField[i]);
					lstLabelEnVal.add(request.getParameter(strField[i]));
				}
				//dis 1
				if(strField[i].contains("<msf_typeEnDis1>"))
				{
					lstTypeEnKeyDis1.add(strField[i]);
					lstTypeEnValDis1.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEnDis1>"))
				{
					lstLabelEnKeyDis1.add(strField[i]);
					lstLabelEnValDis1.add(request.getParameter(strField[i]));
				}
				
				//dis 2
				if(strField[i].contains("<msf_typeEnDis2>"))
				{
					lstTypeEnKeyDis2.add(strField[i]);
					lstTypeEnValDis2.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEnDis2>"))
				{
					lstLabelEnKeyDis2.add(strField[i]);
					lstLabelEnValDis2.add(request.getParameter(strField[i]));
				}
				
				//dis 3
				if(strField[i].contains("<msf_typeEnDis3>"))
				{
					lstTypeEnKeyDis3.add(strField[i]);
					lstTypeEnValDis3.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEnDis3>"))
				{
					lstLabelEnKeyDis3.add(strField[i]);
					lstLabelEnValDis3.add(request.getParameter(strField[i]));
				}
				
				//dis 4
				if(strField[i].contains("<msf_typeEnDis4>"))
				{
					lstTypeEnKeyDis4.add(strField[i]);
					lstTypeEnValDis4.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEnDis4>"))
				{
					lstLabelEnKeyDis4.add(strField[i]);
					lstLabelEnValDis4.add(request.getParameter(strField[i]));
				}
				
				//dis 5
				if(strField[i].contains("<msf_typeEnDis5>"))
				{
					lstTypeEnKeyDis5.add(strField[i]);
					lstTypeEnValDis5.add(request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_labelEnDis5>"))
				{
					lstLabelEnKeyDis5.add(strField[i]);
					lstLabelEnValDis5.add(request.getParameter(strField[i]));
				}
				

				
				
				//end dis
				
				
				//******************SPLIT COMBO VALUE*********************************
				if(strField[i].contains("<msf_comboFile>"))
				{
					strMsf_Combo.append("\n"+strField[i]+"<msf_value>"+request.getParameter(strField[i]));
				}
				
				if(strField[i].contains("<msf_cb_Items>"))
				{
					strMsf_Combo.append("\n"+strField[i]+"<msf_value>"+request.getParameter(strField[i]));
				}
				
				
			}
			
			//********************WRITE FILE*****************
			
			strOut=new StringBuilder();
			if(lstTypeEnKey.size()>0){
				for(int i=1;i<lstTypeEnKey.size();i++)
				{
					strOut.append("\n"+lstTypeEnKey.get(i).replace("<msf_typeEn>", "")+"\t"+lstTypeEnVal.get(i));
				}
				if("<msf_typeEn><File>".compareToIgnoreCase(lstTypeEnKey.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnVal.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKey.size()>0){
				for(int i=1;i<lstLabelEnKey.size();i++)
				{
					strOut.append("\n"+lstLabelEnKey.get(i).replace("<msf_labelEn>", "")+"\t"+lstLabelEnVal.get(i)+"\t"+strLang);
				}
				if("<msf_labelEn><File>".compareToIgnoreCase(lstLabelEnKey.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnVal.get(0), strOut.toString().trim());
				
			}
	
			//dis1
			strOut=new StringBuilder();
			if(lstTypeEnKeyDis1.size()>0){
				for(int i=1;i<lstTypeEnKeyDis1.size();i++)
				{
					strOut.append("\n"+lstTypeEnKeyDis1.get(i).replace("<msf_typeEnDis1>", "")+"\t"+lstTypeEnValDis1.get(i));
				}
				if("<msf_typeEnDis1><File>".compareToIgnoreCase(lstTypeEnKeyDis1.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnValDis1.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKeyDis1.size()>0){
				for(int i=1;i<lstLabelEnKeyDis1.size();i++)
				{
					strOut.append("\n"+lstLabelEnKeyDis1.get(i).replace("<msf_labelEnDis1>", "")+"\t"+lstLabelEnValDis1.get(i)+"\t"+strLang);
				}
				if("<msf_labelEnDis1><File>".compareToIgnoreCase(lstLabelEnKeyDis1.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnValDis1.get(0), strOut.toString().trim());
				
			}
			
			//dis2
			strOut=new StringBuilder();
			if(lstTypeEnKeyDis2.size()>0){
				for(int i=1;i<lstTypeEnKeyDis2.size();i++)
				{
					strOut.append("\n"+lstTypeEnKeyDis2.get(i).replace("<msf_typeEnDis2>", "")+"\t"+lstTypeEnValDis2.get(i));
				}
				if("<msf_typeEnDis2><File>".compareToIgnoreCase(lstTypeEnKeyDis2.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnValDis2.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKeyDis2.size()>0){
				for(int i=1;i<lstLabelEnKeyDis2.size();i++)
				{
					strOut.append("\n"+lstLabelEnKeyDis2.get(i).replace("<msf_labelEnDis2>", "")+"\t"+lstLabelEnValDis2.get(i)+"\t"+strLang);
				}
				if("<msf_labelEnDis2><File>".compareToIgnoreCase(lstLabelEnKeyDis2.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnValDis2.get(0), strOut.toString().trim());
				
			}
			
			
			//dis3
			strOut=new StringBuilder();
			if(lstTypeEnKeyDis3.size()>0){
				for(int i=1;i<lstTypeEnKeyDis3.size();i++)
				{
					strOut.append("\n"+lstTypeEnKeyDis3.get(i).replace("<msf_typeEnDis3>", "")+"\t"+lstTypeEnValDis3.get(i));
				}
				if("<msf_typeEnDis3><File>".compareToIgnoreCase(lstTypeEnKeyDis3.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnValDis3.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKeyDis3.size()>0){
				for(int i=1;i<lstLabelEnKeyDis3.size();i++)
				{
					strOut.append("\n"+lstLabelEnKeyDis3.get(i).replace("<msf_labelEnDis3>", "")+"\t"+lstLabelEnValDis3.get(i)+"\t"+strLang);
				}
				if("<msf_labelEnDis3><File>".compareToIgnoreCase(lstLabelEnKeyDis3.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnValDis3.get(0), strOut.toString().trim());
				
			}
			
			
			//dis4
			strOut=new StringBuilder();
			if(lstTypeEnKeyDis4.size()>0){
				for(int i=1;i<lstTypeEnKeyDis4.size();i++)
				{
					strOut.append("\n"+lstTypeEnKeyDis4.get(i).replace("<msf_typeEnDis4>", "")+"\t"+lstTypeEnValDis4.get(i));
				}
				if("<msf_typeEnDis4><File>".compareToIgnoreCase(lstTypeEnKeyDis4.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnValDis4.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKeyDis4.size()>0){
				for(int i=1;i<lstLabelEnKeyDis4.size();i++)
				{
					strOut.append("\n"+lstLabelEnKeyDis4.get(i).replace("<msf_labelEnDis4>", "")+"\t"+lstLabelEnValDis4.get(i)+"\t"+strLang);
				}
				if("<msf_labelEnDis4><File>".compareToIgnoreCase(lstLabelEnKeyDis4.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnValDis4.get(0), strOut.toString().trim());
				
			}
			
			//dis5
			strOut=new StringBuilder();
			if(lstTypeEnKeyDis5.size()>0){
				for(int i=1;i<lstTypeEnKeyDis5.size();i++)
				{
					strOut.append("\n"+lstTypeEnKeyDis5.get(i).replace("<msf_typeEnDis5>", "")+"\t"+lstTypeEnValDis5.get(i));
				}
				if("<msf_typeEnDis5><File>".compareToIgnoreCase(lstTypeEnKeyDis5.get(0).trim())==0)
					CommonCode.doWrite(dirResource.getPath(),lstTypeEnValDis5.get(0), strOut.toString().trim());
				
			}
			
			strOut=new StringBuilder();
			if(lstLabelEnKeyDis5.size()>0){
				for(int i=1;i<lstLabelEnKeyDis5.size();i++)
				{
					strOut.append("\n"+lstLabelEnKeyDis5.get(i).replace("<msf_labelEnDis5>", "")+"\t"+lstLabelEnValDis5.get(i)+"\t"+strLang);
				}
				if("<msf_labelEnDis5><File>".compareToIgnoreCase(lstLabelEnKeyDis5.get(0).trim())==0)
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),lstLabelEnValDis5.get(0), strOut.toString().trim());
				
			}
			
			
			// end dis
			//*****************************WRITE COMBO*****************************
			String[] strFile=strMsf_Combo.toString().split("<msf_comboFile>");
		
			String fileCB="";
			for(int i=1;i<strFile.length;i++)
			{
				
				if(strFile[i].contains("<msf_cb_Items>"))
				{
					//out.print("\n*************\nstr="+strFile[i]);
					
					strOut=new StringBuilder();
					String[] strItems=strFile[i].split("<msf_cb_Items>");//<msf_cb_Items>
					fileCB=strItems[0].split("<msf_value>")[0];
					for(int j=1;j<strItems.length;j++)
					{
	
						String[] strV=strItems[j].split("<msf_value>");
						strV[0]=strV[0].trim().substring(fileCB.length());// why cannot replace dynamic string
						//out.print("\n"+strV[0]);
	
						strOut.append("\n"+strV[0]+"\t"+strV[1].trim() +"\t"+strLang);
						
					}
					//out.print("\n\nFile: "+fileCB);
					CommonCode.doWriteMultiLang(srcPath,dirResource.getPath(),fileCB, strOut.toString().trim());
					
				}else
				{
					fileCB=strFile[i].split("<msf_value>")[1];
					CommonCode.doWrite(dirResource.getPath(),fileCB, "");
				}
			}
			
			session.setAttribute("encounter1",request.getParameter("encounter1").toString());
			session.setAttribute("encounter2",request.getParameter("encounter2").toString());
			session.setAttribute("encounter3",request.getParameter("encounter3").toString());
			session.setAttribute("encounter4",request.getParameter("encounter4").toString());
			session.setAttribute("encounter5",request.getParameter("encounter5").toString());

			generateProperties(dirResource,session);
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
        strFile.append("\napplicationTitle "+strType+" "+strCode+" EntryForm 3.0 MSF OCG 2014");
        strFile.append("\napplicationTitleAdmin 1.5 "+strName+" Admin 1.5 MSF OCG 2014");
        strFile.append("\napplicationShortName "+strType+"_"+strCode+"\ncountryDeployShortName "+strCode+"\n\n");
        
        strFile.append("\n\n// CODES PROJETS");
        for(int i=0;i<projectCodeSS.length;i++)
        {
        	if("".compareToIgnoreCase(projectNameSS[i].trim())!=0)
        		strFile.append("\nCountryCode-"+strCode+"|"+projectNameSS[i].replace(" ", "\\ ")+"|"+projectCodeSS[i]);
        }
        int counter = 1;
        strFile.append("\n\n// ENCOUNTER TYPES");
        if("1".compareToIgnoreCase(session.getAttribute("encounter1").toString().trim())==0)
        	strFile.append("\nEncounterType-Epidemics_opd " + counter++);
        
        if("1".compareToIgnoreCase(session.getAttribute("encounter2").toString().trim())==0)
        	strFile.append("\nEncounterType-Measles_malaria_severe " + counter++);
        
        if("1".compareToIgnoreCase(session.getAttribute("encounter3").toString().trim())==0)
        	strFile.append("\nEncounterType-Yellow_Fever_Case " + counter++);
        
        if("1".compareToIgnoreCase(session.getAttribute("encounter4").toString().trim())==0)
        	strFile.append("\nEncounterType-Cholera_ORS " + counter++);
        
        if("1".compareToIgnoreCase(session.getAttribute("encounter5").toString().trim())==0)
        	strFile.append("\nEncounterType-Cholera_CTC " + counter++);
        
        strFile.append("\n\n//AIRES SANTE");
        try{
	        if("1".compareToIgnoreCase(session.getAttribute("existProject").toString())==0)
	        	strFile.append("\nvillageOrigin.conceptId village_origin\nhealthAreas.file.name liste_aires_de_sante_"+strCode+".txt");
	        else
	        	strFile.append("\n//villageOrigin.conceptId village_origin\n//healthAreas.file.name liste_aires_de_sante_"+strCode+".txt");
	    }catch(Exception e)
        {
        	e.printStackTrace();
        }
        
        strFile.append("\n\n// USER PWD");
        strFile.append("\nadminUser adminUser\nadminUserPwd ocgipdadmin\nmsfUser msfuser\nmsfUserPwd msfuser");
        
        strFile.append("\n\n// FIELDS BELOW ARE USED FOR DEVELOPMENT PURPOSES\nallCountriesFileName list_countries.csv");
        strFile.append("\nworkspaceresource H:\\\\devel\\\\workspace64Bits\\\\"+strType+"_"+strCode+"\\\\src\\\\main\\\\resources\\\\");
        strFile.append("\nworkspaceresourceCommnon H:\\\\devel\\\\workspace64Bits\\\\CommonIpdApps\\\\src\\\\main\\\\resources\\\\");
        
        
        CommonCode.doWrite(dirResource.getPath(), "defaultProperties.properties", strFile.toString());
	}

}
