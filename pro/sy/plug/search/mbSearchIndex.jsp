<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!--searchIndex.jsp 搜索入口页面-->
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>软虹-云智能搜索</title>
<%@ page import="com.rh.core.plug.search.*"%>
<%@ include file="/sy/base/view/inHeader-mb.jsp"%>
<script src="mbSearchView.js" type="text/javascript"></script>

<style type="text/css">
#suggest {
	position: absolute;
	background-color: #FFFFFF;
	border: 1px solid #CCCCFF;
	width: 404;
}

#suggest div {
	padding: 5px;
	display: block;
	width: 404;
	FONT: 15px arial;
	overflow: hidden;
	white-space: nowrap;
}

#suggest div.select {
	color: #FFFFFF;
	background-color: #3366FF;
}

#suggest div.over {
	background-color: #99CCFF;
}
</style>
</head>
<body class="bodyBack mbDesk-body ">


	<div class="mbTopBar">
	</div>


	<div class="rh-allSearch-door">

		<div class="rh-allSearch-bar">

			<input type="text"
				style="line-height: 25px; height: 26px; width: 430px; border: none; font-size: 18px;"
				id="keywords"></input>
		</div>
		<a href="#" class="rh-allSearch-btn"></a>
		<div id="suggest"
			style="width: 435px; z-index: 10; position: absolute; left: 40px; top: 210px; display: none;"></div>
	</div>

</body>

<script src="/search/search.js" type=text/javascript></script>
<script src="/search/suggest.js" type=text/javascript></script>
<%
String suggestServer = SearchServ.getSuggestionUri();
%>
<script type="text/javascript">
//suggestion
function startSuggest(){
	 var server = "<%=suggestServer%>";
    var list = [""];
    new Suggest.Local("keywords", // input element id.
        "suggest", // suggestion area id.
        server + "?rtn=js&w=",
		 list, {
            dispMax: 10,
            interval: 10
    }); // options
}
window.addEventListener ? window.addEventListener('load', startSuggest, false) : window.attachEvent('onload', startSuggest);


 jQuery(document).ready(function(){
	 Tab.setFrameHei(GLOBAL.getDefaultFrameHei());

	//set mobile top bar
	var searchBar = jQuery(".mbTopBar");
	var back = jQuery("<div>返回</div>").addClass("mbTopBar-back mbTopBar-search-back").appendTo(searchBar); 
	back.bind("click",function() {
		back.addClass("mbTopBar-backActive");
    //	history.go(-1);
		window.location.href="/sy/comm/desk-mb/desk-mb.jsp";
    });

	
	jQuery(".rh-allSearch-btn").bind("click",function() {
		var keywords = jQuery("#keywords").val();
		var filterCache = [];
		var servIds = jQuery("#rh-select-serv-id").val();

		query(keywords);
	});

	jQuery(".rh-allSearch-bar").bind('keydown', function (e) {
        var key = e.which;
        if (key == 13) {
        	var keywords = jQuery("#keywords").val();
        	query(keywords);
        }
    });

	
    
 });

 //query data
 query = function(keywords) {
		window.location.href="/sy/plug/search/mbSearchResult.jsp?k="+keywords;
 };

//选择单位触发方法
 getOrg = function(event) {
 	jQuery("#rh-select-cmpy").empty()
 	var options = {"itemCode":"rh-select-cmpy","config":"SY_ORG_CMPY,{'extendDicSetting':{'rhexpand':false,'cascadecheck':true},'TYPE':'multi','rtnNullFlag':true}","parHandler":null,"hide":"explode","show":"blind",replaceCallBack:function(id,value) {
 	   jQuery("#rh-select-cmpy-id").val(id);
 	   jQuery("#rh-select-cmpy").val(value);
 	   //search...
 	}};
 	var dictView = new rh.vi.rhDictTreeView(options);
 	dictView.show(event,[170,150]);
 	var id = jQuery("#rh-select-cmpy-id").val();
 	jQuery(".ui-dialog-title").text("请选择单位");
 	
 	var array = id.split(",");
   	jQuery.each(array,function(index, n) {
   		dictView.tree.checkNode(n);
   		dictView.tree.expandParent(n);
 	});
 	
 	return false;
 };

//选择服务(类别)触发方法
 getServ = function(event) {
 	jQuery("#rh-select-serv").empty()
 	var options = {"itemCode":"rh-select-serv","config":"SY_SERV_SEARCH,{'extendDicSetting':{'rhexpand':true},'TYPE':'multi','extendWhere':' AND SERV_SEARCH_FLAG=1','rtnNullFlag':true}","parHandler":null,"hide":"explode","show":"blind",replaceCallBack:function(id,value) {
 	   jQuery("#rh-select-serv-id").val(id);
 	   jQuery("#rh-select-serv").val(value);
 	   //search...
 	}};
 	var dictView = new rh.vi.rhDictTreeView(options);
 	dictView.show(event,[170,150]); 
 	var id = jQuery("#rh-select-serv-id").val();
 	jQuery(".ui-dialog-title").text("请选择类别");
 	
 	
 	var array = id.split(",");
   	jQuery.each(array,function(index, n) {
   		dictView.tree.checkNode(n);
   		dictView.tree.expandParent(n);
 	});
   	
 	return false;
 };
 

</script>
</html>