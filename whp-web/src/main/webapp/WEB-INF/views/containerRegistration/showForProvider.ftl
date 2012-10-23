<#import "/spring.ftl" as spring />
<#import "../layout/default-with-menu.ftl" as layout>
<@layout.defaultLayout title="Container-Registration" entity="provider">
    <script type="text/javascript" src="<@spring.url '/resources-${applicationVersion}/js/containerRegistration.js'/>"></script>
    <#if errors??>
            <div id="container-registration-error" class="container-registration-message-alert row alert alert-error fade in">
                <button class="close" data-dismiss="alert">&times;</button>
                <#list errors as error>
                    <div><@spring.messageArgs code=error.code args=error.parameters/></div>
                </#list>
            </div>
    </#if>
    <#if message??>
            <div id="container-registration-confirmation" class="container-registration-message-alert row alert alert-info fade in">
                <button class="close" data-dismiss="alert">&times;</button>
                ${message}
            </div>
    </#if>
        <h1>Container Registration</h1>

        <div id="container-registration">
            <form id="container-registration-form" autocomplete="off" action="<@spring.url '/containerRegistration/by_provider/register'/>"  class="well form-horizontal" input method="POST" submitOnEnterKey="true">
                <table class="controls">
                    <tr>
                        <td>
                            <div class="control-group">
                                <label class="control-label">Container ID*</label>

                                <div class="controls">
                                    <input id="containerId" class="span" name="containerId" type="text" maxlength="${containerIdMaxLength}" />
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-group">
                                <label class="control-label">Instance*</label>

                                <div class="controls">
                                    <select id="instance" name="instance" validate="required:true">
                                           <option value=""></option>
                                           <#list instances as instance>
                                              <option value="${instance}">${instance}</option>
                                           </#list>
                                    </select>
                                </div>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <div class="control-group pull-down">
                                <div class="controls">
                                    <button type="submit" id="registerButton" class="btn btn-primary">
                                        Register
                                    </button>
                                    <a id="back" class="btn" href="<@spring.url ''/>">Back</a>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
</@layout.defaultLayout>