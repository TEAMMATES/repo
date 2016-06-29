<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<t:staticPage jsIncludes="${jsIncludes}">
    <div id="mainContainer">
        <div id="header">
            <div id="headerWrapper">
                <div id="imageHolder">
                    <a href="/index.html">
                        <img alt="TEAMMATES[Logo] - Online Peer Feedback/Evaluation System for Student Team Projects" src="images/teammateslogo.jpg" width="150px" height ="47px">
                    </a>
                </div>
                <div id="menuHolder">
                    <div id="textHolder">
                        <ul id="navbar">
                            <li><a href="index.html">Home</a></li>
                            <li><a href="features.html">Features</a></li>
                            <li><a href="about.html">About Us</a></li>
                            <li><a href="contact.html">Contact</a></li>
                            <li class="current"><strong>Terms of Use</strong></li>
                        </ul>
                    </div>
                    <div id="loginHolder">
                        <form action="/login" style="float:left" name="studentLogin">
                            <input type="submit" name="student" class="button" id="btnStudentLogin" value="Student Login">
                         </form>
                         <form action="/login" name="instructorLogin" style="float:left">
                             <input type="submit" name="instructor" class="button" id="btnInstructorLogin" value=" Instructor Login">
                         </form>
                    </div>
                    <div style="clear: both;"></div>
                </div>
                <div style="clear: both;"></div>
            </div>
        </div>
        
        <div id="mainContent">
            <h1 id="caption">
                Terms of Use
            </h1>
            <img src="images/terms.png" width="125px"  style="padding-left:100px">
            <div id="contentHolder">
                <p>By using <span class="bold">TEAMMATES</span>, you agree to the following terms of use:</p>
                <br>
                <p><span class="bold">Quality of service</span>: We take pride in our work and we shall do our best to provide good service to users. However, the  service is provided &quot;AS IS&quot; and WITHOUT ANY WARRANTY. You use 
                        TEAMMATES at your own discretion and risk and you will be solely responsible for any damages that 
                        may arise from such use. </p>
                <br>
                <p><span class="bold">Changes to the service</span>: We strive to improve TEAMMATES continuously and provide its services free for as long as we  can. However, TEAMMATES services may change or be terminated at our discretion.</p>
                <br>
                <p>
                    <span class="bold">Security and privacy of data</span>: Our data are stored in Google
                    servers and are protected by the same security mechanisms that protect Google data.
                    However, you are advised not to store in TEAMMATES any ‘sensitive’ data such as credit
                    card numbers and passwords.<br>
                    We do not use TEAMMATES data for research. Only TEAMMATES
                    administrators (not developers) have access to data, for performance monitoring and
                    troubleshooting purposes. <br> <br>
                </p>
            </div>
        </div>       
    </div>
</t:staticPage>
