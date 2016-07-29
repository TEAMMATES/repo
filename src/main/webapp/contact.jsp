<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="teammates.common.util.Config" %>
<c:set var="supportEmail" value="<%= Config.SUPPORT_EMAIL %>" />
<t:staticPage currentPage="contact">
    <h1 id="caption">
        Contact Us
    </h1>
    <img src="images/contact.png" width="125px" style="padding-left: 100px;">
    <div id="contentHolder">
        <p>
            <span class="bold">Email: </span> You can contact us at the following email address - <a href="mailto:${supportEmail}">${supportEmail}</a>
        </p>
        <br>
        <p>
            <span class="bold">Blog: </span>Visit the <a href="http://teammatesonline.blogspot.sg/">TEAMMATES Blog</a> to see our latest updates and information.
        </p>
        <br>
        <p>
            <span class="bold">Bug reports and feature requests: </span> Any bug reports or feature requests can be submitted to above email address.
        </p>
        <br>
        <p>
            <span class="bold">Interested in joining us?: </span>Visit our <a href="https://github.com/TEAMMATES/teammates">Developer Website</a>.
        </p>
    </div>
</t:staticPage>
