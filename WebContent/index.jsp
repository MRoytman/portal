<html lang="en">
    <%@include file="./header_new.jsp"%>
    <head>
        <link rel="stylesheet" href="css/home/home.css"/>
    </head>
    <body>
        <div class="main-wrap">
        	<p>
            <div class="title">
                <div class="txt-title-big">HEALTH INFORMATION SYSTEM</div>
                <div class="txt-title-small">MSF OCG Web Portal</div>
                <div class="txt-select">
                    <span>Please Select a Role From Below</span>
                </div>
            </div>
            <ul>
                <li id="hq-medical" class="home-icon">
                    <div class="img">
                        <img alt="" src="img/home/home_hq_medical.png">
                    </div>
                    <div class="txt">
                        <span>HQ Medical</span>
                    </div>
                    <ul id="hq-medical-icons">
                        <a href="./login.jsp">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Database.png">
                                </div>
                                <div>
                                    <span>Database</span>
                                    <div>Generator</div>
                                </div>
                            </li>
                        </a>
                        <a href="./pageNotAvaialble.jsp">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_SurveyCreation.png">
                                </div>
                                <div>
                                    <span>Survey</span>
                                    <div>Creation</div>
                                </div>
                            </li>
                        </a>
                        <a href="http://ecampus.msf.org/moodlemsf/" target="_blank">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Training.png">
                                </div>
                                <div>
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>
                        <a href="https://qv.ocg.msf.org/qlikview/index.htm" target="_blank">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_StatisticsDashboard.png">
                                </div>
                                <div>
                                    <span>Statistics</span>
                                    <div>Dashboard</div>
                                </div>
                            </li>
                        </a>
                        <a href="emailSend.jsp">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Help.png">
                                </div>
                                <div>
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="field-worker-data">
                    <div class="img">
                        <img alt="" src="img/home/home_field_worker_data.png">
                    </div>
                    <div class="txt">
                        <span>Field Worker:</span>
                        <div>Data</div>
                    </div>
                    <ul id="field-data-icons">
                        <a href="http://ecampus.msf.org/moodlemsf/" target="_blank">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Training.png">
                                </div>
                                <div>
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>
                        <a href="#">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img src="img/field_data/FieldData_Downloads.png">
                                </div>
                                <div>
                                    <span>Downloads</span>
                                </div>
                            </li>
                        </a>
                        <a href="emailSend.jsp">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img src="img/field_data/FieldData_Help.png">
                                </div>
                                <div>
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="field-worker-medical">
                    <div class="img">
                        <img alt="" src="img/home/home_field_worker_medical.png">
                    </div>
                    <div class="txt">
                        <span>Field Worker:</span>
                        <div>Medical</div>
                    </div>
                    <ul id="field-med-icons">
                        <a href="http://ecampus.msf.org/moodlemsf/" target="_blank">
                            <li class="field-med-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Training.png">
                                </div>
                                <div>
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>                        
                        <a href="https://qv.ocg.msf.org/qlikview/index.htm" target="_blank">
                            <li class="field-med-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_StatisticsDashboard.png">
                                </div>
                                <div>
                                    <span>Statistics</span>
                                    <div>Dashboard</div>
                                </div>
                            </li>
                        </a>
                        <a href="emailSend.jsp">
                            <li class="field-med-icon">
                                <div class="img">
                                    <img src="img/field_med/FieldMed_Help.png">
                                </div>
                                <div>
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="it-staff">
                    <div class="img">
                        <img alt="" src="img/home/home_it_staff.png">
                    </div>
                    <div class="txt">
                        <span>IT Staff</span>
                    </div>
                    <ul id="it-icons">
                        <a href="#">
                            <li class="it-icon">
                                <div class="img">
                                    <img src="img/it/IT_AdminLogin.png">
                                </div>
                                <div>
                                    <span>Administrative</span>
                                    <div>Login</div>
                                </div>
                            </li>
                        </a>
                        <a href="emailSend.jsp">
                            <li class="it-icon">
                                <div class="img">
                                    <img src="img/it/IT_Triage.png">
                                </div>
                                <div>
                                    <span>Help Desk</span>
                                    <div>Triage</div>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
            </ul>
        </div>
    </body>
    <%@include file="./footer.jsp"%>
</html>