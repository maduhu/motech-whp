<#import "/spring.ftl" as spring />
<#import "../layout/default-with-menu.ftl" as layout>
<#include "../user/resetPassword.ftl">
<@layout.defaultLayout title="MoTeCH-WHP" entity="itadmin">
<script type="text/javascript"
        src="<@spring.url '/resources-${applicationVersion}/js/redirectOnRowClick.js'/>"></script>

    <#if message?exists && (message?length>0)>
    <div id="statusMessage" class="message-alert row text-center alert alert-info fade in">
        <button class="close" data-dismiss="alert">&times;</button>
    ${message}
        <#assign message=""/>
    </div>
    </#if>

<script type="text/javascript">
    createAutoClosingAlert(".message-alert", 5000);
</script>
<div class="well float-right">
    <a class="btn btn-primary" id="createCmfAdmin-button" href="/whp/cmfAdmin/create">Create CMF Admin</a>
</div>
<table id="cmfAdminList" class="table table-bordered table-condensed" redirectOnRowClick="true">
    <thead>
    <tr>
        <th>Staff Name</th>
        <th>User Name</th>
        <th>Location</th>
        <#--Reset Password column should always be the last one (we are appending this column dynamically if this column does not exist)-->
        <th type="reset-password"></th>
    </tr>
    </thead>
    <tbody>
        <#if allCmfAdmins?size == 0>
        <tr>
            <td class="warning" style="text-align: center" colspan="5">
                No registered CMF Admins to show
            </td>
        </tr>
        <#else>
            <#list allCmfAdmins as cmfAdmin>
            <tr id="cmfAdmin_${cmfAdmin.userId}" class="cmfadmin-row" rowId="${cmfAdmin.userId}"
                redirect-url="<@spring.url '/cmfAdmin/edit?userId=${cmfAdmin.userId}' />">
                <td class="staffName">
                ${cmfAdmin.staffName}
                </td>
                <td class="userId">
                ${cmfAdmin.userId}
                </td>
                <td>
                ${cmfAdmin.locationId}
                </td>
                <td type="reset-password" class="row-click-exclude">
                    <a type="reset-password-link" class="reset-password one-line" data-toggle="modal" href="#resetPasswordModal"
                       userName="${cmfAdmin.userId}">Reset Password</a>
                </td>
            </tr>
            </#list>
        </#if>
    </tbody>
</table>
    <@resetPassword/>
<script type="text/javascript">
    $(function () {
        $('#resetPasswordModal').bind('resetPasswordSuccess', function (event, userName) {
            $('tr[rowId=' + userName + ']').effect("highlight", {}, 6000);
        });
        $('a[type=reset-password-link]').click(function () {
            var userId = $(this).closest('tr').attr('rowId');
            $('#resetPasswordModal .user-name').text(userId);
        });
    });
</script>
</@layout.defaultLayout>
