var APP_ITEM_HEIGHT = 28;
var MIN_PNAEL_HEIGHT = 11 * APP_ITEM_HEIGHT;
var SCROLL_HEIGHT = 4 * APP_ITEM_HEIGHT;
var SCREEN_MAX_APP_NUM = 32;

//-- 模块对应ID固定 --
var moduleInfo = {"email":1,"notify":4,"news":147,"vote":119,"workflow":5,"calendar":8,"diary":81,"attendance":7,"bbs":62};

//rh增加=============================begin============================================
//-- 一级菜单 --
//var topMenuModel = [{"_PK_":"m0001","ICON":"info","NAME":"平台","PID":"","ID":"m0001","LEAF":"","TYPE":"3"}];//供portal图标调用
var topMenuModel = [{"_PK_":"m0001","ICON":"info","NAME":
	Language.transStatic("portal_index_desk_string1"),"PID":"","ID":"m0001","LEAF":"","TYPE":"3"}];//供portal图标调用
var first_array = topMenuModel;

//rh增加=============================end============================================
//-- 可用菜单图标 --
var pWindow = window.parent;
var fmenu = first_array;

var default_icon = 'default';
var s_default_icon = 'oa';
var rowAppNum = 8;

window.onactive = function(){
	alert(11);
   jQuery(window).triggerHandler('resize');
   window.onactive = null;
};
//显示消息 @para msg 要显示的提示文字
function portalMessage(msg){
    if(!msg) return;
    msgObj = jQuery("#portalSettingMsg");
    msgObj.html(msg).show();
    setTimeout(function(){msgObj.empty().hide()},5000);
}
//构造自定义图片的对象
function buildSelfImg(imgUrl) {
	var img = jQuery("<img class='rhDesk-theme-self rhDesk-theme-img'></img>").attr("src","/file/" + imgUrl).attr("selfImg",imgUrl);
	img.bind("click",function() {
		var url = "url(/file/" + imgUrl + ")";
		setDeskBackImg(url);
		var selfAll = [];
		jQuery.each(jQuery(".rhDesk-theme-self"),function(i,n) {
			selfAll.push(jQuery(n).attr("selfImg"));
		});
        var opts = {"SD_SELF_SELECT":imgUrl,"SD_SELF_DEF":selfAll.join(","),"SD_BACK_IMG":""};
	    if(rhExcuteDeskSet(opts) == true) {
	    	portalMessage(td_lang.rh.msg_101);
	    	jQuery(".rhDesk-theme-systemSelect").removeClass("rhDesk-theme-systemSelect");
	    	jQuery(this).addClass("rhDesk-theme-systemSelect");
	    };
	});	
	return img;
}
//获取上传返回的主键信息
function readImgPK() {
//	jQuery("#rh-self-form").prepend(jQuery("<span>上传中...</span>"));
	jQuery("#rh-self-form").prepend(jQuery("<span>"+Language.transStatic("portal_index_desk_string2")+"</span>"));
	var si = setInterval(function(){
		var doc = document.getElementById("imgIframe").contentDocument;
		var con = doc.body.innerHTML;
		if (con.indexOf("PK") > 0) {
			clearInterval(si);
			var obj = StrToJson(con);
			var pk = obj._DATA_[0]._PK_;
			jQuery("#rh-self-form").empty();
			jQuery("#rh-self-con").prepend(buildSelfImg(pk));
			var temp = jQuery("<iframe id='imgIframe' src='imgSubmit.jsp' border=0 frameborder=0 height='50' width='400'></iframe>");
			jQuery("#rh-self-form").append(temp);
			var selfAll = [];
			jQuery.each(jQuery(".rhDesk-theme-self"),function(i,n) {
				if (i < 4) {
					selfAll.push(jQuery(n).attr("selfImg"));
				} else {
					jQuery(n).remove();
				}
			});
			var opts = {"SD_SELF_DEF":selfAll.join(",")};
		    if(rhExcuteDeskSet(opts) == true) {
	    		portalMessage(td_lang.rh.msg_106);		    	
		    }
		}
	},200);
}
//lp 设置界面Html 结构
var appboxHtml = '';
appboxHtml =    '<div id="portalSetting">';
appboxHtml +=       '<div id="bar" class="ui-layout-north">'; 
//appboxHtml +=         '<span id="btnAppSet" class="rhDesk-btn">应用设置</span>';
//appboxHtml +=         '<span id="btnScreenSet" class="rhDesk-btn">分屏设置</span>';
//appboxHtml +=         '<span id="rhDesk-deskSet" class="rhDesk-btn">桌面设置</span>';
//appboxHtml +=         '<span id="rhDesk-systemBack" class="rhDesk-btn">系统还原</span>';
//appboxHtml +=         '<span id="rhDesk-msgSet" class="rhDesk-btn">通知设置</span>';
//appboxHtml +=         '<span id="rhDesk-themeSet" class="rhDesk-btn">主题设置</span>';

appboxHtml +=         '<span id="btnAppSet" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string3")+'</span>';
appboxHtml +=         '<span id="btnScreenSet" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string4")+'</span>';
appboxHtml +=         '<span id="rhDesk-deskSet" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string5")+'</span>';
appboxHtml +=         '<span id="rhDesk-systemBack" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string6")+'</span>';
appboxHtml +=         '<span id="rhDesk-msgSet" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string7")+'</span>';
appboxHtml +=         '<span id="rhDesk-themeSet" class="rhDesk-btn">'+Language.transStatic("portal_index_desk_string8")+'</span>';

appboxHtml +=         '<span id="portalSettingMsg"></span>';        
appboxHtml +=      '</div>';
appboxHtml +=      '<div id="appPageAll" class="ui-layout-center">';
appboxHtml +=         '<div id="appPageDom" class="rh-deskDom appPage">';
appboxHtml +=            '<div id="app_cate_list" class="ui-layout-west" style="display:none;">';
appboxHtml +=               '<div class="scroll-up"></div>';
appboxHtml +=               '<ul>';
appboxHtml +=                 '<div class="clearfix"></div>'; 
appboxHtml +=               '</ul>';
appboxHtml +=               '<div class="scroll-down"></div>';    
appboxHtml +=            '</div>';
appboxHtml +=            '<div id="app_list_box" class="ui-layout-center">';
appboxHtml +=               '<div id="app_list_record"></div>';
appboxHtml +=               '<ul></ul>';
appboxHtml +=               '<div class="clearfix"></div>';   
appboxHtml +=            '</div>';
appboxHtml +=         '</div>';
appboxHtml +=         '<div id="screenPageDom" class="rh-deskDom">';
appboxHtml +=            '<div id="screen_list">';
appboxHtml +=               '<div class="clearfix"></div>';   
appboxHtml +=               '<ul></ul>';
appboxHtml +=            '</div>';
appboxHtml +=         '</div>';

appboxHtml +=         '<div id="rh-deskSetDom" class="rh-deskDom">';
//appboxHtml +=               '<h3 class="deskTitle">默认桌面(登录后默认显示)</h3>';
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string9")+'</h3>';
appboxHtml +=               '<div id="rh-deskSetDom-desk" class="deskContent"></div>';   
//appboxHtml +=               '<h3 class="deskTitle">默认应用(登录后默认显示)</h3>';
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string10")+'</h3>';
appboxHtml +=               '<div id="rh-deskSetDom-app" class="deskContent"><ul id="rh-deskSetDom-app-ul"></ul></div>';
appboxHtml +=               '<div class="clearfix"></div>';   
appboxHtml +=         '</div>';

