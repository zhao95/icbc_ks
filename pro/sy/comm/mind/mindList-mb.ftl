<!--按照机构显示意见的列表-->
<#if mindTypeList?size != 0>
    <#if sortType = 'TIME'> <!--按照时间排序-->
		<#include "mindListByTime-mb.ftl">
    <#else> <!--按照类型分组  再排序-->
        <#include "mindListByType-mb.ftl">
	</#if>
</#if>
