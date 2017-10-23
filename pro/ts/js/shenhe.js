var xmid = $("#xmid").val();
var user_code = System.getVar("@USER_CODE@");
//隔一行 进行 背景颜色 渲染
function rowscolor(table){
	 var rows = table.getElementsByTagName("tr");  
	    for(i = 1; i < rows.length; i++){  
	        if(i % 2 == 0){  
	            rows[i].style.backgroundColor = "Azure";  
	       }  
	    } 
}
//所有的button   onmouserover事件
var btns = document.getElementsByTagName('button');
for(var z=0;z<btns.length;z++){
	 btns[z].onmouseover = function() {
		 this.style.backgroundColor = '#23c0b9';
	 }
	 btns[z].onmouseout = function() {
	        this.style.background = 'lightseagreen';
	    }
}
//审核 筛选
var nowlevel = 0;
var nodeid = "";

//------------------------------------------------拖动效果
function Drag(div,table){
	  
    var ochek=document.getElementById(div),
        otable=document.getElementById(table),
        otody=otable.tBodies[0],
        oth=otable.getElementsByTagName("th"),
        otd=otody.getElementsByTagName("td"),
        box=document.getElementById("box"),
        arrn=[];
        var a =0 ;
        var b =1;
        var c= 2;
        for (var i = 0; i < otd.length; i++) {
        	var length = oth.length-1;
        	if(i!=0&&i%length==0){
        		a+=oth.length;
        		b+=oth.length;
        		c+=oth.length;
        	}
        	
        	if(i!=a&&i!=b&&i!=c){
          otd[i].onmousedown=function(e){
              var e=e||window.event,
                  target = e.target||e.srcElement,
									
                  thW = target.offsetWidth,
                  maxl=ochek.offsetWidth-thW,
                  rows=otable.rows,
                  ckL=ochek.offsetLeft,
                  disX=target.offsetLeft,
                  _this=this,
                  cdisX=e.clientX-ckL-disX;
					
                  for (var i = 0; i < rows.length; i++) {
                      var op=document.createElement("p");
                      op.innerHTML=rows[i].cells[this.cellIndex].innerHTML;  
                      box.appendChild(op);
                  };    
                  for (var i = 0; i < oth.length; i++) {
                         arrn.push(oth[i].offsetLeft);      
                  }; 
                  box.style.display="block";
                  box.style.width=thW+"px";
                  box.style.left=disX+"px";
                  //未完成 还有事件没写。
                  document.onmousemove=function(e){
                      var e=e||window.event,
                      target = e.target||e.srcElement,
                      thW = target.offsetWidth;
                      box.style.top=0;
                      box.style.left=e.clientX-ckL-cdisX+"px";
                      if(box.offsetLeft>maxl){
                           box.style.left=maxl+"px";
                      }else if(box.offsetLeft<0){
                           box.style.left=0;
                      }        
                      document.onselectstart=function(){return false};     
                    window.getSelection ? window.getSelection().removeAllRanges() : doc.selection.empty();              
                  }
                  document.onmouseup=function(e){
                     var e=e||window.event,
                         opr=box.getElementsByTagName("p"),
                         oboxl=box.offsetLeft+cdisX;
                        for (var i = 0; i < arrn.length; i++) {
                           if(arrn[i]<oboxl){
                            var index=i;
                           }
                        };
                       for (var i = 0; i < rows.length; i++) {
                          rows[i].cells[_this.cellIndex].innerHTML="";
                          rows[i].cells[_this.cellIndex].innerHTML=rows[i].cells[index].innerHTML;
                          rows[i].cells[index].innerHTML="";
                          rows[i].cells[index].innerHTML=opr[i].innerHTML;
                       };
                       box.innerHTML="";
                       arrn.splice(0,arrn.length);
                       box.style.display="none";
                       document.onmousemove=null; 
                       document.onmouseup=null;
                       document.onselectstart=function(){return false};     
                  }
                   this.onclick=null;
          }
        	}
        };
        
  }

  //动态获取 th重新 排序 拼接
