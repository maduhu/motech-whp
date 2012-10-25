<%@page import="org.springframework.context.ApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="java.util.Properties" %>
<!DOCTYPE html>
<html>
<head>
    <%
    ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
    Properties whpProperties = appCtx.getBean("whpProperties", Properties.class);
    String appVersion = whpProperties.getProperty("application.version");
    %>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/whp/resources-<%=appVersion%>/styles/standard.css"/>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
</head>
<body>

<div class="container">
    <div class="pull-right"><a href="/whp/emulator/">home</a></div>

    <form name="testSubmit">
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Case Id:</span>
            <input id="case_id" class="span2" type="text" value="12345"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Case Name:</span>
            <input id="case_name" class="span2" type="text" value="Raju Singh"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter TB_Id:</span>
            <input id="tb_id" class="span2" type="text" value="12345678891"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter TB Registration Date:</span>
            <input id="tb_registration_date" class="span2" type="text" value="25/11/2012"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Provider Id:</span>
            <input id="provider_id" class="span2" type="text" value="raj"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter First Name:</span>
            <input id="first_name" class="span2" type="text" value="raju"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Last Name:</span>
            <input id="last_name" class="span2" type="text" value="singh"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Age:</span>
            <input id="age" class="span2" type="text" value="12"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Gender:</span>
            <input id="gender" class="span2" type="text" value="M"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Smear test instance:</span>
            <input id="smear_instance" class="span2" type="text" value="PreTreatment"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Test Date 1:</span>
            <input id="test_date1" class="span2" type="text" value="01/03/2012"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Test Result 1:</span>
            <input id="test_result1" class="span2" type="text" value="Positive"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Test Date 2:</span>
            <input id="test_date2" class="span2" type="text" value="01/03/2012"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Test Result 2:</span>
            <input id="test_result2" class="span2" type="text" value="Positive"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Treatment Category:</span>
            <input id="tc" class="span2" type="text" value="01"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Address location:</span>
            <input id="al" class="span2" type="text" value="1001"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Address landmark:</span>
            <input id="landmark" class="span2" type="text" value="Near banyan tree"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Address village:</span>
            <input id="av" class="span2" type="text" value="chambal"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Address block:</span>
            <input id="ab" class="span2" type="text" value="Vijaynagar"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter District:</span>
            <input id="address_district" class="span2" type="text" value="Muzafarrpur"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Address state:</span>
            <input id="as" class="span2" type="text" value="Bihar"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Mobile number:</span>
            <input id="mobile" class="span2" type="text" value="9880123456"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter Disease class:</span>
            <input id="dclass" class="span2" type="text" value="P"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter weight instance:</span>
            <input id="wii" class="span2" type="text" value="PreTreatment"/>
        </div>
        <div class="row-fluid">
            <span class="pull-left span3" style="vertical-align:top">Enter weight:</span>
            <input id="w" class="span2" type="text" value="80"/>
        </div>
        <input type="button" id="submit" value="Submit"/>
    </form>
    <br/>
    <br/>
    <textarea id="statusMessage" rows="30" cols="100" style="min-width: 100%" readonly></textarea>
    <textarea id="template" style="display:none">
        <case xmlns="http://openrosa.org/javarosa" case_id="$CASE_ID$" api_key="3F2504E04F8911D39A0C0305E82C3301" date_modified="03/04/2012 11:23:40"
              user_id="system">
            <create>
                <case_type>whp_tb_patient</case_type>
                <case_name>$CASE_NAME$</case_name>
            </create>
            <update>
                <tb_id>$TB_ID$</tb_id>
                <provider_id>$PROVIDER_ID$</provider_id>
                <patient_type>New</patient_type>
                <first_name>$FIRST_NAME$</first_name>
                <last_name>$LAST_NAME$</last_name>
                <age>$AGE$</age>
                <gender>$GENDER$</gender>
                <smear_sample_instance>$SMEAR_INSTANCE$</smear_sample_instance>
                <smear_test_date_1>$TEST_DATE1$</smear_test_date_1>
                <smear_test_result_1>$TEST_RESULT1$</smear_test_result_1>
                <smear_test_date_2>$TEST_DATE2$</smear_test_date_2>
                <smear_test_result_2>$TEST_RESULT2$</smear_test_result_2>
                <treatment_category>$TC$</treatment_category>
                <address_location>$AL$</address_location>
                <address_landmark>$LANDMARK$</address_landmark>
                <address_village>$AV$</address_village>
                <address_block>$AB$</address_block>
                <address_district>$AD$</address_district>
                <address_state>$AS$</address_state>
                <mobile_number>$MOBILE$</mobile_number>
                <phi>WHP</phi>
                <disease_class>$DCLASS$</disease_class>
                <weight_instance>$WII$</weight_instance>
                <weight>$W$</weight>
                <tb_registration_date>$TB_REGISTRATION_DATE$</tb_registration_date>
            </update>
        </case>
    </textarea>
    <script type="text/javascript">
        var patientXML = "";
        function generatePatientXml() {
            patientXML =  $("#template").val();
            patientXML = $.trim(patientXML);

            patientXML = patientXML.replace('$TB_ID$', $("#tb_id").val());
            patientXML = patientXML.replace('$TB_REGISTRATION_DATE$', $("#tb_registration_date").val());
            patientXML = patientXML.replace('$CASE_NAME$', $("#case_name").val());
            patientXML = patientXML.replace('$CASE_ID$', $("#case_id").val());
            patientXML = patientXML.replace('$PROVIDER_ID$', $("#provider_id").val());
            patientXML = patientXML.replace('$FIRST_NAME$', $("#first_name").val());
            patientXML = patientXML.replace('$LAST_NAME$', $("#last_name").val());
            patientXML = patientXML.replace('$AGE$', $("#age").val());
            patientXML = patientXML.replace('$GENDER$', $("#gender").val());
            patientXML = patientXML.replace('$SMEAR_INSTANCE$', $("#smear_instance").val());
            patientXML = patientXML.replace('$TEST_DATE1$', $("#test_date1").val());
            patientXML = patientXML.replace('$TEST_RESULT1$', $("#test_result1").val());
            patientXML = patientXML.replace('$TEST_DATE2$', $("#test_date2").val());
            patientXML = patientXML.replace('$TEST_RESULT2$', $("#test_result2").val());
            patientXML = patientXML.replace('$TC$', $("#tc").val());
            patientXML = patientXML.replace('$AL$', $("#al").val());
            patientXML = patientXML.replace('$LANDMARK$', $("#landmark").val());
            patientXML = patientXML.replace('$AV$', $("#av").val());
            patientXML = patientXML.replace('$AB$', $("#ab").val());
            patientXML = patientXML.replace('$AD$', $("#address_district").val());
            patientXML = patientXML.replace('$AS$', $("#as").val());
            patientXML = patientXML.replace('$MOBILE$', $("#mobile").val());
            patientXML = patientXML.replace('$DCLASS$', $("#dclass").val());
            patientXML = patientXML.replace('$WII$', $("#wii").val());
            patientXML = patientXML.replace('$W$', $("#w").val());
        }
        $(document).ready(function() {
            $('#submit').click(function () {
                generatePatientXml();
                var host = window.location.host;
                $.ajax({
                    type:'POST',
                    url:"http://" + host + "/whp/patient/process",
                    data:patientXML,
                    contentType:"application/xml; charset=utf-8",
                    success:function (data, textStatus, jqXHR) {
                        $('#statusMessage').text("Status of request: SUCCESS");
                    },
                    error:function (xhr, status, error) {
                        $('#statusMessage').text("Status of request: FAILURE. Reason: " + error + "</br>" + xhr.responseText);
                    }
                })
            });
        });
    </script>
</div>
</body>
</html>
