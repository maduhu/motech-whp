<#macro paginate id entity contextRoot filterSectionId rowsPerPage="10" stylePath="" currentPage=1>


<link rel="stylesheet" type="text/css" href="<@spring.url '${stylePath}/motech-paginator-pagination.css'/>"/>

<#-- Author atish added  style="visibility:hidden" to paginator header -->
<div id="${id}" ng-init="entity='${entity}';contextRoot='${contextRoot}';rowsPerPage='${rowsPerPage}';id='${id}';filterSectionId='${filterSectionId}'" style="visibility:hidden">
    <div class="paginator" ng-controller="PaginationCtrl">
        <div>

            <div class="result-count-info pull-left" ng-show="hasResults()">
                <span>Found <span class="bold">{{data.totalRows}}</span> record(s)</span> |
                <span>Displaying: {{firstRowCount}} - {{lastRowCount}} of {{data.totalRows}} row(s)</span>

            </div>
            <div class="pull-right" ng-show="hasResults()">
                <@filterButtons/>
            </div>
        </div>
        <div class="paginator-content">
            <#nested>
        </div>
        <div class="pull-right" ng-show="hasResults()">
            <@filterButtons/>
        </div>
    </div>
</div>


</#macro>
<#macro filterButtons >
<a link-type="firstPage" ng-show="currentPage > 1" class="page-link" ng-click="firstPage()" onclick="return false">First</a>
<p link-type="firstPage-disabled" ng-hide="currentPage > 1" class="page-link-disabled">First</p>

<a link-type="prevPage" ng-show="currentPage > 1" class="page-link" ng-click="prevPage()" onclick="return false">Previous</a>
<p link-type="prevPage-disabled" ng-hide="currentPage > 1" class="page-link-disabled" >Previous</p>

<div class="current-page-info">{{currentPage}} of {{numberOfPages()}} pages</div>

<a link-type="nextPage" ng-show="currentPage <  numberOfPages()" class="page-link" ng-click="nextPage()" onclick="return false">Next</a>
<p link-type="nextPage-disabled" ng-hide="currentPage <  numberOfPages()" class="page-link-disabled">Next</p>

<a link-type="lastPage" ng-show="currentPage <  numberOfPages()" class="page-link" ng-click="lastPage()" onclick="return false">Last</a>
<p link-type="lastPage-disabled" ng-hide="currentPage <  numberOfPages()" class="page-link-disabled">Last</p>

<div class="goto-page">
    Go to
    <input type="text" class="current-page" value="{{currentPage}}"/> page
</div>
</#macro>

<#macro filter id pagination_id layout="form-inline">
<div ng-init="pagination_id='${pagination_id}'; filter_id = '${id}'"  >
	<form id="${id}" class="${layout}" ng-submit="applyFilter()" ng-controller="FilterCtrl" ng-model="searchCriteria" update-filter>
		<#nested>
	</form>
</div>

</#macro>

<#macro paginationScripts jsPath="" loadJquery="true">
    <#if loadJquery == "true">
    <script type="text/javascript" src="<@spring.url '${jsPath}/motech-paginator-jquery.js'/>"></script>
    </#if>
<script type="text/javascript" src="<@spring.url '${jsPath}/motech-paginator-angular.js'/>"></script>
<script type="text/javascript" src="<@spring.url '${jsPath}/motech-paginator-pagination.js'/>"></script>
<script type="text/javascript" src="<@spring.url '${jsPath}/motech-paginator-filter.js'/>"></script>
</#macro>
