var xmid = jq("#xmid").val();
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
		 this.style.backgroundColor = 'red';
	 }
	 btns[z].onmouseout = function() {
	        this.style.background = 'lightseagreen';
	    }

}
//审核 筛选
var nowlevel = 0;
var nodeid = "";
function selectdata1(user_code,xmid,shenhe,yema){
	//判断审核是待审核  还是 审核通过 还是 审核未通过  然后拼接不同的where 条件 查询
	var param={};
	param["user_code"]=user_code;
	//默认第一个table
	var table = "staytable";
	var fenye="fenyeu1";
	var servId="TS_BMSH_STAY";
	var checkbox="a";
	var diftid="A";
	if(shenhe==1){
	var name = document.getElementById("xm1").value;
    var renlicode =  document.getElementById("rlzybm1").value;
    var shjshu =  document.getElementById("shjs").value;
    var where1 = "";
    var where2 = "";
    var where3 = "";
    var where4="";
    if(name!=""){
    	where1 = "AND BM_NAME like "+"'%"+name+"%'";
    }
    if(renlicode!=""){
    	where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
    }
    if(shjshu!=""){
    	where3 = " AND SH_NODE like "+"'%"+shjshu+"%'";
    }
     	
     	
     	var where5 = " AND XM_ID="+"'"+xmid+"'";
     	var where6 = "AND SH_OTHER like '%"+user_code+"%'";
     	//每页条数
		var select = document.getElementById("select1");
		var index = select.selectedIndex;
		var myts = select.options[index].value;
		//param
		param["nowpage"]=yema;
		param["shownum"]=myts;
     	param["where"]=where1 + where2 + where3 + where5 + where6;
	}else if(shenhe==2){
		//下拉框值
     	
		diftid="B";
		checkbox="b";
		servId = "TS_BMSH_PASS";
		fenye="fenyeu2";
		table = "passtable";
		var name = document.getElementById("xm2").value;
	    var renlicode =  document.getElementById("rlzybm2").value;
	    var where1 = "";
	    var where2 = "";
	    var where3 = "";
	    var where4 = "";
	    var where5 = "";
	    var where6 = "";
	    //判断 审核编码 是否在other字段中
	    if(name!=""){
	    	where1 = "AND BM_NAME like "+"'%"+name+"%'";
	    }
	    if(renlicode!=""){
	    	where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
	    }
	     	 where4 = " AND XM_ID="+"'"+xmid+"'";
	     	 where5 = "AND SH_OTHER like '%"+user_code+"%'";
	     	 
	     	var type =  document.getElementById("zhuangtai1");
	     	var index = type.selectedIndex;
	     	var  zhuangtai = type.options[index].value;
	     	if(zhuangtai!="全部"){
	     		if(zhuangtai=="进行中"){
	     			where6 = " AND SH_LEVEL!='1'";
	     		}else{
	     			where6 = " AND SH_LEVEL='1'";
	     		}
	     	}
	     	 
	     	//每页条数
			var select = document.getElementById("select2");
			var index = document.getElementById("select2").selectedIndex;
			var myts = select.options[index].value;
			//拼接param
			param["nowpage"]=yema;
			param["shownum"]=myts;
	     	param["where"]=where1 + where2 + where3 + where4 + where5+where6;
	}else if(shenhe==3){
		diftid="C";
		checkbox="c";
		servId = "TS_BMSH_NOPASS";
		fenye="fenyeu3";
		table = "nopasstable";
		var name = document.getElementById("xm3").value;
	    var renlicode =  document.getElementById("rlzybm3").value;
	    var where1 = "";
	    var where2 = "";
	    var where3 = "";
	    var where4 = "";
	    var where5 = "";
	    var where6="";
	    if(name!=""){
	    	where1 = "AND BM_NAME like "+"'%"+name+"%'";
	    }
	    if(renlicode!=""){
	    	where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
	    }
	  
	     	 where4 = " AND XM_ID="+"'"+xmid+"'";
	     	 where5 = "AND SH_OTHER like '%"+user_code+"%'";
	     	 
	     	var type =  document.getElementById("zhuangtai2");
	     	var index = type.selectedIndex;
	     	var  zhuangtai = type.options[index].value;
	     	if(zhuangtai!="全部"){
	     		if(zhuangtai=="进行中"){
	     			where6 = " AND SH_LEVEL!='1'";
	     		}else{
	     			where6 = " AND SH_LEVEL='1'";
	     		}
	     	}
	     	 
	     	//每页条数
			var select = document.getElementById("select3");
			var index = document.getElementById("select3").selectedIndex;
			var myts = select.options[index].value;
			//拼接param
			param["nowpage"]=yema;
			param["shownum"]=myts;
	     	param["where"]=where1 + where2 + where3 + where4 + where5+where6;
	}
		var first = (yema-1)*myts+1;
		param["xmid"]=xmid;
     	var result = FireFly.doAct(servId,"getUncheckList",param);
     	var result2 = FireFly.doAct(servId,"getAllData",param);
     	nowlevel=result2.level;
     	nodeid=result2.node_id;
     	//查找排序顺序   根据排序号进行排序
     	var param1 = {};
     	param1["user_code"]=user_code;
     	var result3 = FireFly.doAct("TS_BMSH_PX","getShenheJson",param1);
     	var data3 = result3.list;
     	var pageEntity3 = JSON.parse(data3);
		//data为json格式字符串
		var data2 = result2.list;
     	var data = result.list;
     	//将json字符串 转换为 json对象
     	if(data.length==2){
     		$("#"+table+" tbody").html("");
     		$("#"+fenye).html("");
     		$("#"+fenye).append('<li><a href="#">&laquo;</a></li><li><a  href="#">&raquo;</a></li>');
     	}else{
     		var pageEntity=JSON.parse(data);
     		var pageEntity2 = JSON.parse(data2);
     		$("#"+table+" tbody").html("");
     		
     	    for(var i=0;i<pageEntity.length;i++){
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
     	     	if(yiyi==""){
     	     		$("#"+table+" tbody").append('<tr style="height: 50px"><td><input type="checkbox" name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+bmid+'" style="text-align: center"><a onclick = "formsubmit(this)" href="bmshmx.jsp"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal"  onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
     	     	}else{
     	     		$("#"+table+" tbody").append('<tr style="height: 50px"><td><input type="checkbox" name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td id="'+yiyi+'" style="text-align: center"><a  onclick="yiyi(this)" data-toggle="modal" data-target="#yiyi" href="#"><image title="异议详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a onclick = "formsubmit(this)" href="bmshmx.jsp"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a data-toggle="modal" onclick="form2submit(this)" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
     	     		
     	     	}
     	    	for(var j=0;j<pageEntity3.length;j++){
     	    		var column = pageEntity3[j].PX_COLUMN;
     	    		var fir = pageEntity[i][column];
     	    		if(column=="SH_OTHER"){
     	    			var paramcode = {};
     	    			paramcode["codes"]=pageEntity[i][column];
     	    			var resultname = FireFly.doAct("TS_BMSH_STAY","getusername",paramcode);
     	    			fir = resultname.usernames;
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
     	    	
     	    		if(column=="SH_LEVEL"){
     	    			fir=nowlevel;
     	    		}
     	    		if(fir==null){
     	    			fir="";
     	    		}
     	    		$("#"+table+" tbody").find('tr:eq('+i+')').append('<td>'+fir+'</td>');
     	    	}
     	    	
     	 //总条数/每页
		//页数
		 var yeshu = Math.floor(pageEntity2.length/myts);
		 var yushu = pageEntity2.length%myts;
		 $("#"+fenye).html("");
			 if(yushu!=0){
				 //余数为0
				 yeshu+=1;
			 }
			  for(var z=0;z<yeshu;z++){
				  
			  var j=z+1;
			  var id = diftid+j;
				 if(z==0){
			$("#"+fenye).append('<li onclick="forward('+shenhe+')"><a href="#">&laquo;</a></li><li id="yema'+diftid+'1" class="active" onclick="chaxun('+shenhe+','+j+')"><a href="#">1</a></li>');
				 }else {
			 $("#"+fenye).append('<li id="yema'+id+'" onclick=chaxun('+shenhe+','+j+')><a href="#">'+j+'</a></li>');
					 
				 }
			 
		 }
     		  //最后一页
			 var last =yeshu+1;
     	
			 $("#"+fenye).append(' <li id="yema'+last+'" onclick="backward('+shenhe+','+last+')"><a  href="#">&raquo;</a></li>');
			 //当前选中的页码  移除active
			 $("li.active").removeClass();
			 var s ="yema"+diftid+yema;
			 $("#"+s).addClass("active");	 
     	}	 
     	
     	var tab = document.getElementById(table);  
     	 rowscolor(tab);
     	 if(shenhe==1){
     		 Drag("dshtable",table);
     	 }
}
}
//--------------------------------bmshs.jsp调用

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
	var data = result.list;
	var pageEntity=JSON.parse(data);
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
		selectdata1(user_code,xmid,1,1); 
		tabnum=1;
	}
	function shtga(){
		document.getElementById("shtgsp").style.color="LightSeaGreen";
		document.getElementById("shtgimage").src="/ts/image/u2216.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		document.getElementById("shwtgsp").style.color="black";
		document.getElementById("shwtgimage").src="/ts/image/u1695.png";
		selectdata1(user_code,xmid,2,1); 
		tabnum=2;
	}
	function shwtda(){
		document.getElementById("shwtgsp").style.color="LightSeaGreen";
		document.getElementById("shwtgimage").src="/ts/image/u2813.png";
		document.getElementById("shtgsp").style.color="black";
		document.getElementById("shtgimage").src="/ts/image/u1681.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		selectdata1(user_code,xmid,3,1); 
		tabnum=3;
	}
//---------------------------------------------------------------------------------自定义显示列
	//自定义显示列数据进行回显
	function zdyl(){
		var param={};
		param["user_code"]=user_code;
		var result = FireFly.doAct("TS_BMSH_PX","getUserList",param);
		var data = result.list;
		var pageEntity = JSON.parse(data);
		
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
						 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px lightgray">'+s+'</tr>')
					}
				}
		}
			
			 tuodongtr();
}
	
	//保存自定义显示 的 数据 
	function  savePX(){
		//获取所有的td
		var sentds = document.getElementsByName("rtcheckbox");
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
		selectdata1(user_code,xmid,1,1);
	}
	
	
	//第一个td显示所有的数据
