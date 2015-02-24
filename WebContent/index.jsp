<html lang="en">
    <%@include file="./headerForCommon.jsp"%>
    <head>
        <link rel="stylesheet" href="css/home/home.css"/>
        <script src="js/jquery-1.11.1.js"></script>
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
                        <a id="hq-login">
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
                        <a id="fd-login">
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
                        <a id="it-login">
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
        <div id="id-popup-modal"></div>
        <div style="margin: 0 auto;text-align: center;">
            <div id="id-modal-hq-login" style="display: none;">
                <div class="img-login" style="">
                    <img src="img/login/hqm_key_login.png"/>
                </div>
                <div class="popup">
                    <div class="title-login">HQ LOGIN</div>
                    <br>
                    <div class="error-hq"></div>
                    <br>
                    <div>
                        <input type="text" id="user-hq" name="user" class="txt-field" value="" placeholder="Username">
                    </div>
                    <br>
                    <div>
                        <input type="password" id="pass-hq" name="pass" class="txt-field" value="" placeholder="* * * * * * * * * * *">
                    </div>
                    <br>
                    <div>
                        <input type="submit" class="btn-hq-login" value="LOGIN">
                    </div>
                    <div style="margin-top: 25px">
                        <a href="#" class="txt-forgot-pwd">Forgot Password?</a>
                    </div>
                </div>
            </div>
        </div>
        <div style="margin: 0 auto;text-align: center;">
            <div id="id-modal-fd-login" style="display: none;">
                <div class="img-login" style="">
                    <img src="img/login/fd_key_login.png"/>
                </div>
                <div class="popup">
                    <div class="title-login">DATA LOGIN</div>
                    <br>
                    <div class="error-fd"></div>
                    <br>
                    <div>
                        <input type="text" id="user-fd" name="user" class="txt-field" value="" placeholder="Username">
                    </div>
                    <br>
                    <div>
                        <input type="password" id="pass-fd" name="pass" class="txt-field" value="" placeholder="* * * * * * * * * * *">
                    </div>
                    <br>
                    <div>
                        <input type="submit" class="btn-fd-login" value="LOGIN">
                    </div>
                    <div style="margin-top: 25px">
                        <a href="#" class="txt-forgot-pwd">Forgot Password?</a>
                    </div>
                </div>
            </div>
        </div>
        <div style="margin: 0 auto;text-align: center;">
            <div id="id-modal-it-login" style="display: none;">
                <div class="img-login" style="">
                    <img src="img/login/its_key_login.png"/>
                </div>
                <div class="popup">
                    <div class="title-login">ADMIN LOGIN</div>
                    <br>
                    <div class="error-it"></div>
                    <br>
                    <div>
                        <input type="text" id="user-it" name="user" class="txt-field" value="" placeholder="Username">
                    </div>
                    <br>
                    <div>
                        <input type="password" id="pass-it" name="pass" class="txt-field" value="" placeholder="* * * * * * * * * * *">
                    </div>
                    <br>
                    <div>
                        <input type="submit" class="btn-it-login" value="LOGIN">
                    </div>
                    <div style="margin-top: 25px">
                        <a href="#" class="txt-forgot-pwd">Forgot Password?</a>
                    </div>
                </div>
            </div>
        </div>
    </body>
    <script type="text/javascript">
        
         $(document).ready(function(){
            $('#hq-login').click(function(){
                $("#id-popup-modal").addClass('class-popup-modal hq');
                $("#id-modal-hq-login").addClass('class-modal-login');
            });

            $('#fd-login').click(function(){
                $("#id-popup-modal").addClass('class-popup-modal fd');
                $("#id-modal-fd-login").addClass('class-modal-login');
            });

            $('#it-login').click(function(){
                $("#id-popup-modal").addClass('class-popup-modal it');
                $("#id-modal-it-login").addClass('class-modal-login');
            });

            $('#id-popup-modal').click(function(){
                $("#id-popup-modal").removeClass('class-popup-modal');
                $("#id-popup-modal").removeClass('hq');
                $("#id-popup-modal").removeClass('fd');
                $("#id-popup-modal").removeClass('it');
                $("#id-modal-hq-login").removeClass('class-modal-login');
                $("#id-modal-fd-login").removeClass('class-modal-login');
                $("#id-modal-it-login").removeClass('class-modal-login');

                $('.error-fd').text('');
                $('.error-hq').text('');
                $('.error-it').text('');
             });

            // Add an event that triggers when the submit
            // button is pressed.
            $(".btn-it-login").click(function() {
                // Get the text from the two inputs.
                var user = $("#user-it").val();
                var pass = $("#pass-it").val();

                if (user === "" || pass === "") {
                    if($('.error-it').text() === ""){
                        $('.error-it').append('Invalid username or password');
                    }
                    return;
                }

                // Ajax POST request, similar to the GET request.
                $.post('Login', {"user": user, "pass": pass},
                    function(url) { // on success
                       window.location.href = url;
                    }).fail(function() { //on failure
                        if($('.error-it').text() === ""){
                            $('.error-it').append('Invalid username or password');
                        }
                    });
            });

            // Add an event that triggers when the submit
            // button is pressed.
            $(".btn-hq-login").click(function() {
                // Get the text from the two inputs.
                var user = $("#user-hq").val();
                var pass = $("#pass-hq").val();

                if (user === "" || pass === "") {
                    if($('.error-hq').text() === ""){
                        $('.error-hq').append('Invalid username or password');
                    }
                    return;
                }

                // Ajax POST request, similar to the GET request.
                $.post('Login', {"user": user, "pass": pass},
                    function(url) { // on success
                       window.location.href = url;
                    }).fail(function() { //on failure
                        if($('.error-hq').text() === ""){
                            $('.error-hq').append('Invalid username or password');
                        }
                    });
            });

            // Add an event that triggers when the submit
            // button is pressed.
            $(".btn-fd-login").click(function() {
                // Get the text from the two inputs.
                var user = $("#user-fd").val();
                var pass = $("#pass-fd").val();

                if (user === "" || pass === "") {
                    if($('.error-fd').text() === ""){
                        $('.error-fd').append('Invalid username or password');
                    }
                    return;
                }

                // Ajax POST request, similar to the GET request.
                $.post('Login', {"user": user, "pass": pass},
                    function(url) { // on success
                       window.location.href = url;
                    }).fail(function() { //on failure
                        if($('.error-fd').text() === ""){
                            $('.error-fd').append('Invalid username or password');
                        }
                    });
            });
        });
    </script>
</html>