appboxHtml +=         '<div id="rh-systemBackDom" class="rh-deskDom">';
//appboxHtml +=               '<h3 class="deskTitle">系统程序</h3>';
//appboxHtml +=               '<div class="deskContent"><input id="desk-initApp" type="checkbox" class="rh-checkbox"/>初始化系统桌面程序(还原为初始默认值)</div>';   
//appboxHtml +=               '<h3 class="deskTitle">主题布局</h3>';
//appboxHtml +=               '<div class="deskContent"><p><input id="desk-initTheme" type="checkbox" class="rh-checkbox"/>还原系统默认主题</p><p><input id="desk-initDesk" type="checkbox" class="rh-checkbox"/>还原系统默认桌面设置</p>';
//appboxHtml +=               '<p style="padding:30px 0px;"><input id="desk-okBtn" type=button value=" 确 定 "/></p>'; 
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string11")+'</h3>';
appboxHtml +=               '<div class="deskContent"><input id="desk-initApp" type="checkbox" class="rh-checkbox"/>'+Language.transStatic("portal_index_desk_string12")+'</div>';   
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string13")+'</h3>';
appboxHtml +=               '<div class="deskContent"><p><input id="desk-initTheme" type="checkbox" class="rh-checkbox"/>'+Language.transStatic("portal_index_desk_string14")+'</p><p><input id="desk-initDesk" type="checkbox" class="rh-checkbox"/>'+Language.transStatic(portal_index_desk_string15)+'</p>';
appboxHtml +=               '<p style="padding:30px 0px;"><input id="desk-okBtn" type=button value="'+Language.transStatic("portal_index_desk_string16")+'"/></p>'; 
appboxHtml +=               '</div>';  
appboxHtml +=         '</div>';

appboxHtml +=         '<div id="rh-msgSetDom" class="rh-deskDom">';
//appboxHtml +=               '<h3 class="deskTitle">消息</h3>';
//appboxHtml +=               '<div class="deskContent"><input id="desk-msgFlag" type="checkbox" class="rh-checkbox"/>是否接受来自应用的消息提醒</div>';   
//appboxHtml +=               '<h3 class="deskTitle">声音</h3>';
//appboxHtml +=               '<div class="deskContent"><input id="desk-ringFlag" type="checkbox" class="rh-checkbox"/>是否接受来自应用的消息提醒</div>';  
//appboxHtml +=               '<p style="padding:30px 0px;text-align:left;"><input id="desk-msgOKBtn" type=button value=" 确 定 "/></p>'; 
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string17")+'</h3>';
appboxHtml +=               '<div class="deskContent"><input id="desk-msgFlag" type="checkbox" class="rh-checkbox"/>'+portal_index_desk_string18+'</div>';   
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string19")+'</h3>';
appboxHtml +=               '<div class="deskContent"><input id="desk-ringFlag" type="checkbox" class="rh-checkbox"/>'+portal_index_desk_string18+'</div>';  
appboxHtml +=               '<p style="padding:30px 0px;text-align:left;"><input id="desk-msgOKBtn" type=button value="'+Language.transStatic("portal_index_desk_string16")+'"/></p>'; 
appboxHtml +=         '</div>';

appboxHtml +=         '<div id="rh-themeSetDom" class="rh-deskDom" style="overflow-y:scroll;height:330px;">';
//appboxHtml +=               '<h3 class="deskTitle">系统主题</h3>';
appboxHtml +=               '<h3 class="deskTitle">'+Language.transStatic("portal_index_desk_string20")+'</h3>';
appboxHtml +=               '<div id="rh-theme-system" class="deskContent rhDesk-theme-div"></div>';   
//appboxHtml +=               '<h3 class="deskTitle" style="display:block;">自定义</h3><input id="pImgBtn" style="display:none;" type="button" value="reset" onClick="readImgPK()"></input>';
appboxHtml +=               '<h3 class="deskTitle" style="display:block;">'+Language.transStatic("portal_index_desk_string21")+'</h3><input id="pImgBtn" style="display:none;" type="button" value="reset" onClick="readImgPK()"></input>';
appboxHtml +=         		'<div id="rh-self-form" style="width:100%;height:40px"><iframe id="imgIframe" src="imgSubmit.jsp" border=0 frameborder=0 height="40" width="300" scrolling=no></iframe></div>';
appboxHtml +=               '<div id="rh-self-con" class="deskContent"></div>';
appboxHtml +=         '</div>';

appboxHtml +=      '</div>';
appboxHtml +=   '</div>';

//过滤重复js元素 return array;
function unique(d){
   var o = {};
   jQuery.each(d, function(i, e) { 
      o[e] = i; 
   }); 
   var a = []; 
   jQuery.each(o, function(i, e) { 
      a.push(d[e]); 
   });
   return a;
}
//过滤重复js元素 返回 boolean
Array.prototype.S=String.fromCharCode(2);
Array.prototype.in_array=function(e)
{
    var r=new RegExp(this.S+e+this.S);
    return (r.test(this.S+this.join(this.S)+this.S));
}

function isTouchDevice(){
	alert(22);
    try{
        document.createEvent("TouchEvent");
        return true;
    }catch(e){
        return false;
    }
}

//添加桌面应用 e {"func_id": ,"id": ,"name":} index 为要添加应用的屏幕索引
function addApp(e, index) { 
   var s = slideBox.getScreen(index); 
   if (s) { 
      var ul = s.find("ul"); 
      if (!ul.length) { 
         ul = jQuery("<ul></ul>");
         s.append(ul); 
          ul.sortable({
            revert: true,
            //delay: 200,
            distance: 10,               //延迟拖拽事件(鼠标移动十像素),便于操作性
            tolerance: 'pointer',       //通过鼠标的位置计算拖动的位置*重要属性*
            connectWith: ".screen ul",
            scroll: false,
            stop: function(e, ui) {
              setTimeout(function() {
                    jQuery(".block.remove").remove();
                    jQuery("#trash").hide();
                    ui.item.click(portalOpenTab);
                    serializeSlide();
              }, 0);
            },
            start: function(e, ui) {
               jQuery("#trash").show();
               ui.item.unbind("click");
            }
         });
      } 
      addModule(e, s.find("ul")); 
   } 
}


function getAppMargin(){
      var clientSize = jQuery(document.body).outerWidth(true);
      var appsize = 120 * rowAppNum;
      if(clientSize > appsize){
         var _margin = Math.floor((clientSize - appsize - 70*2)/16);     
      }else{
         var _margin = 0;    
      }
      return _margin; 
}
   
function refixAppPos(){
      var _margin = getAppMargin() + "px";
      jQuery("#container .screen li.block").css({"margin-left": _margin, "margin-right":_margin})   
}
   
function addModule(e, el) {
   el = jQuery(el);
   var countText = 0;
   var _id = e.ICON;
   fixid = fixAppImage(_id);
   var li = jQuery("<li class=\"block\"></li>");
   var img = jQuery("<div class='img'><p><img src='css/images/app_rh-icons/" + fixid + ".png' /></p></div>");
   var divT = jQuery("<div class=\"count\"></div>");
   //var countA = jQuery("<a class='countRight'>aa</a>").appendTo();
   li.attr("id", e.ID);
   li.attr("title", e.NAME);
   //li.attr("index", e.func_id);
   li.attr("type", e.TYPE);
   li.attr("info", e.LEAF);
   li.attr("icon", e.ICON);
   li.attr("menu", e.MENU);
   li.attr("menuid", e.MENUID);
   li.attr("countserv", e.COUNTSERV);
   li.attr("extWhere", "");
   
   var _margin = getAppMargin() + "px";
   li.css({"margin-left": _margin, "margin-right":_margin});
   divT.attr("id", "count_" + e.ID);
   if(rhTempAlertIconsJson[e.ID]) {//如果当前图标启用消息提醒
	   
	   var count = jQuery("#count_"+e.ID).find(".countBack-text");
	   if( count.length>0){
		   countText =  count.text();
	   }
      divT.addClass("countBack"); 
      divT.attr("liid",e.ID);
	  var countA = jQuery("<a></a>").addClass("countBack-a");
	  jQuery("<span></span>").addClass("countBack-text").text(countText).appendTo(countA);
      divT.append(countA);
     
   }
   var a = jQuery("<a class=\"icon-text\" href=\"javascript: void(0)\"></a>"); 
   var span = jQuery("<span></span>").text(e.NAME); 
   li.append(img.append(divT)).append(a.append(span)); 
   el.append(li);
   if(countText<1){
 	  jQuery("#count_"+e.ID).hide();
   } else if(countText>=1){
	   jQuery("#count_"+e.ID).show();
	   if(e.LEAF!="SY_COMM_TODO"){
		   li.attr("info", "SY_COMM_TODO");
		   li.attr("menuid", System.getUser("CMPY_CODE")+"SY_COMM_TODO");
		   li.attr("extWhere", "and  TODO_CODE = '"+e.COUNTSERV+"'");
	   }
   }
}

