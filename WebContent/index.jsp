<html lang="en">
    <%@include file="./headerForCommon.jsp"%>
    <head>
        <link rel="stylesheet" href="css/home/home.css"/>
    </head>
    <body>
        <div class="main-wrap">
            <div class="title">
                <div class="txt-title-small">MSF OCG Web Portal</div>
                <div class="txt-select">
                    <span>Please Select a Role From Below</span>
                </div>
            </div>
            <ul>
                <li id="hq-medical" class="home-icon">
                    <div class="txt">
                        <span>HQ Medical</span>
                    </div>
                    <div class="img-home">
                        <img alt="" src="img/home/home_hq_medical.png">
                    </div>
                    <ul id="hq-medical-icons">
                        <a href="./login.jsp">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_Database.png">
                                </div>
                                <div class="txt-small">
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
                                <div class="txt-small">
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
                                <div class="txt-small">
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>
                        <a href="https://qv.ocg.msf.org/qlikview/index.htm" target="_blank">
                            <li class="hq-medical-icon">
                                <div class="img">
                                    <img alt="" src="img/hq_medical/HQMed_StatisticsDashboard.png">
                                </div>
                                <div class="txt-small">
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
                                <div class="txt-small">
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="field-worker-data">
                    <div class="txt">
                        <span>Field Worker: Data</span>
                    </div>
                    <div class="img-home">
                        <img alt="" src="img/home/home_field_worker_data.png">
                    </div>
                    <ul id="field-data-icons">
                        <a href="http://ecampus.msf.org/moodlemsf/" target="_blank">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img alt="" src="img/field_data/FieldData_Training.png">
                                </div>
                                <div class="txt-small">
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>
                        <a href="listAll.jsp">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img src="img/field_data/FieldData_Downloads.png">
                                </div>
                                <div class="txt-small">
                                    <span>Downloads</span>
                                </div>
                            </li>
                        </a>
                        <a href="emailSend.jsp">
                            <li class="field-data-icon">
                                <div class="img">
                                    <img src="img/field_data/FieldData_Help.png">
                                </div>
                                <div class="txt-small">
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="field-worker-medical">
                    <div class="txt">
                        <span>Field Worker: Medical</span>
                    </div>
                    <div class="img-home">
                        <img alt="" src="img/home/home_field_worker_medical.png">
                    </div>
                    <ul id="field-med-icons">
                        <a href="http://ecampus.msf.org/moodlemsf/" target="_blank">
                            <li class="field-med-icon">
                                <div class="img">
                                    <img alt="" src="img/field_med/FieldMed_Training.png">
                                </div>
                                <div class="txt-small">
                                    <span>Training</span>
                                </div>
                            </li>
                        </a>
                        <a href="https://qv.ocg.msf.org/qlikview/index.htm" target="_blank">
                            <li class="field-med-icon">
                                <div class="img">
                                    <img alt="" src="img/field_med/FieldMed_StatisticsDashboard.png">
                                </div>
                                <div class="txt-small">
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
                                <div class="txt-small">
                                    <span>Help Desk</span>
                                </div>
                            </li>
                        </a>
                    </ul>
                </li>
                <li id="it-staff">
                    <div class="txt">
                        <span>IT Staff</span>
                    </div>
                    <div class="img-home">
                        <img alt="" src="img/home/home_it_staff.png">
                    </div>
                    <ul id="it-icons">
                        <a href="#">
                            <li class="it-icon">
                                <div class="img">
                                    <img src="img/it/IT_AdminLogin.png">
                                </div>
                                <div class="txt-small">
                                    <span>Administrative</span>
                                    <div>Login</div>
                                </div>
                            </li>
                        </a>
                        <a href="http://sourceforge.net/projects/dotproject/">
                            <li class="it-icon">
                                <div class="img">
                                    <img src="img/it/IT_Triage.png">
                                </div>
                                <div class="txt-small">
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