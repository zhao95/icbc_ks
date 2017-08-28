<div class="mindInputDiv">    
    <!-- 固定意见， 如果有，显示 选择框和输入框 --> 
	<#if canRegularMind=true>
	<div>
		<label class="rh-label">${regularMind.CODE_NAME}</label><span class="space require" style="display: inline;"><#if regularMind.MIND_MUST?? && regularMind.MIND_MUST == '1'>*<#else> </#if></span><span>
			<input style="cursor:pointer;display:none;" id="REGULAR_MIND" value="" class="ui-query-default" type="text">
		    <span id="chooseRegularMind" style="cursor: pointer;position:inherit;" class="iconChoose icon-input-select"></span>
		    <span id="cancelRegularMind" style="cursor: pointer;position:inherit;" class="iconCancel icon-input-clear"></span>		
		    <input id="USUAL_MIND_ID" value="" type="hidden">
		    <input id="USUAL_MIND_VALUE" value="" type="hidden">
		</span>
	</div>
	<div>
		<label class="rh-label"> </label><span class="space require" style="display: inline;"> </span><span>
			<!-- 如果当前节点有签，还没送出，在这显示意见内容 --> 
		    <textarea style="height:100%;width:460px;" id="REGULAR_MIND_CONTENT" class="rh-next-select-container blank ui-select-default width460"></textarea>	
		</span>
	</div>
	<div style="display:none;">
		<#if regularMind.READ_ONLY?? && regularMind.READ_ONLY != 'true' >
		    <span id="upload_REGULAR_MIND" class="MIND_INPUT_DIV_BORDER"></span>
		<#else>
		    <span id="upload_REGULAR_MIND" class="MIND_INPUT_DIV_BORDER"></span>
		</#if>
	</div>	
	</#if>
	
	<!-- 最终意见， 如果有，显示输入框 --> 
	<#if canTerminalMind=true>
	<div>
		<label class="rh-label">${terminalMind.CODE_NAME}</label><span class="space require" style="display: inline;"> </span><span>
			<!-- 如果当前节点有签，还没送出，在这显示意见内容 --> 
	        <textarea style="height:100%;width:460px;" class="rh-next-select-container blank ui-select-default width460" id="TERMINAL_MIND" rows="10"></textarea>
		</span>
	</div>
	<div style="display:none;">
		<#if terminalMind.READ_ONLY?? && terminalMind.READ_ONLY != 'true' >
		    <span id="upload_TERMINAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>  
		<#else>
		    <span id="upload_TERMINAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>
		</#if>
	</div>		
	</#if>
	
	<!-- 普通意见， 如果有，显示输入框 --> 
	<#if canGeneralMind=true>
	<div>
		<label class="rh-label">${generalMind.CODE_NAME}</label><span class="space require" style="display:inline;"><#if generalMind.MIND_MUST?? && generalMind.MIND_MUST == '1'>*<#else> </#if></span><span><textarea style="height:80px;width:460px;" class="rh-next-select-container blank ui-select-default width460" id="GENERAL_MIND" rows="10"  oninput= ' var msg = "您输入的留言字数大与4000，多余字数自动被丢弃。";  var len = $(this).val().length; if(len > 4000){SysMsg.alert(msg,"","","",200); $(this).val($(this).val().substring(0,4000)); } ' onpropertychange= 'var msg = "您输入的留言字数大与4000，多余字数自动被丢弃。"; var len = $(this).val().length; if(len > 4000){ SysMsg.alert(msg,"","","",200); $(this).val($(this).val().substring(0,4000)); }'></textarea>
	</span>
	</div>
	<div style="display:none;">
		<#if generalMind.READ_ONLY?? && generalMind.READ_ONLY != 'true' >
		    <span id="upload_GENERAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>    
		<#else>
		    <span id="upload_GENERAL_MIND" class="MIND_INPUT_DIV_BORDER"></span>
		</#if>
	</div>	
	</#if>


	<#if canGeneralMind=true || canTerminalMind=true || canRegularMind=true>
	<div>
		<span class="saveBarLeft"></span><span style="display:inline-block;margin:5px 0 5px 0;"><a id="saveMindBtn" class="rh-icon rhGrid-btnBar-a fr"><span class="rh-icon-inner">保存意见</span><span class="rh-icon-img btn-save"/></a></span>
	</div>
	<#else>
	<div>
	    节点上未设置可填写意见
	</div>	
	</#if>
</div>

