var bmid = $("#prbmid").val();
var shstate = $("#shstatus").val();
//查询所需 报名信息 展示给前台
function init(){
	var param={};
	param["bmids"]=bmid;
	var result = FireFly.doAct("TS_BMLB_BM","getShowData",param);
	//项目名称
	var xmname = result.xmname;
	//开始结束时间
	var xm_state = result.bmglbean.BM_START;
	var xm_end = result.bmglbean.BM_END;
	//考试须知
	var ksxz = result.bmglbean.BM_KSXZ;
	$("#xmname").html(xmname);
	$("#bmtime").html("报名时间："+xm_state+"~~"+xm_end);
	$("#ksxzs").html(ksxz);
		 var jb = "";
		 var lbname = result.bmbean.BM_LB;
		 var  xlname= result.bmbean.BM_XL
		 var  mkname = result.bmbean.BM_MK
		 var jibietype = result.bmbean.BM_TYPE;
		 //考试名称
		 var bm_title = result.bmbean.TITLE;
		 if("1"==jibietype){
			 jb="初级";
		 }else if("2"==jibietype){
			 jb="中级";
		 }else if("3"==jibietype){
			 jb="高级";
		 }
		
		 $("#tableid tbody").html("");
		 var trs = ['<tr>',
		             '<td width="10%">' + lbname + '</td>',
		             '<td width="15%">' + xlname + '</td>',
		             '<td width="15%">' + mkname + '</td>',
		             '<td width="15%">' + jb + '</td>',
		             '<td>' + shstate + '</td>',
		             '</tr>'].join("");
		 $("#tableid tbody").append(trs);
			if(lbname==""){
				//非资格
				$("#tableid").html("");
				$("#tableid").append("<thead><tr><th>考试名称</th><th>考试时间</th><th>审核结果</th></tr></thead>");
				$("#tableid").append('<tbody><tr><td>'+bm_title+'</td><td></td><td>'+shstate+'</td></tr></tbody>')
			}
}