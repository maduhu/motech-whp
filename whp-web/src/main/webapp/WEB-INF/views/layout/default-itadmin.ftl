<#import "/spring.ftl" as spring />
<#macro defaultLayout title="WHP">
<!DOCTYPE html>
<html ng-app>
<head>
    <title> ${title} </title>
    <#include "scripts.ftl"/>
    <link rel="stylesheet" type="text/css" href="<@spring.url '/resources-${applicationVersion}/styles/datepicker.css'/>"/>
    <script type="text/javascript" src="<@spring.url '/resources-${applicationVersion}/js/util.js'/>"></script>
    <script type="text/javascript" src="<@spring.url '/resources-${applicationVersion}/js/autoComplete.js'/>"></script>
</head>
<body>
<div class="row-fluid" id="headerContent">
        <#include "header.ftl"/>
</div>
<div class="container-fluid">

    <div class="row-fluid">
        <div class="span2">
            <#include "../itadmin/menu.ftl"/>
        </div>

        <div class="span8" id="mainContent">
            <noscript>
                <div class="row alert alert-error  javascript-warning">Javascript is not enabled in your browser. The application will not work
                    properly. Please contact your administrator
                </div>
            </noscript>
            <!--Body content-->
            <#nested/>
        </div>
    </div>
</div>


<div class="row-fluid" id="footerContent">
    <#include "footer.ftl"/>
</div>

</body>
</html>
</#macro>