/** 卡片页面渲染引擎 */
GLOBAL.namespace("rh.language");

var Language = {
	baseinfo : {
		en : 'Base Info',
		zh : '基本信息'
	},
	
	languagetype : {
		en : 'en',
		zh : 'zh-cn'
	},
	
	pageTitle : {
		en : "ICBC Platform",
		zh : "工商银行集成平台"
	},
	
	waitUpload : {
		en : 'Waiting Upload...',
		zh : '等待上传...'
	},
	
	handlers_string1 : {
		en : 'Upload file too much.\n You can also upload',
		zh : '上传的文件过多。\n您还能上传 '
	},
	
	
	handlers_string2 : {
		en : 'Files!',
		zh : '个文件！'
	},
	
	handlers_string3 : {
		en : 'The file size is zero!',
		zh : '文件大小不能为0！'
	},
	
	handlers_string4 : {
		en : 'Upload the file is too big!',
		zh : '上传的文件太大！'
	},
	
	handlers_string5 : {
		en : 'Uploading...',
		zh : '正在上传...'
	},
	
	handlers_string6 : {
		en : 'Upload to complete',
		zh : '上传完成'
	},
	
	handlers_string7 : {
		en : 'Cancel the upload',
		zh : '取消上传'
	},
	
	handlers_string8 : {
		en : 'Stop uploading',
		zh : '停止上传'
	},
	
	config_string1 : {
		en : 'Welcome to use',
		zh : '欢迎使用'
	},
	
	rh_ui_card_string1 : {
		en : 'grouping',
		zh : '分组'
	},
	
	rh_ui_card_string2 : {
		en : 'This must be input!',
		zh : '该项必须输入！'
	},
	
	rh_ui_card_string3 : {
		en : 'Please enter an integer length is less than',
		zh : '请输入整数长度不超过'
	},
	
	rh_ui_card_string4 : {
		en : 'A decimal length is less than',
		zh : '位，小数长度不超过'
	},
	
	rh_ui_card_string5 : {
		en : 'A valid number!',
		zh : '位的有效数字！'
	},
	
	rh_ui_card_string6 : {
		en : 'Please enter no more than length',
		zh : '请输入长度不超过'
	},
	
	rh_ui_card_string7 : {
		en : 'A valid number!',
		zh : '位有效数字！'
	},
	
	rh_ui_card_string8 : {
		en : 'Length cannot be more than',
		zh : '长度不能超过'
	},
	
	rh_ui_card_string9 : {
		en : 'Chinese characters (or',
		zh : '个汉字(或'
	},
	
	rh_ui_card_string10 : {
		en : 'Char)!',
		zh : '个字符)！'
	},
	
	rh_ui_card_string11 : {
		en : 'This must be input!',
		zh : '该项必须输入！'
	},
	
	rh_ui_card_string12 : {
		en : 'Upload a file',
		zh : '上传文件'
	},
	
	rh_ui_card_string13 : {
		en : 'Failed to create flash upload component!',
		zh : '创建flash上传组件失败！'
	},
	
	rh_ui_card_string14 : {
		en : 'Overwriting upload?',
		zh : '是否覆盖上传？'
	},
	
	rh_ui_card_string15 : {
		en : 'A file has been uploaded',
		zh : ' 个文件已上传'
	},
	
	rh_ui_card_string16 : {
		en : 'File upload progress',
		zh : '文件上传进度'
	},
	
	rh_ui_card_string17 : {
		en : "'> 0 file uploaded < / div >",
		zh : "'>0 个文件已上传</div>"
	},
	
	rh_ui_card_string18 : {
		en : 'cancel',
		zh : '取消'
	},
	
	rh_ui_card_string19 : {
		en : 'Shut down',
		zh : '关闭'
	},
	
	rh_ui_card_string20 : {
		en : '0 file has been uploaded',
		zh : '0 个文件已上传'
	},
	
	rh_ui_card_string21 : {
		en : '<legend> upload progress <legend>',
		zh : '<legend>上传进度</legend>'
	},
	
	rh_ui_card_string22 : {
		en : 'delete',
		zh : '删除'
	},
	
	rh_ui_card_string23 : {
		en : 'editor',
		zh : '编辑'
	},
	
	rh_ui_card_string24 : {
		en : 'view',
		zh : '查看'
	},
	
	rh_ui_card_string25 : {
		en : 'download',
		zh : '下载'
	},
	
	rh_ui_card_string26 : {
		en : 'Please save',
		zh : '请先保存！'
	},
	
	rh_ui_card_string27 : {
		en : 'Modify the file information',
		zh : '修改文件信息'
	},
	
	rh_ui_card_string28 : {
		en : 'In trying to load...',
		zh : '努力加载中...'
	},
	
	rh_ui_card_string29 : {
		en : 'No files need to be modified!',
		zh : '没有文件需要修改！'
	},
	
	rh_ui_card_string30 : {
		en : 'Sure to delete this file',
		zh : '确定删除该文件？'
	},
	
	rh_ui_card_string31 : {
		en : 'Sure to delete the file',
		zh : '确定删除该文件？'
	},
	
	rh_ui_card_string32 : {
		en : 'Select the relevant documents',
		zh : '选择相关文件'
	},
	
	rh_ui_card_string33 : {
		en : 'Are you sure you want to delete this record?',
		zh : '您确认要删除该条记录吗?'
	},
	
	rh_ui_card_string34 : {
		en : 'Take do article basis',
		zh : '取办文依据'
	},
	
	rh_ui_card_string35 : {
		en : 'Do article basis',
		zh : '办文依据'
	},
	
	rh_ui_card_string36 : {
		en : 'Click on the button below, you can from the existing document in the system has been transferred to the document as do article basis for this document',
		zh : '点击以下按钮，您可以从现有公文系统中取已经办结的公文作为本公文的办文依据'
	},
	
	rh_ui_card_string37 : {
		en : 'The official document system',
		zh : '公文系统'
	},
	
	rh_ui_card_string38 : {
		en : 'Click on the button below, you can get from the local file system in the file as the document do article basis',
		zh : '点击以下按钮，您可以从本地文件系统中取文件作为本公文的办文依据'
	},
	
	rh_ui_card_string39 : {
		en : 'The local file',
		zh : '本地文件'
	},
	
	rh_ui_card_string40 : {
		en : 'Illegal time format!',
		zh : '非法的时间格式！'
	},
	
	rh_ui_card_string41 : {
		en : 'Click on the select',
		zh : '点击选择'
	},
	
	rh_ui_card_string42 : {
		en : 'Click on the clear contents',
		zh : '点击清除内容'
	},
	
	rh_ui_card_string43 : {
		en : 'offline',
		zh : '离线'
	},
	
	rh_ui_card_string44 : {
		en : 'online',
		zh : '在线'
	},
	
	rh_ui_card_string45 : {
		en : 'See -',
		zh : '查看-'
	},
	
	rh_ui_card_string46 : {
		en : 'Open the connection',
		zh : '打开连接'
	},
	
	rh_ui_card_string47 : {
		en : 'Query connection',
		zh : '查询连接'
	},
	
	rh_ui_card_string48 : {
		en : "Field in the file '",
		zh : "字段文件'"
	},
	
	rh_ui_card_string49 : {
		en : "'does not exist!",
		zh : "'不存在！"
	},
	
	rh_ui_card_string50 : {
		en : "The attachment'",
		zh : "附件'"
	},
	
	rh_ui_card_string51 : {
		en : 'The field study',
		zh : "字段项"
	},
	
	rh_ui_card_string52 : {
		en : 'Does not exist!',
		zh : "不存在！"
	},
	
	rh_ui_card_string53 : {
		en : 'The first item does not support before and after adding expansion character!',
		zh : "第一个项不支持前后添加扩符！"
	},
	
	rh_ui_card_string54 : {
		en : 'Please enter an integer length is less than',
		zh : "请输入整数长度不超过"
	},
	
	rh_ui_card_string55 : {
		en : 'A decimal length is less than',
		zh : "位，小数长度不超过"
	},
	
	rh_ui_card_string56 : {
		en : 'A valid number!',
		zh : "位的有效数字！"
	},
	
	rh_ui_card_string57 : {
		en : 'A valid number!',
		zh : "请输入长度不超过{0}xxxx"
	},
	
	rh_ui_card_string58 : {
		en : 'Service items -',
		zh : "服务项-"
	},
	
	rh_ui_card_string59 : {
		en : 'confirm',
		zh : "确认"
	},
	
	rh_ui_card_string60 : {
		en : 'choose',
		zh : "选择"
	},
	
	rh_ui_card_string61 : {
		en : 'Make sure the service code, choose again!',
		zh : "先确定服务编码，再选择！"
	},
	
	rh_ui_card_string62 : {
		en : "Mandatory fields can not be empty, please fill in",
		zh : "必填项不能为空，请填写"
	},
	
	rh_ui_ccexSearch_string1 : {
		en : 'In the implementation, please later..',
		zh : "执行中，请稍后.."
	},
	
	rh_ui_ccexSearch_string2 : {
		en : 'A valid number!',
		zh : "请输入数字"
	},
	
	rh_ui_ccexSearch_string3 : {
		en : 'All',
		zh : "全部"
	},
	
	rh_ui_ccexSearch_string4 : {
		en : 'A valid number!',
		zh : "包含"
	},
	
	rh_ui_ccexSearch_string5 : {
		en : 'contains',
		zh : "不包含"
	},
	
	rh_ui_ccexSearch_string6 : {
		en : 'Is equal to the',
		zh : "等于"
	},
	
	rh_ui_ccexSearch_string7 : {
		en : 'Query',
		zh : "查询"
	},
	
	rh_ui_ccexSearch_string8 : {
		en : 'loading...',
		zh : "加载中..."
	},
	
	rh_ui_Delegate_string1 : {
		en : 'Title',
		zh : "标题"
	},
	
	rh_ui_Delegate_string2 : {
		en : 'Please save the column, in the select the relevant permissions!',
		zh : "请先保存栏目之后，在选择相关的权限！"
	},
	
	rh_ui_Delegate_string3 : {
		en : 'Authority without any change!',
		zh : "权限没有任何改变！"
	},
	
	rh_ui_Delegate_string4 : {
		en : 'Empty the list',
		zh : "清空列表"
	},
	
	rh_ui_Delegate_string5 : {
		en : 'Confirm to empty the current selected nodes?',
		zh : "确认清空当前已选择的节点吗？"
	},
	
	rh_ui_floatMenu_string1 : {
		en : 'The portal map',
		zh : "门户地图"
	},
	
	rh_ui_floatMenu_string2 : {
		en : 'Close the navigation',
		zh : "关闭导航"
	},
	
	rh_ui_floatMenu_string3 : {
		en : 'The questionnaire survey',
		zh : "问卷调查"
	},
	
	rh_ui_floatMenu_string4 : {
		en : 'The online help',
		zh : "在线帮助"
	},
	
	rh_ui_floatMenu_string5 : {
		en : 'Quick function',
		zh : "快捷功能"
	},
	
	rh_ui_floatMenu_string6 : {
		en : 'Click to enter shortcut menu item',
		zh : "点击进入快捷菜单项"
	},
	
	rh_ui_floatMenu_string7 : {
		en : 'set',
		zh : "设置"
	},
	
	rh_ui_floatMenu_string8 : {
		en : 'Set in the skin',
		zh : "设置换肤"
	},
	
	rh_ui_floatMenu_string9 : {
		en : 'Set the layout',
		zh : "设置布局"
	},
	
	rh_ui_floatMenu_string10 : {
		en : 'Back to the top',
		zh : "回顶部"
	},
	
	rh_ui_floatMenu_string11 : {
		en : 'Style definition',
		zh : "风格定义"
	},
	
	rh_ui_floatMenu_string12 : {
		en : 'Component set',
		zh : "组件设置"
	},
	
	rh_ui_floatMenu_string13 : {
		en : 'preview',
		zh : "预览"
	},
	
	rh_ui_floatMenu_string14 : {
		en : 'Set the style of the system',
		zh : "设定系统的风格"
	},
	
	rh_ui_floatMenu_string15 : {
		en : 'The portal style',
		zh : "门户样式"
	},
	
	rh_ui_floatMenu_string16 : {
		en : 'Click on the set ',
		zh : "单击设定 "
	},
	
	rh_ui_floatMenu_string17 : {
		en : 'Page will reload and styling, whether to continue?',
		zh : "页面将会重新加载并应用样式，是否继续?"
	},
	
	rh_ui_grid_string1 : {
		en : 'Data loading in...',
		zh : "数据加载中 ..."
	},
	
	rh_ui_grid_string2 : {
		en : 'The background error!',
		zh : "后台错误！"
	},
	
	rh_ui_grid_string3 : {
		en : 'No relevant record!',
		zh : "无相关记录！"
	},
	
	rh_ui_grid_string4 : {
		en : 'No operation permissions',
		zh : "没有操作权限"
	},
	
	rh_ui_grid_string5 : {
		en : 'Total:',
		zh : "合计:"
	},
	
	rh_ui_grid_string6 : {
		en : 'previous',
		zh : "上一页"
	},
	
	rh_ui_grid_string7 : {
		en : 'next',
		zh : "下一页"
	},
	
	rh_ui_grid_string8 : {
		en : 'No relevant data',
		zh : "无相关数据"
	},
	
	rh_ui_grid_string9 : {
		en : 'No results',
		zh : "没有结果"
	},
	
	rh_ui_grid_string10 : {
		en : 'No operation permissions',
		zh : "没有操作权限"
	},
	
	rh_ui_gridCard_string1 : {
		en : 'Click to load more..',
		zh : "点击加载更多.."
	},
	
	rh_ui_gridCard_string2 : {
		en : 'Click into the detailed processing',
		zh : "点击进入详细处理"
	},
	
	rh_ui_gridCard_string3 : {
		en : 'Emergency treatment to',
		zh : "需紧急处理"
	},
	
	rh_ui_gridCard_string4 : {
		en : 'return',
		zh : "返回"
	},
	
	rh_ui_gridCard_string5 : {
		en : 'associates',
		zh : "同事圈"
	},
	
	rh_ui_gridCard_string6 : {
		en : 'The address book',
		zh : "通讯录"
	},
	
	rh_ui_gridCard_string7 : {
		en : 'Refresh',
		zh : "刷新"
	},
	
	rh_ui_gridCard_string8 : {
		en : 'Detailed information on',
		zh : "详细资料"
	},
	
	rh_ui_gridCard_string9 : {
		en : 'online',
		zh : "在线"
	},
	
	rh_ui_gridCard_string10 : {
		en : 'department',
		zh : "部门"
	},
	
	rh_ui_gridCard_string11 : {
		en : 'The signature',
		zh : "签名"
	},
	
	rh_ui_gridCard_string12 : {
		en : 'The phone',
		zh : "电话"
	},
	
	rh_ui_gridCard_string13 : {
		en : 'email',
		zh : "邮箱"
	},
	
	rh_ui_gridCard_string14 : {
		en : 'Send a message',
		zh : "发消息"
	},
	
	rh_ui_gridCard_string15 : {
		en : 'Add a success!',
		zh : "添加成功!"
	},
	
	rh_ui_gridCard_string16 : {
		en : 'Added to the local address book',
		zh : "添加到本地通讯录"
	},
	
	rh_ui_gridCard_string17 : {
		en : 'determine',
		zh : "确定"
	},
	
	rh_ui_gridCard_string18 : {
		en : 'Not add success!',
		zh : "未添加成功!"
	},
	
	rh_ui_gridCard_string19 : {
		en : 'All of them are loaded!',
		zh : "所有的都加载了！"
	},
	
	rh_ui_gridCard_string20 : {
		en : 'More...',
		zh : "更多..."
	},
	rh_ui_gridCard_string21 : {
		en : 'ruaho company',
		zh : "软虹公司"
	},
	rh_ui_gridCard_string22 : {
		en : 'Click the play',
		zh : "点击播放 "
	},
	
	rh_ui_gridCard_string23 : {
		en : 'Added to the local',
		zh : "添加到本地"
	},
	
	rh_ui_gridCard_string24 : {
		en : 'chat',
		zh : "聊天"
	},
	
	rh_ui_gridCard_string25 : {
		en : 'voice',
		zh : "语音"
	},
	
	rh_ui_gridCard_string26 : {
		en : 'Click start to talk',
		zh : "点击开始说话"
	},
	
	rh_ui_gridCard_string27 : {
		en : 'Click start to reply',
		zh : "点击开始回复"
	},
	
	rh_ui_gridCard_string28 : {
		en : 'send',
		zh : "发送"
	},
	
	rh_ui_gridCard_string29 : {
		en : 'photo',
		zh : "照片"
	},
	
	rh_ui_gridCard_string30 : {
		en : 'shooting',
		zh : "拍摄"
	},
	
	rh_ui_gridCard_string31 : {
		en : 'location',
		zh : "位置"
	},
	
	rh_ui_gridCard_string32 : {
		en : 'voice',
		zh : "语音"
	},
	
	rh_ui_gridCard_string33 : {
		en : 'Click start to talk',
		zh : "点击开始说话"
	},
	
	rh_ui_gridCard_string34 : {
		en : 'Click on the end of the tape',
		zh : "点击结束录音"
	},
	
	rh_ui_gridCard_string35 : {
		en : 'found',
		zh : "发现"
	},
	
	rh_ui_gridCard_string36 : {
		en : 'Function of the desktop',
		zh : "功能桌面"
	},
	
	rh_ui_gridCard_string37 : {
		en : 'scan',
		zh : "扫一扫"
	},
	
	rh_ui_gridCard_string38 : {
		en : 'Desktop Settings',
		zh : "桌面设置"
	},
	
	rh_ui_gridCard_string39 : {
		en : 'My collection',
		zh : "我的收藏"
	},
	
	rh_ui_gridCard_string40 : {
		en : 'Log out',
		zh : "退出登录"
	},
	
	rh_ui_gridCard_string41 : {
		en : 'Function of the desktop',
		zh : "功能桌面"
	},
	
	rh_ui_gridCard_string42 : {
		en : 'Not connected',
		zh : "未连接"
	},
	
	rh_ui_gridCard_string43 : {
		en : 'search',
		zh : "搜索"
	},
	
	rh_ui_mind_string1 : {
		en : "Workflow didn't start",
		zh : "工作流没启动"
	},
	
	rh_ui_mind_string2 : {
		en : 'Comments list',
		zh : "意见列表"
	},
	
	rh_ui_mind_string3 : {
		en : 'Message opinion',
		zh : "留言意见"
	},
	
	rh_ui_mind_string4 : {
		en : 'Please select a',
		zh : "请选择"
	},
	
	rh_ui_mind_string5 : {
		en : 'Please fill out the',
		zh : "请填写"
	},
	
	rh_ui_mind_string6 : {
		en : 'Commonly used thereon',
		zh : "常用批语"
	},
	
	rh_ui_mind_string7 : {
		en : 'remove',
		zh : "清除"
	},
	
	rh_ui_mind_string8 : {
		en : 'Click to view details',
		zh : "点击查看详细"
	},
	
	rh_ui_mind_string9 : {
		en : 'Please select a type',
		zh : "请选择意见类型"
	},
	
	rh_ui_mind_string10 : {
		en : 'A valid number!',
		zh : "是否覆盖已有意见内容！"
	},
	
	rh_ui_mind_string11 : {
		en : 'Overwrite existing opinion content!',
		zh : "确定删除意见？"
	},
	
	rh_ui_mind_string12 : {
		en : 'You have already approved the ShenQian list, please confirm if you have permission to approve?',
		zh : "您已经批准了该审签单，请确认您是否有权限批准？"
	},
	
	rh_ui_mind_string13 : {
		en : 'You have to fill out the final opinion, please confirm if you have permission to fill in the final advice?',
		zh : "您已经填写了最终意见，请确认您是否有权限填写最终意见？"
	},
	
	rh_ui_mind_string14 : {
		en : 'Opinion has been saved!',
		zh : "意见已保存！"
	},
	
	rh_ui_mind_string15 : {
		en : 'Return an error, please check!',
		zh : "返回错误，请检查！"
	},
	
	rh_ui_mind_string16 : {
		en : 'Agreed to',
		zh : "同意"
	},
	
	rh_ui_next_string1 : {
		en : 'The required',
		zh : "该项必填"
	},
	
	rh_ui_next_string2 : {
		en : 'No results',
		zh : "没有结果"
	},
	
	rh_ui_next_string3 : {
		en : 'Existing components:',
		zh : "已经存在组件："
	},
	
	rh_ui_next_string4 : {
		en : 'The component id must be provided!',
		zh : "组件的id必须提供！"
	},
	
	rh_ui_next_string5 : {
		en : 'Error UI type,',
		zh : "错误UI类型，"
	},
	
	rh_ui_next_string6 : {
		en : 'Sure to send the next link',
		zh : "确定送下一环节"
	},
	
	rh_ui_openTab_string1 : {
		en : 'Has reached the set the maximum number of, please close some tags!',
		zh : "已达到设置的最大数，请关闭一些标签！"
	},
	
	rh_ui_openTab_string2 : {
		en : 'Close all',
		zh : "关闭全部"
	},
	
	rh_ui_openTab_string3 : {
		en : 'Close this TAB',
		zh : '关闭此选项卡'
	},
	
	rh_ui_openTab_string4 : {
		en : 'Close the other',
		zh : '关闭其它'
	},
	
	rh_ui_popPrompt_string1 : {
		en : 'Please enter the',
		zh : '请输入'
	},
	
	rh_ui_serrch_string2 : {
		en : 'Please enter the Numbers',
		zh : '请输入数字'
	},
	
	rh_ui_serrch_string3 : {
		en : 'Advanced query',
		zh : '高级查询'
	},
	
	rh_ui_serrch_string4 : {
		en : 'Please enter or select the query conditions',
		zh : '请输入或选择查询条件'
	},
	
	rh_ui_serrch_string5 : {
		en : 'select',
		zh : '选择'
	},
	
	rh_ui_serrch_string6 : {
		en : 'Associated navigation tree query',
		zh : '关联导航树查询'
	},
	
	rhListViewSortArrow_string1 : {
		en : 'The sorting',
		zh : '排序'
	},
	
	rhWfCardViewNodeExtends_string1 : {
		en : 'Check failed!',
		zh : '校验未通过！'
	},
	
	rhWfCardViewNodeExtends_string2 : {
		en : 'Back to deal with',
		zh : '退回处理'
	},
	
	rhWfCardViewNodeExtends_string3 : {
		en : 'The current link',
		zh : '当前环节'
	},
	
	rhWfCardViewNodeExtends_string4 : {
		en : 'Read to know',
		zh : '阅知'
	},
	
	rhWfCardViewNodeExtends_string5 : {
		en : 'Have successfully saved',
		zh : '草稿成功保存'
	},
	
	rhWfCardViewNodeExtends_string6 : {
		en : 'Back to the',
		zh : '退回'
	},
	
	rhCardView_string1 : {
		en : 'Data is modified, whether to save?',
		zh : "数据有修改，是否保存？"
	},
	
	rhCardView_string2 : {
		en : 'Refresh the success',
		zh : "刷新成功"
	},
	
	rhCardView_string3 : {
		en : 'Add state can not copy!',
		zh : "添加状态不能复制！"
	},
	
	rhCardView_string4 : {
		en : 'In the new...',
		zh : "新建中..."
	},
	
	rhCardView_string5 : {
		en : 'Change history',
		zh : "变更历史"
	},
	
	rhCardView_string6 : {
		en : 'processed',
		zh : "处理完毕"
	},
	
	rhCardView_string7 : {
		en : 'Modified preservation effect has a new copy, please!',
		zh : "已新复制，请修改后保存生效！"
	},
	
	rhCardView_string8 : {
		en : 'Not modify the data, do not submit!',
		zh : "没有修改数据，未做提交！"
	},
	
	rhCardView_string9 : {
		en : 'Js card page loading errors',
		zh : "卡片页面js加载错误"
	},
	
	rhCardView_string10 : {
		en : 'No relevant files!',
		zh : "暂无相关文件！"
	},
	
	rhCardView_string11 : {
		en : 'No file!',
		zh : "暂无文件！"
	},
	
	rhCardView_string12 : {
		en : 'There did not fill in the required fields!',
		zh : "有必填项未填写！"
	},
	
	rhCommentView_string1 : {
		en : 'Have to load all the comments.',
		zh : "已加载全部评论."
	},
	
	rhCommentView_string2 : {
		en : 'People support',
		zh : "人支持"
	},
	
	rhCommentView_string3 : {
		en : 'Operation is successful!',
		zh : "操作成功!"
	},
	
	rhCommentView_string4 : {
		en : 'against',
		zh : "反对"
	},
	
	rhCommentView_string5 : {
		en : 'Modify the',
		zh : "修改"
	},
	
	rhCommentView_string6 : {
		en : 'Modify comments',
		zh : "修改评论"
	},
	
	rhCommentView_string7 : {
		en : 'Only yourself',
		zh : "仅自己"
	},
	
	rhCommentView_string8 : {
		en : 'Designated personnel',
		zh : "指定人员"
	},
	
	rhCommentView_string9 : {
		en : 'save',
		zh : "保存"
	},
	
	rhCommentView_string10 : {
		en : 'Save the success!',
		zh : "保存成功!"
	},
	
	rhCommentView_string11 : {
		en : 'Open range',
		zh : "公开范围"
	},
	
	rhCommentView_string12 : {
		en : 'Visible to who?',
		zh : "对谁可见？"
	},
	
	rhCommentView_string13 : {
		en : 'Choose to view',
		zh : "选择可查看人"
	},
	
	rhCommentView_string14 : {
		en : 'authorization',
		zh : "授权"
	},
	
	rhCommentView_string15 : {
		en : 'Confirm to delete?',
		zh : "确认删除？"
	},
	
	rhCommentView_string16 : {
		en : 'Delete the success',
		zh : "删除成功"
	},
	
	rhCommentView_string17 : {
		en : 'reply',
		zh : "回复"
	},
	
	rhCommentView_string18 : {
		en : 'Please select a range of public!',
		zh : "请选择公开范围！"
	},
	
	rhCommentView_string19 : {
		en : 'Please enter a comment!',
		zh : "请输入评论内容!"
	},
	
	rhCommentView_string20 : {
		en : 'comments',
		zh : "评论"
	},
	
	rhCommentView_string21 : {
		en : 'More comments',
		zh : "更多评论"
	},
	
	rhDictTreeView_string1 : {
		en : 'The dictionary to choose',
		zh : "字典选择"
	},
	
	rhDictTreeView_string2 : {
		en : 'Please select a specific',
		zh : "请选择具体的"
	},
	
	rhDictTreeView_string3 : {
		en : 'Empty list item (0)',
		zh : "清空列表（共0项）"
	},
	
	rhDictTreeView_string4 : {
		en : 'Confirm to empty the current selected nodes?',
		zh : "确认清空当前已选择的节点吗？"
	},
	
	rhListBatchView_string1 : {
		en : 'add',
		zh : " 添 加 "
	},
	
	rhListExpanderView_string1 : {
		en : 'No service',
		zh : "无关联服务"
	},
	
	rhListView_string1 : {
		en : 'Loaded!',
		zh : "已加载！"
	},
	
	rhListView_string2 : {
		en : 'Input',
		zh : "输入条件"
	},
	
	rhListView_string3 : {
		en : 'As a result, loading..',
		zh : "结果加载中.."
	},
	
	rhListView_string4 : {
		en : 'The input conditions',
		zh : "输入条件"
	},
	
	rhListView_string5 : {
		en : 'senior',
		zh : "高级"
	},
	
	rhListView_string6 : {
		en : 'Please select the corresponding record!',
		zh : "请选择相应记录！"
	},
	
	rhListView_string7 : {
		en : 'Submit...',
		zh : "提交中..."
	},
	
	rhListView_string8 : {
		en : 'Please select items to delete',
		zh : "请选择要删除的条目"
	},
	
	rhListView_string9 : {
		en : 'Are you sure you want to delete the data?',
		zh : "您确定要删除该数据么？"
	},
	
	rhListView_string10 : {
		en : 'Import Excel file',
		zh : "导入Excel文件"
	},
	
	rhListView_string11 : {
		en : 'Please select a file',
		zh : "请选择文件"
	},
		
	rhListView_string12 : {
		en : 'Please choose the Excel file to import:',
		zh : "请选择要导入的Excel文件："
	},
	
	rhListView_string13 : {
		en : 'Please select a file upload',
		zh : "请选择文件上传"
	},
	
	rhListView_string14 : {
		en : 'Import the compressed data',
		zh : "导入压缩数据"
	},
	
	rhListView_string15 : {
		en : 'Please choose the zip file to import:',
		zh : "请选择要导入的zip文件："
	},
	
	rhListView_string16 : {
		en : 'Execute a list of js error:',
		zh : "执行列表js错误："
	},
	
	rhListView_string16 : {
		en : 'Failed to import file, click ok button to download files.Please open the file for import results.',
		zh : "导入文件失败，点击“确定按钮”下载文件。请打开文件查看导入结果。"
	},
	
	rhPageView_string1 : {
		en : 'Pack up the sidebar',
		zh : "收起边栏"
	},
	
	rhPageView_string2 : {
		en : 'Style configuration',
		zh : "风格配置"
	},
	
	rhPageView_string3 : {
		en : 'On the sidebar',
		zh : "展开边栏"
	},
	
	rhPageView_string4 : {
		en : 'A valid number!',
		zh : "公司门户"
	},
	
	rhPageView_string5 : {
		en : 'Company portal',
		zh : "机构门户"
	},
	
	rhPageView_string6 : {
		en : 'Department of portal',
		zh : "部门门户"
	},
	
	rhPageView_string7 : {
		en : 'User portal',
		zh : "用户门户"
	},
	
	rhPageView_string8 : {
		en : 'Intelligent search',
		zh : "智能搜索"
	},
	
	rhPageView_string9 : {
		en : 'search',
		zh : "搜索"
	},
	
	rhPageView_string10 : {
		en : 'Enter the personal basic information',
		zh : "进入个人基本信息"
	},
	
	rhPageView_string11 : {
		en : 'The basic information',
		zh : "基本信息"
	},
	
	rhPageView_string12 : {
		en : 'Personal data integrity',
		zh : "个人资料完整度"
	},
	
	rhPageView_string13 : {
		en : 'Personal basic information',
		zh : "个人基本资料"
	},
	
	rhPageView_string14 : {
		en : 'To view and post',
		zh : "查看兼岗情况"
	},
	
	rhPageView_string15 : {
		en : 'And post',
		zh : "兼岗"
	},
	
	rhPageView_string16 : {
		en : 'Will refresh the current page, make sure to continue?',
		zh : "当前页面将刷新，确定继续吗？"
	},
	
	rhPageView_string17 : {
		en : 'exit',
		zh : "退出"
	},
	
	rhPageView_string18 : {
		en : 'Pack up',
		zh : "收起"
	},
	
	rhPageView_string19 : {
		en : 'System message',
		zh : "系统消息"
	},
	
	rhPageView_string20 : {
		en : 'to-do',
		zh : "待办"
	},
	
	rhPageView_string21 : {
		en : 'To be read',
		zh : "待阅"
	},
	
	rhPageView_string22 : {
		en : 'remind',
		zh : "提醒"
	},
	
	rhPageView_string23 : {
		en : 'more',
		zh : "更多"
	},
	
	rhPageView_string24 : {
		en : 'To do the transaction',
		zh : "待办事务"
	},
	
	rhPageView_string25 : {
		en : 'The message center',
		zh : "消息中心"
	},
	
	rhPageView_string26 : {
		en : 'From:',
		zh : "来自:"
	},
	
	rhPageView_string27 : {
		en : 'Set the style of the system',
		zh : "设定系统的风格"
	},
	
	rhPageView_string28 : {
		en : 'The overall style',
		zh : "整体样式"
	},
	
	rhPageView_string29 : {
		en : 'The current system will be restored to the original style',
		zh : "将当前系统风格恢复到原始风格"
	},
	
	rhPageView_string30 : {
		en : 'Restore the style',
		zh : "恢复风格"
	},
	
	rhPageView_string31 : {
		en : 'The overall style',
		zh : "整体风格"
	},
	
	rhPageView_string32 : {
		en : 'High contrast blue (the default)',
		zh : "高对比蓝色(默认)"
	},
	rhPageView_string33 : {
		en : 'Light blue',
		zh : "浅蓝"
	},
	rhPageView_string34 : {
		en : 'orange',
		zh : "橙色"
	},
	rhPageView_string35 : {
		en : 'The sky is blue',
		zh : "天空蓝"
	},
	rhPageView_string36 : {
		en : 'Pure and fresh and green',
		zh : "清新绿"
	},
	rhPageView_string37 : {
		en : 'Blue ink',
		zh : "墨蓝"
	},
	rhPageView_string38 : {
		en : 'Light grey pattern',
		zh : "浅灰花纹"
	},
	rhPageView_string39 : {
		en : 'Brown pattern',
		zh : "棕色花纹"
	},
	rhPageView_string40 : {
		en : 'A valid number!',
		zh : "黑木纹"
	},
	rhPageView_string41 : {
		en : 'Black wood grain',
		zh : "红色"
	},
	
	rhPageView_string42 : {
		en : 'The menu',
		zh : "菜单"
	},
	
	rhPageView_string43 : {
		en : 'Grey (the default)',
		zh : "灰(默认)"
	},
	
	rhPageView_string44 : {
		en : 'orange',
		zh : "橙"
	},
	
	rhPageView_string45 : {
		en : 'green',
		zh : "绿"
	},
	
	rhPageView_string46 : {
		en : 'Light grey',
		zh : "浅灰"
	},
	
	rhPageView_string47 : {
		en : 'brown',
		zh : "棕色"
	},
	
	rhPageView_string48 : {
		en : 'background',
		zh : "背景"
	},
	
	rhPageView_string49 : {
		en : 'Gray (the default)',
		zh : "灰色(默认)"
	},
	
	rhPageView_string50 : {
		en : 'Wood grain',
		zh : "木纹"
	},
	rhPageView_string51 : {
		en : 'The white pattern',
		zh : "白花纹"
	},
	rhPageView_string52 : {
		en : 'Green grass',
		zh : "绿草"
	},
	rhPageView_string53 : {
		en : 'Yellow wood grain',
		zh : "黄木纹"
	},
	rhPageView_string54 : {
		en : 'Red wood grain',
		zh : "红木纹"
	},
	rhPageView_string55 : {
		en : 'Information is bigger',
		zh : "信息块头"
	},
	rhPageView_string56 : {
		en : 'white',
		zh : "白"
	},
	rhPageView_string57 : {
		en : 'green',
		zh : "绿色"
	},
	rhPageView_string58 : {
		en : 'orange',
		zh : "桔色"
	},
	rhPageView_string59 : {
		en : 'Design and color',
		zh : "花色"
	},
	
	rhPageView_string60 : {
		en : 'Yellow flower',
		zh : "黄色花形"
	},
	
	rhPageView_string61 : {
		en : 'The light blue',
		zh : "浅蓝色"
	},
	
	rhPageView_string62 : {
		en : 'Page will reload and styling, whether to continue?',
		zh : "页面将会重新加载并应用样式，是否继续?"
	},
	
	rhPortalView_string1 : {
		en : 'Office home page',
		zh : "办公首页"
	},
	
	rhPortalView_string2 : {
		en : 'Select the layout',
		zh : "选择布局"
	},
	
	rhPortalView_string3 : {
		en : 'Select the layout of the template',
		zh : "选择模版的布局"
	},
	
	rhPortalView_string4 : {
		en : 'Select the component',
		zh : "选择组件"
	},
	
	rhPortalView_string5 : {
		en : 'Choose the layout component',
		zh : "选择布局上的组件"
	},
	
	rhPortalView_string6 : {
		en : 'save',
		zh : " 保存 "
	},
	
	rhPortalView_string7 : {
		en : 'Save the entire template Settings',
		zh : "保存整个模版的设置"
	},
	
	rhPortalView_string8 : {
		en : 'preview',
		zh : " 预览 "
	},
	
	rhPortalView_string9 : {
		en : 'Preview the current template',
		zh : "预览当前模版"
	},
	
	rhPortalView_string10 : {
		en : 'Refresh',
		zh : "刷新 "
	},
	
	rhPortalView_string11 : {
		en : 'Refresh the current template',
		zh : "刷新当前模版 "
	},
	
	rhPortalView_string12 : {
		en : 'Close this page',
		zh : "关闭本页  "
	},
	
	rhPortalView_string13 : {
		en : 'Close the current template page',
		zh : "关闭当前模版页面"
	},
	
	rhPortalView_string14 : {
		en : 'Choose the style of the template, click save to take effect',
		zh : "选择模版的风格，点击保存生效"
	},
	
	rhPortalView_string15 : {
		en : 'Template style (the default)',
		zh : "模版风格（默认）"
	},
	
	rhPortalView_string16 : {
		en : 'Template style (blue)',
		zh : "模版风格（蓝）"
	},
	
	rhPortalView_string17 : {
		en : 'Template style (orange)',
		zh : "模版风格（桔色）"
	},
	
	rhPortalView_string18 : {
		en : 'Template style (ash)',
		zh : "模版风格（灰）"
	},
	
	rhPortalView_string19 : {
		en : 'The template preview',
		zh : "模版预览"
	},
	
	rhPortalView_string20 : {
		en : 'Select a template',
		zh : "选择模版"
	},
	
	rhPortalView_string21 : {
		en : 'operation',
		zh : "操作"
	},
	
	rhPortalView_string22 : {
		en : 'Select the template',
		zh : "选择该模版"
	},
	
	rhPortalView_string23 : {
		en : 'Enter the HTML editor',
		zh : "进入html编辑"
	},
	
	rhPortalView_string24 : {
		en : 'Confirm the application of the template?',
		zh : "是否确认应用该模版？"
	},
	
	rhPortalView_string25 : {
		en : 'Application code',
		zh : "应用代码"
	},
	
	rhPortalView_string26 : {
		en : '(template will be applied within text box code implementation)',
		zh : "（模版将应用文本框内的代码实现）"
	},
	
	rhPortalView_string27 : {
		en : 'The component number',
		zh : "组件编号"
	},
	
	rhPortalView_string28 : {
		en : 'Component name',
		zh : "组件名称"
	},
	
	rhPortalView_string29 : {
		en : 'A valid number!',
		zh : "点击查询"
	},
	
	rhPortalView_string30 : {
		en : 'Click on the query',
		zh : "设置参数"
	},
	
	rhPortalView_string31 : {
		en : 'Inputs (note: use the default value is empty, set after the completion of the click save button at the top of the effective!)',
		zh : "(注：输入项为空则使用默认值,设定完成后点击顶部保存按钮生效！)"
	},
	
	rhPortalView_string32 : {
		en : 'Column selection',
		zh : "栏目选择"
	},
	
	rhPortalView_string33 : {
		en : 'Select the column',
		zh : "选择栏目"
	},
	
	rhPortalView_string34 : {
		en : 'There is no',
		zh : "无"
	},
	
	rhPortalView_string35 : {
		en : 'The title bar type',
		zh : "标题条式"
	},
	
	rhPortalView_string36 : {
		en : 'Frame type',
		zh : "边框式"
	},
	
	rhPortalView_string37 : {
		en : 'According to',
		zh : "显示"
	},
	
	rhPortalView_string38 : {
		en : "Do not show",
		zh : "不显示"
	},
	
	rhPortalView_string39 : {
		en : 'Increase the height',
		zh : "增加高度"
	},
	
	rhPortalView_string40 : {
		en : 'Reduce the height',
		zh : "减少高度"
	},
	
	rhPortalView_string41 : {
		en : 'Choose the icon',
		zh : "选择图标"
	},
	
	rhPortalView_string42 : {
		en : 'empty',
		zh : "清空"
	},
	
	rhPortalView_string43 : {
		en : 'Parameter Settings',
		zh : "参数设置"
	},
	
	rhPortalView_string44 : {
		en : 'Delete the current component',
		zh : "删除当前组件"
	},
	
	rhPortalView_string45 : {
		en : 'Set the component and this component removed from the template?',
		zh : "将该组件和此组件的相关设置从模版中移除？"
	},
	
	rhSelectListView_string1 : {
		en : 'The query selection',
		zh : "查询选择"
	},
	
	rhSelectListView_string2 : {
		en : 'Have to reload the current list!',
		zh : "已重新加载当前列表！"
	},
	
	rhSelectListView_string3 : {
		en : 'Added!',
		zh : "添加！"
	},
	
	rhSelectListView_string4 : {
		en : 'Modify!',
		zh : "修改！"
	},
	
	rhSelectListView_string5 : {
		en : 'Please select a record',
		zh : "请选择记录"
	},
	
	rhSelectListView_string6 : {
		en : 'Add a option',
		zh : "添加一条选择"
	},
	
	rhSelectListView_string7 : {
		en : 'Please select a record!',
		zh : "请选择一条记录！"
	},
	
	rhSelectListView_string8 : {
		en : 'Change to choose',
		zh : "修改选择"
	},
	
	rhSelectListView_string9 : {
		en : 'Double click here to determine the choice',
		zh : "双击此处确定选择"
	},
	
	rhSelectListView_string10 : {
		en : 'Click ok to select',
		zh : "单击确定选择"
	},
	
	rhSelectListView_string11 : {
		en : 'Please select the record!',
		zh : "请选中记录！"
	},
	
	rhUserInfoView_string1 : {
		en : '<No>',
		zh : "<暂无>"
	},
	
	rhUserInfoView_string2 : {
		en : 'The phone',
		zh : "电话"
	},
	
	rhUserInfoView_string3 : {
		en : 'chat',
		zh : "聊天"
	},
	
	rhUserInfoView_string4 : {
		en : 'im',
		zh : "即时通讯"
	},
	
	rhUserInfoView_string5 : {
		en : 'Send a text message',
		zh : "发送短信"
	},
	
	rhUserInfoView_string6 : {
		en : 'mail',
		zh : "邮件"
	},
	
	rhUserInfoView_string7 : {
		en : 'Send E-mail',
		zh : "发送邮件"
	},
	
	rhWfCardView_string1 : {
		en : 'System configuration: button rendering SY_WF_BTN_RENDER configuration value is wrong, please check!',
		zh : "系统配置：按钮条渲染方式SY_WF_BTN_RENDER配置值有误，请检查！"
	},
	
	rhWfCardView_string2 : {
		en : 'The file has been locked!',
		zh : "文件已锁定!"
	},
	
	rhWfCardView_string3 : {
		en : 'Process terminated, need to resume in the trash box',
		zh : "流程已终止，需在废件箱中恢复"
	},
	
	rhWfCardView_string4 : {
		en : 'Has been successfully sent to',
		zh : "已经成功送交 "
	},
	
	rhWfCardView_string5 : {
		en : 'Sent to the wrong',
		zh : "送交错误！"
	},
	
	rhWfCardView_string6 : {
		en : 'At the bottom of the screen to return',
		zh : "返回上一步"
	},
	
	rhWfCardView_string7 : {
		en : 'Please click on the [save] button to save the data',
		zh : "请先点击[保存]按钮保存数据"
	},
	
	rhWfCardView_string8 : {
		en : 'Page does not add sign department!',
		zh : "页面没有添加会签部门！"
	},
	
	rhWfCardView_string9 : {
		en : 'Has been successfully sent to',
		zh : "已经成功送交给"
	},
	
	rhWfCardView_string10 : {
		en : 'Not find to the person, please contact the system administrator!',
		zh : "没有找到送交人，请联系系统管理员!"
	},
	
	rhWfCardView_string11 : {
		en : 'The current node without selection of personnel, please contact the system administrator!',
		zh : "当前节点没有可供选择的人员，请联系系统管理员！"
	},
	
	rhWfCardView_string12 : {
		en : 'Personnel selection',
		zh : "人员选择"
	},
	
	rhWfCardView_string13 : {
		en : 'Role of choice',
		zh : "角色选择"
	},
	
	rhWfCardView_string14 : {
		en : 'No selected personnel, please select again to the staff',
		zh : "没有选中人员，请重新选择送交人员"
	},
	
	rhWfCardView_string15 : {
		en : 'To failure.',
		zh : "送交失败。"
	},
	
	rhWfCardView_string16 : {
		en : 'Change history',
		zh : "变更历史"
	},
	
	rhWfCardView_string17 : {
		en : 'Please fill out the return reason',
		zh : "请填写退回原因"
	},
	
	rhWfCardView_string18 : {
		en : 'Content must not exceed 1000 characters.',
		zh : "内容不能超过1000个汉字。"
	},
	
	rhWfCardView_string19 : {
		en : 'Returned to the successful',
		zh : "退回成功"
	},
	
	rhWfCardView_string20 : {
		en : 'Return the failure',
		zh : "退回失败"
	},
	
	rhWfCardView_string21 : {
		en : 'The operation failure',
		zh : "操作失败"
	},
	
	rhWfCardView_string22 : {
		en : 'Please determine whether to terminate?',
		zh : "请确定是否终止?"
	},
	
	rhWfCardView_string23 : {
		en : 'File has been abolished, if you want to restore, please through the trash box operation.',
		zh : "文件已废止，如需恢复，请通过废件箱操作。"
	},
	
	rhWfCardView_string24 : {
		en : 'The operation failure',
		zh : "操作失败"
	},
	
	rhWfCardView_string25 : {
		en : 'Locking success',
		zh : "锁定成功"
	},
	
	rhWfCardView_string26 : {
		en : 'unlocked',
		zh : "解锁成功"
	},
	
	rhWfCardView_string27 : {
		en : 'Page data has been modified, please save or restore modified',
		zh : "页面数据已修改，请先保存或恢复修改"
	},
	
	rhWfCardView_string28 : {
		en : 'Please make sure the node json string format is correct, format for',
		zh : "请确定节点json串格式是否正确，格式为"
	},
	
	rhWfCardView_string29 : {
		en : 'Please configure the workflow button configuration parameters',
		zh : "请配置工作流按钮配置参数"
	},
	
	rhWfCardView_string30 : {
		en : 'Page data has been modified, please save or restore modified',
		zh : "页面数据已修改，请先保存或恢复修改"
	},
	
	rhWfCardView_string31 : {
		en : 'This document has been distributed, whether to distribute again?',
		zh : "该文件已经分发过，是否再次分发?"
	},
	
	rhWfCardView_string32 : {
		en : 'Distributed file',
		zh : "分发文件"
	},
	
	rhWfCardView_string33 : {
		en : 'Automatic distribution mode distribution field is empty, please check the distribution of node configuration',
		zh : "自动分发模式下分发字段为空，请检查分发节点配置"
	},
	
	rhWfCardView_string34 : {
		en : 'You choose the distribution of field does not exist, field called:',
		zh : "您选择的分发字段不存在，字段名为："
	},
	
	rhWfCardView_string35 : {
		en : 'Please confirm whether to send the file?',
		zh : "请确认是否要发送文件？"
	},
	
	rhWfCardView_string36 : {
		en : 'Send complete',
		zh : "发送完成"
	},
	
	rhWfCardView_string37 : {
		en : 'To distribute personnel list',
		zh : "待分发人员列表"
	},
	
	rhWfCardView_string38 : {
		en : 'distribution',
		zh : "分发"
	},
	
	rhWfCardView_string39 : {
		en : 'Send failure',
		zh : "发送失败"
	},
	
	rhWfCardView_string40 : {
		en : 'Data management',
		zh : "数据管理"
	},
	
	rhWfCardView_string41 : {
		en : 'Currently there is no fill, leadership can choose!',
		zh : "当前没有补登领导可以选择!"
	},
	rhWfCardView_string42 : {
		en : 'Not configured to fill an opinion on workflow type!',
		zh : "工作流中没有配置补登意见的意见类型!"
	},
	rhWfCardView_string43 : {
		en : 'To the opinions of the workflow configuration code, please check whether the opinion encoding exists!',
		zh : "没有查到工作流中配置的意见编码，请检查意见编码是否存在!"
	},
	rhWfCardView_string44 : {
		en : 'Fill an opinion',
		zh : "补登意见"
	},
	rhWfCardView_string45 : {
		en : 'leadership',
		zh : "领导"
	},
	rhWfCardView_string46 : {
		en : 'Repair thein time',
		zh : "补登时间"
	},
	
	rhWfCardView_string47 : {
		en : 'Opinion type',
		zh : "意见类型"
	},
	
	rhWfCardView_string48 : {
		en : 'Commonly used thereon',
		zh : "常用批语"
	},
	
	rhWfCardView_string49 : {
		en : 'Fill an opinion',
		zh : "补登意见"
	},
	
	rhWfCardView_string50 : {
		en : 'Please fill in the fill an opinion',
		zh : "请填写补登意见"
	},
	
	rhWfCardView_string51 : {
		en : 'Return an error, please check!',
		zh : "返回错误，请检查！"
	},
	rhWfCardView_string52 : {
		en : 'Tracking information',
		zh : "跟踪信息"
	},
	rhWfCardView_string53 : {
		en : 'Whether confirm transferred?',
		zh : "是否确认办结?"
	},
	rhWfCardView_string54 : {
		en : 'Transferred to success!',
		zh : "办结成功！"
	},
	rhWfCardView_string55 : {
		en : 'Transferred to failed, please check',
		zh : "办结未成功，请检查"
	},
	rhWfCardView_string56 : {
		en : 'No process can take back.',
		zh : "没有可以收回的流程。"
	},
	
	rhWfCardView_string58 : {
		en : 'Take back the process',
		zh : "选择收回流程"
	},
	
	rhWfCardView_string59 : {
		en : 'Distribution of detail',
		zh : "分发明细"
	},
	
	rhWfCardView_string60 : {
		en : 'Focus on',
		zh : "关注"
	},
	
	rhWfCardView_string61 : {
		en : 'Are you sure to cancel?',
		zh : "您确定取消么？"
	},
	
	rhWfCardView_string62 : {
		en : 'Please select you want to delete the data!',
		zh : "请选择您要删除的数据！"
	},
	rhWfCardView_string63 : {
		en : 'Favorites name no more than six words',
		zh : "收藏夹名称不超过6个字"
	},
	
	rhWfCardView_string64 : {
		en : 'Saved to favorites:',
		zh : "已保存至收藏夹:"
	},
	
	rhWfCardView_string65 : {
		en : 'Optional favorites:',
		zh : "可选收藏夹:"
	},
	
	rhWfCardView_string66 : {
		en : 'Future generations',
		zh : "全选"
	},
	
	rhWfCardView_string67 : {
		en : "All do not choose",
		zh : "全不选"
	},
	
	rhWfCardView_string68 : {
		en : 'Cancel the collection',
		zh : "取消收藏"
	},
	
	rhWfCardView_string69 : {
		en : 'The new favorites',
		zh : "新建收藏夹"
	},
	
	rhWfCardView_string70 : {
		en : 'Delete favorites',
		zh : "删除收藏夹"
	},

	rhWfCardView_string71 : {
		en : 'Add to favorites',
		zh : "添加至收藏夹"
	},
	
	rhWfCardView_string72 : {
		en : 'Name of favorites:',
		zh : "收藏夹名称:"
	},
	
	rhWfCardView_string73 : {
		en : 'add',
		zh : "添加"
	},
	
	rhWfCardView_string74 : {
		en : 'Please open for this document',
		zh : "请先将此公文公开"
	},
	
	rhWfCardView_string75 : {
		en : 'Please select the document type publicly',
		zh : "请先选择此公文公开类型"
	},
	
	rhWfCardView_string76 : {
		en : 'Please save the data first!',
		zh : "请先保存数据！"
	},
	
	rhWfCardView_string77 : {
		en : 'Release success!',
		zh : "发布成功！"
	},
	
	rhWfCardView_string78 : {
		en : 'Publish failed!',
		zh : "发布失败！"
	},
	
	rhWfCardView_string79 : {
		en : 'Have read',
		zh : "已阅"
	},
	
	rhWfCardView_string80 : {
		en : 'A valid number!',
		zh : "同意"
	},
	
	rhWfCardView_string81 : {
		en : 'Agreed to',
		zh : "努力加载中..."
	},
	
	rhWfCardView_string82 : {
		en : 'Leave a message to line leader',
		zh : "给行领导留言"
	},
	
	rhWfCardView_string83 : {
		en : "Can not be empty",
		zh : "意见不能为空"
	},
	
	rhWfCardView_string84 : {
		en : 'A valid number!',
		zh : "总经理给行领导留言"
	},
	
	rhWfCardView_string85 : {
		en : 'Save failed',
		zh : "保存失败"
	},
	
	rhWfCardView_string86 : {
		en : 'Access to data error',
		zh : "获取意见数据出错"
	},
	
	rhWfCardView_string87 : {
		en : 'The next link',
		zh : "下一环节"
	},
	
	rhWfCardView_string88 : {
		en : 'Dealing with people',
		zh : "处理人"
	},
	
	rhWfCardView_string89 : {
		en : 'A valid number!',
		zh : "是否确定废止?"
	},
	
	rhWfCardView_string90 : {
		en : 'Determine the repeal?',
		zh : "请选择拟稿单位"
	},
	
	platform_string1 : {
		en : 'System timeout, need to login again',
		zh : "系统超时，需要重新登录！"
	},
	platform_string2 : {
		en : 'System timeout, need to login again',
		zh : "系统超时，需要重新登录"
	},
	platform_string3 : {
		en : 'Error!',
		zh : "操作错误！"
	},
	platform_string4 : {
		en : 'Warning!',
		zh : "警告提示！"
	},
	tools_string1 : {
		en : 'The key word is empty',
		zh : "关键字为空"
	},
	tools_string2 : {
		en : 'No search results',
		zh : "没有搜索结果"
	},
	tools_string3 : {
		en : 'just',
		zh : "刚刚"
	},
	tools_string4 : {
		en : '1 minute ago',
		zh : "1分钟前"
	},
	tools_string5 : {
		en : 'Minutes ago',
		zh : "分钟前"
	},
	tools_string6 : {
		en : '1 hour ago',
		zh : "1小时前"
	},
	tools_string7 : {
		en : 'Hours before',
		zh : "小时前"
	},
	tools_string8 : {
		en : 'One day before',
		zh : "1天前"
	},
	tools_string9 : {
		en : 'Days ago',
		zh : "天前"
	},
	tools_string10 : {
		en : 'A month ago',
		zh : "1个月前"
	},
	tools_string11 : {
		en : 'Month ago',
		zh : "月前"
	},
	tools_string12 : {
		en : 'A year ago',
		zh : "1年前"
	},
	tools_string13 : {
		en : 'Years ago',
		zh : "年前"
	},
	tools_string14 : {
		en : 'In the morning',
		zh : "上午"
	},
	tools_string15 : {
		en : 'In the afternoon',
		zh : "下午"
	},
	tools_string16 : {
		en : 'In the evening',
		zh : "晚上"
	},
	tools_string17 : {
		en : 'In the morning',
		zh : "凌晨"
	},
	tools_string18 : {
		en : 'Beyond the calculation',
		zh : "超出计算范围"
	},
	tools_string19 : {
		en : 'Loads the javascript is unusual,',
		zh : "加载javascript异常，"
	},
	tools_string20 : {
		en : 'Debugging information',
		zh : "调试信息"
	},
	tools_string21 : {
		en : 'Select distribution scheme',
		zh : "选择分发方案"
	},
	tools_string22 : {
		en : 'Open a window, the address must have!',
		zh : "打开一个窗口，地址必须有！"
	},
	tools_string23 : {
		en : 'Have been transferred to',
		zh : "已办结"
	},
	tools_string24 : {
		en : 'Multiple to deal with the user',
		zh : "多个办理用户"
	},
	tools_string25 : {
		en : 'Connect the interrupt',
		zh : "连接中断"
	},
	tools_string26 : {
		en : 'determine',
		zh : "确定"
	},
	tools_string27 : {
		en : 'prompt',
		zh : "提示"
	},
	tools_string28 : {
		en : 'yes',
		zh : "是"
	},
	tools_string29 : {
		en : 'no',
		zh : "否"
	},
	
	portal_index_desk_string1 : {
		en : 'platform',
		zh : "平台"
	},
	
	portal_index_desk_string2 : {
		en : 'On the cross...',
		zh : "上传中..."
	},
	portal_index_desk_string3 : {
		en : 'Application Settings',
		zh : "应用设置"
	},
	portal_index_desk_string4 : {
		en : 'Split screen Settings',
		zh : "分屏设置"
	},
	portal_index_desk_string5 : {
		en : 'Desktop Settings',
		zh : "桌面设置"
	},
	portal_index_desk_string6 : {
		en : 'System restore',
		zh : "系统还原"
	},
	portal_index_desk_string7 : {
		en : 'Notification Settings',
		zh : "通知设置"
	},
	portal_index_desk_string8 : {
		en : 'Theme Settings',
		zh : "主题设置"
	},
	
	portal_index_desk_string9 : {
		en : 'The default display after the default desktop (login)',
		zh : "默认桌面(登录后默认显示)"
	},
	
	portal_index_desk_string10 : {
		en : 'The default application (the default display after login)',
		zh : "默认应用(登录后默认显示)"
	},
	
	portal_index_desk_string11 : {
		en : 'System program',
		zh : "系统程序"
	},
	portal_index_desk_string12 : {
		en : 'Initialize the system desktop application (revert to the initial default value)',
		zh : "初始化系统桌面程序(还原为初始默认值)"
	},
	portal_index_desk_string13 : {
		en : 'Theme layout',
		zh : "主题布局"
	},
	portal_index_desk_string14 : {
		en : 'Restore the default theme system',
		zh : "还原系统默认主题"
	},
	portal_index_desk_string15 : {
		en : 'Restore the system default desktop Settings',
		zh : "还原系统默认桌面设置"
	},
	portal_index_desk_string16 : {
		en : 'determine',
		zh : " 确 定 "
	},
	portal_index_desk_string17 : {
		en : 'The message',
		zh : "消息"
	},
	portal_index_desk_string18 : {
		en : 'Alerted to the fact that whether to accept applications from',
		zh : "是否接受来自应用的消息提醒"
	},
	portal_index_desk_string19 : {
		en : 'voice',
		zh : "声音"
	},
	portal_index_desk_string20 : {
		en : 'The system theme',
		zh : "系统主题"
	},
	portal_index_desk_string21 : {
		en : 'The custom',
		zh : "自定义"
	},
	portal_index_desk_string22 : {
		en : 'System will restore the desktop to set as the initial value, and the page will refresh the current system.',
		zh : "系统将还原桌面设置为初始值，并将刷新当前系统页面。"
	},
	
	rhDeskView_string1 : {
		en : 'A desktop application',
		zh : "桌面应用"
	},
	
	rhDeskView_string2 : {
		en : 'The default desktop',
		zh : "默认桌面"
	},
	rhDeskView_string3 : {
		en : 'The default application',
		zh : "默认应用"
	},
	rhDeskView_string4 : {
		en : 'The wallpaper is set',
		zh : "墙纸设置"
	},
	rhDeskView_string5 : {
		en : 'Mobile phone login',
		zh : "手机登录"
	},
	rhDeskView_string6 : {
		en : 'Click add to your desktop!',
		zh : "单击添加至桌面！"
	},
	
	rhDeskView_string7 : {
		en : 'Click the Settings to take effect!',
		zh : "单击设置生效！"
	},
	rhDeskView_string8 : {
		en : 'The icon on the desktop to reply to the initial state',
		zh : "桌面上的图标回复到初始状态"
	},
	rhDeskView_string9 : {
		en : 'Restore the system default wallpaper',
		zh : "还原系统默认墙纸"
	},
	rhDeskView_string10 : {
		en : 'Cancel the default desktop',
		zh : "取消默认桌面"
	},
	rhDeskView_string11 : {
		en : 'The system wallpaper',
		zh : "系统墙纸"
	},
	rhDeskView_string12 : {
		en : 'Scan the qr code into the mobile phone to log in',
		zh : "扫描二维码进入手机登录"
	},
	rhDeskView_string13 : {
		en : 'The current application has reached maximum number, please add new screen or other screen in continue to add.',
		zh : "当前应用已达最大数，请新加屏幕或在其它屏幕继续添加。"
	},
	rhDeskView_string14 : {
		en : 'Into limited, please check the table management, there is no set associated menu \ n (note: the associated menu must be leaves menu item).',
		zh : "进入受限，请检查工作台管理 ,没有设定关联菜单\n(注意：关联的菜单必须是叶子菜单项)!"
	},
	
	incl_home_string1 : {
		en : 'Personal to-do',
		zh : "个人待办"
	},
	incl_home_string2 : {
		en : 'Individuals to read',
		zh : "个人待阅"
	},
	incl_home_string3 : {
		en : 'Individual host',
		zh : "个人主办"
	},
	incl_home_string4 : {
		en : 'News',
		zh : "新闻浏览"
	},
	incl_home_string5 : {
		en : 'Message to remind',
		zh : "消息提醒"
	},
	
	incl_index_string1 : {
		en : 'Ongoing login authentication, please wait...',
		zh : "正在进行登录验证，请稍候..."
	},
	incl_index_string2 : {
		en : "The company can not be empty, please input",
		zh : "公司不能为空，请输入"
	},
	incl_index_string3 : {
		en : "Account can not be empty, please input",
		zh : "账号不能为空，请输入"
	},
	incl_index_string4 : {
		en : 'Password cannot be empty. Please enter',
		zh : "密码不能为空，请输入"
	},
	incl_index_string5 : {
		en : 'System login error',
		zh : "系统登录出错"
	},
	incl_index_string6 : {
		en : 'Verification through, is preparing to enter the system...',
		zh : "验证通过，正准备进入系统......"
	},
	incl_index_string7 : {
		en : 'Please input again!',
		zh : "  请重新输入!"
	},
	incl_index_string8 : {
		en : 'Please select a unit',
		zh : "请选择单位"
	},
	
	calDay_string1 : {
		en : 'Monday',
		zh : "星期一"
	},
	calDay_string2 : {
		en : 'Tuesday',
		zh : "星期二"
	},
	calDay_string3 : {
		en : 'Wednesday',
		zh : "星期三"
	},
	calDay_string4 : {
		en : 'Thursday',
		zh : "星期四"
	},
	calDay_string5 : {
		en : 'Friday',
		zh : "星期五"
	},
	calDay_string6 : {
		en : 'Saturday',
		zh : "星期六"
	},
	calDay_string7 : {
		en : 'Sunday',
		zh : "星期日"
	},
	mind_string1 : {
		en : "Workflow didn't start",
		zh : "工作流没启动"
	},
	rh_vi_reportView_string1 : {
		en : "Began to query",
		zh : "开始查询"
	},
	
	rh_vi_reportView_string2 : {
		en : 'The report server address is not configured to use',
		zh : "报表服务器地址未配置，无法使用"
	},
	cuSearchView_string1 : {
		en : 'Key words:',
		zh : "关键词："
	},
	cuSearchView_string2 : {
		en : 'Personalized subscription',
		zh : "个性化订阅"
	},
	cuSearchView_string3 : {
		en : 'Personalized subscription Settings',
		zh : "个性化订阅设置"
	},
	cuSearchView_string4 : {
		en : 'Keyword position:',
		zh : "关键词位置:"
	},
	cuSearchView_string5 : {
		en : 'content',
		zh : "内容"
	},
	cuSearchView_string6 : {
		en : 'Limited search data sources:',
		zh : "限定搜索的数据源:"
	},
	cuSearchView_string7 : {
		en : 'The results are sorted:',
		zh : "结果排序方式:"
	},
	cuSearchView_string8 : {
		en : 'Sort by focus',
		zh : "按焦点排序"
	},
	
	cuSearchView_string9 : {
		en : 'According to time order',
		zh : "按时间排序"
	},
	
	cuSearchView_string10 : {
		en : 'Results show that the number:',
		zh : "结果显示条数:"
	},
	cuSearchView_string11 : {
		en : 'Cancel the keywords customization',
		zh : "取消该关键词定制"
	},
	cuSearchView_string12 : {
		en : 'Return an error, please check!',
		zh : "返回错误，请检查！"
	},
	
	cuSearchView_string13 : {
		en : 'To view more',
		zh : "查看更多"
	},
	
	cuSearchView_string14 : {
		en : 'Please select a category',
		zh : "请选择类别"
	},
	
	cuSearchView_string15 : {
		en : 'The unknown',
		zh : "未知"
	},
	
	cuSearchView_string16 : {
		en : 'The Internet',
		zh : "互联网"
	},
	cuSearchView_string17 : {
		en : 'Show only the part of the item',
		zh : "只显示部分项"
	},
	cuSearchView_string18 : {
		en : 'All show',
		zh : "全部显示"
	},
	cuSearchView_string19 : {
		en : 'Relevant search',
		zh : "相关搜索"
	},
	cuSearchView_string20 : {
		en : 'The test attachment preview',
		zh : "测试附件预览"
	},
	cuSearchView_string21 : {
		en : 'This is preview the attachment',
		zh : "这是附件预览"
	},
	
	lineJs_string1 : {
		en : 'Invalid node data objects.',
		zh : "无效的节点数据对象。"
	},
	lineJs_string2 : {
		en : 'To choice',
		zh : "条件选择"
	},
	lineJs_string3 : {
		en : 'Double click on the select defined variables',
		zh : "双击选择已定义变量"
	},
	lineJs_string4 : {
		en : 'Please double click and select the variables.',
		zh : "请双击并选择变量!"
	},
	
	lineJs_string5 : {
		en : 'A valid number!',
		zh : "请选择操作符!"
	},
	
	lineJs_string6 : {
		en : 'Please select operator!',
		zh : "请填写值!"
	},
	lineJs_string7 : {
		en : 'Determine to cancel?',
		zh : "是否确定取消？"
	},
	nodeJs_string1 : {
		en : 'Node defined data formats may not correct, please do not save the process, and contact the administrator.',
		zh : "节点定义数据格式可能不正确，请不要保存流程，并与管理员联系。"
	},
	nodeJs_string2 : {
		en : 'Transferred to',
		zh : "办结"
	},
	nodeJs_string3 : {
		en : 'Back to the agent',
		zh : "退回经办人"
	},
	nodeJs_string4 : {
		en : 'Back to the',
		zh : "退回"
	},
	nodeJs_string5 : {
		en : 'CuiBan',
		zh : "催办"
	},
	nodeJs_string6 : {
		en : 'Please check the timeout time need to fill in the Numbers!',
		zh : "请检查，超时时间需填写数字!"
	},
	nodeJs_string7 : {
		en : 'General opinion',
		zh : "普通意见"
	},
	
	nodeJs_string8 : {
		en : 'Please select fixed opinions',
		zh : "请先选择固定意见"
	},
	nodeJs_string9 : {
		en : 'draftsman',
		zh : "起草人"
	},
	
	nodeJs_string10 : {
		en : 'Whether to delete the?',
		zh : "是否删除该行?"
	},
	nodeJs_string11 : {
		en : 'Please select the basic information of the first choice treatment.',
		zh : "请先选择处理选择的基本信息。"
	},
	
	nodeJs_string12 : {
		en : 'A valid number!',
		zh : "签名"
	},		
	nodeJs_string13 : {
		en : 'The signature',
		zh : "添加"
	},
	nodeJs_string14 : {
		en : 'Enter the node',
		zh : "进入节点"
	},
	nodeJs_string15 : {
		en : 'The end node',
		zh : "结束节点"
	},
	nodeJs_string16 : {
		en : 'Save the opinion',
		zh : "保存意见"
	},
	nodeJs_string17 : {
		en : 'Check the approval sheet',
		zh : "查看审批单"
	},
	procDefJs_string1 : {
		en : 'Please input you want to save the file name:',
		zh : "请输入您要保存的文件名:"
	},
	
	procDefJs_string2 : {
		en : 'Has been saved in the directory',
		zh : "已经保存到目录"
	},
	procDefJs_string3 : {
		en : 'Has been saved in the directory',
		zh : "已经保存到目录"
	},
	procDefJs_string4 : {
		en : 'The unknown node type',
		zh : "未知的结点类型"
	},
	procDefJs_string5 : {
		en : 'Node properties',
		zh : "节点属性"
	},
	procDefJs_string6 : {
		en : 'Line attribute',
		zh : "线属性"
	},
	PAGE_RIGHT_CONTENT_remind : {
		en : 'Remind',
		zh : "提醒"
	},
	PAGE_RIGHT_CONTENT_commFunc : {
		en : 'Common function',
		zh : "常用功能"
	},
	PAGE_RIGHT_CONTENT_inform : {
		en : 'Information',
		zh : "情况通报"
	},
	PAGE_RIGHT_CONTENT_system : {
		en : 'System entrance',
		zh : "系统入口"
	},
	
	test : {
		en : '',
		zh : '请输入整数长度不超过{0}位，小数长度不超过{1}位的有效数字！"))'
	},
	
	rh_ui_card_L1 :{
		en : 'Please enter an integer length less than {0}, decimal length less than {1} valid number!',
		zh : "请输入整数长度不超过{0}位，小数长度不超过{1}位的有效数字！"
	},
	rh_ui_card_L2 :{
		en : 'Please enter the length of no more than {0} a valid number!',
		zh : "请输入长度不超过{0}位有效数字！"
	},
	rh_ui_card_L3 :{
		en : "Length can not more than {0} characters (or {1} characters).",
		zh : "长度不能超过{0}个汉字(或{1}个字符)！"
	},
	rh_ui_card_L4 :{
		en : 'Field file {0} does not exist!',
		zh : "字段文件{0}不存在！"
	},
	rh_ui_card_L5 :{
		en : 'Attachment {0} does not exist!',
		zh : "附件{0}不存在！"
	},
	rh_ui_card_L6 :{
		en : 'Field item {0} does not exist!',
		zh : "字段项{0}不存在！"
	},
	rh_ui_card_L7 :{
		en : 'Length cannot exceed {0}!',
		zh : "长度不能超过{0}位！"
	},
	rh_ui_card_L8 :{
		en : 'Without creating any UI for {0} the from rh. The UI. The Form',
		zh : "没有创建任何UI for {0} from rh.ui.Form"
	},
	rh_ui_grid_L1 : {
		en : 'total {0}.',
		zh : "共{0}条"
	},
	rh_ui_gridCard_L1 : {
		en : 'From: {0} time: {1}',
		zh : "来自：{0} 时间:{1}"
	},
	rh_ui_gridCard_L2 : {
		en : 'From: {0} is one of the companies believe the message',
		zh : "来自：{0}的一条企信消息"
	},
	rh_ui_next_L1 : {
		en : 'Id for {0} component does not exist!',
		zh : "id为{0}的组件不存在！"
	},
	rhCommentView_L1 : {
		en : '#Reply {0} floor#',
		zh : "#回复{0}楼# "
	},
	rhDictTreeView_L1 : {
		en : 'Empty list ({0})',
		zh : "清空列表（共{0}项）"
	},
	rhPortalView_L1 : {
		en : '{0} < system generated {1} >',
		zh : "{0}<系统生成{1}>"
	},
	rhUserInfo_L1 : {
		en : '{0} no < / span >',
		zh : "{0} 无</span>"
	},
	platform_L1 : {
		en : 'Please to create components "{0}" id attribute set value.',
		zh : "请为要创建的组件【{0}】的id属性设置值。"
	},
	platform_L2 : {
		en : 'The error information {0} the error',
		zh : "错误信息{0}error"
	},
	portal_index_desk_L1 : {
		en : 'The first {0} screen desktop',
		zh : "第{0}屏桌面"
	},
	rhDeskView_L1 : {
		en : 'The first page {0}',
		zh : "第{0}页"
	},
	syServ_List1 : {
		en : 'Select File',
		zh : '请选择文件'
	},
	syServ_List2 : {
		en : 'Import a single service definition can choose json files or zip files, import multiple file selection zip file.',
		zh : '请选择要导入的文件，导入单个服务定义可以选择json文件或zip文件，导入多个文件选择zip文件。'
	},
	syServ_List3 : {
		en : 'Please select a file!',
		zh : '请选择文件上传'
	},
	syServ_List4 : {
		en : 'Please input table name!',
		zh : '请输入表名'
	},
	syServ_List5 : {
		en : 'Please enter you need to import the table name?',
		zh : '请输入需要导入的表名？（格式为：数据源.表名；没有数据源则使用缺省数据源）'
	},
	syServ_List6 : {
		en : 'Did not enter a valid table name!',
		zh : '没有输入有效的表名！'
	},
	syServ_Card1 : {
		en : 'Please enter the need to replicate definition from which service primary key information?',
		zh : '请输入需要从哪个服务主键中复制定义信息？'
	},
	syServ_Card2 : {
		en : 'Did not enter a valid service primary key!',
		zh : '没有输入有效的服务主键！'
	},
	syServ_Card3 : {
		en : 'Please set the title format!',
		zh : '请先设置标题格式！'
	},
	
	track_table1 : {
		en : 'Sender',
		zh : '发送人'
	},
	
	track_table2 : {
		en : 'Send time',
		zh : '发送时间'
	},
	
	track_table3 : {
		en : 'Transactor',
		zh : '办理人'
	},
	
	track_table4 : {
		en : 'To deal with time',
		zh : '办理时间'
	},
	
	/**转化页面静态文本，例如在页面写死的中文内容 */
	transStatic: function(key) {
		try{
			var rhLanguage = Tools.getLanguageFromCookie();
			return this[key][rhLanguage];
		} catch(e){
			console.error(e);
		}
		return '';
	},
	
	/**转化页面动态文本，例如在页面根据服务配置动态读取的内容 */
	transDynamic: function(key, enObj, value) {
		var rhLanguage = Tools.getLanguageFromCookie();
		if(typeof(enObj) == 'string') {
			enObj = StrToJson(enObj);
		}
		if (rhLanguage == "en") {
			if(enObj && enObj[key]) {
				return enObj[key];
			} else {
				return value;
			}
		} else {
			return value;
		}
	},
	
	/**转换模板页面静态文本，如html、ftl、jsp**/
	transTemplate : function(items) {
		var _self = this;
		var rhLanguage = Tools.getLanguageFromCookie();

		if (rhLanguage == "en") {
			$(items).each(function(index, obj) {
				var code = $(obj).attr("transCode");
				var value = _self.transStatic(code);
				if (value != undefined && value != '') {
					$(obj).text(value);
				}
			});
		}
	},

	transArr: function(key, valArr) {
		var rhLanguage = Tools.getLanguageFromCookie();
		var tempStr = this[key][rhLanguage];
		for(var i = 0; i < valArr.length; i++) {
			tempStr = tempStr.replace(new RegExp("\\{" + i + "\\}","g"), valArr[i]);  
		}
		return tempStr;
	}
}

