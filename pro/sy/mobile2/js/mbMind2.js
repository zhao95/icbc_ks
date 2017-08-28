GLOBAL.namespace('mb.vi');
mb.vi.mind = function(options) {
	var defaults = {
		'parHandler': null,
		'wfCard': null,
		'servId': null,
		'dataId': null,
		'pCon': null
	};
	this.opts = $.extend(defaults, options);
	this._mind = this.opts.mind;				// mindDatas, mindInput
	this._servId = this.opts.servId;			// servId
	this._dataId = this.opts.dataId;			// 卡片数据主键
	this._parHandler = this.opts.parHandler;	// 句柄
	this._wfCard = this.opts.wfCard;			// wfCard
	this._pCon = this.opts.pCon;				// mindfieldcontain
	this._mindInput = '';
	this._mindList = '';							
	this._odeptList = '';						// 填写过意见的所有机构
	this.oldMindText = '';
	
	// 意见是否已修改,包括意见内容
	this.isModify = function() {
		return this._textIsModify();
	};
	// 意见内容是否已修改
	this._textIsModify = function() {
		var newVal = $('#mind-content').val() || '';
		if (newVal == this.oldMindText	|| newVal == '') {
			return false;
		}
		return true;
	};
};
mb.vi.mind.prototype.show = function() {
	this._renderMindList();
	this._getUseMindData();
	this._bindEvent();
};
mb.vi.mind.prototype._renderMindList = function() {
	var _self = this;
	
	var params = {
			'_isMobile': true,
			'CAN_COPY': false,
			'userDoInWf': true, 	// 此用户是否正在当前节点
			'SERV_ID': _self._servId,
			'DATA_ID': _self._dataId,
			'EXPANDSION_ALL': true
	};
	_self._wfCard.appendAgentUserFlagTo(params);
	var resultData = FireFly.doAct('SY_COMM_MIND', 'displayMindTitleMB', params, false, false);
	$(this._pCon[1]).html('').append(resultData['MIND_TITLE']);
	console.debug(resultData['_DATA_']);
	_self.curMind = '';
	_self.curMindCon = '';
	$.each(resultData['_DATA_'], function(i, mind) {
		if (mind['S_USER'] == System.getUser('USER_CODE')
				|| (mind['IS_BD'] != '1' && mind['BD_USER'] == System.getUser('USER_CODE'))) {  //当前人自己的意见，或是委托处理的意见
			_self.curMind = mind;
			_self.curMindCon = mind['MIND_CONTENT'];
			_self.files = mind['SY_COMM_FILE'];
		}
	});
	// 处理滚动条
	if (_self._parHandler._parHandler.cardIScroll) {
		_self._parHandler._parHandler.cardIScroll.refresh();
	}
	// 处理意见图片
	$('.mind-image').css('height', $('.mind-image').width());
	// 处理意见的时间
	_self._parHandler._parHandler.listenTimeago();
};
mb.vi.mind.prototype._bindEvent = function() {
	var _self = this;
	
	// 给添加按钮绑定事件
	$(this._pCon[0]).off('click').on('click', function() {
		var params = {
				customMindList: _self.useMindDatas,
				uploadUrl: window.location.href + "?SERV_ID=SY_COMM_MIND&WF_NI_ID=" + _self._wfCard.getNodeInstBean().NI_ID + "",
				content: _self.curMindCon,
				placeholder: '请添加意见...',
				serverUrl: window.location.protocol + '//' + window.location.host, // http://localhost:8080
				files: _self.files
		};
		rh.displayMindInputDialog(params, function(outBean) {
			console.debug(_self._wfCard.getMindCodeBean());
			var data = {};
				data['MIND_CODE'] = _self._wfCard.getMindCodeBean().CODE_ID;
				data['MIND_CONTENT'] = outBean['content'];
				data['MIND_CODE_NAME'] = _self._wfCard.getMindCodeBean().CODE_NAME;
				data['SERV_ID'] = _self._servId;
				data['DATA_ID'] = _self._dataId;
				data['WF_NI_ID'] = _self._wfCard.getNodeInstBean().NI_ID;
				data['WF_NI_NAME'] = _self._wfCard.getNodeInstBean().NODE_NAME;
				data['MIND_DIS_RULE'] = _self._wfCard.getMindCodeBean().MIND_DIS_RULE; // 显示规则
				data['MIND_TYPE'] = '1'; // 1:文字意见;2:手写意见
				data['S_FLAG'] = '2';
				
			if (_self.curMind && _self.curMind.MIND_ID) { // 如果存在则更新，否则保存
				data['_PK_'] = _self.curMind.MIND_ID;
			}
			
			_self._wfCard.appendAgentUserFlagTo(data);
			
			// 保存意见
			FireFly.doAct('SY_COMM_MIND', 'save', data).then(function(result) {
				console.debug(result);
				// 删除老数据
				if (outBean['delFileIds'].length > 0) {
					var delIdsArr = [];
					$.each(outBean['delFileIds'], function(i, o) {
						delIdsArr.push(o['FILE_ID']);
					});
					FireFly.listDelete('SY_COMM_FILE', {'_PK_': delIdsArr.join(',')}, false, false);
				}
				// 添加新数据
				if (outBean['newFiles'].length > 0) {
					var newFilesArr = [];
					$.each(outBean['newFiles'], function(i, o) {
						o['DATA_ID'] = result['_PK_'];
						newFilesArr.push(o);
					});
					
					FireFly.batchSave('SY_COMM_FILE', {'BATCHDATAS': newFilesArr}, false, false);
				}
				_self._renderMindList();
			});
		}, function(outBean) {
			console.debug(outBean);
		});
	});
	
	// 给声音绑定事件
	$('.mind-sounds').off('click', 'button').on('click', 'button', function(event) {
		var _this = this;

		event.preventDefault();
		
		if ($(_this).hasClass('active')) { // 如果当前语音正在播放
			$(_this).removeClass('active');
			_self.pauseAudio(_self.audioObj);
		} else { // 当前语音未播放
			$('.mind-sounds').find('button').removeClass('active'); // 清除其他语音样式
			$(_this).addClass('active'); // 添加正在播放样式
			if (!_self.audioDocumentFragment) {
				_self.audioDocumentFragment = document.createDocumentFragment();
			}
			if (_self.audioObj) {
				_self.pauseAudio(_self.audioObj);
			}
			_self.audioObj = new Audio();
			var soundSrc = '/file/' + $(_this).attr('data-fileid'); // 语音文件路径
			// 创建语音元素
			_self.audioObj.src = soundSrc;
			_self.audioObj.autoplay = true;
			_self.audioObj.controls = 'controls';
			
			// 追加至文档碎片中
			_self.audioDocumentFragment.appendChild(_self.audioObj);
			
			// 给播放结束绑定事件
			$(_self.audioObj).on('ended', function() {
				$(_this).removeClass('active');
				_self.pauseAudio(this);
			});
		}
	});
};
/**
 * 停止语音播放
 */
mb.vi.mind.prototype.pauseAudio = function(audioObj) {
	audioObj.src = '';
	audioObj.pause();
	$(audioObj).remove();
};
/**
 * 获取常用意见
 */
mb.vi.mind.prototype._getUseMindData = function() {
	var _self = this;
	
	var params = {
			'_NOPAGE_': true
	};
	
	FireFly.doAct('SY_COMM_USUAL', 'query', params).then(function(result) {
		_self.useMindDatas = result['_DATA_'];
	});
};






























