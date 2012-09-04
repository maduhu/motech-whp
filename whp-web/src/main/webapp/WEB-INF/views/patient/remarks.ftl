<h3 id="Remarks" class="underline">Remarks</h3>
<div id="remarks-content">
<#if cmfAdminRemarks?size == 0 && providerRemarks?size == 0>
    <label>
        No remarks added for the patient.
    </label>
</#if>

    <div id="cmf-admin-remarks">
    <#list cmfAdminRemarks as cmfAdminRemark>
        <div class="cmf-admin-remark">
            <h5>
            ${cmfAdminRemark.user} on ${cmfAdminRemark.creationTime.toString("dd/MM/yyy")}
                at ${cmfAdminRemark.creationTime.toString("hh:mm a")} says:
            </h5>

            <div>${cmfAdminRemark.remark}</div>
            <br/>
        </div>
    </#list>
    </div>
<#if cmfAdminRemarks?size!=0>
   <hr class="line"/>
    <br/>
</#if>
    <div id="provider-remarks">
    <#list providerRemarks as providerRemark>
        <div class="provider-remark">
            <h5>
                Provider ${providerRemark.providerId()} on ${providerRemark.creationTime.toString("dd/MM/yyy")}
                at ${providerRemark.creationTime.toString("hh:mm a")} says:
            </h5>

            <div>${providerRemark.remark()}</div>
            <br/>
        </div>
    </#list>
    </div>
</div>
