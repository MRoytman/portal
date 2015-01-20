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
	String patientPath = session.getServletContext().getRealPath("/f3/Files_patients/");
	File dirPatientFiles=new File(patientPath);
	
	if (!dirPatientFiles.exists()) {
		dirPatientFiles.mkdirs();
		out.print("First time: need to copy the resources.");
	}else{
		 	String fileNameType="Patient-FieldsIdType.txt";
		 	String fileNameLabel="Patient-FieldsLabel.txt";
		 	String xmlFileType="<msf_typeEn><File>";
		 	String xmlFileLabel="<msf_labelEn><File>";
		 	String xmlLable="<msf_labelEn>";
		 	String xmlType="<msf_typeEn>";
		 	String prefixCB="Patient-";
		 	String subfixCB="-comboValue.txt";
		 	String language = "en";
			ArrayList<String> lstNameEnKey=new ArrayList<String>();
			ArrayList<String> lstTypeEnVal=new ArrayList<String>();
			ArrayList<String> lstLabelEnVal=new ArrayList<String>();
			ArrayList<String> lstCombo=new ArrayList<String>();		
			ArrayList<String> lstComboTitle=new ArrayList<String>();	
           BufferedReader rdType = new BufferedReader(new FileReader(patientPath+ "/"+fileNameType));
           BufferedReader rdLabel = new BufferedReader(new FileReader(patientPath+ "/"+fileNameLabel));

           String lineType,lineLabel;
           String[] strSplit;
           while((lineType = rdType.readLine())!= null){
        	   	strSplit=lineType.split("	");
        	   	lstNameEnKey.add(strSplit[0]);
        	   	lstTypeEnVal.add(strSplit[1]);
        		lineLabel=rdLabel.readLine().trim();//en
        		if(lineLabel.endsWith("en"))
        		{
        			lineLabel=lineLabel.subSequence(0, lineLabel.length()-2).toString();
        			lstLabelEnVal.add(lineLabel.replace(strSplit[0], ""));	
        			lineLabel=rdLabel.readLine().trim();//fr
        		}
           }
           //out.print(sb1.toString());
           rdType.close();
           rdLabel.close();
           StringBuilder sbCombo = new StringBuilder();
           StringBuilder sb = new StringBuilder();
           
           /*
           * Edit by thaovd
           * @since 2015-01-19
           */
           HashMap<String, String> idLabelMap = new HashMap<String, String>();
           boolean isEdit = false;
           // Get direction of resources folder when click edit button
           String dirResources = (String) session.getAttribute("dirResources");
           // Check edit or add form
           if(dirResources != null && dirResources.length() != 0){// Edit form
               String dirLabelFile = dirResources + fileNameLabel;
               File labelFile = new File(dirLabelFile);
               if(labelFile.exists()){
                  // Get resource
                   FileInputStream fis = new FileInputStream(dirLabelFile);
                   InputStreamReader isr = null;
                   BufferedReader br = null;
                   try{
                      isr = new InputStreamReader(fis , "UTF-8");
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
           }
%>
           <input name="<%=xmlFileType%>" type="hidden"  value="<%=fileNameType%>">
           <input name="<%=xmlFileLabel%>" type="hidden" value="<%=fileNameLabel%>"> 
           <table border="1"><tr><td><strong>Field Title</strong></td><td><strong>Content</strong></td><td><strong>Select</strong></td></tr>
<%                      
          for(int i=0;i<lstNameEnKey.size();i++)
          {
              if(lstTypeEnVal.get(i).contains("Combo-")&&!lstTypeEnVal.get(i).contains("["))
              {
                  lstCombo.add(lstNameEnKey.get(i));
                  lstComboTitle.add(lstLabelEnVal.get(i));
                  if("".compareToIgnoreCase(lstLabelEnVal.get(i))!=0){
%>                 
                  <tr><td><%=lstLabelEnVal.get(i)%><input name="<%=xmlLable %><%=lstNameEnKey.get(i)%>" type="hidden"  value="<%=lstLabelEnVal.get(i)%>">
                  <input name="<%=xmlType%><%=lstNameEnKey.get(i)%>"  type="hidden" value="<%=lstTypeEnVal.get(i)%>"></td><td><a href="#" onClick="pop('<%=lstNameEnKey.get(i)%>')">Define</a></td><td><input type="checkbox" name="msf_checked" <% if (!isEdit || idLabelMap.containsKey(lstNameEnKey.get(i))) { %>checked <% } %> value="<%=lstNameEnKey.get(i)%>" ></td></tr>
<%
                  }
                  else
                  {
                      %>                  
                      <tr><td><%=lstLabelEnVal.get(i)%><input name="<%=xmlLable %><%=lstNameEnKey.get(i)%>" type="hidden"  value="<%=lstLabelEnVal.get(i)%>">
                      <input name="<%=xmlType%><%=lstNameEnKey.get(i)%>"  type="hidden" value="<%=lstTypeEnVal.get(i)%>"></td><td><a href="#" onClick="pop('<%=lstNameEnKey.get(i)%>')">Define</a></td><td><input type="checkbox" style="display:none"  name="msf_checked"  <% if (!isEdit || idLabelMap.containsKey(lstNameEnKey.get(i))) { %>checked <% } %> value="<%=lstNameEnKey.get(i)%>" ></td></tr>
                       <%                 
                  }
              }else
              {
                  if("".compareToIgnoreCase(lstLabelEnVal.get(i).trim())!=0){
                   %>
                   <tr><td><%=lstLabelEnVal.get(i)%><input name="<%=xmlLable %><%=lstNameEnKey.get(i)%>"  type="hidden"  value="<%=lstLabelEnVal.get(i)%>">
                   <input name="<%=xmlType%><%=lstNameEnKey.get(i)%>" type="hidden"  value="<%=lstTypeEnVal.get(i)%>"></td><td></td><td><input type="checkbox" name="msf_checked" <% if (!isEdit || idLabelMap.containsKey(lstNameEnKey.get(i))) { %>checked <% } %> value="<%=lstNameEnKey.get(i)%>"></td></tr>
                   <%   
                  }else
                  {
                   %>
                   <tr><td><%=lstLabelEnVal.get(i)%><input name="<%=xmlLable %><%=lstNameEnKey.get(i)%>"  type="hidden"  value="<%=lstLabelEnVal.get(i)%>">
                   <input name="<%=xmlType%><%=lstNameEnKey.get(i)%>" type="hidden"  value="<%=lstTypeEnVal.get(i)%>"></td><td></td><td><input type="checkbox" style="display:none"  name="msf_checked" <% if (!isEdit || idLabelMap.containsKey(lstNameEnKey.get(i))) { %>checked <% } %> value="<%=lstNameEnKey.get(i)%>"></td></tr>
                   <%                     
                  }
              }
          }
%>
           </table>
<%
          //create div for combobox
          String txtFileCombo;
          BufferedReader readerCb;
          String lineCb;
          String[] strSplitCb;
          for(int i=0;i<lstCombo.size();i++)
          {
              %>
              <div id="<%=lstCombo.get(i)%>" class="ontop">
               <table border="1" id="popup"><tr><td>
                   <input type="hidden" name="<msf_comboFile><%=prefixCB%><%=lstCombo.get(i) %><%=subfixCB%>" value="<%=prefixCB%><%=lstCombo.get(i) %><%=subfixCB%>" readonly>
                   <input type="text" style="width:580px;font-weight: bold;" value="<%=lstComboTitle.get(i) %>" readonly>  
                   <a href="#" onClick="hide('<%=lstCombo.get(i)%>')" style="float: right;"><input type="button" value="X" style="background-color: red"></a>
                   <div>
                   <%
                   //load file combobox
                   txtFileCombo = prefixCB+lstCombo.get(i)+subfixCB;
                   readerCb = new BufferedReader(new FileReader(patientPath+"/"+txtFileCombo));
                   
                   /*
                   * Edit by thaovd
                   * @since 2015-01-19
                   */
                   // Read comboBox
                   HashMap<String, String> comboMap = new HashMap<String, String>();
                   boolean isCombo = false;
                   if(dirResources != null && dirResources.length() != 0){// Edit form
                       String dirResourceCombo = dirResources + txtFileCombo;
                       File comboFile = new File(dirResourceCombo);
                       if(comboFile.exists()){
                           // Get resource
                           FileInputStream fis = new FileInputStream(dirResourceCombo);
                           InputStreamReader isr = null;
                           BufferedReader br = null;
                           try {
                               isr = new InputStreamReader(fis , "UTF-8");
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
                   
                   while((lineCb = readerCb.readLine())!= null){
                       strSplitCb=lineCb.split("   ");
                       if("en".compareToIgnoreCase(strSplitCb[strSplitCb.length-1])==0)
                       {
                           %>
                           <input name="<msf_cb_Items><%=txtFileCombo%><%=strSplitCb[0]%>"  type= "text" value="<%=strSplitCb[1]%>" readonly>
                           <input name="msf_combo_checked" type="checkbox" value="<msf_cb_Items><%=txtFileCombo%><%=strSplitCb[0]%>" <% if (!isCombo || comboMap.containsValue(strSplitCb[1])) { %>checked <% } %> >
                           <br>
                           <%
                       }
                   }
                   readerCb.close();
                   %>
                   </div>
                   </td></tr><tr><td>
                   <a href="#" onClick="hide('<%=lstCombo.get(i)%>')" style="float: right;">SAVE</a>
               </td></tr></table>
               </div>
              <%
          }
   }
%>
