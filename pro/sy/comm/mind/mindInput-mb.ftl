<!--意见输入框的显示-->
<style type="text/css">
.MIND_INPUT_DIV_BORDER div{border:0px !important;}
</style>
<table id="mindInput" class="mindInput mb-mind-input" width="100%">
    <!-- 固定意见， 如果有，显示 选择框和输入框 --> 
	<#if canRegularMind=true>
	<tr>
		<td class="mb-mind-title"><span class="ml50">${regularMind.CODE_NAME}<#if regularMind.MIND_MUST?? && regularMind.MIND_MUST == '1'><span class="space require" style="display: inline;">*</span></#if></span></td>
	</tr>
	<tr>
		<td>
			<span class="ui-form-default" style="position: relative;display:inline-block;width:100%">
				    <input style="cursor: pointer;" id="REGULAR_MIND" value="" class="ui-query-default" type="text">
				    <span id="chooseRegularMind" style="cursor: pointer;" class="iconChoose icon-input-select"></span>
				    <span id="cancelRegularMind" style="cursor: pointer;" class="iconCancel icon-input-clear"></span>		
				    <input id="USUAL_MIND_ID" value="" type="hidden">
				    <input id="USUAL_MIND_VALUE" value="" type="hidden">
			</span>
		</td>
	</tr>
	<tr>
	    <td>
	        <span class="ui-form-default mb-mind-textarea-container" style="position: relative;display:inline-block;width:100%">
			    <!-- 如果当前节点有签，还没送出，在这显示意见内容 --> 
		        <textarea style="height: 100%;" id="REGULAR_MIND_CONTENT" class="ui-textarea-default mb-radius-9 mb-mind-textarea" rows="10"></textarea>	  
			</span>
	    </td>
	</tr>
	<tr class="mb-mind-upload">
	    <td> <!--如果只读，不显示-->
	        <#if regularMind.READ_ONLY?? && regularMind.READ_ONLY != 'true' >
			    <span id="upload_REGULAR_MIND" class="MIND_INPUT_DIV_BORDER"></span>
			<#else>
			    <span id="upload_REGULAR_MIND" class="MIND_INPUT_DIV_BORDER"></span>
			</#if>
	    </td>
	</tr>	
	</#if>
	
	<!-- 最终意见， 如果有，显示输入框 --> 
	<#if canTerminalMind=true>
	<tr>
		<td class="mb-mind-title"><span class="ml50">${regularMind.CODE_NAME}<#if terminalMind.MIND_MUST?? && terminalMind.MIND_MUST == '1'><span class="space require" style="display: inline;">*</span></#if></span></td>
	</tr>
	<tr>
		<td>
		<div class="mindTerminalInput mb-mind-textarea-container">
		    <!-- 如果当前节点有签，还没送出，在这显示意见内容 --> 
	        <textarea style="height: 100%;" class="ui-textarea-default mb-radius-9 mb-mind-textarea" id="TERMINAL_MIND" rows="10"></textarea>	
		</div>
		</td>	
	</tr>
	<tr class="mb-mind-upload">
	    <td>
	        <#if terminalMind.READ_ONLY?? && regularMind.READ_ONLY != true >
			    <span id="upload_TERMINAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>  
			<#else>
			    <span id="upload_TERMINAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>
			</#if>
	    </td>
	</tr>			
	</#if>
	
	<!-- 普通意见， 如果有，显示输入框 --> 
	<#if canGeneralMind=true>
	<tr>
		<td class="mb-mind-title"><span class="mindInputTitle" id="GENERAL_MIND_NAME">${generalMind.CODE_NAME}<#if generalMind.MIND_MUST?? && generalMind.MIND_MUST == '1'><span id="GENERAL_MIND_REQUIRE" class="space require" style="display: inline;">*</span></#if></span></td>
	</tr>
	<tr>
		<td>
		<div class="mindGeneralInput mb-mind-textarea-container">
		    <!-- 如果当前节点有签，还没送出，在这显示意见内容 -->
	        <textarea style="height: 100%;" id="GENERAL_MIND" class="ui-textarea-default mb-radius-9 mb-mind-textarea" rows="10"></textarea>
		</div>
		</td>
	</tr>	
	<tr class="mb-mind-upload">
	    <td>
	        <#if terminalMind.READ_ONLY?? && regularMind.READ_ONLY != true >
			    <span id="upload_GENERAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>    
			<#else>
			    <span id="upload_GENERAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>
			</#if>  
	    </td>
	</tr>	
	</#if>
	

</table>
<table class="mindSaveBar" width="100%">
	<#if canGeneralMind=true || canTerminalMind=true || canRegularMind=true>
	<tr>
	    <td align="center" class="mt20 mb10">
	        <span id="saveMindBtn" class="mb-mind-button"><a href="javascript:;" class="mb-mind-a">保存意见</a></span>
	    </td>
	</tr>
	<#else>
	<tr>
	    <td colspan='2' align="center" class="mt20 mb10">
	        节点上未设置可填写意见
	    </td>
	</tr>	
	</#if>
</table>