function appendTh(user_code){
	$("#staytable thead").html("");
	$("#nopasstable thead").html("");
	$("#passtable thead").html("");
	$("#staytable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 2%; text-align: center"><input type="checkbox" name="checkbox1" value="checkboxaa" onchange="change(this)"></th><th style="width: 3%; text-align: center">序号</th><th style="width: 10%; text-align: center">操作</th>');
	$("#passtable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 2%; text-align: center"><input type="checkbox" name="checkbox2" value="checkboxbb" onchange="changeb(this)"></th><th style="width: 3%; text-align: center">序号</th><th style="width: 10%; text-align: center">操作</th>');
	$("#nopasstable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 2%; text-align: center"><input type="checkbox" name="checkbox3" value="checkboxcc" onchange="changec(this)"></th><th style="width: 3%; text-align: center">序号</th><th style="width: 10%; text-align: center">操作</th>');
	var param ={};
	param["user_code"]=user_code;
	var result = FireFly.doAct("TS_BMSH_PX","getShenheJson",param);
	var pageEntity = result.list;
	for(var i=0;i<pageEntity.length;i++){
		
		var px_name=pageEntity[i].PX_NAME;
		
		$("#staytable tr").append('<th style="text-align: center">'+px_name+'</th>');
		$("#nopasstable tr").append('<th style="text-align: center">'+px_name+'</th>');
		$("#passtable tr").append('<th style="text-align: center">'+px_name+'</th>');
	}  
	$("#staytable thead").append('</tr>');
}
	
	//可选报名  已选报名字体图片改变
var tabnum=1;
	function dsha(){
		
		document.getElementById("dshimage").src="/ts/image/u1677.png";
		document.getElementById("dshsp").style.color="LightSeaGreen";
		document.getElementById("shtgsp").style.color="black";
		document.getElementById("shtgimage").src="/ts/image/u1681.png";
		document.getElementById("shwtgsp").style.color="black";
		document.getElementById("shwtgimage").src="/ts/image/u1695.png";
		tabnum=1;
		 new listPage().gotoPage(1);
	}
	function shtga(){
		document.getElementById("shtgsp").style.color="LightSeaGreen";
		document.getElementById("shtgimage").src="/ts/image/u2216.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		document.getElementById("shwtgsp").style.color="black";
		document.getElementById("shwtgimage").src="/ts/image/u1695.png";
		tabnum=2;
		new listPage().gotoPage(1);	}
	function shwtda(){
		document.getElementById("shwtgsp").style.color="LightSeaGreen";
		document.getElementById("shwtgimage").src="/ts/image/u2813.png";
		document.getElementById("shtgsp").style.color="black";
		document.getElementById("shtgimage").src="/ts/image/u1681.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		tabnum=3;
		new listPage().gotoPage(1);
	}
//---------------------------------------------------------------------------------自定义显示列
	//自定义显示列数据进行回显
	function zdyl(){
		var param={};
		param["user_code"]=user_code;
		var result = FireFly.doAct("TS_BMSH_PX","getUserList",param);
		var pageEntity = result.list;
		
			for(var i=0;i<pageEntity.length;i++){
				var px_column = pageEntity[i].PX_COLUMN;
				var name = pageEntity[i].PX_NAME;
				//都处于选中状态
				//第一个td
				var firtds = document.getElementsByName("firtd");
				for(var j=0;j<firtds.length;j++){
					if(firtds[j].children[0].id==px_column){
						firtds[j].children[0].name="rtcheckbox";
						var s =  firtds[j].parentNode.innerHTML;
						firtds[j].parentNode.remove();
						 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px #f5f5f5">'+s+'</tr>')
					}
				}
		}
			
			 tuodongtr();
}
	
	//保存自定义显示 的 数据 
	function  savePX(){
		//获取所有的td
		var sentds = $('input:checkbox[name=rtcheckbox]');
		if(sentds.length==0){
			alert("至少选择一条");
			return;
		}
		//循环遍历所有的td
		var param={};
		var aa = false;
		for(var i=0;i<sentds.length;i++){
			var id = sentds[i].id;
			var name = sentds[i].value;
			param["id"]=id;
			param["user_code"]=user_code;
			param["xuhao"]=i+1;
			param["name"]=name;
			FireFly.doAct("TS_BMSH_PX","paixu",param);
		}
		$('#paixu').modal('hide');
		//重新append th
		appendTh(user_code);
		 new listPage().gotoPage(1);
	}
	
	
	//第一个td显示所有的数据
function firall(){
	var param1={};
    var result1 = FireFly.doAct("TS_BMSH_PX","getShenhelist",param1);
	var pageEntity1= result1.list;
    for(var i=0;i<pageEntity1.length;i++){
    	var name = pageEntity1[i].PX_NAME;
    	var px_column= pageEntity1[i].PX_COLUMN;
    	$("#pxtable tbody").append('<tr style="border-bottom:solid 1px #f5f5f5"><td id="fir'+i+'" name="firtd" position="absolute"; style="line-height: 30px;padding-left:20px;text-align:left;height:20px;font-size:13px"><input style="position:relative;top:5px;background:white;width:15px;height=15px" type="checkbox" id='+px_column+' value='+name+'  name="pxcheckbox">&nbsp;&nbsp;'+name+'</td></tr>');
    }

}

//-----------------------------------------------------------------------------------------------------审核按钮
		//checkbox全选   第一个table
		function shenheA(){
			var radio = $("#radiospan2").show();
			var radio = $("#radiospan1").show();
			//是否有checkbox被选中
			 var checkboxchecked = $('input:checkbox[name=checkboxa]:checked');
			 var flag = false;
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheA").removeAttr("data-target");
			}else{
				 $('input:checkbox[name=checkboxa]:checked').each(function(){
					 if($(this).attr("SHLV")!=0){
					 if(nowlevel>$(this).attr("SHLV")){
						 var color=$(this.parentNode.parentNode).css("background-color");
							$(this.parentNode.parentNode).css("background-color", "#99FFFF");
							$(this).click(function(){
								if(!$(this).is(":checked")){
									$(this.parentNode.parentNode).css("background-color", color);
								}
							});
							 alert("请查看审核级数您的上级已审核您没有审核此数据的权利");
							 flag=true;
							 return;
						 
					 }
					 }
				 });
				 if(flag){
					 $("#shenheA").attr("data-target",'');
				 }else{
					 $("#shenheA").attr("data-target",'#tiJiao');
					 document.getElementById("mokuai").value="A";
				 }
			}
		}
		function change(obj){
			if($(obj).prop("checked")){
				  var kslxArray = $('input:checkbox[name=checkboxa]');
				  for(var i=0;i<kslxArray.length;i++){
			     	kslxArray[i].checked=true;
			     	}
				}else{
					 var kslxArray = $('input:checkbox[name=checkboxa]');
					  for(var i=0;i<kslxArray.length;i++){
				     	kslxArray[i].checked=false;
				     	}
		}
		}
		function mttijiao(){
			//提交审核
			var mokuai = document.getElementById("mokuai").value;
			var  s = "";
			var param={};
			if(mokuai=="A"){
			//获取第一个 tab  选中的checkbox
				  $('input:checkbox[name=checkboxa]:checked').each(function(){
					  var i=0;
					  i++;
					  var aa = $(this).val();
					  if(i==0){
						  s+=aa;
					  }else{
						  s+=","+aa;
					  }
				  });
					  var radiovalue = $('input:radio:checked').val();
					  var liyou = document.getElementById("liyou").value;
					  param["user_code"]=user_code;
					  param["checkedid"]=s;
					  param["radiovalue"]=radiovalue;
					  param["liyou"]=liyou;
					  param["xmid"]=xmid;
					  param["level"]=nowlevel;
					  param["nodeid"]=nodeid;
					  FireFly.doAct("TS_BMSH_STAY","update",param);
					  $('#tiJiao').modal('hide');
					  dsha();
			}else if(mokuai=="B"){
				//获取第一个 tab  选中的checkbox
				  $('input:checkbox[name=checkboxb]:checked').each(function(){
					  var i=0;
					  i++;
					  var aa = $(this).val();
					  if(i==0){
						  s+=aa;
					  }else{
						  s+=","+aa;
					  }
					  
				  });
					  var radiovalue = $('input:radio:checked').val();
					  if(radiovalue==1){
					  alert("状态未改变");
					  }else{
					  var liyou = document.getElementById("liyou").value;
					  param["user_code"]=user_code;
					  param["checkedid"]=s;
					  param["radiovalue"]=radiovalue;
					  param["liyou"]=liyou;
					  param["xmid"]=xmid;
					  param["level"]=nowlevel;
					  param["nodeid"]=nodeid;
					  FireFly.doAct("TS_BMSH_PASS","update",param);
					  //局部刷新
					   $('#tiJiao').modal('hide');
						 shtga();
			   }
			}else if(mokuai=="C"){
				//获取第一个 tab  选中的checkbox
				  $('input:checkbox[name=checkboxc]:checked').each(function(){
					  var i=0;
					  i++;
					  var aa = $(this).val();
					  if(i==0){
						  s+=aa;
					  }else{
						  s+=","+aa;
					  }
						  });
					  var radiovalue = $('input:radio:checked').val();
					  if(radiovalue==1){
						  
					  var liyou = document.getElementById("liyou").value;
					  param["user_code"]=user_code;
					  param["checkedid"]=s;
					  param["radiovalue"]=radiovalue;
					  param["liyou"]=liyou;
					  param["xmid"]=xmid;
					  param["level"]=nowlevel;
					  param["nodeid"]=nodeid;
					  FireFly.doAct("TS_BMSH_NOPASS","update",param);
					  $('#tiJiao').modal('hide');
					  shwtda();
					  }else{
						  alert("状态未改变");
					  }
					  
					}
		}
		//第二个tab 
		function shenheB(){
			//是否有checkbox被选中
			var radio = $("#radiospan1").hide();
			var radio = $("#radiospan2").show();
			 var checkboxchecked = $('input:checkbox[name=checkboxb]:checked');
			 var flag = false;
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheB").removeAttr("data-target");
			}else{
				 $('input:checkbox[name=checkboxb]:checked').each(function(){
					 if($(this).attr("SHLV")!=0){
					 if(nowlevel>$(this).attr("SHLV")){
						var color=$(this.parentNode.parentNode).css("background-color");
						$(this.parentNode.parentNode).css("background-color", "#99FFFF");
						$(this).click(function(){
							if(!$(this).is(":checked")){
								$(this.parentNode.parentNode).css("background-color", color);
							}
						});
						 alert("请查看审核级数您的上级已审核您没有审核此数据的权利");
						 flag=true;
						 return;
					 }
					 }
				 });
				 if(flag){
					 $("#shenheB").attr("data-target",'');
				 }else{
					 $("#shenheB").attr("data-target",'#tiJiao');
					 document.getElementById("mokuai").value="B";
				 }
			}
		}
		function changeb(obj){
			if($(obj).prop("checked")){
				  var kslxArray = document.getElementsByName("checkboxb");
				  for(var i=0;i<kslxArray.length;i++){
			     	kslxArray[i].checked=true;
			     	}
				}else{
					 var kslxArray = document.getElementsByName("checkboxb");
					  for(var i=0;i<kslxArray.length;i++){
				     	kslxArray[i].checked=false;
				     	}
		}
		}
		//第三个tab 
		function shenheC(){
			var radio = $("#radiospan2").hide();
			var radio = $("#radiospan1").show();
			//是否有checkbox被选中
			 var checkboxchecked = $('input:checkbox[name=checkboxc]:checked');
			 var flag = false;
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheC").removeAttr("data-target");
			}else{
				 $('input:checkbox[name=checkboxc]:checked').each(function(){
					 if($(this).attr("SHLV")!=0){
					 if(nowlevel>$(this).attr("SHLV")){
						 var color=$(this.parentNode.parentNode).css("background-color");
							$(this.parentNode.parentNode).css("background-color", "#99FFFF");
							$(this).click(function(){
								if(!$(this).is(":checked")){
									$(this.parentNode.parentNode).css("background-color", color);
								}
							});
							 alert("请查看审核级数您的上级已审核您没有审核此数据的权利");
							 flag=true;
							 return;
					 }
					 }
				 })
				 if(flag){
					 $("#shenheC").attr("data-target",'');
				 }else{
					 $("#shenheC").attr("data-target",'#tiJiao');
					 document.getElementById("mokuai").value="C";
				 }
			}
		}
		function changec(obj){
			if($(obj).prop("checked")){
				  var kslxArray = $('input:checkbox[name=checkboxc]');
				  for(var i=0;i<kslxArray.length;i++){
			     	kslxArray[i].checked=true;
			     	}
				}else{
					 var kslxArray = $('input:checkbox[name=checkboxc]');
					  for(var i=0;i<kslxArray.length;i++){
				     	kslxArray[i].checked=false;
				     	}
		}
		}