function delModule(el){
   var pObj = jQuery("#container .screen ul li.block");
   pObj.each(function(){
      var index = jQuery(this).attr("id");
      if(el == index){
         jQuery(this).remove();
         var flag = serializeSlide();
         if (flag == true) {//删除保存成功
         	leftMenuModel[el] = leafMenuModel[el];
         }
      }
   });
}

//lp 检查应用图片是否存在
function fixAppImage(e){
//   var els = availappicon;   
//   if(jQuery.inArray(e,els) == -1){
//      return default_icon;
//   }else{
      return e;   
//   }            
}

//lp 获取当前屏幕应用的个数
function getAppNums(index){
   var index = (index == "" || typeof(index) == "undefined") ? slideBox.getCursor() : index;  
   var num =  jQuery("#container .screen:eq("+index+") ul li.block").size();
   return num;          
}

function initModules(modules, el) {
   window.slideBox = jQuery("#container").slideBox({
      count: (modules.length == 0) ? 1:modules.length,
      cancel: isTouchDevice() ? "" : ".block", 
      obstacle: "200",
      speed: "slow",
      //active: 1,
      touchDevice: isTouchDevice(),
      control: "#control .control-c",
      listeners: {
          afterScroll: function(i) {
          },
          beforeScroll: function(i) {
             jQuery(".background").stop().animate({
                //left: - i * 70   
             }, "normal");
          }
       }
   });
   el = jQuery(el);
   var count = 0;
   jQuery.each(modules || [] , function(i, e) {
      var ul = jQuery("<ul></ul>");
      slideBox.getScreen(i).append(ul);
      slideBox.getScreen(i).attr("id",e.id);
      jQuery.each(e.items || [], function(i, e) {
         addModule(e, ul);
      });
      i++;
   });

}