function firall(){
	var param1={};
    var result1 = FireFly.doAct("TS_BMSH_PX","getShenhelist",param1);
	var data1= result1.list;
	var pageEntity1 = JSON.parse(data1);
    for(var i=0;i<pageEntity1.length;i++){
    	var name = pageEntity1[i].PX_NAME;
    	var px_column= pageEntity1[i].PX_COLUMN;
    	$("#pxtable tbody").append('<tr style="border-bottom:solid 1px lightgray"><td id="fir'+i+'" name="firtd" style="padding-left:5px;text-align:left;height:20px;font-size:13px"><input style="width:50px;height=50px" type="checkbox" id='+px_column+' onclick="changetd(this)" value='+name+'  name="pxcheckbox">'+name+'</td></tr>');
    }

}
//-----------------------------------------------------------------------------------------------------审核按钮
		//checkbox全选   第一个table
		function shenheA(){
			//是否有checkbox被选中
			 var checkboxchecked = $('input:checkbox[name=checkboxa]:checked');
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheA").removeAttr("data-target");
			}else{
				$("#shenheA").attr("data-target",'#tiJiao');
			    document.getElementById("mokuai").value="A";
			}
		}
		function change(obj){
			if($(obj).prop("checked")){
				  var kslxArray = document.getElementsByName("checkboxa");
				  for(var i=0;i<kslxArray.length;i++){
			     	kslxArray[i].checked=true;
			     	}
				}else{
					 var kslxArray = document.getElementsByName("checkboxa");
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
			 var checkboxchecked = $('input:checkbox[name=checkboxb]:checked');
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheB").removeAttr("data-target");
			}else{
				$("#shenheB").attr("data-target",'#tiJiao');
			document.getElementById("mokuai").value="B";
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
			//是否有checkbox被选中
			 var checkboxchecked = $('input:checkbox[name=checkboxc]:checked');
			if(checkboxchecked.length==0){
				alert("您未选中任何记录,请选择");
				$("#shenheC").removeAttr("data-target");
			}else{
				$("#shenheC").attr("data-target",'#tiJiao');
			    document.getElementById("mokuai").value="C";
			}
		}
		function changec(obj){
			if($(obj).prop("checked")){
				  var kslxArray = document.getElementsByName("checkboxc");
				  for(var i=0;i<kslxArray.length;i++){
			     	kslxArray[i].checked=true;
			     	}
				}else{
					 var kslxArray = document.getElementsByName("checkboxc");
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
			selectdata1(user_code,xmid,obj,1);
		}
		//查询按钮
		function xzcu(obj){
			//跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
			//判断i
			selectdata1(user_code,xmid,obj,1);
		}
		
		function fanhui(){
			window.history.go(-1);
		}
		
		//上一页 按钮
		function forward(obj){
			//判断obj
			
			
			//获取页码 数    li active  三种情况              
			var yema = document.querySelectorAll("li[class='active']")[0].innerText;
			if(yema==1){
				return false;
			}else{
				//要显示的页码
				var yema1 = yema-1;
				selectdata1(user_code,xmid,obj,yema1);
				
			}
		}
		//下一页按钮
		function backward(obj,last){
			//获取页码 数//获取页码 数    li active  三种情况              
			var yema = document.querySelectorAll("li[class='active']")[0].innerText;
			var lastyema = yema-1+2;
			if(lastyema==last){
				return false;
			}else{
				//要显示的页码数
				 var yema1 = yema-1+2;
				
				selectdata1(user_code,xmid,obj,yema1);
				
		     	  
			}
		}
		//点击第几页跳转
		 function chaxun(obj,i){
			if(obj==1){
			var id = "yemaA"+i;
			//点击第几页
			var ym = document.getElementById(id).innerText;
			//传入 要显示的页码数即可
			selectdata1(user_code,xmid,obj,ym);
			}else if(obj==2){
				var id = "yemaB"+i;
				//点击第几页
				var ym = document.getElementById(id).innerText;
				//传入 要显示的页码数即可
				selectdata1(user_code,xmid,obj,ym);
			}else{
				var id = "yemaC"+i;
				//点击第几页
				var ym = document.getElementById(id).innerText;
				//传入 要显示的页码数即可
				selectdata1(user_code,xmid,obj,ym);
			}
			
		} 
		
//---------------------------------------------------------------------------------------------------------
			//加载完后自动调用
			jq(function(){
				appendTh(user_code);
				selectdata1(user_code,xmid,1,1);
				firall();
				var table = document.getElementById("cxkuang");  
				rowscolor(table);
			});
				//审核明细
				function formsubmit(obj){
					var bmid = obj.parentNode.id;
					document.getElementById("bmid4").value=bmid;
					document.getElementById("form5").submit();
				}
				//导出
				//定义一个公共变量  当进行条件查询时  将 数据ID放入数组中
				function exportdata(obj,name){
					var checkboxchecked = $('input:checkbox[name='+name+']:checked');
					//没有选中
					if(checkboxchecked.length!=0){
						//定义变量 把id拼成 逗号分隔字符串
					var arrstring = "";
						//定义 变量 
							  var i=0;
					$('input:checkbox[name='+name+']:checked').each(function(){
							  i++;
							  var aa = $(this).val();
							  arrstring+=aa+",";
				 });
				} else{
					var param={};
					param["servId"]=obj;
					var result = FireFly.doAct(obj,"reSids",param);
					arrstring = result.string;
					arrstring=arrstring.substring(1,arrstring.length - 1); 
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
			var filedata = fileresult.list;
			var fileEntity = JSON.parse(filedata);
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
	var data = result.list;
	if(data.length!=2){
	$(obj).attr("data-target","#userbminfo");
	var pageEntity = JSON.parse(data);
	 $("#ks_title").text(pageEntity[0].BM_TITLE);
	 $("#bm_name").text(pageEntity[0].BM_NAME);
	 $("#work_num").text(pageEntity[0].BM_CODE);
	 $("#phone_num").text(pageEntity[0].BM_PHONE);
	 $("#starttime").text(pageEntity[0].S_MTIME);
	 if(pageEntity[0].BM_SEX==0){
		 $("#gender").text("女");
	 }else if(pageEntity[0].BM_SEX==1){
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
		  var kslxArray = document.getElementsByName("pxcheckbox");
		  for(var i=0;i<kslxArray.length;i++){
	     	kslxArray[i].checked=true;
	     	}
		}else{
			 var kslxArray = document.getElementsByName("pxcheckbox");
			  for(var i=0;i<kslxArray.length;i++){
		     	kslxArray[i].checked=false;
		     	}
}
	
}
//右侧全选
function checkallright(obj){
	if($(obj).prop("checked")){
		  var kslxArray = document.getElementsByName("rtcheckbox");
		  for(var i=0;i<kslxArray.length;i++){
	     	kslxArray[i].checked=true;
	     	}
		}else{
			 var kslxArray = document.getElementsByName("rtcheckbox");
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
		 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px lightgray">'+s+'</tr>')
	  });
	document.getElementById("daixuan").checked=false;
	 tuodongtr();
}
function removeright(){
	  $('input:checkbox[name=rtcheckbox]:checked').each(function(){
		  $(this).attr("name","pxcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable tbody").append('<tr style="border-bottom:solid 1px lightgray">'+s+'</tr>')
	  });
	  document.getElementById("daixuanrt").checked=false;
	  tuodongtr();
}
//全部移动左边
jq("#leftalla").click(function(){
	$('input:checkbox[name=pxcheckbox]').each(function(){
		  $(this).attr("name","rtcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable2 tbody").append('<tr style="border-bottom:solid 1px lightgray">'+s+'</tr>')
	  });
	 tuodongtr();
});
jq("#rightalla").click(function(){
	 $('input:checkbox[name=rtcheckbox]').each(function(){
		  $(this).attr("name","pxcheckbox");
		 var s =  this.parentNode.parentNode.innerHTML;
		 this.parentNode.parentNode.remove();
		 $("#pxtable tbody").append('<tr style="border-bottom:solid 1px lightgray">'+s+'</tr>')
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
jq(document).ready(function(){
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
			 this.style.backgroundColor = 'red';
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
var z=0;
function importdata(obj){
	
	if(z==0){
var eles=[ 
          [ 
            {ele:{type:'img',id:'img1',name:'files',title:'',extendAttr:{filed:'deatil_img',handle:'single',url:''}}}   
          ] 
      ]; 
      var bsForm = new BSForm({ eles: eles, autoLayout:true}).Render('excleupload11',function(bf){ 
     	 
          global.Fn.InitPlugin1('img','excleupload11',data_id); 
          
      }); 
      z++;
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
jq("#excelimp").click(function(){
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
		 FireFly.doAct("TS_BMLB_BM","getDataFromXls",param);
		 $("#excleupload").modal('hide');
		 selectdata1(user_code,xmid,tabnum,1); 
		 
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
	return z;
}
function closemot(){
	deletefiles();	
	 document.getElementById("shanchu").parentNode.parentNode.remove();
}