//----------------------------------------------------------------------------------------分页
		//分页查询
		function fenyeselect(obj){	
			//跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
			//判断i
			 new listPage().gotoPage(1);
		}
		
		function fanhui(){
			window.history.go(-1);
		}
	
//---------------------------------------------------------------------------------------------------------
			//加载完后自动调用
			$(function(){
				appendTh(user_code);
				firall();
				
				new listPage().gotoPage(1);
				var table = document.getElementById("cxkuang");  
				rowscolor(table);
			});
				//审核明细
				function formsubmit(obj){
					var bmids = obj.parentNode.id;
					/*// 定义一个对象
					var params = {"_extWhere" : "AND DATA_ID='"+bmids+"'"};
					var url = "TS_COMM_MIND.list.do?";
					var options = {"url" : url,"params" : params,"menuFlag" : 3};
					Tab.open(options);*/
					doPost('bmshmx.jsp', {bmidmx: bmids});
				}
				//导出
				//定义一个公共变量  当进行条件查询时  将 数据ID放入数组中
				function exportdata(obj,name){
					var checkboxchecked = $('input:checkbox[name='+name+']:checked');
					//没有选中
					var arrstring = "";
					if(checkboxchecked.length!=0){
						//定义变量 把id拼成 逗号分隔字符串
						//定义 变量 
							  var i=0;
					$('input:checkbox[name='+name+']:checked').each(function(){
							  i++;
							  var aa = $(this).val();
							  arrstring+=aa+",";
				 });
				} else{
					//导出所有数据
					var where5 = " AND XM_ID="+"'"+xmid+"'";
					var where6 = "AND SH_OTHER like '%"+user_code+"%'";
					var param={};
					param["where"]=where5+where6;
					param["servId"]=obj;
					param["xianei"]="belong";
					var result =FireFly.doAct("TS_BMLB_BM","getexportdata",param);
					arrstring=result.dataids;
					} 
				var whereData={};
				var data = {"_PK_":arrstring};
				data = jQuery.extend(data,whereData);
				
				window.open(FireFly.getContextPath() + '/' + obj + '.exp.do?data=' + 
				encodeURIComponent(jq.toJSON(data)));
				
				}

