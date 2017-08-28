<#assign mindTypeList = userMind.getMindTypeList(odeptCode) >
	<#list mindTypeList as mindType>
	<#assign mindList = userMind.getMindListByType(mindType.ID, odeptCode) > 
		<#if mindList?size != 0>
		<div class="mind-divider">${mindType.NAME}</div>
		<#list mindList as mind>
		<div class="mind-item">
			<div style="margin:.5em 0;">
				<#if mind.MIND_CONTENT??>
				${mind.MIND_CONTENT?replace("\n", "<br>")}
				</#if>
			</div>
			<div class="mind-sounds">
				<#if (mind.SY_COMM_FILE)??>
					<#if mind.SY_COMM_FILE?size != 0>
						<#list mind.SY_COMM_FILE as file>
							<#if file.FILE_CAT == 'MIND_SOUNDS'>
								<button class="ui-btn ui-icon-audio ui-btn-icon-right ui-shadow ui-corner-all" style="width:${(file.FILE_SIZE/300000) * 100}%;" data-fileid="${file.FILE_ID}"></button>
							</#if>
						</#list>
					</#if>
				</#if>
			</div>
			<div style="overflow:hidden;">
			<#if (mind.SY_COMM_FILE)??>
				<#if mind.SY_COMM_FILE?size != 0>
					<#list mind.SY_COMM_FILE as file>
						<#if file.FILE_CAT == 'MIND_IMAGES'>	
							<div class="mind-image" style="background:url('/sy/mobile2/images/user.jpg') center center;background-size:cover;"></div>
						</#if>
					</#list>
				</#if>
			</#if>
			</div>
	        <div class="mind-infos">
	        	<div class="mind-img-wrp">
	        		<#if mind.IS_BD?? && mind.IS_BD = 1 && mind.BD_USER?length gt 0>
				    	<img class="mind-img" data-user-img="${mind.S_USER}" onerror="this.src='/sy/mobile2/images/default_avatar.png'" src="http://172.16.0.5:9090/file/USER_${mind.S_USER}.jpg?size=50x50" />
				    <#elseif mind.BD_USER?length gt 0>
				    	<img class="mind-img" data-user-img="${mind.S_USER}" onerror="this.src='/sy/mobile2/images/default_avatar.png'" src="http://172.16.0.5:9090/file/USER_${mind.BD_USER}.jpg?size=50x50" />
				    <#else>
				    	<img class="mind-img" data-user-img="${mind.S_USER}" onerror="this.src='/sy/mobile2/images/default_avatar.png'" src="http://172.16.0.5:9090/file/USER_${mind.S_USER}.jpg?size=50x50" />
				    </#if>
	        	</div>
	        	<div class="mind-leader-wrp">
	        		<div style="display:block;line-height:1.6em;">
	        			<div class="mind-leader-name">
	        				<#if mind.IS_BD?? && mind.IS_BD = 1 && mind.BD_UNAME?length gt 0>
						    	${mind.S_UNAME}
						    <#elseif mind.BD_UNAME?length gt 0>
						    	${mind.BD_UNAME}
						    <#else>
						    	${mind.S_UNAME}
						    </#if>
	        			</div>
	        			<div class="mind-time" data-time-ago="${mind.MIND_TIME}">
							<#if mind.MIND_TIME??>
						        <#if mind.MIND_TIME?length gt 16>    
									${mind.MIND_TIME?substring(5,16)}
						        <#else>
									${mind.MIND_TIME}
						        </#if>
						    </#if>
					    </div>
	        		</div>
					<div class="mind-leader-dept">
						<#if mind.IS_BD?? && mind.IS_BD = 1 && mind.BD_UNAME?length gt 0>
					    	(${mind.S_TNAME} ${mind.BD_UNAME} 补登)
					    <#elseif mind.BD_UNAME?length gt 0>
					    	(${mind.S_TNAME} ${mind.S_UNAME} 授权)
					    <#else>
					    	${mind.S_TNAME}
					    </#if>
					</div>
	        	</div>
	        </div>
		</div>
		</#list>
	    </#if>
	</#list>