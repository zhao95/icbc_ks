var bmid = $("prbmid").val();
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
		 var shstate = "";
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
		 var bmshstate = result.bmbean.BM_SH_STATE;
		 if(bmshstate==1){
			 shstate = "恭喜您！审核已通过，请及时请假参加考试";
		 }else if(bmshstate==3){
			 shstate="不好意思！审核未通过，如有需要请及时上诉";
		 }else if(bmshstate==0){
			 shstate="已提交上诉申请，请耐心等待......";
		 }else if(bmshstate==2){
			 shstate="不好意思！审核未通过，您未获得考试资格";
		 }
		 var chexiao = result.bmbean.BM_STATE;
		 if(2==chexiao){
			 shstate = "您已取消此次考试，考试不能恢复，请等待下次考试......";
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