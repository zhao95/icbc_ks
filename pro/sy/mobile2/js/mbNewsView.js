/**
 * 新闻浏览页面渲染引擎
 */
GLOBAL.namespace('mb.vi');
/**
 * 构造方法
 */
mb.vi.newsView = function(options) {
	var defaults = {
		'id': 'newsview'	
	};
	this.opts = $.extend(defaults, options);
	this.id = this.opts.id;
	this.servId = this.opts.sId;
	this.pkCode = this.opts.pkCode;
	this.headerTitle = this.opts.headerTitle;
	this._data = null;
};
mb.vi.newsView.prototype.show = function() {
	var _self = this;
	$.mobile.loading('show', {
		text: '加载中...',
		textVisible: true,
		textonly: false
	});
	this._initMainData().then(function() {
		_self._layout();
		_self._render();
		_self._bindEvent();
		_self._afterLoad();
	});
	$.mobile.loading('hide');
	
	// 这段压缩的时候报错
	/*.catch(function(err) {
		console.log(err);
	}).finally(function() {
		$.mobile.loading("hide");
	});*/
};
/**
 * 初始化主数据
 */
mb.vi.newsView.prototype._initMainData = function() {
	var _self = this;
	return FireFly.byId4Card(this.servId, this.pkCode).then(function(result) { // TODO 没有写这个byId4Card方法
		if (result) {
			_self._data = result;
			_self.news = result["form"]; // 新闻内容
			if (result["file"] && result["file"]["_DATA_"].length>0) {
				_self.attachList = result["file"]["_DATA_"]; // 新闻附件
			}
		}
	});
};
/**
 * 初始化页面各部分
 */
mb.vi.newsView.prototype._layout = function() {
	this.pageWrp = $('#' + this.id);
	this.headerWrapper = $('#' + this.id + '_header');
	this.contentWrapper = $('#' + this.id + '_content');
};
/**
 * 渲染新闻页面
 */
