
<%@ page contentType="text/html; charset=UTF-8" %>
<%--@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" --%>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <link type="text/css" rel="stylesheet" href="js/jqwidgets/styles/jqx.base.css" />
    <link type="text/css" rel="stylesheet" href="js/jqwidgets/styles/jqx.darkblue.css" />
    <link type="text/css" rel="stylesheet" href="js/jqalert/jquery.alerts.css" />
    <script type="text/javascript" src="js/jquery-1.11.1.js" ></script>
    <script type="text/javascript" src="js/json2.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxcore.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxdata.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxbuttons.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxscrollbar.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxmenu.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.edit.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.sort.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.filter.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.selection.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxgrid.storage.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxpanel.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxwindow.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxcheckbox.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxlistbox.js" ></script>
    <script type="text/javascript" src="js/jqwidgets/jqxdropdownlist.js" ></script>
    <script type="text/javascript" src="js/jqalert/jquery.ui.draggable.js" ></script>
    <script type="text/javascript" src="js/jqalert/jquery.alerts.js" ></script>

<style>
body{padding:0;margin:0;font-size: 13px;
    font-family: Verdana;}
.myFloatBar{
   bottom:0;
   left:0;
   width:100%;
   position:fixed;
   background-color:#E5E5E5;
}

#addProjectButton
{
    font-size: 13px;
    font-family: Verdana;
}

input[type="button"]
{
    font-size: 13px;
    font-family: Verdana;
}