jQuery.noConflict();
//图标点击事件
function portalOpenTab() {
    var type = jQuery(this).attr("type");
    var name = jQuery(this).attr("title");
    var info = jQuery(this).attr("info");
    var menu = jQuery(this).attr("menu");
    var menuId = jQuery(this).attr("menuid");
    var extWhere =  jQuery(this).attr("extWhere");
    if (info) {
	    if (type == 1) { //服务
		    var opts = {"sId":info,"sName":name,"extWhere":extWhere};
	        if (menu) {
	        	opts = {"sId":info,"sName":name,"menuFlag":menu,"extWhere":extWhere};
	        }
	   	    openNewLinkListPage(opts);   	    	
	    } else if (type == 2) {//链接
	        var sId = info.substring(0,info.indexOf("."));
	        if (menu) {//是否显示左侧菜单1、显示当前层级 2、全部显示 3、不显示
	          var options = {"sId":sId,"sName":name,"url":info,"menuFlag":menu,"menuId":menuId};
			  openNewLinkPage(options);
	        } else {
	          var opts = {"sId":sId,"sName":name,"url":info,"menuId":menuId};
			  openNewLinkListPage(opts);
	        }
	    }
    	
    }
}
(function($){
   function resizeContainer()
   {
      var wWidth = Math.floor(parseInt((window.innerWidth || (window.document.documentElement.clientWidth || window.document.body.clientWidth))*0.9));
      var blockWidth = $('#container > .block:first').outerWidth();
      if(blockWidth <= 0)
         return;
      
      var count = Math.min(4, Math.max(3, Math.floor(wWidth/blockWidth)));
      $('#container').width(blockWidth*count);
   }

   function initBlock()
   {
      $('#container .screen ul li.block').live("click",portalOpenTab);
   }
   
   function initDialog()
   {
      $('div.dialogContainer', document.body).live('_show', function(){
         var wWidth = (window.innerWidth || (window.document.documentElement.clientWidth || window.document.body.clientWidth));
         var hHeight = (window.innerHeight || (window.document.documentElement.clientHeight || window.document.body.clientHeight));
              
         var left = 100;
         var top = 20;
         var maxWidth = wWidth-200;
         var maxHeight = hHeight - 100;
         var minHeight = 200;
         
         if(wWidth - $(this).outerWidth() > 200 )
         {
            left = Math.floor((wWidth - $(this).outerWidth())/2);
         }
         else
         {
            $("div.msg-content", this).width(maxWidth-18);
         }

         if($(this).outerHeight() < minHeight)
         {
            $("div.msg-content", this).height(minHeight);
         }
         else if(hHeight - $(this).outerHeight() > 100 )
         {
            top = Math.floor((hHeight - $(this).outerHeight())/2);
         }
         else
         {
            $("div.msg-content", this).height(maxHeight-88);
         }
         
         var top = 0;
         var bst = document.body.scrollTop || document.documentElement.scrollTop;
         top = Math.round((hHeight - $(this).height())/2 + bst) + "px";

         $(this).css({left:left});
         $(this).css({top:top});
         $('#overlay').height(hHeight);

      });
      
      //对话框关闭按钮
      var dialogClose = $('a.close', $('div.dialogContainer'));
      dialogClose.live('click', function(){
         var dialog = $('div.dialogContainer:visible', document.body).first();
         dialog.trigger('_hide');
         refixminScreenbtn();
         $('#overlay').hide();
         $("body").focus();
         dialog.hide();
      });
   }
   
   

   
   function GetCounts(moduleIdStr)
   {
      $.ajax({
         type: 'GET',
         url: 'count.php',
         data: {'OUTPUT':'1', 'MODULE_ID_STR': moduleIdStr},
         success: function(data){
            var array = pWindow.Text2Object(data);
            if(typeof(array) == "object")
            {
               var counts = 0;
               for(var id in array)
               {
                  var count = Math.min(10, eval('array.' + id));
                  var className = count > 0 ? ('count count' + count) : 'count';
                  if(moduleInfo[id]){
                     $('#count_' + moduleInfo[id]).attr('class', className);   
                  }
                  counts += count;
               }
               
               if(counts > 0)
                  parent.BlinkTabs('p0');
            }
            
            window.setTimeout(GetCounts, monInterval*60*1000, moduleIdStr);
         },
         error: function(request, textStatus, errorThrown){
            window.setTimeout(GetCounts, monInterval*60*1000, moduleIdStr);
         }
      });
   }
   
         
   function CreateDialog(id, title, parent)
   {
      var html = '<div id="dialog_' + id + '" index="' + id + '" class="dialogContainer">';
      html += '<table class="dialog" align="center">';
      html += '   <tr class="head">';
      html += '      <td class="left"></td>';
      html += '      <td class="center">';
      html += '         <div class="title">' + title + '</div>';
      html += '         <a class="close" href="javascript:;"></a>';
      html += '      </td>';
      html += '      <td class="right"></td>';
      html += '   </tr>';
      html += '   <tr class="body">';
      html += '      <td class="left"></td>';
      html += '      <td class="center">';
      html += '         <div id="dialog_content_' + id + '" class="msg-content"></div>';
      html += '      </td>';
      html += '      <td class="right"></td>';
      html += '   </tr>';
      html += '   <tr class="foot">';
      html += '      <td class="left"></td>';
      html += '      <td class="center"></td>';
      html += '      <td class="right"></td>';
      html += '   </tr>';
      html += '</table>';
      html += '</div>';
      $(parent).append(html);
      $("#dialog_"+id).draggable({handle: 'tr.head',containment: 'window' , scroll: false});
   }
   
   function initTrash() {
         $("#trash").droppable({
            over: function() {
               $("#trash").addClass("hover");
            },
            out: function() {
               $("#trash").removeClass("hover");
            },
            drop: function(event, ui) {
               ui.draggable.addClass("remove").hide();
               delModule && delModule(ui.draggable.attr("id"));
               $(".ui-sortable-placeholder").animate({
                  width: "0"
               }, "normal", function() {
               });
               $("#trash").removeClass("hover");
            }
         });   
   }
   
   //lp 扩展对话框
   $.extend({
      tExtDialog: function (options) {
         var defaults = {
            width: 600,
            height: 400,
            parent: $("body"),
            title: ''
         };
         
         var options = $.extend(true, defaults, options);
                 
         var width = options.width;
         var height = options.height;
         var id = options.id;
         var title = options.title;
         var parent = options.parent;
         var src = options.src;
         var icon = options.icon;
         var content = options.content;
        
         if(!$('#dialog_' + id).length)
         {         
            CreateDialog(id, title, parent);
            $('#dialog_' + id).draggable("destroy");
            $('#dialog_' + id).addClass('extDialog');
            $('#dialog_' + id + ' .dialog tr.head').css("cursor","");
            $('#dialog_' + id).css({"width" : width +"px","height" : height +"px"});
            $('#dialog_' + id + ' > .dialog').css({"width":"100%"});
            $("div.msg-content", $('#dialog_' + id)).css({"height":(height - 48) + "px"})
            if(icon){
               $('#dialog_' + id + ' .dialog .head .center .title').prepend("<img src = '"+icon+"' style='margin-right:5px' width='16' height='16' />");
            }
            if(src){
               $("#dialog_content_"+id).html("<iframe name='iframe' src='" + src +"' width='100%' height='100%' border='0' frameborder='0' marginwidth='0' marginheight='0'></iframe>");
            }else{
               $("#dialog_content_"+id).html(content);   
            }
         }
         
         function display()
         {
            var wWidth = (window.innerWidth || (window.document.documentElement.clientWidth || window.document.body.clientWidth));
            var hHeight = (window.innerHeight || (window.document.documentElement.clientHeight || window.document.body.clientHeight));
            
            var top = left = 0;
            var bst = document.body.scrollTop || document.documentElement.scrollTop;
            top = Math.round((hHeight - height)/2 + bst) + "px";
            mleft = "-" + Math.round(width/2) + "px";
            top = top < 0 ? top = 0 : top;

            $('#dialog_' + id).css({"top":top,"left":"50%","margin-left":mleft});
            $('#dialog_' + id).show();
            $('#overlay').height(window.document.documentElement.scrollHeight);
            $('#overlay').show();
            if (jQuery(".rhDesk-btnActive").length == 0) {//设置选中tab样式，第一次打开时执行
	            jQuery("#btnAppSet").addClass("rhDesk-btnActive");
            }
         }
         return{
            display: display   
         }
      }
   });
   
   //lp 构造一级菜单html结构 return str;
   function returnFmenu()
   {
      var html = "";
      for(var i=0; i< fmenu.length; i++)
      {
         var menu = fmenu[i];
         var menuId = menu.ID;
         var menuName = menu.NAME;
         var image = !menu.ICON ? s_default_icon : menu.ICON;
         html += '<li><a id="' + menuId + '" href="javascript:;" hidefocus="hidefocus" title="'+menuName+'"><span class="icon-' + image + '" style="padding:3px 10px 2px 15px;"></span> ' + menuName + '</a></li>';
      }
     // html += '<li><a id="extWebApp" href="javascript:;" hidefocus="hidefocus" title='+td_lang.inc.msg_74+'><img width="20" height="20" align="absMiddle" class="icon-appbox"/> '+td_lang.inc.msg_74+'</a></li>';//"互联网应用"
      return html;
   }
        
   //生成顶级菜单下属的所有服务功能 @fappid 一级菜单ID return array;
   function returnSTmenu(fappid){//rh-ljk
   	  var topMenu = leftMenuModel;

   	  var curList = [];
      jQuery.each(topMenu,function(i,n) {
      	if (n) {//过滤掉内容为undefined的对象
	      	//if (n.TOPID == fappid) {
	      		curList.push(n);
	      	//}
      	}
      });
      return curList;
   }
   //构造一级菜单下所有除桌面已有菜单的图标 return str;
   function appBuilding(appids){
      var html = menu_id = '';
      var _len = appids.length;
      for(var i=0; i< _len; i++)
      {
         var menuId = appids[i].ID;
         var menuName = appids[i].NAME;
         var menuIcon = appids[i].ICON;
         var menuInfo = appids[i].LEAF;
         var menuType = appids[i].TYPE;
         var countserv = appids[i].COUNTSERV;
         var menuid = appids[i].MENUID;
         var menu = appids[i].MENU;
         if(menuId.indexOf('ewp')!="-1"){
         }else{
            var image = !(menuIcon.length > 0) ? 'default' : menuIcon;
            html += '<li><a id="' + menu_id + '" appid ="'+ menuId +'" apptitle="'+menuName+'" appicon="'
            +image+'" appinfo ="'+menuInfo+'" apptype="'+ menuType 
            + '" appmenu="' + menu + '" appcountserv="' + countserv + '" appmenuid="' + menuid + '" href="javascript:;" hidefocus="hidefocus" title="'
            + menuName +'"><img width="48" height="48" src="css/images/app_rh-icons/' + image + '.png" align="absMiddle" /><span class="lleft"><span class="lright">' + menuName + '</span></span></a></li>';   
         }
      }
      return html;
   }
   //构造桌面设置->桌面已存在图标
   function appBuildingDeskHave(){
      var html = menu_id = '';
      var _len = deskMenuModel.length;
      jQuery.each(deskMenuModel,function(i,n) {
        var menuId = n.ID;
        var menuName = n.NAME;
        var menuIcon = n.ICON;
        var menuInfo = n.LEAF;
        var menuType = n.TYPE;

        var image = !(menuIcon.length > 0) ? 'default' : menuIcon;
        html += '<li appid ="'+ menuId +'"><a id="' + menu_id + '" appid ="'+ menuId +'" apptitle="'+menuName+'" appicon="'
        +image+'" appinfo ="'+menuInfo+'" apptype="'+ menuType + '" href="javascript:;" hidefocus="hidefocus" title="'
        + menuName +'"><img width="48" height="48" src="css/images/app_rh-icons/' + image + '.png" align="absMiddle" /><span class="lleft"><span class="lright">' + menuName + '</span></span></a></li>';    	
      });

      return html;
   }
        
   //构造屏幕设置html结构 return str;
   function returnScreen(){
      var html = '';
      var _len = slideBox.getCount();
      for(var i=0; i< _len; i++)
      {
         html += '<li class="minscreenceil" index='+i+'>' + (i+1) +'</li>';
      }
      return html;
   }
   
   //选中桌面已有的app，@para srceenid 屏幕自然索引    
   function getScreenAppIds(srceenid){
      var idstr = sep = '';
      if(srceenid){
         obj = $("#container .screen").eq(srceenid).find("li.block")
      }else{
         obj = $("#container .screen li.block");
      }
      obj.each(function(){
         var appid = $(this).attr("index");
         idstr += sep + appid;
         sep = ',';
      });
      return idstr;
   }
        

   
   //修正点击按钮出现屏幕小按钮width为0的现象
   function refixminScreenbtn(){
      $('#control').width(window.document.documentElement.clientWidth);   
   }
   
   //refixDialogPos
   function refixDialogPos(){
      var dialog = $('div.extDialog:visible', document.body).first();
      height = dialog.height();
      width = dialog.width();
      var wWidth = (window.innerWidth || (window.document.documentElement.clientWidth || window.document.body.clientWidth));
      var hHeight = (window.innerHeight || (window.document.documentElement.clientHeight || window.document.body.clientHeight));
      var top = left = 0;
      var bst = document.body.scrollTop || document.documentElement.scrollTop;
      top = Math.round((hHeight - height)/2 + bst) + "px";
      mleft = "-" + Math.round(width/2) + "px";
      top = top < 0 ? top = 0 : top;
      dialog.css({"top":top,"left":"50%","margin-left":mleft});
   }
   
   $(window).resize(function(){
      
      refixAppPos();
            
      $('#overlay').height(window.document.documentElement.scrollHeight);
      
      refixminScreenbtn();
      
      refixDialogPos();
      
   });
   
   //菜单滚动箭头事件,id为app_cate_list
   function initAppScroll(id)
   {
      //菜单向上滚动箭头事件
      $('#' + id + ' > .scroll-up:first').hover(
         function(){$(this).addClass('scroll-up-hover');},
         function(){$(this).removeClass('scroll-up-hover');}
      );

      //点击向上箭头
      $('#' + id + ' > .scroll-up:first').click(
         function(){
            var ul = $('#' + id + ' > ul:first');
            ul.animate({'scrollTop':(ul.scrollTop()-SCROLL_HEIGHT)}, 600);
         }
      );

      //向下滚动箭头事件
      $('#' + id + ' > .scroll-down:first').hover(
         function(){$(this).addClass('scroll-down-hover');},
         function(){$(this).removeClass('scroll-down-hover');}
      );

      //点击向下箭头
      $('#' + id + ' > .scroll-down:first').click(
         function(){
            var ul = $('#' + id + ' > ul:first');
            ul.animate({'scrollTop':(ul.scrollTop()+SCROLL_HEIGHT)}, 600);
         }
      );
   }
   
   function initAppListScroll(){
      var su = $("#app_cate_list .scroll-up:first");
      var sd = $("#app_cate_list .scroll-down:first");
      var scrollHeight = $("#app_cate_list ul").attr('scrollHeight');
      var orgheight = $("#app_cate_list ul").height();
      if(orgheight < scrollHeight)
      {
         var height = scrollHeight > MIN_PNAEL_HEIGHT ? MIN_PNAEL_HEIGHT : scrollHeight;
         $("#app_cate_list ul").height(height);
      }
      
      if(orgheight >= scrollHeight)
      {
         su.hide();
         sd.hide();
      }
      initAppScroll('app_cate_list'); 
   }
   
   function reSortMinScreen(){
      $("#screenPageDom #screen_list ul li.minscreenceil").each(function(i){
         $(this).text(i+1);
         $(this).attr("index",i);      
      });      
   }
   
   $(document).ready(function($){
      
      $("body").focus();
      
      $('#overlay').height(window.document.documentElement.scrollHeight);
      
      //初始化显示列数
      //resizeContainer();
      
      //初始化图标
      initModules(modules);
      
     //初始化图标间距
      refixAppPos();
      
      //模块点击事件
      initBlock();
      
      //对话框事件
      initDialog();
      //获取代办数量
      //getToDoCounts("1w7yyRPAVefECYhYVgcSeO");
      
      //GetCounts(moduleIdStr);
      
      initTrash();
      
      //初始化屏幕
      $(".screen ul").sortable({
            revert: true,
            //delay: 200,
            //distance: 10,               //延迟拖拽事件(鼠标移动十像素),便于操作性
            tolerance: 'pointer',       //通过鼠标的位置计算拖动的位置*重要属性*
            connectWith: ".screen ul",
            scroll: false,
            stop: function(e, ui) {
               if (jQuery("#normalUse ul li").length > 0) {//码头不为空的时候
               	 jQuery("#normalUse ul").droppable({"accept":".normal-li"});
               }
               setTimeout(function() {
                    $(".block.remove").remove();
                    $("#trash").hide();
                    ui.item.click(portalOpenTab);
                    serializeSlide();
               }, 0);
            },
            start: function(e, ui) {
               if (jQuery("#normalUse ul li").length == 0) {//码头为空的时候
               	 jQuery("#normalUse ul").droppable({"accept":".block"});
               }
               $("#trash").show();
               refixminScreenbtn();
               ui.item.unbind("click");
            }
      });
      
    // 打开搜索框
	$("#openSearch").click(function(e){
//		var options = {"sId":"SY_PLUG_SEARCH","sName":"智能搜索","url":"SY_PLUG_SEARCH.show.do"};
		var options = {"sId":"SY_PLUG_SEARCH","sName":Language.transStatic("rhPageView_string8"),"url":"SY_PLUG_SEARCH.show.do"};
		openNewLinkPage(options);
	/*	var _self = this;
		
      	// 构造Dialog
		var searchFrame = jQuery("<div id='searchDialog' class='searchDialog'></div>");
		
		// 构造搜索框
		var searchText = jQuery("<input type='text' id='keyword' />");

		var searchBtn = jQuery("<input id='search' class='searchBtn' type='button' value='搜  索' />").click(function(){
			searchFrame.remove();
			//alert("搜索关键字：" + searchText.val());
      openNewLinkListPage("SEARCH", "搜索", "SEARCH.query.do");
		});
					
		var cancelBtn = jQuery("<input id='cancel' class='searchBtn' type='button' value='取  消' />").click(function(){
			searchFrame.remove();
		});
		
		searchFrame.append(searchText).append(searchBtn).append(cancelBtn).appendTo("body");

		var height = 50;
    	var width = 350;
		searchFrame.dialog({
			height: height,
			width: width,
			modal: true,
			resizable: false,
			draggable: false,
			hide: "",
			show: "",
			position: 'center'
		});
		searchFrame.dialog("open");*/
    });
      //lp 绑定“界面设置”事件   
      var d = '';
      $("#openAppBox").click(function(){
         
         refixminScreenbtn();
          
         if(!d){
            d = new $.tExtDialog({
               height: 420,
               width: 800,
               id: "appbox",
               title: td_lang.inc.msg_75,//"应用盒子"
               content: appboxHtml
            });
            d.display();
            
         }else{
            
            $('#overlay').css("display","block");
            
            d.display();
            
            $("#screenPageDom #screen_list ul li.minscreenceil").each(function(i){
               $(this).html(i+1);
            });
            
            //重新加载点击分类
            if($("#app_cate_list ul li a.current").length > 0){
               $("#app_cate_list ul li a.current").trigger("click");
            }
            
            //如果已经创建过那么就显示且退出
            return;   
         }
         
         //重新加载点击分类
         if($("#app_cate_list ul li a.current").length > 0){
            $("#app_cate_list ul li a.current").trigger("click");
         }
         
         //lp 绑定应用设置和屏幕设置的操作
         $("#btnAppSet").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive ");
         	jQuery(this).addClass("rhDesk-btnActive ");
            var _display = $("#appPageDom").css("display");
            if(_display == "none"){
               $(".rh-deskDom").hide();
               $("#appPageDom").show();
               
               //重新加载点击分类
               $("#app_cate_list ul li a").eq(0).trigger("click");
            }
         });
         //屏幕设置
         $("#btnScreenSet").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive");
         	jQuery(this).addClass("rhDesk-btnActive");
            var _display = $("#screenPageDom").css("display");
            if(_display == "none"){
                $(".rh-deskDom").hide();
               $("#screenPageDom").show();
            }
         });
         //桌面设置
         jQuery("#rhDesk-deskSet").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive");
         	jQuery(this).addClass("rhDesk-btnActive");
            var _display = $("#rh-systemBackDom").css("display");

            jQuery(".rh-deskDom").hide();
            jQuery("#rh-deskSetDom").show();
            if (jQuery(".rh-deskSetDom-show").length > 0) {
            	return false;
            }
            //默认屏幕
            jQuery.each(jQuery("#container .screen"),function(i,n) {
		         var span = jQuery("<span style='margin:0px 10px;'></span>");
		         var radio = jQuery("<input type='radio' class='rh-radiobox' name='desk'></input>").appendTo(span);
		         var count = i + 1;
//		         var text = jQuery("<label></label>").text("第" + count + "屏桌面").appendTo(span);
		         var text = jQuery("<label></label>").text(Language.transArr("portal_index_desk_L1",[count])).appendTo(span);
		         radio.bind("click",function() {
		         	var opts = {"SD_DESK_DEFAULT":i};
		         	if(rhExcuteDeskSet(opts) == true) {
            	    	portalMessage(td_lang.rh.msg_102);
            	    };
		         });
		         if (deskScreen == i) {//设置屏幕选中
		         	radio.attr("checked",true);
		         }
		         jQuery("#rh-deskSetDom-desk").append(span);
	        });
            //默认应用
            jQuery("#rh-deskSetDom-app-ul").addClass("rh-deskSetDom-show").append(appBuildingDeskHave());
            jQuery("#rh-deskSetDom-app-ul li").bind("click",function() {
            	jQuery(".select").removeClass("select");
            	jQuery(this).addClass("select");
            	var appid = jQuery(this).attr("appid");
            	var opts = {"SD_APP_DEFAULT":appid};
        		if(rhExcuteDeskSet(opts) == true) {
        	    	portalMessage(td_lang.rh.msg_103);
        	    };
            });
            jQuery("li[appid='" + deskApp + "']").addClass("select");
         });
         //系统还原
         jQuery("#rhDesk-systemBack").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive");
         	jQuery(this).addClass("rhDesk-btnActive");
            var _display = $("#rh-systemBackDom").css("display");
  
            jQuery(".rh-deskDom").hide();
            jQuery("#rh-systemBackDom").show();
            

            jQuery("#desk-okBtn").bind("click",function() {
				var initApp = jQuery("#desk-initApp").attr("checked");
				var initTheme = jQuery("#desk-initTheme").attr("checked");
				var initDesk = jQuery("#desk-initDesk").attr("checked");

			    if (initApp || initTheme || initDesk) {
//			    	 var res = confirm("系统将还原桌面设置为初始值，并将刷新当前系统页面。");
			    	 var res = confirm(Language.transStatic("portal_index_desk_string22"));
		    		 if (res == true) {
		    		 	var opts = {}
		    		 	if (initTheme == "checked") {//默认主题
		    		 		initTheme = 1;
		    		 		opts["SD_INIT_THEME"] = initTheme;
		    		 		opts["SD_BACK_IMG"] = "";
		    		 	} else {
		    		 		opts["SD_INIT_THEME"] = 0;
		    		 	}
		    		 	if (initDesk == "checked") {//默认桌面设置
		    		 		initDesk = 1;
		    		 		opts["SD_INIT_DESK"] = initDesk;
		    		 		opts["SD_DESK_DEFAULT"] = "";
		    		 		opts["SD_APP_DEFAULT"] = "";
		    		 	} else {
		    		 		opts["SD_INIT_DESK"] = 0;
		    		 	}
		    		 	if (initApp == "checked") {//初始化程序
		    		 		initApp = 1;
		    		 		opts["SD_INIT_APP"] = initApp;
		    		 	} else {
		    		 		opts["SD_INIT_APP"] = 0;
		    		 	}
		        	    if(rhExcuteDeskSet(opts) == true) {
		        	    	if (initApp == 1) {
				    		 	var temp = [];
				    		 	jQuery.each(jQuery("#container .screen"),function(i,n) {
									temp.push(jQuery(this).attr("id"));
			        			});
			        			temp.push(normalUlId);
			        			var pkData = {};
					    		pkData[UIConst.PK_KEY]=temp.join(",");
					    		var resultData = FireFly.listDelete("SY_ORG_USER_DESK_ICON",pkData,false);
		        	    	}
		        	    	portalMessage(td_lang.rh.msg_104);
		        	    };	
		        	    parent.window.location.href = parent.window.location.href;
		    		 } else {
		    		 	return false;
		    		 }
			    }
            });
         });
         //通知设置
         jQuery("#rhDesk-msgSet").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive");
         	jQuery(this).addClass("rhDesk-btnActive");
            var _display = $("#rh-msgSetDom").css("display");
  
            jQuery(".rh-deskDom").hide();
            jQuery("#rh-msgSetDom").show();
            
            if (jQuery(this).hasClass("openFlagTrue")) {
            	return false;
            }
            if (deskMsg == 1) {
            	jQuery("#desk-msgFlag").click();
            }
            if (deskRing == 1) {
            	jQuery("#desk-ringFlag").click();
            }
            jQuery("#desk-msgOKBtn").bind("click",function() {
				var msgFlag = jQuery("#desk-msgFlag").attr("checked");
				var ringFlag = jQuery("#desk-ringFlag").attr("checked");

//		    	var res = confirm("系统将还原桌面设置为初始值，并将刷新当前系统页面。");
		    	var res = confirm(Language.transStatic("portal_index_desk_string22"));
	    		if (res == true) {
	    		 	if (msgFlag == "checked") {
	    		 		msgFlag = 1;
	    		 	} else {
	    		 		msgFlag = 0;
	    		 	}
	    		 	if (ringFlag == "checked") {
	    		 		ringFlag = 1;
	    		 	} else {
	    		 		ringFlag = 0;
	    		 	}
		            var opts = {"SD_MSG_FLAG":msgFlag,"SD_RING_FLAG":ringFlag};
	        	    if(rhExcuteDeskSet(opts) == true) {
	        	    	portalMessage(td_lang.rh.msg_105);
	        	    };
	        	    parent.window.location.href = parent.window.location.href;	
	    		} else {
	    		 	return false;
	    		}
            });
            jQuery(this).addClass("openFlagTrue");
         });
         //主题设置
         jQuery("#rhDesk-themeSet").live("click",function(){
         	jQuery(".rhDesk-btnActive").removeClass("rhDesk-btnActive");
         	jQuery(this).addClass("rhDesk-btnActive");
            var _display = $("#rh-themeSetDom").css("display");
  
            jQuery(".rh-deskDom").hide();
            jQuery("#rh-themeSetDom").show();
            if (jQuery(".rhDesk-theme-system").length > 0) {
            	return false;
            }
            var data = {"wu":{"img":"wu.jpg","title":"水天一色"},"cloud":{"img":"cloud.jpg","title":"蓝天白云"},
            "xiaochuan":{"img":"xiaochuan.jpg","title":"小船"},"black":{"img":"black.jpg","title":"黑色线条"},
            "diban":{"img":"diban.jpg","title":"地板"},"xiaodao":{"img":"xiaodao.jpg","title":"小岛湖水"},
            "hengmu":{"img":"hengmu.jpg","title":"横木纹"},"wucai":{"img":"wucai.jpg","title":"五彩世界"},
            "jia":{"img":"jia.jpg","title":"绿色小家"},"maitian":{"img":"maitian.jpg","title":"麦田"},
            "lvye":{"img":"lvye.jpg","title":"绿叶"},"zhu":{"img":"zhu.jpg","title":"竹色"},
            "qipao":{"img":"qipao.jpg","title":"气泡"},"dianying":{"img":"dianyingyuan.jpg","title":"电影院"},
            "xingqiu":{"img":"xingqiu.jpg","title":"星球"},"music":{"img":"music.jpg","title":"音乐"}};
            
            jQuery.each(data,function(i,n) {//绑定背景图片点击
            	var systemBlock = jQuery("<div></div>").addClass("rhDesk-theme-system").attr("deskImg",n.img).appendTo(jQuery("#rh-theme-system"));
            	systemBlock.bind("click",function() {
            		var url = "url(css/images/rh-back/" + n.img + ")";
					setDeskBackImg(url);
            		var opts = {"SD_BACK_IMG":n.img,"SD_SELF_SELECT":""};
            	    if(rhExcuteDeskSet(opts) == true) {
            	    	portalMessage(td_lang.rh.msg_101);
            	    	jQuery(".rhDesk-theme-systemSelect").removeClass("rhDesk-theme-systemSelect");
            	    	jQuery(this).addClass("rhDesk-theme-systemSelect");
            	    };
            	});
            	if (n.img == deskImg) {
            		systemBlock.addClass("rhDesk-theme-systemSelect");
            	}
            	jQuery("<img></img>").addClass("rhDesk-theme-img").attr("src","css/images/rh-back/" + n.img).appendTo(systemBlock);
            	jQuery("<span></span>").addClass("rhDesk-theme-text").text(n.title).appendTo(systemBlock);
            });
            //自定义区域rh-self-con

            var array = selfDeskImgAll.split(",");
            
            jQuery.each(array,function(i,n) {//绑定背景图片点击
            	var img  = buildSelfImg(n);
            	img.appendTo(jQuery("#rh-self-con"));
            	if (n == selfDeskImg) {
            		jQuery(".rhDesk-theme-systemSelect").removeClass("rhDesk-theme-systemSelect");
            		img.addClass("rhDesk-theme-systemSelect");
            		
            	}
            });
         });
         //====================================桌面设置部分修改==========================
         //根据权限生成一级菜单分类
         var Fmenu = returnFmenu();
         $("#app_cate_list ul").html(Fmenu);
         
         //根据个人屏幕设置生成
         var screenHtml = returnScreen();
         $("#screenPageDom #screen_list ul").html(screenHtml);
         $("#screenPageDom #screen_list ul").append("<li id='btnAddScreen' class='no-draggable-holder' title="+td_lang.inc.msg_76+"></li>");//'添加屏幕'
         
         //高亮显示当前屏幕 Todo
         var currentScreen = slideBox.getCursor();
         $("#screenPageDom #screen_list ul li.minscreenceil").eq(currentScreen).addClass("current");
         
         //移动屏幕
         $("#screenPageDom #screen_list ul").sortable({
               cursor: 'move', 
               tolerance: 'pointer',
               cancel: '#btnAddScreen',
               stop: function(){
                  var arrScreen = new Array();
                  $(this).find("li").each(function(){
                     arrScreen.push($(this).attr("index"));
                  });
                  slideBox.sortScreen(arrScreen);
                  $(this).find("li").each(function(i){
                     $(this).attr("index",i);
                  });
                  var flag = sortSlideDesk(arrScreen);
                  if(flag)   portalMessage(td_lang.inc.msg_77);      //"桌面顺序已设置成功！"
               }
         });
         
         //添加屏幕
         $("#btnAddScreen").live("click",function(){
            slideBox.addScreen();
            slideBox.scroll(slideBox.getCount() - 1);
            var screenlist = $("#screenPageDom #screen_list ul");
            var _max = 0;
            screenlist.find("li.minscreenceil").each(function(){
               _max = _max > parseInt($(this).attr("index")) ? _max : parseInt($(this).attr("index"));      
            });
            screenlist.find("#btnAddScreen").remove();
            screenlist.append("<li class='minscreenceil' index='"+ (_max+1) +"'>"+(_max+2)+"</li><li id='btnAddScreen' class='no-draggable-holder' title="+td_lang.inc.msg_76+"></li>");//'添加屏幕'
            var flag = serializeSlideDesk();
            //填充页码
            jQuery(".control-c a.btn").last().addClass("rh-slide-num").text(slideBox.getCount());
            if(flag) portalMessage(td_lang.inc.msg_78);      //"屏幕添加成功！"
         });
         
         //鼠标滑过屏幕样式
         $("#screenPageDom #screen_list ul li.minscreenceil'").live('mouseenter', function(){
            $(this).css({"font-size":"60px"});
            if($('span.closebtn', this).length <= 0)
               $(this).append("<span class='closebtn' title="+td_lang.inc.msg_79+"></span>");//'移除此屏'
            $('span.closebtn', this).show();
         });
         
         $("#screenPageDom #screen_list ul li.minscreenceil").live('mouseleave', function(){
            $(this).css({"font-size":""});
            $('span.closebtn', this).hide();
         });
         
         //删除屏幕
         $("#screenPageDom #screen_list ul li.minscreenceil span").live("click",function(){
            if(confirm(td_lang.inc.msg_80)){//"删除桌面，将删除桌面全部应用模块，确定要删除吗？"
               var currentDom = $(this).parent("li");
               var index = currentDom.index("li.minscreenceil");
               var deskPK = slideBox.getScreen(index).attr("id");
//         	alert(slideBox.getScreen(index).attr("id"));
               slideBox.removeScreen(currentDom.index("li.minscreenceil"));
               var flag = serializeSlideDesk(deskPK);
               if(flag)
               {
                  portalMessage(td_lang.inc.msg_81);//"桌面删除成功！"
                  currentDom.remove();
                  reSortMinScreen();
               }
            }   
         });
         
         //绑定一级菜单分类点击事件
         $("#app_cate_list ul li a").live("click",function(){
            $("#app_cate_list ul li a").removeClass("current");
            $(this).addClass("current");
            
            //显示一级对应的所有2级菜单
            var appId = $(this).attr("id");
            var appIds = returnSTmenu(appId);
            var apphtml = appBuilding(appIds);
            
            $("#app_list_box ul").html(apphtml);
         });
         
         //绑定右侧应用,点击事件
         $("#app_list_box ul li").live("click",function(){
            var obj = $(this).find("a");
            var appid = obj.attr("appid");
            //var appEid = obj.attr("appEid");
            var appIcon = obj.attr("appicon");
            var apptitle = obj.attr("apptitle");
            var appInfo = obj.attr("appinfo");
            var appType = obj.attr("apptype");
            var appmenu = obj.attr("appmenu");
            var countserv = obj.attr("appcountserv");
            var menuid = obj.attr("appmenuid");
            if(getAppNums() > SCREEN_MAX_APP_NUM){
            	 var msg1 = sprintf(td_lang.inc.msg_124,SCREEN_MAX_APP_NUM);
               if(!confirm(msg1)){
                  return;   
               }   
            }
            
            //rh-toodo 添加桌面图标事件
            addApp({"ID":appid,"ICON":appIcon, "NAME":apptitle,"TYPE":appType,"LEAF":appInfo,
                    "MENU":appmenu,"MENUID":menuid,"COUNTSERV":countserv},slideBox.getCursor());
            var flag = serializeSlide();

            if(flag){
               $(this).fadeOut(($.browser.msie ? 1 : 300),function(){$(this).remove();});
               portalMessage(td_lang.inc.msg_82);//"应用已添加到当前桌面！"
               leftMenuModel[appid] = undefined;
            }else{
               portalMessage(td_lang.inc.msg_83);      //"应用添加错误！"
            }
         });
         
         //默认选中第一个
         $("#app_cate_list ul li a:first").trigger("click");
         
         //更新高度
         $("#portalSetting").layout({north:{size:38}, center:{}});
         $("#appPageDom").layout({west: {size:'auto'},center:{}});
         
         //定义应用一级菜单是否滚动
         initAppListScroll();
         
         $("#app_cate_list ul").mousewheel(function(){
            $('#app_cate_list ul').stop().animate({'scrollTop':($('#app_cate_list ul').scrollTop() - this.D)}, 50);
         });
         
      });
      
   });
      
})(jQuery);