mb.vi.newsView.prototype._render = function() {
	var _self = this;
	this.picArr = [];
	this.fileArr = [];
	this.hasFileFlag = false;
	this.hasPicFlag = false;
	
	// 1.标题
	this.headerWrapper.find("h2").html(this.headerTitle);
	
	if (this.attachList) { // 如果有附件
		// 只取图片集
		$.each(this.attachList, function(i, obj) {
			if (obj["FILE_CAT"] == 'TUPIANJI') {
				_self.hasPicFlag = true;
				var filename = obj["DIS_NAME"] ? obj["DIS_NAME"] : obj["FILE_NAME"];
//				_self.picArr.push("<img src='" + ZhbxImgPath + "'/file/" + obj["FILE_ID"] + "?mobile=1' alt='" + obj["FILE_NAME"] + "' />");
				var url = ZhbxImgPath + "/file/" + obj["FILE_ID"];
				_self.picArr.push({url: url});
			}
			if (obj["FILE_CAT"] == 'FUJIAN') {
				_self.hasFileFlag = true;
				var filename = obj["DIS_NAME"] ? obj["DIS_NAME"] : obj["FILE_NAME"];
				var sMtime = obj["S_MTIME"];
				var sUname = obj["S_UNAME"];
				var fileSize = obj['FILE_SIZE']
				,	fileMtype = obj['FILE_NAME'];
				var fileSizeH = fileSize == '' ? '' : (fileSize/1024 > 1024 ? (fileSize/1024/1024 > 1024 ? parseInt(fileSize/1024/1024/1024) + 'GB' : parseInt(fileSize/1024/1024) + 'MB') : parseInt(fileSize/1024) + 'KB');
				var fileMtypeH = 'default';
				if (fileMtype) { // 如果有fileName
					var switchStr = fileMtype.substring(fileMtype.lastIndexOf('.') + 1, fileMtype.length + 1);
					switchStr = switchStr.toUpperCase();
					
					switch(switchStr) {
						case 'DOCX':
						case 'DOC':
							fileMtypeH = 'doc';break;
						case 'XLSX':
						case 'XLS':
							fileMtypeH = 'xls';break;
						case 'PPTX':
						case 'PPT':
							fileMtypeH = 'ppt';break;
						case 'PDF':
							fileMtypeH = 'pdf';break;
						case 'TXT':
							fileMtypeH = 'txt';break;
						case 'GIF':
							fileMtypeH = 'gif';break;
						case 'JPG':
							fileMtypeH = 'jpg';break;
						case 'PNG':
							fileMtypeH = 'png';break;
						case 'ZIP':
							fileMtypeH = 'zip';break;
						case 'RAR':
							fileMtypeH = 'rar';break;
					}
					
				}
				_self.fileArr.push("<li data-href='" + 
								ZhbxImgPath + "/file/" + obj["FILE_ID"] + "?mobile=1' class='zhbx-file-link' data-ajax='false' data-fileName='" + 
								filename + "' data-fileId='" + 
								obj['FILE_ID'] + "' data-filePath='" + obj['FILE_PATH'] + "' data-fileType='" + 
								fileMtypeH + "' data-fileSize='" + fileSize + "' data-filesmTime='" + sMtime + "'><h5 style='color:#5C5CE8;'>" + 
								filename + "</h5><p class='zhbx-p-text-right'><strong>" + 
								sUname + "</strong>" + 
								sMtime + "</p></li>");
			}
		});
	}
	// 标题
	var $title = $("<div class='zhbx-news-title'>" + this.news["NEWS_SUBJECT"] + "</div>").appendTo(this.contentWrapper);
	// 信息来源、点击量
	var $viewcount = $("<div class='zhbx-news-viewcount'>来源：" + this.news["NEWS_SOURCE"] + "/点击量：" + this.news["COUNTER"] + "</div>").appendTo(this.contentWrapper);
	// 发布时间
	var $time = $("<div class='zhbx-news-time'>发布日期：" + this.news["NEWS_TIME"] + "</div>").appendTo(this.contentWrapper);
	// 内容
	_self.$content = $("<div class='zhbx-news-content'>" + this.news["NEWS_BODY"] + "</div>").appendTo(this.contentWrapper);
	// 图片
	if (_self.hasPicFlag) {
		_self.$PhotoSwipeTarget = $("<div id='PhotoSwipeTarget'></div>");
		var $imgs = $("<div class='zhbx-news-imgs'></div>").appendTo(this.contentWrapper);
			$imgs.append(_self.$PhotoSwipeTarget);
		var indicators = $("<div id='Indicators'></div>");
		$.each(_self.picArr, function() {
			indicators.append("<span></span>");
		});
		$imgs.append(indicators);
	}
//	var $imgs = $("<div class='zhbx-news-imgs'><div id='PhotoSwipeTarget'></div><div id='Indicators'></div></div>").appendTo(this.contentWrapper);
	// 附件
	if (_self.hasFileFlag) {
		var $collSet = $("<div data-role='collapsible-set'></div>").appendTo(this.contentWrapper);
		_self.$files = $("<div class='zhbx-news-files' data-role='collapsible' data-collapsed='false'></div>").appendTo($collSet);
		_self.$files.append("<h4>附件</h4>");
		_self.$fileListView = $("<ul data-role='listview'></ul>");
		$.each(_self.fileArr, function(i, obj) {
			_self.$fileListView.append(obj);
		});
		_self.$files.append(_self.$fileListView);
		
		// 刷新组件，必须先初始化一下
		_self.$fileListView.listview().listview('refresh');
		$collSet.collapsibleset().collapsibleset('refresh');
	}
};
mb.vi.newsView.prototype._openFileDef = function(fileId, method, filesmTime, rootPath) {
	var deferred = Q.defer();
	$.ajax({
		url	: encodeURI(FireFly.getContextPath() + '/OA_MOBILE_FILE_SERV.' + method + '.do')  + '?t=' + Math.random(),
		type: 'POST',
		data: {'fileId': fileId, 'filesmTime': filesmTime, 'rootPath': rootPath},
		dataType: 'json',
		cache: false,
		timeout: 8000,
		success: function(result) {
			// 如果出现fileId为空时，将返回ERROR信息
			if (result['_MSG_'].indexOf('ERROR,') >= 0) {
				deferred.reject(); // 拒绝
			} else {
				deferred.resolve(result);
			}
		},
		error: function(err) {
			deferred.reject();
		}
	});
	return deferred.promise;
};
/**
 * 附件绑定查看事件
 */
