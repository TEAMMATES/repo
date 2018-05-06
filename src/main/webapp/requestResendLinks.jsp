<%@ page import="teammates.common.util.Config" %>
<%@ page import="teammates.common.util.Const" %>
<%@ page import="teammates.common.util.FrontEndLibrary" %>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="jsIncludes">
  <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP %>"></script>
  <script type="text/javascript" src="<%= FrontEndLibrary.BOOTSTRAP_CSS %>"></script>
  <script src="https://www.google.com/recaptcha/api.js" async defer></script>
  <script type="text/javascript" src="/js/requestResendLinks.js"></script>
</c:set>
<c:set var="recaptchaSitekey" value="<%= Config.RECAPTCHA_SITEKEY %>" />
<c:set var="shouldSkipRecaptchaVerification" value="<%= Const.ParamsNames.SHOULD_SKIP_RECAPTCHA_VERIFICATION %>" />
<t:statusMessage statusMessagesToUser="${data.statusMessage}"/>
<t:staticPage jsIncludes="${jsIncludes}" currentPage="requestAccessLinksResend">
  <link type="text/css" href="/stylesheets/teammatesCommon.css" rel="stylesheet">
  <h1 class="color-orange">
    Request for Resending of Access Links
  </h1>
  <p id="message">Please enter your email address; an email containing links to all the feedback sessions that you participated in over the recent six months will be sent to you.</p>
  <form id="requestForm" action="/page/resendLinks" name="requestForm" method="POST">
    <input id="email" class="form-control" name="studentemail" placeholder="Enter your email address">
    <br/>
    <div id="recaptcha" class="g-recaptcha" data-sitekey="${recaptchaSitekey}"></div>
    <br/>
    <input class="btn btn-primary btn-md" id="submitButton" type="submit" value="Submit">
    <input type="hidden" name="${shouldSkipRecaptchaVerification}" value="false">
  </form>
</t:staticPage>
