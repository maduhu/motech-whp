<%@page import="org.motechproject.security.service.MotechAuthenticationService" %>
<%@page import="org.motechproject.whp.user.domain.Provider" %>
<%@page import="org.motechproject.whp.user.repository.AllProviders" %>
<%@page import="org.springframework.context.ApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Properties" %>
<%@page import="java.util.Arrays" %>
<!DOCTYPE html>
<html>
<head>
    <%
        ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
        Properties whpProperties = appCtx.getBean("whpProperties", Properties.class);
        String appVersion = whpProperties.getProperty("application.version");
    %>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/standard.css"/>
</head>
<body>

<div class="container">
    <p class=""><a href="/whp/emulator/"><i class="icon-home"></i> Home</a></p>

    <form name="testSubmit">
        <div class="row-fluid">
            <span style="vertical-align:top" class="span3">Enter Provider Id</span>
            <input id="provider_id" class="span2" name="provider_id" type="text" value="raj"/>
        </div>
        <div class="row-fluid">
            <span class="span3" style="vertical-align:top">Enter Password</span>
            <input id="password" name="password" class="span2" type="text" value="raj"/>
        </div>
        <div class="row-fluid">
            <span class="span3" style="vertical-align:top">Enter Primary Mobile Number:</span>
            <input id="primary_mobile" class="span2" type="text" value="1234567890"/>
        </div>
        <div class="row-fluid">
            <span class="span3" style="vertical-align:top">Enter Secondary Mobile:</span>
            <input id="secondary_mobile" class="span2" type="text" value="1234567890"/>
        </div>
        <div class="row-fluid">
            <span class="span3" style="vertical-align:top">Enter Tertiary Mobile:</span>
            <input id="tertiary_mobile" class="span2" type="text" value="1234567890"/>
        </div>
        <div class="row-fluid">
            <span class="span3" style="vertical-align:top">Enter District:</span>
            <input id="district" class="span2" type="text" value="Begusarai"/>
        </div>
        <div class="row-fluid">
            <input type="button" id="post-button" value="Submit" class="btn btn-large btn-primary offset3"/>
        </div>

        <br/>
        <br/>
        <textarea id="statusMessage" rows="30" cols="100" style="min-width: 100%"  readonly></textarea>
    </form>

    <textarea id="template" style="display:none"><$REGISTRATION$ api_key="3F2504E04F8911D39A0C0305E82C3301" xmlns="http://openrosa.org/user/registration">
        <username></username>
        <password></password>
        <uuid></uuid>
        <date>20/02/2012 10:10:15</date>
        <user_data>
            <data key="primary_mobile">$PRIMARY_MOBILE$</data>
            <data key="secondary_mobile">$SECONDARY_MOBILE$</data>
            <data key="tertiary_mobile">$TERTIARY_MOBILE$</data>
            <data key="provider_id">$PROVIDER_ID$</data>
            <data key="district">$DISTRICT$</data>
        </user_data>
    </$REGISTRATION$></textarea>
    <script type="text/javascript">
        var providerXML;

        function generateProviderXml() {
            providerXML = $("#template").val();
            providerXML = $.trim(providerXML);

            providerXML = providerXML.replace('$PROVIDER_ID$', $("#provider_id").val());
            providerXML = providerXML.replace('$PRIMARY_MOBILE$', $("#primary_mobile").val());
            providerXML = providerXML.replace('$SECONDARY_MOBILE$', $("#secondary_mobile").val());
            providerXML = providerXML.replace('$TERTIARY_MOBILE$', $("#tertiary_mobile").val());
            providerXML = providerXML.replace('$DISTRICT$', $("#district").val());
            providerXML = providerXML.replace('$REGISTRATION$', "Registration");
            providerXML = providerXML.replace('$REGISTRATION$', "Registration");
        }
        $('#post-button').click(function () {
            generateProviderXml();
            var host = window.location.host;
            $.ajax({
                type:'POST',
                url:"http://" + host + "/whp/provider/process",
                data:providerXML,
                contentType:"application/xml; charset=utf-8",
                success:function (data, textStatus, jqXHR) {
                    $('#statusMessage').text("Status of request: SUCCESS");
                },
                error:function (xhr, status, error) {
                    $('#statusMessage').text("Status of request: FAILURE. Reason: " + error + "</br>" + xhr.responseText);
                }
            });

        });
    </script>
</div>
</body>
</html>
