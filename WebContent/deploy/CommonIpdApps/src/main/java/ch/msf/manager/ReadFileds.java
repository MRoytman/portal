package ch.msf.manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadFileds {
	
	public  List<String> readFile(String strFile) {
		String strPath="/ch/msf/fields/f1/";
		InputStream is= getClass().getResourceAsStream(strPath+strFile);
		int ch;
		StringBuilder sb = new StringBuilder();
		try {

			while ((ch = is.read()) != -1)
				sb.append((char) ch);
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//read all code
		
		String lsttmp[]= sb.toString().split("\n");
		List<String> strCode = new ArrayList<String>();
		for(int i=0;i<lsttmp.length;i++)
		{
			if(!lsttmp[i].startsWith("_title")){
				strCode.add(lsttmp[i].split("\t")[0].toString());
			}
		}
		return strCode;
	}
}