mb.vi.newsView.prototype._bindEvent = function() {
	var _self = this;
	
	if (_self.$files) {
		_self.$files.on('vclick', '.zhbx-file-link', function(event) {
			event.preventDefault();
			var url = $(this).attr("data-href"),
				fileId = $(this).attr("data-fileId"),
				fileName = $(this).attr("data-fileName"),
				filePath = $(this).attr("data-filePath"),
				fileType = $(this).attr('data-fileType'),
				fileSize = $(this).attr('data-fileSize'),
				filesmTime = $(this).attr('data-filesmTime');
			
			// 检测文件类型
			if (fileType == 'zip' || fileType == 'rar' || fileType == 'default' ) {
				alert('文件格式不支持，请在电脑端查看！');
				return;
			}
			
			// 检测文件大小
			if (fileSize > 1024*1024*10) {
				alert('文件过大，请在电脑端查看！');
				return;
			}
			if (fileSize > 1024*1024*5 && fileSize <= 1024*1024*10) {
				if (!confirm('文件过大,是否继续用手机浏览?')) {
					return;
				}
			}
			
			$.mobile.loading('show', {
				text: "加载中...",
				textVisible: true,
				textonly: false
			});
			if (fileId) { // 如果fileId不为空，则发送请求
				
				if (fileType == 'gif' || fileType == 'jpg' || fileType == 'png') {
					wx.previewImage({
						current: url,
						urls: [url]
					});
					$.mobile.loading('hide');
				} else if (fileType == 'pdf') {
					_self._openFileDef(fileId, 'getImageByFileIdFromPortalForPdf', filesmTime, ZhbxImgPath).then(function(result) {
						
						$.mobile.loading('hide');
						var picList = []
						,	pageCount = result['pageCount']
						,	time = result['time'];
						for (var i=0; i<pageCount; i++) {
//							picList.push(FireFly.getContextPath() + '/oa/mobile/file-temp-dir/' + fileId + '/' + i + '.jpg?t=' + Math.random());
							picList.push(FireFly.getContextPath() + "/jqm_test/server/jsp/download.jsp?fileId=" + fileId + "&pageName=" + i + ".jpg&t=" + Math.random());
						}
						if (picList.length > 0) {
							wx.previewImage({
								current: picList[0],
								urls: picList
							});
						}
					}, function() {
						$.mobile.loading('hide');
						alert('系统正在处理文件,请稍后再试！');
					});
				} else {
					_self._openFileDef(fileId, 'getImageByFileIdFromPortal', filesmTime, ZhbxImgPath).then(function(result) {
						
						$.mobile.loading('hide');
						var picList = []
						,	pageCount = result['pageCount']
						,	time = result['time'];
						for (var i=0; i<pageCount; i++) {
//							picList.push(FireFly.getContextPath() + '/oa/mobile/file-temp-dir/' + fileId + '/' + i + '.jpg?t=' + Math.random());
							picList.push(FireFly.getContextPath() + "/jqm_test/server/jsp/download.jsp?fileId=" + fileId + "&pageName=" + i + ".jpg&t=" + Math.random());
						}
						wx.previewImage({
							current: picList[0],
							urls: picList
						});
					}, function() {
						$.mobile.loading('hide');
						alert('系统正在处理文件,请稍后再试！');
					});
				}
			}
		});
	}
};
/**
 * 加载后执行
 */
mb.vi.newsView.prototype._afterLoad = function() {
	var _self = this;
	if (_self.hasPicFlag) { // 如果有图片集，则执行此操作
		_self._initPhotoSwipe(window.Code.PhotoSwipe);
	}
	
	// 处理内容中图片的请求路径
	_self._formatBodyImgSrc();
	
	$.mobile.pageContainer.pagecontainer("change", _self.pageWrp);
	
	// 如果有附件列表，刷新
//	if (_self.$fileListView) {
//		_self.$fileListView.listview().listview('refresh');
//	}
};
/**
 * 初始化PhotoSwipe组件
 */
mb.vi.newsView.prototype._initPhotoSwipe = function(PhotoSwipe) {
	var _self = this;
	var instance, indicators;
	instance = PhotoSwipe.attach(_self.picArr, {
		target: _self.$PhotoSwipeTarget[0],
		preventHide: true,
		allowUserZoom: false,
		captionAndToolbarHide: true,
		zIndex: 100,
		getImageSource: function(obj) {
			return obj.url;
		},
		getImageCaption: function(obj) {
			return obj.caption;
		}
	});
	
	indicators = $("#Indicators span");
	
	instance.addEventHandler(PhotoSwipe.EventTypes.onDisplayImage, function(e) {
		var i, len;
		for (i=0, len=indicators.length; i<len; i++) {
			$(indicators[i]).removeClass("current");
		}
		$(indicators[e.index]).addClass("current");
	});
	instance.show(0);
};
/**
 * 处理内容中图片的请求路径
 */
mb.vi.newsView.prototype._formatBodyImgSrc = function() {
	var _self = this;
	var imgList = _self.$content.find('img');
	$.each(imgList, function(i, obj) {
		var originalSrc = $(obj).attr('src');
		var newSrc = ZhbxImgPath + originalSrc;
		$(obj).attr('src', newSrc);
	})
};