var __sto = setTimeout;
window.setTimeout = function(callback,timeout,param)
{
   var args = Array.prototype.slice.call(arguments,2);
   var _cb = function()
   {
      callback.apply(null,args);
   }
   return __sto(_cb,timeout);
};
//排序桌面设置,并且更新后台
function sortSlideDesk(arrScreen) {
	var datas = [];
	var flag = false;
	jQuery.each(arrScreen,function(i,n) {
		var screen = slideBox.getScreen(i);
		if (screen.attr("id")) {
			var data = {};
			data[UIConst.PK_KEY] = screen.attr("id");
			data["PI_ORDER"] = i;
			datas.push(data);
		}
	});
	var batchData = {};
	batchData["BATCHDATAS"] = datas;
	var resultData = FireFly.batchSave("SY_ORG_USER_DESK_ICON",batchData,null,false);
    if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
	   flag = true;
    } 
	return flag;
}
//序列化桌面上的图标,并且更新
function serializeSlideDesk(deskPK) {
   var s = "";
   var screenIndex = slideBox.getCursor();
   var temp = [];
   jQuery(jQuery("#container .screen")[screenIndex]).find("li.block").each(function(j, el) {
       temp.push(jQuery(el).attr("id"));
   });
   var flag = false;

   var data = {};
   if (deskPK) {//删除桌面
   	   data[UIConst.PK_KEY] = deskPK;
	   var resultData = FireFly.doAct("SY_ORG_USER_DESK_ICON","delete",data,false);
	   if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
		   flag = true;
	   }    	
   } else {//新增桌面
	   data["PI_TITLE"] = "第" + screenIndex + "页";
	   data["PI_ORDER"] = screenIndex;
	   data["PI_SERVS"] = temp.join(",");
	   var resultData = FireFly.doAct("SY_ORG_USER_DESK_ICON","save",data,false);
	   if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
	   	   var pk = resultData[UIConst.PK_KEY];
	   	   slideBox.getScreen(screenIndex).attr("id",pk);
		   flag = true;
	   } 
   }
   return flag;
}
//序列化桌面上的图标,并且更新
function serializeSlide() {
   var s = "";
   var screenIndex = slideBox.getCursor();
   var temp = [];
   jQuery(jQuery("#container .screen")[screenIndex]).find("li.block").each(function(j, el) {
       temp.push(jQuery(el).attr("id"));
   });
   var flag = false;
   var data = {};
   data[UIConst.PK_KEY] = slideBox.getScreen(screenIndex).attr("id") ? slideBox.getScreen(screenIndex).attr("id"):"";
   data["PI_SERVS"] = temp.join(",");
   //常用操作
   var normalData = {};
   var normal = [];
   jQuery("#normalUse .normal-li").each(function(j, el) {
       normal.push(jQuery(el).attr("id"));
   });
   if (jQuery(".normal-ul").attr("id") && (jQuery(".normal-ul").attr("id").length > 0)) {
	   normalData[UIConst.PK_KEY] = jQuery(".normal-ul").attr("id");  
   }
   normalData["PI_NORMAO_ICON"] = normal.join(",");
   normalData["PI_TITLE"] = "ICONS";
   var datasArray = [];
   datasArray.push(data);
   datasArray.push(normalData);
   	
   var batchData = {};
   batchData["BATCHDATAS"] = datasArray;
   var resultData = FireFly.batchSave("SY_ORG_USER_DESK_ICON",batchData,null,false,false);
   if (resultData[UIConst.RTN_MSG] && resultData[UIConst.RTN_MSG].indexOf(UIConst.RTN_OK) == 0) {
   	   	var str = resultData._OKIDS_.split(",");
   	   	//常用图标条
   	    if (jQuery(".normal-ul").attr("id")) { 	
   	    } else {
		   	var normalId = str[1];
		   	jQuery(".normal-ul").attr("id",normalId);  	
   	    }
   	    //slide屏幕
        var tempId = data[UIConst.PK_KEY];
   	    if (tempId.length == 0) {
   	    	slideBox.getScreen(screenIndex).attr("id",str[0]);
   	    }
	   flag = true;
   } 
   return flag;
}
//桌面设置和后台的交互
function rhExcuteDeskSet(opts) {
   var flag = false;
   var data = {};
   data[UIConst.PK_KEY] = deskSetPK;
   data = jQuery.extend(data,opts);

   var res = FireFly.doAct("SY_ORG_USER_DESK","save",data,false);
   if (res[UIConst.PK_KEY] && res[UIConst.PK_KEY].length > 0) {
	   	deskSetPK = res[UIConst.PK_KEY];
	   	flag = true;
   }
   return flag;
}

