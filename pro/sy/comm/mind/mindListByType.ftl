<#assign mindTypeList = userMind.getMindTypeList(odeptCode) >
	<#list mindTypeList as mindType>
	<#assign mindList = userMind.getMindListByType(mindType.ID, odeptCode) > 
		<#if mindList?size != 0>	
		<tr>
			<td width="15%" class="tc byTypeTd">${mindType.NAME}</td>
			<td width="85%" class="byTypeTd">
				<table class='mindShowList' width="100%" border="0" cellspacing="0" cellpadding="0">
				<#list mindList as mind>
					<tr><td style="word-wrap:break-word;word-break:break-all;text-align:left;" class="p5 lh150">
						<#if mind.MIND_CONTENT??>
						<span class="MIND_CONTENT">${mind.MIND_CONTENT?replace("\n","<br>")?replace(" ","&nbsp;")}</span>
						</#if>
						<#if mind._MIND_FILE_LIST?? && mind._MIND_FILE_LIST?size gt 0>
						&nbsp;&nbsp;&nbsp;&nbsp;（
						<#list mind._MIND_FILE_LIST as fileBean>
						${fileBean_index + 1}.&nbsp;<a href="#" onclick="RHFile.read('${fileBean.FILE_ID}','${fileBean.FILE_NAME}')" fileID="${fileBean.FILE_ID}" class="MIND_FILE"><span class="icon-image iconC" style="border:0"></span>${fileBean.FILE_NAME}</a>
						[<a href="/file/${fileBean.FILE_ID}" target="_blank">下载</a>]；&nbsp;
						</#list>
						）
						</#if>
						<#--
						<#if userDoInWf?? && userDoInWf=true>
							<#if canCopy?? && canCopy=true>
							[<a href="javascript:void(0)" class="COPY_MIND">复制</a>]
							</#if>
							<#if NI_ID?? && NI_ID= mind.WF_NI_ID && mind.S_USER = userBean.USER_CODE>
								[<a href="javascript:void(0)" MIND_ID="${mind.MIND_ID}" class="DELETE_MIND">删除</a>]
							<#elseif NI_ID?? && DEL_MIND?? && DEL_MIND = "true" && mind.S_USER = userBean.USER_CODE>
								[<a href="javascript:void(0)" MIND_ID="${mind.MIND_ID}" class="DELETE_MIND">删除</a>]
							<#elseif NI_ID?? && DEL_SELF_MIND?? && DEL_SELF_MIND && mind.S_USER = userBean.USER_CODE>
							    [<a href="javascript:void(0)" MIND_ID="${mind.MIND_ID}" class="DELETE_MIND">删除</a>]		
							</#if>
						</#if>
						-->
					</td>
					<td width="200" style="border-width:0 1px;">${mind.S_TNAME}</td>
					<td class="tr p5" width="100">
						<#if mind.IS_BD?? && mind.IS_BD = 1 && mind.BD_UNAME?length gt 0>
					    	<span>${mind.S_UNAME}(${mind.BD_UNAME}补登)</span>
					    <#elseif mind.BD_UNAME?length gt 0>
					    	<span>${mind.BD_UNAME}(${mind.S_UNAME}授权)</span>
					    <#else>
					    	<span>${mind.S_UNAME}<span>
					    </#if>
					</td>
					<td width="140" style="border-left:1px #ddd solid;">
						<#if mind.MIND_TIME??> <!--从别处导过来的数据，有的没取到时间值，或者时间格式是只到日期的-->
					        <#if mind.MIND_TIME?length gt 16>    
					            ${mind.MIND_TIME?substring(0,16)}
					        <#else>
					            ${mind.MIND_TIME}
					        </#if>
					    </#if>
					 </td>
					</tr>
				</#list>
				</table>
			</td>
		</tr>
		</#if>
	</#list>
	<script type="text/javascript">
	$(document).ready(function(){
		$(".MIND_CONTENT").each(function(index,item){
			$(item).html($(item).text());
		});
	});
	</script>