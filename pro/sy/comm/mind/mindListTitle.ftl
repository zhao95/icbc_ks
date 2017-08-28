<div class="onlylastmind" >
<#assign mindList = userMind.getMindList(odeptCode) > 
<#if mindList?size != 0 >
<span class="left form__left15"><div class="ui-label-default">&nbsp</div></span>
<span class="right form__right85">

<table class="mindTable wp" border="1" width="100%" >
	<tr>
		
		<td>
			<table width="100%" class="tab2" style="margin: 10px 0;">
				<#list mindList as mind>
					<#if mind_index=0>
				<tr>
					<td style="text-align:left;padding-left:2%;">

						${mind.WF_NI_NAME}&nbsp;${mind.MIND_CONTENT}
					</td>
				</tr>
				<tr>
				<td style="border-left-width:0px; text-align:right;padding-right:10%; font-size:15px">
						<#if mind.BD_UNAME?? && mind.BD_UNAME?length gt 1 >
							${mind.BD_UNAME}&nbsp;(${mind.S_UNAME} 授权)
						<#else>
							${mind.S_UNAME}
						</#if>
						&nbsp;${mind.MIND_TIME}
				</td>

				</tr>
			</#if>
				</#list>
			</table>
		</td>
</tr>
</table>
</span>
</#if>
</div>

<div class="allminds" style="display: none">
<#assign mindList = userMind.getMindList(odeptCode) > 
<#if mindList?size != 0 >
<span class="left form__left15"><div class="ui-label-default">&nbsp</div></span>
<span class="right form__right85">

<table class="mindTable wp" border="1" width="100%" >
	<tr>
		
		<td>
			<table width="100%" class="tab2" style="margin: 10px 0;">
				<#list mindList as mind>
				<#if mindList?size == 1>
					<tr>
						<td style="text-align:left;padding-left:2%;">
							${mind.WF_NI_NAME}&nbsp;${mind.MIND_CONTENT}
						</td>
					</tr>
					<tr>
						<td style="border-left-width:0px; text-align:right;padding-right:10%; font-size:15px">
							<#if mind.BD_UNAME?? && mind.BD_UNAME?length gt 1 >
								${mind.BD_UNAME}&nbsp;(${mind.S_UNAME} 授权)
							<#else>
								${mind.S_UNAME}
							</#if>
							&nbsp;${mind.MIND_TIME}
						</td>
					</tr>
				<#else>
					<tr>
						<td style="text-align:left;padding-left:2%;">
							${mind.WF_NI_NAME}&nbsp;${mind.MIND_CONTENT}
						</td>
					</tr>
					<tr>
						<td style="border-left-width:0px; text-align:right;padding-right:10%; font-size:15px;border-bottom: #ddd 1px dashed;">
							<#if mind.BD_UNAME?? && mind.BD_UNAME?length gt 1 >
								${mind.BD_UNAME}&nbsp;(${mind.S_UNAME} 授权)
							<#else>
								${mind.S_UNAME}
							</#if>
							&nbsp;${mind.MIND_TIME}
						</td>
					</tr>
					</#if>
				</#list>
			</table>
		</td>
</tr>
</table>
</span>
</#if>
</div>
<script type="text/javascript">
	$("#MIND_FIELDSET span.legend :last-child").attr("class","iconC icon-card-open");
	$("#MIND_FIELDSET span.legend").unbind("click").bind("click",function(){
		if($(".allminds").css("display")=="none"){
			$(".onlylastmind").css("display", "none");
			$(".allminds").css("display","block");
			$("#MIND_FIELDSET span.legend :last-child").attr("class","iconC icon-card-close");
		}else{
			$(".allminds").css("display","none");
			$(".onlylastmind").css("display","block");
			$("#MIND_FIELDSET span.legend :last-child").attr("class","iconC icon-card-open");
		}
		
	});

</script>

 <!--本机构  意见列表表头  如果本机构列表中有意见  --> 
 
<#-- <#if existCurOdeptMind=true>
	<div class="mindODeptTable" id='mind${userBean.ODEPT_CODE}' deptCode='${userBean.ODEPT_CODE}' border="0">
		<#if odeptList?size gt 1> 
		    <span class='left mindOdpt' deptCode='${userBean.ODEPT_CODE}' style='cursor:pointer'>本机构</span><span class='left'>&nbsp;&nbsp;</span>
		</#if> -->
		<#-- 这里源文件就注释了的
		<span class='mindSortClick <#if sortType="TYPE" >mindTypeSelected</#if>' deptCode='${userBean.ODEPT_CODE}' sortType="TYPE" style='cursor:pointer'>按类别排序</span>
        <span class='left'>&nbsp;&nbsp;</span>
		<span class='mindSortClick <#if sortType="TIME" >mindTypeSelected</#if>' deptCode='${userBean.ODEPT_CODE}' sortType="TIME" style='cursor:pointer'>按时间排序</span>
		-->
	<#--</div>
	<div id="mindContent${userBean.ODEPT_CODE}">
		<#include "mindList.ftl">
	</div>
</#if> -->


<#-- <#assign odeptList = userMind.getOdeptList() > -->
<!--机构数大于1的时候，显示机构名称-->
<#-- <#list odeptList as odept>
	<#if odept.DEPT_CODE != userBean.ODEPT_CODE >
		<div class="mindODeptTable fl" id='mind${odept.DEPT_CODE}' deptCode='${odept.DEPT_CODE}' border="0">
			<#if odeptList?size gt 0> 
			    <span class='left mindOdpt' deptCode='${odept.DEPT_CODE}' style='cursor:pointer'>${odept.DEPT_NAME}</span><span class='left'>&nbsp;&nbsp;</span>
			</#if> -->
			<#-- 这里源文件就注释了的
			<span class='mindSortClick' sortType="TYPE" deptCode='${odept.DEPT_CODE}' style='cursor:pointer'>按类别排序</span>
	        <span class='left'>&nbsp;&nbsp;</span>
			<span class='mindSortClick' sortType="TIME" deptCode='${odept.DEPT_CODE}' style='cursor:pointer'>按时间排序</span>
			-->
		<#--</div>
		<div id="mindContent${odept.ODEPT_CODE}"></div>
	</#if>
</#list> 
-->