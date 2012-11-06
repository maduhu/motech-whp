    <div class="row" id="footer">
        <div class="pull-left" id="footer-left-section">
            <div class="row">
                <a href="<@spring.url '/' />">
                    <img class="pull-left" src="<@spring.url '/resources-${applicationVersion}/images/motechlogo.png'/>"/>
                </a>
            </div>
            <div class="row" id="version">
                <#if Session.version?exists>
                    Version 1.0.${Session.version}
                </#if>
            </div>
        </div>
        <div class="pull-right">
            <div class="row">
                <a href="<@spring.url '/' />">
                    <img  class="pull-right" src="<@spring.url '/resources-${applicationVersion}/images/whplogo.png'/>"/>
                </a>
            </div>
        </div>
    </div>