</style>

    <script type="text/javascript">
        var Constants = {
            PROJECT_GRID_ID: "#jqxProjectGrid",
            CARE_CENTER_GRID_ID: "#jqxCareCenterGrid",
            FORM_ID: "#newConfig",
            POPUP_WINDOW_ID: "#popupWindow",
            POPUP_CLOSE_BUTTON_ID: "#Cancel",
            THEME_PROJECT: "darkblue",
            THEME_CARE_CENTER: "base"
        }       
        
        var countryCode = "${sessionScope.CountryCode}";
        
        var validateProjectField = function(cell, value) {
        	if (value === "") {
        		return true;
        	}

            var dataAdapter = $(Constants.PROJECT_GRID_ID).jqxGrid("source");
            var currentData = dataAdapter.cachedrecords;
        	if (cell.datafield === "projectCode") {
        		for (var i = 0; i < currentData.length; i++) {
        			if (cell.row !== currentData[i].uid && currentData[i].projectCode == value) {
        				return { result: false, message: "Project Code " + value + " already exists. Please choose another one." };
        			}
        		}
        		return true;
        	} else if (cell.datafield === "projectName") {
        		for (var i = 0; i < currentData.length; i++) {
        			if (cell.row !== currentData[i].uid && currentData[i].projectName == value) {
        				return { result: false, message: "Project Name " + value + " already exists. Please choose another one." };
        			}
        		}
        		return true;
        	}
        	return true;
        }
        
        var validateProjectList = function(currentData) {
            for (var i = 0; i < currentData.length; i++) {
            	var currentRecord = currentData[i];
            	if (jQuery.trim(currentRecord.projectCode) === "") {
            		jAlert("Project Code " + currentRecord.projectCode + " is required. Please enter it.", "Save Projects");
            		return false;
            	}
            	if (currentRecord.projectCode.indexOf(countryCode) !== 0) {
            		jAlert("Project Code " + currentRecord.projectCode + " is not valid, it must be started with " + countryCode + ".", "Save Projects");
            		return false;
            	}
                if (jQuery.trim(currentRecord.projectName) === "") {
                	jAlert("Project Name " + currentRecord.projectName + " is required. Please enter it.", "Save Projects");
                    return false;
                }
                for (var j = 0; j < currentData.length; j++) {
                	if (currentRecord.uid != currentData[j].uid) {
                		if (currentData[j].projectCode == currentRecord.projectCode) {
                			jAlert("Project Code " + currentRecord.projectCode + " already exists. Please choose another one.", "Save Projects");
                            return false;
                        }
                		if (currentData[j].projectName == currentRecord.projectName) {
                			jAlert("Project Name " + currentRecord.projectName + " already exists. Please choose another one.", "Save Projects");
                            return false;
                        }
                	}
                }
            }
            return true;
        }
        
        var validateCareCenterField = function(cell, value) {
        	if (value === "") {
        		return true;
        	}

            var dataAdapter = $(Constants.CARE_CENTER_GRID_ID).jqxGrid("source");
            var currentData = dataAdapter.cachedrecords;
        	if (cell.datafield === "careCenterCode") {
        		for (var i = 0; i < currentData.length; i++) {
        			if (cell.row !== currentData[i].uid && currentData[i].careCenterCode == value) {
        				return { result: false, message: "Care Center Code " + value + " already exists. Please choose another one." };
        			}
        		}
        		return true;
        	} else if (cell.datafield === "careCenterName") {
        		for (var i = 0; i < currentData.length; i++) {
        			if (cell.row !== currentData[i].uid && currentData[i].careCenterName == value) {
        				return { result: false, message: "Care Center Name " + value + " already exists. Please choose another one." };
        			}
        		}
        		return true;
        	}
        	return true;
        }
        
        var validateCareCenterList = function(currentData) {
            for (var i = 0; i < currentData.length; i++) {
            	var currentRecord = currentData[i];
            	if (jQuery.trim(currentRecord.careCenterCode) === "") {
            		jAlert("Care Center Code " + currentRecord.careCenterCode + " is required. Please enter it.", "Save Care Centers");
            		return false;
            	}
                if (jQuery.trim(currentRecord.careCenterName) === "") {
                	jAlert("Care Center Name " + currentRecord.careCenterName + " is required. Please enter it.", "Save Care Centers");
                    return false;
                }
                for (var j = 0; j < currentData.length; j++) {
                	if (currentRecord.uid != currentData[j].uid) {
                		if (currentData[j].careCenterCode == currentRecord.careCenterCode) {
                			jAlert("Care Center Code " + currentRecord.careCenterCode + " already exists. Please choose another one.", "Save Care Centers");
                            return false;
                        }
                		if (currentData[j].careCenterName == currentRecord.careCenterName) {
                			jAlert("Care Center Name " + currentRecord.careCenterName + " already exists. Please choose another one.", "Save Care Centers");
                            return false;
                        }
                	}
                }
            }
            return true;
        }
        
        var saveProjectList = function() {
            var dataAdapter = $(Constants.PROJECT_GRID_ID).jqxGrid("source");
            var records = dataAdapter.cachedrecords;
            if (!validateProjectList(records)) {
                return;                    	
            }
            //console.log(dataAdapter.cachedrecords);
            var theForm = $(Constants.FORM_ID);
            theForm.append("<input type='hidden' name='projects' value='"+ JSON.stringify(records)  +"'>");
            theForm.append("<input type='hidden' name='saveCareCenters' value='true'>");
            theForm.attr("action", "./ProjectSelection");
            theForm.submit();
        }
        
        $(document).ready(function () {
        	var data = ${requestScope.projectList};
            
            var editrow = -1;
            
            // prepare the data
            var source =
            {
                datatype: "json",
                datafields: [
                    { name: 'projectCode', type: 'string' },
                    { name: 'projectName', type: 'string' },
                    { name: 'careCenters', type: 'array' }
                ],
                localdata: data,
                addrow: function (rowid, rowdata, position, commit) {
                    // synchronize with the server - send insert command
                    // call commit with parameter true if the synchronization with the server is successful 
                    //and with parameter false if the synchronization failed.
                    // you can pass additional argument to the commit callback which represents the new ID if it is generated from a DB.
                    commit(true);
                },
                updaterow: function (rowid, rowdata, commit) {
                    // synchronize with the server - send update command
                    // call commit with parameter true if the synchronization with the server is successful 
                    // and with parameter false if the synchronization failder.
                    commit(true);
                }
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            
            var projectColumns = [
                      { text: 'Project Code', datafield: 'projectCode', width: 150,
                    	  validation: function (cell, value) {
                              return validateProjectField(cell, value);
                          }
                      },
                      
                      { text: 'Project Name', datafield: 'projectName', width: 250,
                          validation: function (cell, value) {
                              return validateProjectField(cell, value);
                          }
                      },
                      { text: 'Care Centers', datafield: 'CareCenters', width: 150, columntype: 'button', 
                          cellsrenderer: function (rowIndex, columnfield, value, defaulthtml, columnproperties, rowdata) {
                            if (rowdata.careCenters && rowdata.careCenters.length > 0) {
                                return "Show";
                            } else {
                                return " ";
                            }
                          }, buttonclick: function (rowIndex) {
                              showCareCenters(rowIndex);
                          }
                      }
                   ];
            
            var projectStatusBar = function (statusbar) { // rendertoolbar: function (toolbar) { 
                var me = this;
                var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
                var addButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/add.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Add</span></div>");
                var deleteButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/close.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Delete</span></div>");
//                var reloadButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/refresh.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Reload</span></div>");
//                var searchButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/search.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Find</span></div>");
                var saveButton = $("<div style='float: right; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/save.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Save</span></div>");
                container.append(addButton);
                statusbar.append(container);
                container.append(deleteButton);
                container.append(saveButton);
                addButton.jqxButton({  width: 60, height: 20 });
                deleteButton.jqxButton({  width: 65, height: 20 });
                saveButton.jqxButton({  width: 65, height: 20 });
                // add new row.
                addButton.click(function (event) {
                    var datarow = {};
                    datarow["projectCode"] = countryCode;
                    datarow["projectName"] = "";
                    var commit = $(Constants.PROJECT_GRID_ID).jqxGrid('addrow', null, datarow);
                    resetFontForButtons();
                });
                // delete selected row.
                deleteButton.click(function (event) {
                    var selectedrowindexes = $(Constants.PROJECT_GRID_ID).jqxGrid('getselectedrowindexes');
                    if (selectedrowindexes.length > 0) {
                        var selectedRowIDs = [];
                        var rowscount = $(Constants.PROJECT_GRID_ID).jqxGrid('getdatainformation').rowscount;
                        for (var i = 0; i < selectedrowindexes.length; i++) { // Get Row IDs
                            var rowIndex = selectedrowindexes[i];
                            if (rowIndex >= 0 && rowIndex < rowscount) {
                                var rowId = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowid', rowIndex);
                                selectedRowIDs[selectedRowIDs.length] = rowId;
                            }
                        }
                        for (var i = 0; i < selectedRowIDs.length; i++) { // Delete based on Row IDs
                            $(Constants.PROJECT_GRID_ID).jqxGrid('deleterow', selectedRowIDs[i]);
                        }
                    }
                    resetFontForButtons();
                });
                // save changes.
                saveButton.click(function (event) {
                    saveProjectList();
                });
            }
			
            var addfilter = function() {
            	var filtergroup = new $.jqx.filter();
                var filter_or_operator = 1;
                var filtervalue = 'LB';
                var filtercondition = 'starts_with';
                var productCodefilter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
                filtergroup.addfilter(filter_or_operator, productCodefilter);
                
             // add the filters.
                $(Constants.PROJECT_GRID_ID).jqxGrid('addfilter', 'projectCode', filtergroup);
                // apply the filters.
                $(Constants.PROJECT_GRID_ID).jqxGrid('applyfilters');
            }
            
         // Care Center Grid
            var ccDataFields = [
                        { name: 'careCenterCode', type: 'string' },
                        { name: 'careCenterName', type: 'string' }
                    ];
            var ccColumns = [
                             { text: 'Care Center Code', datafield: 'careCenterCode', width: 200,
                                validation: function (cell, value) {
                                    return validateCareCenterField(cell, value);
                                }
                            },
                             { text: 'Care Center Name', datafield: 'careCenterName', width: 250,
                                validation: function (cell, value) {
                                    return validateCareCenterField(cell, value);
                                }
                            }
                          ];
         
            var ccStatusBar = function (statusbar) { // rendertoolbar: function (toolbar) { 
                var me = this;
                var container = $("<div style='overflow: hidden; position: relative; margin: 5px;'></div>");
                var addButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/add.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Add</span></div>");
                var deleteButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/close.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Delete</span></div>");
//                var reloadButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/refresh.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Reload</span></div>");
//                var searchButton = $("<div style='float: left; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/search.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Find</span></div>");
                var saveButton = $("<div style='float: right; margin-left: 5px;'><img style='position: relative; margin-top: 2px;' src='./img/icon/save.png'/><span style='margin-left: 4px; position: relative; top: -3px;'>Save</span></div>");
                container.append(addButton);
                statusbar.append(container);
                container.append(deleteButton);
                addButton.jqxButton({  width: 60, height: 20 });
                deleteButton.jqxButton({  width: 65, height: 20 });
                saveButton.jqxButton({  width: 65, height: 20 });
                container.append(saveButton);
                // add new row.
                addButton.click(function (event) {
                    var datarow = {};
                    datarow["careCenterCode"] = "";
                    datarow["careCenterName"] = "";
                    var commit = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('addrow', null, datarow);
                });
                // delete selected row.
                deleteButton.click(function (event) {
                    var selectedrowindexes = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getselectedrowindexes');
                    if (selectedrowindexes.length > 0) {
                        var selectedRowIDs = [];
                        var rowscount = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getdatainformation').rowscount;
                        for (var i = 0; i < selectedrowindexes.length; i++) { // Get Row IDs
                            var rowIndex = selectedrowindexes[i];
                            if (rowIndex >= 0 && rowIndex < rowscount) {
                                var rowId = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getrowid', rowIndex);
                                selectedRowIDs[selectedRowIDs.length] = rowId;
                            }
                        }
                        for (var i = 0; i < selectedRowIDs.length; i++) { // Delete based on Row IDs
                            $(Constants.CARE_CENTER_GRID_ID).jqxGrid('deleterow', selectedRowIDs[i]);
                        }
                    }
                });
                // save changes.
                saveButton.click(function (event) {
                    if (editrow >= 0) {
                    var rowID = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowid', editrow);
                    var rowData = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowdatabyid', rowID);
                    var dataAdapter = $(Constants.CARE_CENTER_GRID_ID).jqxGrid("source");
                    var records = dataAdapter.cachedrecords;
                    if (!validateCareCenterList(records)) {
                        return;                    	
                    }
                    rowData.careCenters = records;
                    $(Constants.PROJECT_GRID_ID).jqxGrid('updaterow', rowID, rowData);
                    $(Constants.POPUP_WINDOW_ID).jqxWindow('hide');
                    //$(Constants.CARE_CENTER_GRID_ID).jqxGrid('clear');
                    saveProjectList();
                }
                });
            }
            
            var showCareCenters = function(row) {
                // open the popup window when the user clicks a button.
                editrow = row;
                var offset = $(Constants.PROJECT_GRID_ID).offset();
                $(Constants.POPUP_WINDOW_ID).jqxWindow({ position: { x: parseInt(offset.left) - 60, y: parseInt(offset.top) - 60 } });
                // get the clicked row's data and initialize the input fields.
                var dataRecord = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowdata', editrow);
                var ccData;
                if (dataRecord.careCenters && dataRecord.careCenters.length > 0) {
                	ccData = dataRecord.careCenters;
                } else {
                	ccData = [];
                }
                
                $("#projectName").html("of " + dataRecord.projectName);
                
                var dataSource = {
                    datafields: ccDataFields,
                    localdata: ccData,
                    addrow: function (rowid, rowdata, position, commit) {
                        // synchronize with the server - send insert command
                        // call commit with parameter true if the synchronization with the server is successful 
                        //and with parameter false if the synchronization failed.
                        // you can pass additional argument to the commit callback which represents the new ID if it is generated from a DB.
                        commit(true);
                    },
                    updaterow: function (rowid, rowdata, commit) {
                        // synchronize with the server - send update command
                        // call commit with parameter true if the synchronization with the server is successful 
                        // and with parameter false if the synchronization failder.
                        commit(true);
                    }
                }
                var adapter = new $.jqx.dataAdapter(dataSource);

                // Create the Project grid.
                $(Constants.CARE_CENTER_GRID_ID).jqxGrid(
                {
                    width: 550,
                    height: 200,
                    source: adapter,
                    sortable: true,
                    editable: true,
                    editmode: 'dblclick', // 'click', 'dblclick', 'selectedcell'
                    //keyboardnavigation: false,
                    columns: ccColumns,
                    selectionmode: 'checkbox', // singlecell, multiplerows
                    showstatusbar: true, // showtoolbar
                    renderstatusbar: ccStatusBar
                });

                // update data source.
                //$(Constants.CARE_CENTER_GRID_ID).jqxGrid({ source: adapter });
                // show the popup window.
                $(Constants.POPUP_WINDOW_ID).jqxWindow('open');
            } // showCareCenters
            
            // Create the Project grid.
            $(Constants.PROJECT_GRID_ID).jqxGrid(
            {
                width: 580,
                height: 260,
                source: dataAdapter,
                sortable: true,
                editable: true,
                editmode: 'dblclick', // 'click', 'dblclick', 'selectedcell'
                //filterable: true,
                showstatusbar: true, // showtoolbar
                renderstatusbar: projectStatusBar,
                ready: function()
                {
                    // called when the Grid is loaded. Call methods or set properties here.
                	//addfilter();
                },
                selectionmode: 'checkbox', // singlecell
                altrows: true,
                columns: projectColumns,
                theme: Constants.THEME_PROJECT
            });
            
         	// initialize the popup window and buttons.
            $(Constants.POPUP_WINDOW_ID).jqxWindow({
                width: 560, height: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $(Constants.POPUP_CLOSE_BUTTON_ID), modalOpacity: 0.01           
            });
            $(Constants.POPUP_WINDOW_ID).on('open', function () {
                $(Constants.CARE_CENTER_GRID_ID).jqxGrid('selectallrows');
            	//$(Constants.CARE_CENTER_GRID_ID).jqxGrid('clear');
            });
            $("#ccConfirm").jqxButton({ theme: Constants.THEME_PROJECT });
            $("#ccConfirm").on('click', function () {
            	if (editrow >= 0) {
	            	var list = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getselectedrowindexes');
	            	var selectedCareCenters = [];
	            	for (var i = 0; i < list.length; i++) {
	                    // @param bound index. Bound index is the row's index in the array returned by the "getboundrows" method.
	                    var rowid = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getrowid', list[i]);
	                    // @param row id
	                    var data = $(Constants.CARE_CENTER_GRID_ID).jqxGrid('getrowdatabyid', rowid);
	                    selectedCareCenters[i] = data;
	                }
	            	// Save the selected care centers into its parent project with a new property "selectedCareCenters"
                    var projectRowID = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowid', editrow);
                    var projectRowData = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowdatabyid', projectRowID);
                    projectRowData["selectedCareCenters"] = selectedCareCenters;
                    $(Constants.PROJECT_GRID_ID).jqxGrid('updaterow', projectRowID, projectRowData);
            	}
            	$(Constants.POPUP_WINDOW_ID).jqxWindow('hide');
            });
         
            $(Constants.POPUP_CLOSE_BUTTON_ID).jqxButton({ theme: Constants.THEME_PROJECT });
            
            // Change font style of Show buttons
            var resetFontForButtons = function() {
                $(Constants.PROJECT_GRID_ID).find("input[type=button]").each(function() {
                    $(this).css('font-size','13px').css('font-family','Verdana');
                });
            }
            resetFontForButtons();
        }); // dom ready
        
        function submitSelectedRows() {
            var list = $(Constants.PROJECT_GRID_ID).jqxGrid('getselectedrowindexes');
            var theForm = $(Constants.FORM_ID);
            var selectedProjects = [];
            for (var i = 0; i < list.length; i++) {
                // @param bound index. Bound index is the row's index in the array returned by the "getboundrows" method.
                var rowid = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowid', list[i]);
                // @param row id
                var data = $(Constants.PROJECT_GRID_ID).jqxGrid('getrowdatabyid', rowid);
                selectedProjects[i] = data;
                if (data.selectedCareCenters !== undefined) {
                    selectedProjects[i].careCenters = data.selectedCareCenters; // use "selectedCareCenters" property instead of "careCenters" one if it exists
                }
            }
            theForm.append("<input type='hidden' name='projects' value='"+ JSON.stringify(selectedProjects)  +"'>");
            theForm.attr("action", "./CreateConfigXML");
            theForm.submit();
        }
    </script>
    

</head>

<%@include file="./welcomeForCommon.jsp" %>
<%@include file="./bigger.jsp" %>
<center><h1>Selected country: ${sessionScope.CountryName}</h1></center>
<center>
    <form action="CreateConfigXML" id="newConfig" method="post">
        <table>
            <tbody>
                <tr>
                    <th><h2>Select Projects</h2></th>
                </tr>
                <tr>
                    <td>
                        <div id='jqxWidget' style="font-size: 13px; font-family: Verdana; float: top">
                            <div id="jqxProjectGrid" style="float: left">
                            </div>
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>
                        <a href='./modify.jsp'><input type="button" value ="Back"></a>
                        <input type="submit"  value="Confirm" onclick="submitSelectedRows()">
                    </td>
                </tr>
            </tbody>
        </table>
        <div id='jqxWidget' style="font-size: 13px; font-family: Verdana; float: top">
	        <div id="popupWindow">
	            <div><h3>Care Centers <span id="projectName"></h3></div>
	            <div style="overflow: hidden;">
	            	<div id="jqxCareCenterGrid" style="float: left">
	                </div>
	                <p/>
                        <input id="Cancel" type="button" value="Close" style="font-size: 13px; font-family: Verdana;" />
                        <input id="ccConfirm" type="button" value="Confirm" style="font-size: 13px; font-family: Verdana;" />
	            </div>
	        </div>
        </div>
    </form>
</center>
<%@include file="./footer.jsp" %>
</html>