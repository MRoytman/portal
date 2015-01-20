<%@page import="java.io.File"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
 <%


	File dirEncounterFiles2=new File(session.getServletContext().getRealPath("/f3/Files_encounters/"));
	if (!dirEncounterFiles2.exists()) {
		dirEncounterFiles2.mkdirs();
		out.print("First time: need to copy the resources.");
	}else{
			String encounterPath1 = session.getServletContext().getRealPath("/f3/Files_encounters/");
		 	String fileNameTypeDis1="Encounterpaediatrics-FieldsIdType.txt";
		 	String fileNameLabelDis1="Encounterpaediatrics-FieldsLabel.txt";
		 	String strTitle="Paediatrics";//read from encounterLabel.txt
		 	String xmlFileTypeDis1="<msf_typeEnDis2><File>";
		 	String xmlFileLabelDis1="<msf_labelEnDis2><File>";
		    String xmlLableDis1="<msf_labelEnDis2>";
		    String xmlTypeDis1="<msf_typeEnDis2>";
		 	String strCheck="encounter2";
	        String language = "en";
		 	
		 	String prefixCBDis1="Encounter-";
		 	String subfixCBDis1="-comboValue.txt";

			ArrayList<String> lstNameEnKeyDis1=new ArrayList<String>();
			ArrayList<String> lstTypeEnValDis1=new ArrayList<String>();
			ArrayList<String> lstLabelEnValDis1=new ArrayList<String>();
			ArrayList<String> lstComboDis1=new ArrayList<String>();
			ArrayList<String> lstComboDisTitle=new ArrayList<String>();

           	BufferedReader rdTypeDis1 = new BufferedReader(new FileReader(encounterPath1+ "/"+fileNameTypeDis1));
          	BufferedReader rdLabelDis1 = new BufferedReader(new FileReader(encounterPath1+ "/"+fileNameLabelDis1));
			StringBuilder sbComboDis1 = new StringBuilder();
	        String txtFileComboDis1;
	        BufferedReader readerCbDis1;
	        String lineCbDis1;
	        String[] strSplitCbDis1;//1 ... other
           	String lineTypeDis1,lineLabelDis1;
           	String[] strSplitDis1;
           
            while((lineTypeDis1 = rdTypeDis1.readLine())!= null){
        	   	strSplitDis1=lineTypeDis1.split("\t");
        	   	lstNameEnKeyDis1.add(strSplitDis1[0]);
        	   	lstTypeEnValDis1.add(strSplitDis1[1]);
        	   	try{
	        		lineLabelDis1=rdLabelDis1.readLine();//en
	        		
	        		if(lineLabelDis1.endsWith("en"))
	        		{
	        			lineLabelDis1=lineLabelDis1.subSequence(0, lineLabelDis1.length()-2).toString().trim();
	        			lstLabelEnValDis1.add(lineLabelDis1.replace(strSplitDis1[0], "").trim());	
	        			lineLabelDis1=rdLabelDis1.readLine().trim();//es
	        		}
        	   	}catch(Exception e){
        	   		/*
        	   		comments	Comments	en
					comments	Commentaires	fr
        	   		*/
        			lstLabelEnValDis1.add("");
        	   	}
           }
           //out.print(sb1.toString());
           rdTypeDis1.close();
           rdLabelDis1.close();
           
           /*
           * Edit by thaovd
           * @since 2015-01-19
           */
           HashMap<String, String> idLabelMap = new HashMap<String, String>();
           boolean isEdit = false;
           boolean isChecked = false;
           // Get direction of resources folder when click edit button
           String dirResources = (String) session.getAttribute("dirResources");
           // Check edit or add form
           if(dirResources != null && dirResources.length() != 0){// Edit form
               String dirLabelFile = dirResources + fileNameLabelDis1;
               File labelFile = new File(dirLabelFile);
               if(labelFile.exists()){
                  // Get resource
                   FileInputStream fis = new FileInputStream(dirLabelFile);
                   InputStreamReader isr = null;
                   BufferedReader br = null;
                   try{
                      isr = new InputStreamReader(fis, "UTF-8");
                      br = new BufferedReader(isr);
                      String line;
                      
                      while((line = br.readLine()) != null){
                          
                          if (!line.endsWith(language)) continue;
                          
                          // Get key: {id} value: {label}, if label empty value: {id}
                          String[] idLabels = line.split("\\s+");
                          if (idLabels.length < 2) continue;
                          
                          // Get label value: weight_kg Weight (kg) en -> Weight (kg)
                          String label = line.substring(idLabels[0].length(), line.length() - language.length());
                          if (label.trim().length() == 0) {
                              // Put key and value -> key: {id}, value: {id}
                              idLabelMap.put(idLabels[0], idLabels[0]);
                          } else {
                              // Put key and value -> key: {id}, value: {label}
                              idLabelMap.put(idLabels[0], label.trim());
                          }
                          isEdit = true;
                      }
                   }catch(Exception e){
                   }finally{
                       try{
                           if(br != null) br.close();
                           if(isr != null) isr.close();
                           if(fis != null) fis.close();
                       }catch(Exception e){}
                   }
               }
               
               // Check choose checkbox
               String dirPropertiesFile = dirResources + "defaultProperties.properties";
               File file = new File(dirPropertiesFile);
               if(file.exists()){
                   BufferedReader bufferedReader = new BufferedReader(new FileReader(dirPropertiesFile));
                   String strLine = "";
                   while((strLine = bufferedReader.readLine())!= null){
                       if (strLine.contains("paediatrics")) {
                           isChecked = true;
                           break;
                       }
                   }
                   bufferedReader.close();
               }
           }

%>
            <a  href="javascript:toggle('<%=fileNameTypeDis1 %>');">
            <h2><%=strTitle %></a>&nbsp;&nbsp;&nbsp;&nbsp;Select:&nbsp;<input type="checkbox" name="<%=strCheck%>" value="1" <% if(!isEdit || isChecked){ %>checked<% } %>><input type="hidden" name="<%=strCheck%>" value="0"> 
            </h2><div id="<%=fileNameTypeDis1 %>" style="display: none" href="javascript:toggle();">    
            
            <input name="<%=xmlFileTypeDis1%>" type="hidden"  value="<%=fileNameTypeDis1%>">
            <input name="<%=xmlFileLabelDis1%>" type="hidden" value="<%=fileNameLabelDis1%>"> 
            
            <table border="1"><tr><td><strong>Field Title</strong></td><td><strong>Content</strong></td><td><strong>Select</strong></td></tr>
<%                      
           for(int i=0;i<lstNameEnKeyDis1.size();i++)
           {
               if(lstTypeEnValDis1.get(i).contains("Combo-"))
               {
                   lstComboDis1.add(lstNameEnKeyDis1.get(i));
                   lstComboDisTitle.add(lstLabelEnValDis1.get(i));
                   if("".compareToIgnoreCase(lstLabelEnValDis1.get(i))!=0){
%>                 
                   <tr><td><%=lstLabelEnValDis1.get(i)%><input name="<%=xmlLableDis1 %><%=lstNameEnKeyDis1.get(i)%>" type="hidden"  value="<%=lstLabelEnValDis1.get(i)%>">
                   <input name="<%=xmlTypeDis1%><%=lstNameEnKeyDis1.get(i)%>"  type="hidden" value="<%=lstTypeEnValDis1.get(i)%>"></td><td><a href="#" onClick="pop('<%=lstNameEnKeyDis1.get(i)%>')">Define</a></td><td><input type="checkbox" name="msf_checked" <% if (!isEdit || idLabelMap.containsKey(lstNameEnKeyDis1.get(i))) { %>checked <% } %> value="<%=lstNameEnKeyDis1.get(i)%>" ></td></tr>
<%
                   }else
                   {
                       %>                  
                       <tr><td><%=lstLabelEnValDis1.get(i)%><input name="<%=xmlLableDis1 %><%=lstNameEnKeyDis1.get(i)%>" type="hidden"  value="<%=lstLabelEnValDis1.get(i)%>">
                       <input name="<%=xmlTypeDis1%><%=lstNameEnKeyDis1.get(i)%>"  type="hidden" value="<%=lstTypeEnValDis1.get(i)%>"></td><td><a href="#" onClick="pop('<%=lstNameEnKeyDis1.get(i)%>')">Define</a></td><td><input type="checkbox" style="display:none"  name="msf_checked"  <% if (!isEdit || idLabelMap.containsKey(lstNameEnKeyDis1.get(i))) { %>checked <% } %> value="<%=lstNameEnKeyDis1.get(i)%>" ></td></tr>
                         <%                
                   }
               }else
               {
                   if("".compareToIgnoreCase(lstLabelEnValDis1.get(i))!=0){
%>
                    <tr><td><%=lstLabelEnValDis1.get(i)%><input name="<%=xmlLableDis1 %><%=lstNameEnKeyDis1.get(i)%>"  type="hidden" value="<%=lstLabelEnValDis1.get(i)%>">
                    <input name="<%=xmlTypeDis1%><%=lstNameEnKeyDis1.get(i)%>" type="hidden"  value="<%=lstTypeEnValDis1.get(i)%>"></td><td></td><td><input type="checkbox" name="msf_checked"  <% if (!isEdit || idLabelMap.containsKey(lstNameEnKeyDis1.get(i))) { %>checked <% } %> value="<%=lstNameEnKeyDis1.get(i)%>"></td></tr>
<%        
                   }else
                   {
                       %>
                    <tr><td><%=lstLabelEnValDis1.get(i)%><input name="<%=xmlLableDis1 %><%=lstNameEnKeyDis1.get(i)%>"  type="hidden" value="<%=lstLabelEnValDis1.get(i)%>">
                    <input name="<%=xmlTypeDis1%><%=lstNameEnKeyDis1.get(i)%>" type="hidden"  value="<%=lstTypeEnValDis1.get(i)%>"></td><td></td><td><input type="checkbox" style="display:none" name="msf_checked"  <% if (!isEdit || idLabelMap.containsKey(lstNameEnKeyDis1.get(i))) { %>checked <% } %> value="<%=lstNameEnKeyDis1.get(i)%>"></td></tr>
   <%    
                   }
               }
           }
%>
            </table></div>
<%
           //create div for combobox

           for(int i=0;i<lstComboDis1.size();i++)
           {
               %>
               <div id="<%=lstComboDis1.get(i)%>" class="ontop">
                <table border="1" id="popup"><tr><td>
                    <input type="hidden"   name="<msf_comboFile><%=prefixCBDis1%><%=lstComboDis1.get(i) %><%=subfixCBDis1%>" value="<%=prefixCBDis1%><%=lstComboDis1.get(i) %><%=subfixCBDis1%>" readonly>  
                    <input type="text"  style="width:580px;font-weight: bold;" value="<%=lstComboDisTitle.get(i) %>" readonly>  
                    
                    <a href="#" onClick="hide('<%=lstComboDis1.get(i)%>')" style="float: right;"><input type="button" value="X" style="background-color: red"></a>
                    <div>
                    <%
                    //load file combobox
                    txtFileComboDis1 = prefixCBDis1+lstComboDis1.get(i)+subfixCBDis1;
                    readerCbDis1 = new BufferedReader(new FileReader(encounterPath1+"/"+txtFileComboDis1));
                    
                    /*
                    * Edit by thaovd
                    * @since 2015-01-19
                    */
                    // Read comboBox
                    HashMap<String, String> comboMap = new HashMap<String, String>();
                    boolean isCombo = false;
                    if(dirResources != null && dirResources.length() != 0){// Edit form
                        String dirResourceCombo = dirResources + txtFileComboDis1;
                        File comboFile = new File(dirResourceCombo);
                        if(comboFile.exists()){
                            // Get resource
                            FileInputStream fis = new FileInputStream(dirResourceCombo);
                            InputStreamReader isr = null;
                            BufferedReader br = null;
                            try {
                                isr = new InputStreamReader(fis, "UTF-8");
                                br = new BufferedReader(isr);
                                String line;
                                int h = 0;
                                while((line = br.readLine()) != null){
                                    
                                    if (!line.endsWith(language)) continue;
                                    
                                    // Get key: {id} value: {label}, if label empty value: {id}
                                    String[] idLabels = line.split("\\s+");
                                    if (idLabels.length < 2) continue;
                                    
                                    // Get label value: weight_kg Weight (kg) en -> Weight (kg)
                                    String label = line.substring(idLabels[0].length(), line.length() - language.length());
                                    if (label.trim().length() == 0) {
                                        // Put key and value -> key: {id}, value: {id}
                                        comboMap.put(String.valueOf(h), String.valueOf(h));
                                    } else {
                                        // Put key and value -> key: {id}, value: {label}
                                        comboMap.put(String.valueOf(h), label.trim());
                                    }
                                    h++;
                                    isCombo = true;
                                }
                            }catch(Exception e){
                            }finally{
                                try{
                                    if(br != null) br.close();
                                    if(isr != null) isr.close();
                                    if(fis != null) fis.close();
                                }catch(Exception e){}
                            }
                        }
                    }
                    
                    while((lineCbDis1 = readerCbDis1.readLine())!= null){
                        strSplitCbDis1=lineCbDis1.split("\t");
                        if("en".compareToIgnoreCase(strSplitCbDis1[strSplitCbDis1.length-1])==0)
                        {
                            %>
                            <input name="<msf_cb_Items><%=txtFileComboDis1%><%=strSplitCbDis1[0]%>" type= "text"  value="<%=strSplitCbDis1[1]%>" readonly>
                            <input name="msf_combo_checked" type="checkbox" value="<msf_cb_Items><%=txtFileComboDis1%><%=strSplitCbDis1[0]%>" <% if (!isCombo || comboMap.containsValue(strSplitCbDis1[1])) { %>checked<% } %>>
                            <br>
                            <%
                        }
                    }
                    readerCbDis1.close();
                    %>
                    </div>
                    </td></tr><tr><td>
                    <a href="#" onClick="hide('<%=lstComboDis1.get(i)%>')" style="float: right;">SAVE</a>
                </td></tr></table>
                </div>
               <%
           }
    }
%>