//--------------------------------------------------------------------------异议图标
function yiyi(obj){
		var a = obj.parentNode.id;
  			$("#filehistory").html("");
			var param = {};
			param["bmid"]=a;
			var fileresult = FireFly.doAct("TS_BMLB_BM","filehist",param);
			var fileEntity = fileresult.list;
			//审核理由
			var param1 = {};
			param1["bmid"]=a;
			var result = FireFly.doAct("TS_BMLB_BM","getLiyou",param1);
			var reason = result.liyou;
			$("#backliyou").text(reason);
			  $("#backliyou").attr("disabled","disabled");
				  //已有上传文件记录
				  //模态窗口 append上传的东西
				  for(var i=0;i<fileEntity.length;i++){
					  var fileid = fileEntity[i].FILE_ID;
					  $("#filehistory").append('<tr style="height:30px"><td style="width:30%"></td><td><a href="/file/'+fileid+'" onclick="xiazai()">'+fileid+'&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<image title="点击进行下载" src="/ts/image/u344.png"></image></a></td></tr>');
				  }
	}
//-------------------------------------------------------------------------------报名详细信息图标
function form2submit(obj){
	var bmid = obj.parentNode.id;
		var param = {};
		param["bmid"]=bmid;
	var result = FireFly.doAct("TS_BMLB_BM","getSingle",param);
	var pageEntity = result.list;
	if(pageEntity.length!=2){
	$(obj).attr("data-target","#userbminfo");
	 $("#ks_title").text(pageEntity[0].BM_TITLE);
	 $("#bm_name").text(pageEntity[0].BM_NAME);
	 $("#work_num").text(pageEntity[0].BM_CODE);
	 $("#phone_num").text(pageEntity[0].BM_PHONE);
	 $("#starttime").text(pageEntity[0].S_MTIME);
	 if(pageEntity[0].BM_SEX==0){
		 $("#gender").text("女");
	 }else{
		 $("#gender").text("男");
	 }
	 $("#belongto").text(pageEntity[0].ODEPT_NAME);
	}else{
		 $(obj).attr("data-target","");
	}
}
//左侧全选
function checkall(obj){
	if($(obj).prop("checked")){
		  var kslxArray = $('input:checkbox[name=pxcheckbox]');
		  for(var i=0;i<kslxArray.length;i++){
	     	kslxArray[i].checked=true;
	     	}
		}else{
			 var kslxArray = $('input:checkbox[name=pxcheckbox]');
			  for(var i=0;i<kslxArray.length;i++){
		     	kslxArray[i].checked=false;
		     	}
}
	
}
//右侧全选
function checkallright(obj){
	if($(obj).prop("checked")){
		  var kslxArray =$('input:checkbox[name=rtcheckbox]');
		  for(var i=0;i<kslxArray.length;i++){
	     	kslxArray[i].checked=true;
	     	}
		}else{
			 var kslxArray = $('input:checkbox[name=rtcheckbox]');
			  for(var i=0;i<kslxArray.length;i++){
		     	kslxArray[i].checked=false;
		     	}
}
	
}
function removeleft(){
	  $('input:checkbox[name=pxcheckbox]:checked').each(function(){
		  $(this).attr("name","rtcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px #f5f5f5">'+s+'</tr>')
	  });
	document.getElementById("daixuan").checked=false;
	 tuodongtr();
}
function removeright(){
	  $('input:checkbox[name=rtcheckbox]:checked').each(function(){
		  $(this).attr("name","pxcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable tbody").append('<tr style="border-bottom:solid 1px #f5f5f5">'+s+'</tr>')
	  });
	  document.getElementById("daixuanrt").checked=false;
	  tuodongtr();
}
//全部移动左边
$("#leftalla").click(function(){
	$('input:checkbox[name=pxcheckbox]').each(function(){
		  $(this).attr("name","rtcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px #f5f5f5">'+s+'</tr>')
	  });
	 tuodongtr();
});
$("#rightalla").click(function(){
	 $('input:checkbox[name=rtcheckbox]').each(function(){
		  $(this).attr("name","pxcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable tbody").append('<tr style="border-bottom:solid 1px #f5f5f5">'+s+'</tr>')
	  });
	 tuodongtr();
});

document.getElementById("imageleft").onmouseover = function(){
	 this.src = "/ts/image/2020.png";
	 
}
document.getElementById("imageleft").onmouseout = function() {
       this.src = '/ts/image/1124.png';
   }
document.getElementById("imageright").onmouseover = function(){
	 this.src = "/ts/image/2024.png";
}
document.getElementById("imageright").onmouseout = function() {
      this.src = '/ts/image/1348.png';
  }
document.getElementById("leftall").onmouseover = function(){
	 this.src = "/ts/image/2028.png";
}
document.getElementById("leftall").onmouseout = function() {
      this.src = '/ts/image/1440.png';
  }
document.getElementById("rightall").onmouseover = function(){
	 this.src = "/ts/image/2032.png";
}
document.getElementById("rightall").onmouseout = function() {
      this.src = '/ts/image/1552.png';
  }
//表格行拖动
$(document).ready(function(){
    var fixHelperModified = function(e, tr) {
                var $originals = tr.children();
                var $helper = tr.clone();
                $helper.children().each(function(index) {
                    $(this).width($originals.eq(index).width())
                });
                return $helper;
            },
            updateIndex = function(e, ui) {
                /*$('td.index', ui.item.parent()).each(function (i) {
                    $(this).html(i + 1);
                });*/
            };
        	
        	jq("#pxtable2 tbody").sortable({
        helper: fixHelperModified,
        stop: updateIndex
    }).disableSelection();
});
//找到所有的td  需要拖动的 td onmousevoer时间改变背景颜色
function tuodongtr(){
	var btns = document.getElementsByName('rtcheckbox');
	for(var z=0;z<btns.length;z++){
		var td = btns[z].parentNode;
		td.onmouseover = function() {
			 this.style.backgroundColor = '#e9e9e9';
		 }
		 td.onmouseout = function() {
		        this.style.background = 'white';
		    }

	}
}
//进行中 已结束 下拉框 onchange
function ztcx(obj){
	if(obj=="通过"){
		selectdata1(user_code,xmid,2,1)
	}else{
		selectdata1(user_code,xmid,3,1)
	}
}
var data_id = xmid;
//区分点击
var zzzz=0;
function importdata(obj){
	
	if(zzzz==0){
var eles=[ 
          [ 
            {ele:{type:'img',id:'img1',name:'files',title:'',extendAttr:{filed:'deatil_img',handle:'single',url:''}}}   
          ] 
      ]; 
      var bsForm = new BSForm({ eles: eles, autoLayout:true}).Render('excleupload11',function(bf){ 
     	 
          global.Fn.InitPlugin1('img','excleupload11',data_id); 
          
      }); 
      zzzz++;
	}
}
function deletefile(obj){
	var id =  obj.parentNode.parentNode.id;
	 //删除数据库
	 var param={};
	 param["SERV_ID"]="TS_BMLB_BM";
	 param["DATA_ID"]=data_id;
	 param["_PK_"]=id;
	 //删除
	FireFly.doAct("SY_COMM_FILE","delete",param);
	
	 //删除页面
	 document.getElementById(id).remove();
	 $("#uploadefile").attr("disabled","false");
 }
//导入数据库
$("#excelimp").click(function(){
	 var linum =  $("#excleupload").find('li').length;
	 //不为0 时可以删除
	 if(linum==0){
		 alert("文件不能为空");
	 }else{
		 var servid="";
		 var param = {};
		 var s = document.getElementById("shanchu").innerHTML;
		 param["fileId"]=s;
		 if(tabnum==1){
			  servid = "TS_BMSH_STAY";
		 }else if(tabnum==2){
			 servid="TS_BMSH_PASS"
		 }else{
			 servid="TS_BMSH_NOPASS"
		 }
		 param["serv_id"]=servid;
		 FireFly.doAct("TS_BMLB_BM","getDataFromXls",param,false,true);
		 $("#excleupload").modal('hide');
		 
		 new listPage().gotoPage(1);
	 }
});
//验证不是xls 或  xl 删除 
function deletefiles(s){
	 var param={};
	 param["SERV_ID"]="TS_BMSH_STAY";
	 param["DATA_ID"]=data_id;
	 param["_PK_"]=s;
	 //删除
	FireFly.doAct("SY_COMM_FILE","delete",param);
}
function deletefiles(){
	var linum =  $("#excleupload").find('li').length;
	if(linum!=0){
	var s =document.getElementById("shanchu").innerHTML;
	 var param={};
	 param["SERV_ID"]="TS_BMSH_STAY";
	 param["DATA_ID"]=data_id;
	 param["_PK_"]=s;
	 //删除
	FireFly.doAct("SY_COMM_FILE","delete",param);
	}
}
function returnz(){
	return zzzz;
}
function closemot(){
	deletefiles();	
	 document.getElementById("shanchu").parentNode.parentNode.remove();
}

function doPost(to, data) {  // to:提交动作（action）,data:参数
    var myForm = document.createElement("form");
    myForm.method = "post";
    myForm.action = to;
    myForm.target="_blank";
    for (var i in data) {
        var myInput = document.createElement("input");
        myInput.setAttribute("name", i);  // 为input对象设置name
        myInput.setAttribute("value", data[i]);  // 为input对象设置value
        myForm.appendChild(myInput);
    }
    document.body.appendChild(myForm);
    myForm.submit();
    document.body.removeChild(myForm);  // 提交后移除创建的form
}
//进行中  已结束 状态  onchange 事件
$("#zhuangtai1").change(function(){
	 new listPage().gotoPage(1);
});
$("#zhuangtai2").change(function(){
	 new listPage().gotoPage(1);
});




/**
 * 考试日历查看详情js
 */

//分页+查询
var listPage = function () {
    // 构建页码所需参数
    this.showPageNum = 5; // 最多显示的页码
    this.startNum = 1; // 中间页码的第一个页码
    this.endNum = this.startNum; // 中间页码的最后一个页码
};
 listPage.prototype.getListData = function (num) {
	 
	 var servid = "";
	 var myts="";
	 var where3 = "";
	if(tabnum==1){
	 servid = "TS_BMSH_STAY";
	 myts = $("#select1").children('option:selected').val();
	}else if(tabnum==2){
		 servid = "TS_BMSH_PASS";
		 myts = $("#select2").children('option:selected').val();
		 var  zhuangtai = $("#zhuangtai1").children('option:selected').val();
		 	if(zhuangtai!="全部"){
		 		if(zhuangtai=="进行中"){
		 			where3 = " AND SH_LEVEL!='1' ";
		 		}else{
		 			where3 = " AND SH_LEVEL='1' ";
		 		}
		 	}
	}else{
		servid = "TS_BMSH_NOPASS";
		myts = $("#select3").children('option:selected').val();
		var  zhuangtai = $("#zhuangtai2").children('option:selected').val();
	 	if(zhuangtai!="全部"){
	 		if(zhuangtai=="进行中"){
	 			where3 = " AND SH_LEVEL!='1' ";
	 		}else{
	 			where3 = " AND SH_LEVEL='1' ";
	 		}
	 	}
	}
	
	//param
	var where5 = " AND XM_ID="+"'"+xmid+"'";
	var where6 = "AND SH_OTHER like '%"+user_code+"%'";
	 var param = {};
	 param["shownum"]=myts;
	 param["nowpage"]=num;
	 param["xmid"]=xmid;
	 param["user_code"]=user_code;
	 param["where"]=where5+where6+where3;
     return FireFly.doAct(servid,"getUncheckList",param);
//     debugger;
 };
// 创建页面显示数据的主体
 listPage.prototype._bldBody = function (num) {
     var listData = this.getListData(num);
     this._lPage = listData._PAGE_;
     this._lData = listData.list;
     this.bldTable(listData);
     this.bldPage();
     var listPage=this;
   //查询条件按钮（设置查询考试名称和年份的条件）
  jQuery("#check1").unbind("click").click(function(){
		  myts = $("#select1").children('option:selected').val();
	  var name = $("xm1").val();
	    var renlicode =  $("rlzybm1").val();
	    var shjshu =  $("shjs").val();
	    var where1 = "";
	    var where2 = "";
	    var where3 = "";
	    var where4="";
	    var where5 = " AND XM_ID="+"'"+xmid+"'";
		var where6 = "AND SH_OTHER like '%"+user_code+"%'";
	   
	    if(jQuery.trim(shjshu)!=""){
	    	where3 = " AND SH_NODE like "+"'%"+shjshu+"%'";
	    }
     	if(jQuery.trim(name)!==""){
     		where1 = "AND BM_NAME like "+"'%"+name+"%'";
     	}
     	if(jQuery.trim(renlicode)!==""){
     		where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
     	}
     	var param={};
     	param["where"]=where1 + where2+where3+where5+where6;
    	 param["shownum"]=myts;
    	 param["nowpage"]=1;
    	 param["user_code"]=user_code;
     	//页面的输入查询条件放入传递的参数中
     	//获取到查询后的数据
     	var searchResult = FireFly.doAct("TS_BMSH_STAY","getUncheckList",param);
     	//将数据填入页面
     	listPage._lPage = searchResult._PAGE_;
     	listPage._lData = searchResult.list;
     	listPage.bldTable(searchResult);
     	listPage.bldPage();
        //table tr  隔行改变背景色
     	//var table = jQuery("#kstable");  
     	//rowscolor(table);
     	//去掉字符串中所有的空格方法
     	 function trimAll(str) {return str.replace(/\s+/g, "");}
     });
  jQuery("#check2").unbind("click").click(function(){
	  myts = $("#select2").children('option:selected').val();
	  var name = $("#xm2").val();
	    var renlicode =  $("#rlzybm2").val();
    var where1 = "";
    var where2 = "";
    var where3 = "";
    var where5 = " AND XM_ID="+"'"+xmid+"'";
	var where6 = "AND SH_OTHER like '%"+user_code+"%'";
 	if(jQuery.trim(name)!==""){
 		where1 = "AND BM_NAME like "+"'%"+name+"%'";
 	}
 	if(jQuery.trim(renlicode)!==""){
 		where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
 	}
	 
 	var  zhuangtai = $("#zhuangtai1").children('option:selected').val();
 	if(zhuangtai!="全部"){
 		if(zhuangtai=="进行中"){
 			where3 = " AND SH_LEVEL!='1' ";
 		}else{
 			where3 = " AND SH_LEVEL='1' ";
 		}
 	}
 	
 	var param={};
 	param["where"]=where1 + where2+where3+where5+where6;
	 param["shownum"]=myts;
	 param["nowpage"]=1;
	 param["xmid"]=xmid;
	 param["user_code"]=user_code;
 	//页面的输入查询条件放入传递的参数中
 	//获取到查询后的数据
 	var searchResult = FireFly.doAct("TS_BMSH_PASS","getUncheckList",param);
 	//将数据填入页面
 	listPage._lPage = searchResult._PAGE_;
 	listPage._lData = searchResult.list;
 	listPage.bldTable(searchResult);
 	listPage.bldPage();
    
 	 function trimAll(str) {return str.replace(/\s+/g, "");}
 });
  jQuery("#check3").unbind("click").click(function(){
	  myts = $("#select3").children('option:selected').val();
	  var name = $("#xm3").val();
	    var renlicode =  $("#rlzybm3").val();
    var where1 = "";
    var where2 = "";
    var where3 = "";
    var where5 = " AND XM_ID="+"'"+xmid+"'";
	var where6 = "AND SH_OTHER like '%"+user_code+"%'";
 	if(jQuery.trim(name)!==""){
 		where1 = "AND BM_NAME like "+"'%"+name+"%'";
 	}
 	if(jQuery.trim(renlicode)!==""){
 		where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
 	}
	 
 	var  zhuangtai = $("#zhuangtai2").children('option:selected').val();
 	if(zhuangtai!="全部"){
 		if(zhuangtai=="进行中"){
 			where3 = " AND SH_LEVEL!='1' ";
 		}else{
 			where3 = " AND SH_LEVEL='1' ";
 		}
 	}
 	
 	var param={};
 	param["where"]=where1 + where2+where3+where5+where6;
	 param["shownum"]=myts;
	 param["nowpage"]=1;
	 param["xmid"]=xmid;
	 param["user_code"]=user_code;
 	//页面的输入查询条件放入传递的参数中
 	//获取到查询后的数据
 	var searchResult = FireFly.doAct("TS_BMSH_NOPASS","getUncheckList",param);
 	//将数据填入页面
 	listPage._lPage = searchResult._PAGE_;
 	listPage._lData = searchResult.list;
 	listPage.bldTable(searchResult);
 	listPage.bldPage();
    
 	 function trimAll(str) {return str.replace(/\s+/g, "");}
 });
		//渲染隔行背景色
  		//var table = jQuery("#kstable");  
  		//     rowscolor(table);
     	//背景色渲染方法
  		//function rowscolor(table){
		//	 var rows =jQuery("#tbody_data").find("tr");  
		//   	 for(i = 0; i < rows.length; i++){  
		//      	 if(i % 2 == 0){  
		//      	    rows[i].style.backgroundColor = "Azure";  
		//      	  }  
		//      	} 
		//}
 };
 listPage.prototype.shaixuan=function(){
	 
 }
 /*跳转到指定页*/
 listPage.prototype.gotoPage = function (num) {
     this._bldBody(num);
 };
 /*上一页*/
 listPage.prototype.prePage = function() {
     var prePage = parseInt(this._lPage.NOWPAGE) - 1;
     var nowPage = "" + ((prePage > 0) ? prePage:1);
     this.gotoPage(nowPage);
 };
 /*下一页*/
 listPage.prototype.nextPage = function() {
     var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
     var pages = parseInt(this._lPage.PAGES);
     var nowPage = "" + ((nextPage > pages) ? pages:nextPage);
     this.gotoPage(nowPage);
//     debugger;
 };
 /*首页*/
 listPage.prototype.firstPage = function() {
     this.gotoPage(1);
 };
 /*末页*/
 listPage.prototype.lastPage = function() {
     this.gotoPage(this._lPage.PAGES);
 };
 listPage.prototype.bldTable = function (listData) {
	 debugger;
	 var table = "";
	 var checkbox = "";
	 if(tabnum==1){
		 table = "staytable";
		 checkbox="a";
	 }else if(tabnum==2){
		 table = "passtable";
		 checkbox="b";
	 }else{
		 table = "nopasstable";
		 checkbox="c";
	 }
	 
	 var where5 = " AND XM_ID="+"'"+xmid+"'";
	 var where6 = "AND SH_OTHER like '%"+user_code+"%'";
	 var param={};
	 param["where"]=where5+where6;
	 param["xmid"]=xmid;
	 param["user_code"]=user_code;
	 var result2 = FireFly.doAct("TS_BMSH_STAY","getAllData",param);
  	 nowlevel=result2.level;
  	 nodeid=result2.node_id;
  	 
	 var data = listData.list;
	 var pageEntity=JSON.parse(data);
		$("#"+table+" tbody").html("");
		
	    for(var i=0;i<pageEntity.length;i++){
	    	var strfirst = listData.first;
	    	var first = parseInt(strfirst);
	    	var xuhao = first+i;
	    	var id = pageEntity[i].SH_ID;
	    	//获取人力资源编码
	    	var BM_CODE = pageEntity[i].BM_CODE;
	    	//获取所有 user信息
	    	var paramuser={}
	    	paramuser["bm_code"]=BM_CODE;
	    	//获取对象信息
	    	var userinfo = FireFly.doAct("TS_BMSH_STAY","getUserInfo",paramuser);
	    	var userdata = userinfo.list;
	     	var userEntity = JSON.parse(userdata);
	     	var yiyi = pageEntity[i].BM_YIYI;
	     	var bmid = pageEntity[i].BM_ID;
	     	var shlevel = pageEntity[i].SH_LEVEL;
	     	//已有的审核层级大于 当前人的审核层级  当前审核人可以审核
	     	if(tabnum==3||tabnum==2){
	     	//审核level 审核层级大于当前审核层级的人没有审核权限  （TS_BMSH_PASS,TS_BMSH_NOPSS中的数据）
	     	if(yiyi==""){
	     		$("#"+table+" tbody").append('<tr style="height: 50px"><td style="text-align:center" ><input type="checkbox" SHLV='+shlevel+' name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+bmid+'" style="text-align: right"><a onclick = "formsubmit(this)" href="#"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal"  onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
	     	}else{
	     		$("#"+table+" tbody").append('<tr style="height: 50px"><td style="text-align:center" ><input style="text-align:center" type="checkbox"  SHLV='+shlevel+'  name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+yiyi+'" style="text-align: right"><a  onclick="yiyi(this)" data-toggle="modal" data-target="#yiyi" href="#"><image title="异议详细信息" src="/ts/image/u205.png"></image></a>&nbsp;&nbsp;<a onclick = "formsubmit(this)" href="#"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal" onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
	     	}
	     	}else{
	     		if(yiyi==""){
		     		$("#"+table+" tbody").append('<tr style="height: 50px"><td style="text-align:center" ><input style="text-align:center" type="checkbox"  SHLV='+shlevel+' name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+bmid+'" style="text-align: right"><a onclick = "formsubmit(this)" href="#"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal"  onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
		     	}else{
		     		$("#"+table+" tbody").append('<tr style="height: 50px"><td style="text-align:center" ><input style="text-align:center" type="checkbox"  SHLV='+shlevel+' name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+yiyi+'" style="text-align: right"><a  onclick="yiyi(this)" data-toggle="modal" data-target="#yiyi" href="#"><image title="异议详细信息" src="/ts/image/u205.png"></image></a>&nbsp;&nbsp;<a onclick = "formsubmit(this)" href="#"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal" onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
		     		
		     	}
	     	}
	     	var param1 = {};
	     	param1["user_code"]=user_code;
	     	var result3 = FireFly.doAct("TS_BMSH_PX","getShenheJson",param1);
	     	var pageEntity3 = result3.list;
	    	for(var j=0;j<pageEntity3.length;j++){
	    		var column = pageEntity3[j].PX_COLUMN;
	    		var fir = pageEntity[i][column];
	    		if(column=="SH_OTHER"){
	    			var paramcode = {};
	    			paramcode["codes"]=pageEntity[i][column];
	    			var resultname = FireFly.doAct("TS_BMSH_STAY","getusername",paramcode);
	    			fir = resultname.usernames;
	    		}     
	    		if(column=="SH_STATUS"){
	    			if(tabnum==1){
	    				fir="审核中...."
	    			}else if(tabnum==2){
	    				fir="审核通过"
	    			}else{
	    				fir="审核不通过";
	    			}
	    		}
	    		if(column=="BM_TYPE"){
	    			if(fir=="1"){
	    				BM_TYPE="初级";
	    			}else if(fir=="2"){
	    				BM_TYPE="中级";
	    			}else{
	    				BM_TYPE="高级";
	    			}
	    			fir = BM_TYPE;
	    		}
	    		if(column=="SH_USER"){
	    			fir = userEntity[0][column];
	    		}
	    		if(fir==null){
	    			fir = userEntity[0][column];
	    			if(column=="JOB_LB"){
	    				fir = pageEntity[i].BM_LB;
	    			}
	    			if(column=="JOB_XL"){
	    				fir = pageEntity[i].BM_XL;
	    			}
	    			if(column=="TONGYI"){
	    				fir=pageEntity[i].BM_CODE;
	    			}
	    			var BM_TYPE = "";
	    		}
	    		if(tabnum==1){
	    			if(column=="SH_LEVEL"){
	    				fir=nowlevel;
	    			}
	    		}
	    		if(fir==null){
	    			fir="";
	    		}
	    		$("#"+table+" tbody").find('tr:eq('+i+')').append('<td style="text-align:center">'+fir+'</td>');
	    	}
	    }
	    
	    var tab = document.getElementById(table);  
    	 rowscolor(tab);
    	 if(tabnum==1){
    		 Drag("dshtable",table);
    	 }
 }; 
 
 /*添加分页展示*/
 listPage.prototype.bldPage = function () {
     this._buildPageFlag = true;
     var _self = this;
     this._page = jQuery(".rhGrid-page");
     this._page.html('');
     //判断是否构建分页
     if (this._buildPageFlag === "false" || this._buildPageFlag === false) {
         this._page.addClass("rhGrid-page-none");
     } else if (this._lPage.PAGES === null) {
    	 //没有总条数的情况
         if (this._lPage.NOWPAGE > 1) {
        	 //上一页 {"ALLNUM":"1","SHOWNUM":"1000","NOWPAGE":"1","PAGES":"1"}
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
             this._page.append("<span class='disabled ui-corner-4'><</span>");
         }
         this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
         if (this._lData.length === this._lPage.SHOWNUM) {//下一页
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>></span>");
         }
     } else if (!jQuery.isEmptyObject(this._lPage)) {
         // 当前页码
         var currentPageNum = parseInt(this._lPage.NOWPAGE);
         // 总页数
         var sumPage = parseInt(this._lPage.PAGES);
         if (this.startNum + this.showPageNum < sumPage) {
             this.endNum = this.startNum + this.showPageNum
         } else {
             this.endNum = sumPage;
         }

         // 总条数
         var allNum = parseInt(this._lPage.ALLNUM);
         // 显示上一页
         if (currentPageNum !== 1) {
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                 _self.prePage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
             this._page.append("<span class='disabled ui-corner-4'><</span>");
         }
         // 移动页码
         if (currentPageNum > this.startNum + Math.floor((this.endNum - this.startNum) / 2)) {// 如果点击了后面的页码，则后移
             if (currentPageNum === sumPage) {// 点击了最后一页
                 this.endNum = sumPage;

                 if (this.endNum - this.showPageNum > 0) {
                     this.startNum = this.endNum - this.showPageNum;
                 } else {
                     this.startNum = 1;
                 }
             } else {
                 if (currentPageNum > this.showPageNum) {
                     this.endNum = currentPageNum + 1;
                     this.startNum = currentPageNum - this.showPageNum + 1;
                 }
             }
         } else {// 否则前移
             if (currentPageNum === 1) {// 点击了第一页
                 this.startNum = 1;
             } else {
                 this.startNum = currentPageNum - 1;
             }
             if (this.startNum + this.showPageNum < sumPage) {
                 this.endNum = this.startNum + this.showPageNum;
             } else {
                 this.endNum = sumPage;
             }
         }
         // 显示首页
         if (this.startNum !== 1) {
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>1</a>").click(function () {
                 _self.gotoPage(parseInt(jQuery(this).html()));
             })).append("...");
         }
         // 如果总页数小于本页显示的最大页码
         if (sumPage < this.endNum) {
             this.endNum = sumPage;
         }
         // 显示中间页码
         for (var i = this.startNum; i <= this.endNum; i++) {
             if (i === currentPageNum) {// 构建当前页
                 this._page.append("<span class='current ui-corner-4'>" + i + "</span>");
             } else {
                 this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + i + "</a>").click(function () {
                     _self.gotoPage(parseInt(jQuery(this).html()));
                 }));
             }
         }
         // 显示尾页
         if (sumPage > this.endNum) {
             this._page.append("...").append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + sumPage + "</a>").click(function () {
                 _self.lastPage();
             }));
         }
         // 显示下一页
         if (currentPageNum !== sumPage) {
//		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
             this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                 _self.nextPage();
             }));
         } else {
//		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
             this._page.append("<span class='disabled ui-corner-4'>></span>");
         }
         // 显示跳转到指定页码
         if (sumPage > 6) {
             this._page.append("<input class='toPageNum ui-corner-4' type='text' value=''/>").append(jQuery("<input class='toPageBtn' type='button' value='GO' />").click(function () {
                 try {
                     var val = parseInt(jQuery(this).prev().val());
                     if (val >= 1 && val <= sumPage) {
                         _self.gotoPage(val);
                     }
                 } catch (e) {
                     // 页码转换异常，忽略
                 }
             }));
         }
         //总条数显示
//	jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
         jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
     }
     return this._page;
 };
 //默认跳转到第一页
 new listPage().gotoPage(1);