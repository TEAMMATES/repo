<%@ tag description="Generic TEAMMATES Help Page" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="t" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TEAMMATES</title>
    <link rel="stylesheet" href="/stylesheets/lib/bootstrap.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/lib/bootstrap-theme.min.css" type="text/css">
    <link rel="stylesheet" href="/stylesheets/teammatesCommon.css" type="text/css">
    <link rel="apple-touch-icon" href="apple-touch-icon.png" />
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]--> 
</head>
<body>
    <div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="/index.jsp">TEAMMATES</a>
            </div>
        </div>
    </div>
    <div class="container" id="mainContent">
        <jsp:doBody />
    </div>
    <div id="footerComponent" class="container-fluid">
        <div class="container">
            <div class="row">
                <div class="col-md-2">
                    <span>[<a href="/index.jsp">TEAMMATES</a>]</span>
                </div>
                <div class="col-md-8">
                    [hosted on <a href="http://code.google.com/appengine/">Google App Engine</a>]
                </div>
                <div class="col-md-2">
                    <span>[Send <a class="link" href="/contact.jsp" target="_blank">Feedback</a>]</span>
                </div>
            </div>
        </div>
    </div>
</body>
</html>