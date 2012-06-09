<#macro treatmentCardView>
    <#if treatmentCard?exists>
    <div id="legend-container" class="pull-right">
    <table id="legend" class="table table-bordered table-condensed">
        <thead>
        <tr>
            <th>Legend</th>
        </tr>
        </thead>
    <tbody>
        <tr>
            <td>
                <div class="label pausedAdherenceData">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
            </td>
            <td>
                <div class="text-center">Therapy Paused</div>
            </td>
        </tr>
        <#list treatmentCard.providerIds as providerId>
        <tr>
            <td>
                <div class="windings-font" providerId=${providerId}> &#252; &#251;</div>
            </td>
            <td>
                <div class="text-center text-center">Adherence given by provider ${providerId}</div>
            </td>
        </tr>
        </#list>
    </tbody>
    </table>
    </div>

    <table class="table table-bordered table-condensed fixed sharp disable-select">
        <thead>
        <tr>
            <th>Month/Year</th>
            <#list 1..31 as x>
                <th>${x}</th>
            </#list>
        </tr>
            <#list treatmentCard.monthlyAdherences as monthlyAdherence>
            <tr>
                <div>
                    <td>${monthlyAdherence.monthAndYear}</td>
                </div>
                <#assign sunday = monthlyAdherence.firstSunday/>
                <#list 1..31 as day>
                    <#assign drawn = false/>
                    <#assign isItASunday = ""/>
                    <#if day == sunday>
                        <#assign isItASunday = "sunday"/>
                        <#assign sunday=sunday+7/>
                    </#if>
                    <#list monthlyAdherence.getLogs() as dailyAdherence>
                        <#assign isItDuringPausedPeriod = ""/>
                        <#if dailyAdherence.adherenceCapturedDuringPausedPeriod>
                            <#assign isItDuringPausedPeriod = "pausedAdherenceData">
                        </#if>
                        <#if dailyAdherence.day == day>
                            <#assign drawn = true/>
                            <td class=
                            <#if dailyAdherence.pillStatus == 1> "tick-icon editable ${isItASunday} ${isItDuringPausedPeriod}"
                            <#elseif dailyAdherence.pillStatus == 2> "cross-icon editable ${isItASunday} ${isItDuringPausedPeriod}"
                            <#else> "round-icon editable ${isItASunday} ${isItDuringPausedPeriod}"
                            </#if>
                            providerId = ${dailyAdherence.providerId}>
                            <#if dailyAdherence.pillStatus == 1> &#252;
                            <#elseif dailyAdherence.pillStatus == 2> &#251;
                            </#if>
                            </td>
                            <#break/>
                        </#if>
                    </#list>
                    <#if drawn == false>
                        <td class=
                            <#if monthlyAdherence.maxDays &lt; day >"bg-gray"
                            <#else>"bg-gray ${isItASunday}"
                            </#if>
                            >
                        <div/>
                        </td>
                    </#if>
                </#list>
            </tr>
            </#list>
        </thead>
        <tbody>
        </tbody>
    </table>

    <script type="text/javascript">
        $(function () {
            var providerIds = new Array();
            var providerColors = new Array();
            <!--setting provider id & colors to javascript variables-->
            <#assign i=0/>
            <#list treatmentCard.providerIds as providerId>
                providerIds[${i}] = "${providerId}";
                <#assign i=i+1/>
            </#list>
            <#assign i=0/>
            <#list treatmentCard.providerColorCodes as color>
                providerColors[${i}] = "${color}";
                <#assign i=i+1/>
            </#list>

            for (var i = 0; i < providerIds.length; i++) {
                $("[providerid =" + providerIds[i] + "]").css('color', providerColors[i % providerColors.length]);
            }
        });

        <!--Tooltips to show provider information and dose status-->

        function annotate(){
            $('.tick-icon, .cross-icon').each(function(){
                var providerId = $(this).attr('providerId');
                if(!providerId) {
                    providerId = "CMF Admin";
                }
                var pillStatus;
                if ($(this).hasClass('tick-icon')) {
                    pillStatus = "Taken."
                }
                else if ($(this).hasClass('cross-icon')) {
                    pillStatus = "Not Taken."
                }
                else {
                    pillStatus = "";
                }
                $(this).tooltip({title: "Provider " + providerId + " gave adherence as " + pillStatus});
            })
        }

        annotate();

        <!--Troggle - 3 state toggling-->
        $('.editable').click(function () {
            if ($(this).hasClass('tick-icon')) {
                $(this).removeClass('tick-icon');
                $(this).addClass('cross-icon');
                $(this).html('&#251;');
            }
            else if ($(this).hasClass('cross-icon')) {
                $(this).removeClass('cross-icon');
                $(this).addClass('round-icon');
                $(this).text('');
            }
            else {
                $(this).removeClass('round-icon');
                $(this).addClass('tick-icon');
                $(this).html('&#252;');
            }
        });
    </script>
    </#if>
</#macro>