//每15秒定期刷新，用此setInterval方法，时间精确
//window.setInterval(getCounts,15000);

//动态获取 服务的提醒数量
function getCounts() {
	   jQuery.each(jQuery(".countBack"),function(i,n) {
		   var appId = jQuery(n).attr("liid");
		   var e = jQuery("#"+appId)
		   var countserv = e.attr("countserv");
		   if(countserv){
		   var count;
		   var ajaxUrl =countserv+".getCount.do";
	       jQuery.ajax({
	    	 
	         type: 'post',
	         url: ajaxUrl,
	         dataType:"json",
	         //data:params,
	         cache:false,
	         async:false,
	         timeout:60000,
	         success:function(data) { 
	        	 if(data.ALLNUM >= 1) {
	        		 var li = jQuery("#"+appId);
	        		 jQuery("#count_"+appId).show();
	        		 jQuery("#count_"+appId).find(".countBack-text").text(data.ALLNUM);

	        		 if(li.attr("info")!="SY_COMM_TODO"){
	        			
	        			   li.attr("info", "SY_COMM_TODO");
	        			   li.attr("menuid", System.getUser("CMPY_CODE")+"SY_COMM_TODO");
	        			   li.attr("extWhere", "and  TODO_CODE = '"+countserv+"'");
	        		   }
	        	 }
	        	 
	        	 
	         },
	         error:function(err) {

	         }
		   
	   });
	   
		   }
  });
	   //每15秒定期刷新,时间有误差，用setTimeout方法压力小些，适合函数的调用需要繁重的计算以及很长的处 理时间
	   //window.setTimeout(getCounts,15000);    
}
//====================================底部常用功能条==========================
function initBottomUse() {
	var bottomFunc = {};
	var ul = jQuery("<ul></ul>").attr("id",normalUlId).addClass("normal-ul").appendTo(jQuery(".normalUse-c"));
	var data = normalIcons.split(",");
	jQuery.each(data,function(y,m) {
		var n = leafMenuModel[m];
	    if (n == null) {
	    	return;
	    }
		var li = jQuery("<li></li>").addClass("normal-li").attr("id",n.ID);
		li.attr("type",n.TYPE);
		li.attr("info",n.LEAF);
		li.attr("menu",n.MENU);
		li.attr("menuid",n.MENUID);
		li.attr("title",n.NAME);
		li.attr("icon",n.ICON);
		li.attr("countserv", n.COUNTSERV);
		var div = jQuery("<div></div>").addClass("img").appendTo(li);;
		var p = jQuery("<p></p>").appendTo(div);
		var img = jQuery("<img src='css/images/app_rh-icons/" + n.ICON + ".png'></img>").appendTo(p);
		var divT = jQuery("<div></div>").addClass("count").appendTo(div);
		divT.attr("id", "count_" + n.ID);
	    if(rhTempAlertIconsJson[n.ID]) {//如果当前图标启用消息提醒
	      divT.addClass("countBack"); 
	      divT.attr("liid",n.ID);
	      var countText = 0;
		  var countA = jQuery("<a></a>").addClass("countBack-a");
		  jQuery("<span></span>").addClass("countBack-text").text(countText).appendTo(countA);
	      divT.append(countA);
	    }
		var a = jQuery("<a href='javascript: void(0)'></a>").addClass("icon-text").addClass("normal-a").appendTo(li);
		var span = jQuery("<span></span>").text(n.NAME).appendTo(a);
		li.appendTo(ul);
	});
	var counts = jQuery(".btn",jQuery(".control-c "));
	jQuery.each(counts,function(y,m) {
		jQuery(m).addClass("rh-slide-num").addClass("rh-slideIndex-" + y).text(y+1);
	});
	jQuery(".normal-li").live("click",portalOpenTab);
	
	initBottomUseMove();
}
function initBottomUseMove() {
	 var sortFlag = true;
     jQuery("#normalUse ul").sortable({
		items: ".normal-li",
		revert: false,
		sort: function() {
			sortFlag = true;
			jQuery("#normalUse .remove").remove();
		},
	    stop: function(e, ui) {
             jQuery("#normalUse .remove").remove();
 
             if (sortFlag) {
                serializeSlide();
             }

        }
	});

     jQuery("#normalUse ul li").droppable({
       over: function(event, ui) {
       	   if (ui.draggable.hasClass("normal-li")) {
	   	   	   return false;
	   	   }
           jQuery(this).css("top","-20");
       },
	   out: function(event, ui) {
			jQuery(this).css("background-color","");
			jQuery(this).css("top","0");
	   },
	   drop: function(event, ui) {
	   	   if (ui.draggable.hasClass("normal-li")) {
	   	   	   jQuery(this).css("top","0");
	   	   	   return false;
	   	   }
	   	   sortFlag = false;
	       var temp = ui.draggable.clone().attr("style","").removeClass("block").removeClass("ui-sortable-helper");
	       temp.click(portalOpenTab());
	       temp.insertAfter(jQuery(this));
	       temp.addClass("normal-li");
	       temp.find("a.icon-text").addClass("normal-a");
	       //替换图片
           var thisObj = jQuery(this);
           thisObj.css("background-color","");
           thisObj.css("top","0");

           ui.draggable.addClass("remove").hide();
           delModule && delModule(ui.draggable.attr("id"));
           if (jQuery("#normalUse ul li").length == 7) {
	           var aa = leafMenuModel[thisObj.attr("id")];
	           addApp(aa,slideBox.getCursor());
	           thisObj.addClass("remove");
	           thisObj.remove();
           }
           initBottomUseMove();
	   }
	});
     jQuery(".screen .ui-sortable").droppable({
       over: function(event, ui) {
       },
	   out: function(event, ui) {

	   },
	   drop: function(event, ui) {
	   	   if (ui.draggable.hasClass("normal-li")) {
	           var aa = leafMenuModel[ui.draggable.attr("id")];
	           addApp(aa,slideBox.getCursor());
	           ui.draggable.addClass("remove").hide();
	           var id = ui.draggable.attr("id");
	           jQuery("#" + id,jQuery("#normalUse")).remove();
	   	   	   serializeSlide();
	   	   	   sortFlag = false;
	   	   }
	   }
	});
	
	jQuery("#normalUse ul").droppable({
	   accept: '.normal-li',
       over: function(event, ui) {
       	   if (jQuery("#normalUse ul li").length == 0) {
	   	   	   //jQuery(this).css("background-color","red");
	   	   }
       },
	   out: function(event, ui) {
	   },
	   drop: function(event, ui) {
	   	   if (jQuery("#normalUse ul li").length == 0) {
		       var temp = ui.draggable.clone().attr("style","").removeClass("block").removeClass("ui-sortable-helper");
		       temp.appendTo(jQuery(this));
		       temp.addClass("normal-li");
		       temp.find("a.icon-text").addClass("normal-a");
	           ui.draggable.addClass("remove");
	
	           jQuery(this).css("background-color","");
	           ui.draggable.addClass("remove").hide();
	           delModule && delModule(ui.draggable.attr("id"));	
	           initBottomUseMove();
           }
	   }
	});
	 jQuery(".normalUse-c img").reflect({ height:.5, opacity:.4 });
}




