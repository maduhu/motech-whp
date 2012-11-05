<%@page import="org.springframework.context.ApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@page import="java.util.Properties" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
    <%
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        Properties whpProperties = appCtx.getBean("whpProperties", Properties.class);
        String appVersion = whpProperties.getProperty("application.version");
        if (request.getParameter("remedi-response-type") != null)
            application.setAttribute("remedi-response-type", request.getParameter("remedi-response-type"));
        Object responseParam = application.getAttribute("remedi-response-type");
        boolean success = responseParam == null ? true : responseParam.equals("success");

        if (request.getParameter("request-method-type") != null)
            application.setAttribute("request-method-type", request.getParameter("request-method-type"));
        Object methodTypeParam = application.getAttribute("request-method-type");
        boolean isPostMethod = methodTypeParam == null ? true : methodTypeParam.equals("POST");

        ArrayList<String> responsesGotSoFar = (ArrayList<String>) application.getAttribute("responses");
        if (responsesGotSoFar == null)
            responsesGotSoFar = new ArrayList<String>();
    %>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/standard.css"/>
</head>
<body>
<div class="container">
    <p class=""><a href="/whp/emulator/"><i class="icon-home"></i> Home</a></p>
    <form name="testSubmit" method="GET">
        <br/>
        <br/>

        <div class="row-fluid">
            <span style="vertical-align:top" class="pull-left span3">Set ReMeDI response type as</span>
            <input id="response_type_success" type="radio" name="remedi-response-type" <%=success ? "checked" : ""%>
                   value="success"/> Success
            <input id="response_type_failure" type="radio" name="remedi-response-type" <%=!success ? "checked" : ""%>
                   value="failure"/> Failure
        </div>
        <div class="row-fluid">
            <span style="vertical-align:top" class="pull-left span3">Set Request Method type</span>
            <input id="request_type_post" type="radio" name="request-method-type" <%=isPostMethod ? "checked" : ""%>
                   value="POST"/> POST
            <input id="request_type_get" type="radio" name="request-method-type" <%=!isPostMethod ? "checked" : ""%>
                   value="GET"/> GET
        </div>
        <div class="row-fluid">
             <input type="submit" id="post-button" value="Set" class="btn btn-primary offset2"/>
        </div>
        <br/>
        <br/>
    </form>

    <div id="statusMessage" style="color: chocolate;">
        From now on response type "<%=success ? "Success" : "Failure"%>" will be returned as ReMeDI response
        Request type "<%=isPostMethod ? "POST" : "GET"%>" will be supported
    </div>

    <br/><br/>

    <p class="bold">Last 10 responses : </p><br/>
    <%
        for (int i=responsesGotSoFar.size()-1; i>=0; i--) {
    %>
    <div class="well" style="position: relative;">
    <pre><textarea rows="20" cols="200" style="width: 880px; height: 370px;" disabled="true"><%=responsesGotSoFar.get(i)%></textarea></pre>
    </div>
    <br/>
    <%
        }
    %>

    <br/><br/>
</div>
</body>
</html>
