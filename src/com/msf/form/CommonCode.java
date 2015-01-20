package com.msf.form;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

public class CommonCode {
    Logger logger = java.util.logging.Logger.getLogger(GenerateJar.class.getName());

	public static String readFileProp(String dirPath, String file, String name) {
		StringBuilder sb;
		BufferedReader reader;
		String line;
		try {
			sb = new StringBuilder();
			try {
				reader = new BufferedReader(
						new FileReader(dirPath + "/" + file));
				try {
					while ((line = reader.readLine()) != null) {
						sb.append("\n" + line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strFile = sb.toString();
			return strFile.split("<" + name + ">")[1].split("</" + name + ">")[0]
					.trim();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return "";
	}

	public static void copyDir(File dirSrc, File dirDes) {
		File[] fList = dirSrc.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				try {
					Files.copy(file.toPath(), new File(dirDes.getAbsolutePath()
							+ "/" + file.getName()).toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
	}

	public static void copyFile(String fileName, File dirSrc, File dirDes) {

		try {
			Files.copy(new File(dirSrc.getAbsolutePath() + "/" + fileName)
					.toPath(), new File(dirDes.getAbsolutePath() + "/"
					+ fileName).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void doWrite(String desPath, String filename, String input) {
		BufferedWriter out = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(desPath + "/" + filename.trim());
			out = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			out.write(input);
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	public static String readFile(String dirPath, String file) {
		StringBuilder sb;
		BufferedReader reader;
		String line;
		try {
			sb = new StringBuilder();
			try {
				reader = new BufferedReader(
						new FileReader(dirPath + "/" + file));
				try {
					while ((line = reader.readLine()) != null) {
						sb.append("\n" + line);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return sb.toString();

		} catch (Exception e) {

			e.printStackTrace();
		}
		return "";
	}
	
	public static void doWriteMultiLang(String srcPath,String desPath, String filename, String input) {
		BufferedWriter out = null;
		FileWriter fw = null;
		String strPath="";
		String strOrg="";
		StringBuilder strAddLang=new StringBuilder();
		try {
			fw = new FileWriter(desPath + "/" + filename.trim());
			out = new BufferedWriter(fw);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			// read string input, split, find ID 
			// open file origin in srcPath, find label of another language rely on ID
			// add to output string to write file
			if(checkFile(new File(srcPath+"Files_encounters"), filename))
				strPath=srcPath+"Files_encounters";
			if(checkFile(new File(srcPath+"Files_patients"), filename))
				strPath=srcPath+"Files_patients";
			
			strOrg=readFile(strPath, filename.trim());
			
			String[] arrInput=input.split("\n");
			String[] arrOrg=strOrg.split("\n");
			ArrayList<String> arrID=new ArrayList<String>();
			ArrayList<String> arrIDOrg=new ArrayList<String>();
			ArrayList<String> arrLineOrg=new ArrayList<String>();
			
			for(int i=0;i<arrInput.length;i++)
			{
				arrID.add(arrInput[i].split("\t")[0]);
			}
			for(int j=0;j<arrOrg.length;j++)
			{
				arrIDOrg.add(arrOrg[j].split("\t")[0]);
			}
			for(int k=0;k<arrOrg.length;k++)
			{
				arrLineOrg.add(arrOrg[k]);
			}

			for(int r=0;r<arrID.size();r++){
				for(int s=0;s<arrIDOrg.size();s++)
				{
					if(arrIDOrg.get(s).compareToIgnoreCase(arrID.get(r))==0)
					{
						strAddLang.append(arrLineOrg.get(s)+"\n");
					}
				}
			}
			
			out.write(strAddLang.toString());
		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public static boolean checkFile(File dirDes, String file) {
		File f = new File(dirDes.getAbsolutePath() + "/" + file);
		if (f.exists()) {
			return true;
		}
		return false;
	}

	public static void replaceString(File dirPath, ArrayList<String> strOld,
			ArrayList<String> strNew) {
		BufferedReader reader;
		String line;
		StringBuilder sb;
		File[] fList = dirPath.listFiles();
		if (fList.length == 0)
			return;
		for (File file : fList) {
			if (file.isFile()) {
				try {
					sb = new StringBuilder();
					try {
						reader = new BufferedReader(new FileReader(
								dirPath.getAbsolutePath() + "/"
										+ file.getName()));
						try {
							while ((line = reader.readLine()) != null) {
								sb.append("\n" + line);
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							reader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String strFile = sb.toString();
					for (int i = 0; i < strOld.size(); i++)
						strFile = strFile.replace(strOld.get(i), strNew.get(i));
					doWrite(dirPath.getAbsolutePath(), file.getName(), strFile);

				} catch (Exception e) {

					e.printStackTrace();
				}
			}
		}

	}
	
	public static void writeFileSection(File dirResource, HttpServletRequest request,String strSection)
	{
        StringBuilder strAGG=new StringBuilder();
        String strResult="";
    	int totalTb=Integer.valueOf(request.getParameter("TotalTableSection"+strSection).toString().trim());
    	for(int i=0;i<totalTb;i++)
    		strAGG.append(CommonCode.genStrTb(i,strSection,request));
    	strResult=strAGG.toString();
    	if(strAGG.toString().endsWith("SectionSeparator\n"))
    		strResult=strResult.substring(0, strResult.lastIndexOf("SectionSeparator\n"));
    	
    	CommonCode.doWrite(dirResource.getPath(), request.getParameter("fileSection"+strSection).toString(), strResult);
	}
	
	public static String genStrTb(int iTb,String strSection, HttpServletRequest request)
	{
		StringBuilder str=new StringBuilder();
		String xmlTb="<msf_tableAGG_"+strSection+">";
		String xmlRows="<msf_rowAGG_"+strSection+">";
		String strFirstRow="";
		String strNextRow="";
		String[] arrStr=new String[4];
		String strNewFirstRow="";
		int totalRow=0;
		int realRow=0;
		//for table i
		String xmlTbi=xmlTb+"["+String.valueOf(iTb)+"]";
		String xmlTbiRow=xmlRows+"["+String.valueOf(iTb)+"]";
		strFirstRow=request.getParameter(xmlTbi).toString();
		if("0".compareToIgnoreCase(strFirstRow)!=0)
		{
			arrStr=strFirstRow.split("\t");
			totalRow=Integer.valueOf(arrStr[3]);
			strNewFirstRow=arrStr[0]+"\t"+arrStr[1]+"\t"+arrStr[2]+"\t";
			for(int i=1;i<=totalRow;i++)
			{
				strNextRow=request.getParameter(xmlTbiRow+"["+String.valueOf(i)+"]").toString();
				if("0".compareToIgnoreCase(strNextRow)!=0){
					str.append(strNextRow+"\n");
					realRow++;
				}
			}
			if(realRow==0)
				return"";
			str.append("SectionSeparator\n");
			strNewFirstRow+=String.valueOf(realRow)+"\n";
			return strNewFirstRow+str.toString();
		}
		return "";
	}
}
