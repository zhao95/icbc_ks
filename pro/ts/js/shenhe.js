var xmid = jQuery("#xmid").val();
var user_code = jQuery("#user_code").val();
//隔一行 进行 背景颜色 渲染
function rowscolor(table){
	 var rows = table.getElementsByTagName("tr");  
	    for(i = 1; i < rows.length; i++){  
	        if(i % 2 == 0){  
	   
	            rows[i].style.backgroundColor = "Azure";  
	       }  
	    } 
}
//审核 筛选
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
     	//下拉框值
     	var type =  document.getElementById("zhuangtai1");
     	var index = type.selectedIndex;
     	var  zhuangtai = type.options[index].value;
     	if(zhuangtai!="全部"){
     		where4 = " AND BM_TYPE="+"'"+zhuangtai+"'";
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
     	param["where"]=where1 + where2 + where3 + where4+ where5 + where6;
	}else if(shenhe==2){
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
	    //判断 审核编码 是否在other字段中
	    if(name!=""){
	    	where1 = "AND BM_NAME like "+"'%"+name+"%'";
	    }
	    if(renlicode!=""){
	    	where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
	    }
	     	 where4 = " AND XM_ID="+"'"+xmid+"'";
	     	 where5 = "AND SH_OTHER like '%"+user_code+"%'";
	     	//每页条数
			var select = document.getElementById("select2");
			var index = document.getElementById("select2").selectedIndex;
			var myts = select.options[index].value;
			//拼接param
			param["nowpage"]=yema;
			param["shownum"]=myts;
	     	param["where"]=where1 + where2 + where3 + where4 + where5;
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
	    if(name!=""){
	    	where1 = "AND BM_NAME like "+"'%"+name+"%'";
	    }
	    if(renlicode!=""){
	    	where2 = " AND BM_CODE like "+"'%"+renlicode+"%'";
	    }
	  
	     	
	     	 where4 = " AND XM_ID="+"'"+xmid+"'";
	     	 where5 = "AND SH_OTHER like '%"+user_code+"%'";
	     	//每页条数
			var select = document.getElementById("select3");
			var index = document.getElementById("select3").selectedIndex;
			var myts = select.options[index].value;
			//拼接param
			param["nowpage"]=yema;
			param["shownum"]=myts;
	     	param["where"]=where1 + where2 + where3 + where4 + where5;
	}
	
	
		var first = (yema-1)*myts+1;
     	var result = FireFly.doAct(servId,"getUncheckList",param);
     	var result2 = FireFly.doAct(servId,"getAllData",param);
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
     	    	$("#"+table+" tbody").append('<tr style="height: 50px"><td><input type="checkbox" name="checkbox'+checkbox+'" value="'+id+'"></td><td style="text-align: center">'+xuhao+'</td><td style="text-align: center"><a onclick = "formsubmit('+i+')" href="bmshmx.jsp"><image title="审核详细信息" src="/ts/image/u2055.png"></image></a>&nbsp;&nbsp;<a onclick="form2submit('+i+')" href="#"><image title="报名详细信息" src="/ts/image/u1755.png"></image></a></td>');
     	    	for(var j=0;j<pageEntity3.length;j++){
     	    		var column = pageEntity3[j].PX_COLUMN;
     	    		var fir = pageEntity[i][column];
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

//项目筛选  能审核的项目   
function selectxmdata(user_code,yema){
	var name = document.getElementById("mc").value;
    var zuzhidanwei =  document.getElementById("zzdw").value;
    var where1 = "";
    var where2 = "";
    var where3="";
    if(name!=""){
    	where1 = "AND XM_NAME like"+"'%"+name+"%'";
    }
    if(zuzhidanwei!=""){
    	where2 = " AND XM_FQDW_NAME like"+"'%"+zuzhidanwei+"%'"
    }
     	//下拉框值
     	var type =  document.getElementById("zhuangtai");
     	var index = type.selectedIndex;
     	var  zhuangtai = type.options[index].value;
     	
     	if(zhuangtai!="全部"){
     		//进行状态匹配 进行中和一结束
     		where3 = " AND BM_ZHUANGTAI="+"'"+zhuangtai+"'";
     	}
     	//每页条数
		var select = document.getElementById("yema");
		 var index = document.getElementById("yema").selectedIndex;
		var myts = select.options[index].value;
		//重新计算 页码
		var param={};
		param["user_code"]=user_code;
		param["nowpage"]=yema;
		param["shownum"]=myts;
     	param["where"]=where1 + where2 + where3;
     	//在某一结点下 可以查看哪些机构的 审核  过滤出来
     	var result = FireFly.doAct("TS_XMGL","getUncheckList",param);
     	var result2 = FireFly.doAct("TS_XMGL","getShJsonList",param);
		//data为json格式字符串
		var data2 = result2.list;
     	var data = result.list;
     	//将json字符串 转换为 json对象
     	var first = (yema-1)*myts+1;
     	if(data.length==2){
     		$("#table tbody").html("");
     		$("#fenyeul").html("");
     		$("#fenyeul").append('<li><a href="#">&laquo;</a></li><li><a  href="#">&raquo;</a></li>');
     	}else{
     		
     		
     	var pageEntity=JSON.parse(data);
     	var pageEntity2 = JSON.parse(data2);
     	 //总条数/每页
		//页数
		 var yeshu = Math.floor(pageEntity2.length/myts);
		 var yushu = pageEntity2.length%myts;
		 $("#fenyeul").html("");
		
			 if(yushu!=0){
				 //余数为0
				 yeshu+=1;
			 }
			  for(var z=0;z<yeshu;z++){
			  var j=z+1;
				 if(z==0){
			$("#fenyeul").append('<li onclick="forward()"><a href="#">&laquo;</a></li><li id="yema1" class="active" onclick="chaxun('+j+')"><a href="#">1</a></li>');
				 }else {
			 $("#fenyeul").append('<li id="yema'+j+'" onclick=chaxun('+j+')><a  href="#">'+j+'</a></li>');
					 
				 }
				 
			 
		 }
     		  //最后一页
			 var last =yeshu+1;
			 $("#fenyeul").append(' <li id="yema'+last+'" onclick="backward('+last+')"><a  href="#">&raquo;</a></li>');
			 $("li.active").removeClass();
			 var s ="yema"+yema;
			 $("#"+s).addClass("active");
     	$("#table tbody").html("");
     	  for(var i=0;i<pageEntity.length;i++){
     		  
     		 var name = pageEntity[i].XM_NAME;
				var zzdw = pageEntity[i].XM_FQDW_NAME;
				var startdate = pageEntity[i].XM_START;
				var cjsj = pageEntity[i].S_ATIME;
				var enddate = pageEntity[i].XM_END;
				var xmtype = pageEntity[i].XM_TYPE;
				//创建新时间 判断 状态
				var str1 =startdate;
				var str2 = enddate;
				str1 = str1.replace(/-/g,"/");
				str2 = str2.replace(/-/g,"/");
				var state = "已结束";
				if(startdate!=""&&enddate!=""){
				var end = new Date(str2);
				var start = new Date(str1);
				var date = new Date();
			
				if(date.getTime()>start.getTime()&&date.getTime()<end.getTime()){
					
				 state = "报名审核";
				}else if(date.getTime()<start.getTime()){
					state = "未开始";
				}
				}
				//添加一行隐藏的项目id
				var id = pageEntity[i].XM_ID;
				var xuhao = first+i;
     		//为table重新appendtr
				if(state=="报名审核"){
     			$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="XM_ID'+i+'" >'+id+'</td><td class="rhGrid-td-hide" id="XM_TYPE'+i+'">'+xmtype+'</td><td><input type="button" onclick="tiaozhuan('+i+')" style="margin-top:7px;border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="审核"></input>&nbsp;&nbsp;<input type="button" style="border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="配置"></input>&nbsp;&nbsp;<input type="button" style="border:none;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="查看"></input></td></tr>');
	     		
				}else{
					$("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">'+xuhao+'</td><td class="indexTD" style="text-align: left">'+name+'</td><td class="rhGrid-td-left " icode="BM_ODEPT"style="text-align: left">'+zzdw+'</td><td class="rhGrid-td-left " icode="S_ATIME"style="text-align: left">'+cjsj+'</td><td class="rhGrid-td-left " icode="BM_STATE__NAME"style="text-align: left">'+state+'</td><td class="rhGrid-td-hide" id="BM_ID'+i+'" >'+id+'</td><td></td></tr>');	
				}
     	  }
     	
     	}	 
     	
   	 
}
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
                  console.log(arrn);
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
                          console.log(rows[i].cells[index].innerHTML);
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
	$("#staytable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 5%; text-align: center"><input type="checkbox" name="checkbox1" value="checkboxaa" onchange="change(this)"></th><th style="width: 5%; text-align: center">序号</th><th style="width: 5%; text-align: center">操作</th>');
	$("#passtable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 5%; text-align: center"><input type="checkbox" name="checkbox2" value="checkboxbb" onchange="change(this)"></th><th style="width: 5%; text-align: center">序号</th><th style="width: 5%; text-align: center">操作</th>');
	$("#nopasstable thead").append('<tr style="backGround-color:WhiteSmoke; height: 30px"><th style="width: 5%; text-align: center"><input type="checkbox" name="checkbox3" value="checkboxcc" onchange="change(this)"></th><th style="width: 5%; text-align: center">序号</th><th style="width: 5%; text-align: center">操作</th>');
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
	function dsha(){
		
		document.getElementById("dshimage").src="/ts/image/u1677.png";
		document.getElementById("dshsp").style.color="LightSeaGreen";
		document.getElementById("shtgsp").style.color="black";
		document.getElementById("shtgimage").src="/ts/image/u1681.png";
		document.getElementById("shwtgsp").style.color="black";
		document.getElementById("shwtgimage").src="/ts/image/u1695.png";
		selectdata1(user_code,xmid,1,1); 
	}
	function shtga(){
		document.getElementById("shtgsp").style.color="LightSeaGreen";
		document.getElementById("shtgimage").src="/ts/image/u2216.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		document.getElementById("shwtgsp").style.color="black";
		document.getElementById("shwtgimage").src="/ts/image/u1695.png";
		selectdata1(user_code,xmid,2,1); 
	}
	function shwtda(){
		   
		document.getElementById("shwtgsp").style.color="LightSeaGreen";
		document.getElementById("shwtgimage").src="/ts/image/u2813.png";
		document.getElementById("shtgsp").style.color="black";
		document.getElementById("shtgimage").src="/ts/image/u1681.png";
		document.getElementById("dshimage").src="/ts/image/u2212.png";
		document.getElementById("dshsp").style.color="black";
		selectdata1(user_code,xmid,3,1); 
	    
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
				//第二个td
				var sentds = document.getElementsByName("sentd");
				
				sentds[i].innerHTML='<input type="checkbox" id='+px_column+' onclick="changetd(this)" value='+name+' name="pxcheckbox">'+name+'';
				//都处于选中状态
				sentds[i].children[0].checked=true;
				//第一个td
				var value = sentds[i].children[0].value;
				var firtds = document.getElementsByName("firtd");
				for(var j=0;j<firtds.length;j++){
					
					if(value==firtds[j].children[0].value){
						firtds[j].children[0].checked=true;
					}
				}
		}
			var table = document.getElementById("pxtable");  
			rowscolor(table);
		
}

	
	//保存自定义显示 的 数据 
	function  savePX(){
		//获取所有的td
		var sentds = document.getElementsByName("sentd");
		//循环遍历所有的td
		var param={};
		var aa = false;
		for(var i=0;i<sentds.length;i++){
			if(sentds[i].innerHTML!=""){
			var id = sentds[i].children[0].id;
			var name = sentds[i].children[0].value;
			param["id"]=id;
			param["user_code"]=user_code;
			param["xuhao"]=i+1;
			param["name"]=name;
			FireFly.doAct("TS_BMSH_PX","paixu",param);
			}
		}
		$('#paixu').modal('hide');
		//重新append th
		appendTh(user_code);
		selectdata1(user_code,xmid,1,1);
	}
	
	//自定义显示列
	var tr;
	function xuanzhong(obj){
		//判断tr是否 已经被选中了
		if(!tr){
			//没有选中的
			//选中td 改变 背景颜色
			var s =  document.getElementById("sen"+obj)
			s.style.color="red";
			//获取tr
			tr=s.parentNode;
			return
		}
		//当前选中的 行数
		var j= tr.sectionRowIndex;
		//取消当前绑定
		$("#sen"+j).unbind("click"); 
		//将颜色改回
		document.getElementById("sen"+j).style.color="black";
		//选中td 改变 背景颜色
		var s =  document.getElementById("sen"+obj)
		s.style.color="red";
		//获取tr
		tr=s.parentNode;
}
	
	
	var t1=document.getElementById("pxtable");
	//上下移动
	function test(n){
		//选中空行不能 进行 移动
		
		//判断上一行或者下一行是否还有数据 没有的话return
			var z = tr.sectionRowIndex;
			var m= tr.sectionRowIndex+n;
			var tdnext = document.getElementById("sen"+m);
			var td = document.getElementById("sen"+z)
			if(td.innerHTML==""||tdnext.innerHTML==""){
				return;
			}
	//tr还没赋值  没有选中
	if(!tr){
	   alert('请选择一行');
	   return;
	}
	//当前行 和要移动到的行
	var j= tr.sectionRowIndex;
	var i=tr.sectionRowIndex+n;
	//顺序调整
	if(i>=0 && i<t1.rows.length){
	   var origin = document.getElementById('sen'+tr.sectionRowIndex);
	   var target = document.getElementById('sen'+i);
	   var tmp = origin.innerHTML;
	   origin.innerHTML=target.innerHTML;
	   target.innerHTML=tmp;
	}
	//解绑当前行
	$("#sen"+j).unbind("click"); 
	document.getElementById("sen"+j).style.color="black";
	//要移动的行默认选中
	 $("#sen"+i).trigger("click"); 
	 //全选中  checkbox
	 var sentds = document.getElementsByName("sentd");
		for(var i=0;i<sentds.length;i++){
			if(sentds[i].innerHTML!=""){
			sentds[i].children[0].checked=true;
			}
		}
	}
	

	//checkbox  选中和取消选中的时候    进行  联动选中或取消选中
		function changetd(obj){
			//选中时  将 此td的值放入  第二个td中 且  排序 从上往下
			if($(obj).prop("checked")){
				var row = obj.parentNode.parentNode;
				var tds=row.getElementsByTagName("td");
				var inner = tds[0].innerHTML;
				var sentds = document.getElementsByName("sentd");
				//排序
				for(var i=0;i<sentds.length;i++){
					if(sentds[i].innerHTML==""){
					sentds[i].innerHTML = inner;
					sentds[i].children[0].checked=true;
					return;
					}
				}
			}else{
				//左边td不选中
				var sentds = document.getElementsByName("sentd");
				var firtds = document.getElementsByName("firtd");
				//checckbox  取消选中   第二个td删除 数据  且重新排序
				for(var i=0;i<sentds.length;i++){
				if(obj.parentNode.innerHTML==sentds[i].innerHTML){
					for(var a=0;a<firtds.length;a++){
						if(firtds[a].innerHTML==sentds[i].innerHTML){
							firtds[a].children[0].checked=false;
						}
					}
					sentds[i].innerHTML="";
					//排序
					for(var j=i+1;j<sentds.length;j++){
						if(sentds[j].innerHTML==""){
						}else{
							sentds[i].innerHTML=sentds[j].innerHTML;
							sentds[j].innerHTML="";
							sentds[i].children[0].checked=true;
							i++;
						}
					}
					return;
				}
					
				}
				// 右边td不选中 
				var row = obj.parentNode.parentNode;
				var tds=row.getElementsByTagName("td");
				var inner = tds[0].innerHTML;
				for(var i=0;i<sentds.length;i++){
					if(sentds[i].innerHTML==inner){
					sentds[i].innerHTML = "";
					for(var j=i+1;j<sentds.length;j++){
						if(sentds[j].innerHTML==""){
						}else{
							sentds[i].innerHTML=sentds[j].innerHTML;
							sentds[j].innerHTML="";
							i++;
						}
					}
					return;
					}
				}
			}
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
    	$("#pxtable tbody").append('<tr><td id="fir'+i+'" name="firtd" style="padding-left:5px;text-align:left;height:30px;font-size:20px"><input type="checkbox" id='+px_column+' onclick="changetd(this)" value='+name+'  name="pxcheckbox">'+name+'</td><td id="sen'+i+'" onclick="xuanzhong('+i+')" style="text-align:center;font-size:20px" name="sentd"></td></tr>');
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
			selectdata1(user_code,xmid,tab,ym);
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
			jQuery(function(){
				appendTh(user_code);
				selectdata1(user_code,xmid,1,1);
				firall();
				var table = document.getElementById("cxkuang");  
				rowscolor(table);
			});
				//审核明细
				function formsubmit(obj){
					var id = "BM_ID"+obj;
					var bmid = document.getElementById(id).innerHTML;
					document.getElementById("bmid").value=bmid;
					$("#form1").submit();
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
				encodeURIComponent(jQuery.toJSON(data)));
				
				}
			