_viewer=this;
$("#TS_BMSH_RULE_POST-POST_FUHAO_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_POST-POST_YEAR_FUHAO_div").find("span:last").find("div").css("width","50px");
$("#TS_BMSH_RULE_POST-POST_YEAR_div").css("margin-left","-35%");
$("#TS_BMSH_RULE_POST-POST_DUTIES_div").css("margin-left","-35%");

$("#TS_BMSH_RULE_POST-POST_DUTIES").css("cursor","pointer");
$("#TS_BMSH_RULE_POST-POST_DUTIES").unbind('click').bind('click',function(){
    var xls = $("#TS_BMSH_RULE_POST-POST_XL__NAME").val();
    var lb = $("#TS_BMSH_RULE_POST-POST_TYPE__NAME").val();
    var extwhere = "";
    var table = "TS_ORG_POSTION";
    var radioval  = $("input[name='TS_BMSH_RULE_POST-POST_ZD']:checked").val();
    if(radioval==2){
    	if(lb==""){
    		alert("请先选择类别");
    		return false;
    	}
    	if(xls == ""||xls=='全部'){
    		if(lb=="专业类"){
    			extwhere = 'AND POSTION_TYPE_NAME =^'+lb+'^';
    			table="TS_ORG_POSTION_K";
    		}else{
    			extwhere = 'AND POSTION_TYPE_NAME =^'+lb+'^';
    		}
    	}else{
    		extwhere = 'AND POSTION_TYPE_NAME =^'+lb+'^ AND POSTION_SEQUENCE=^'+xls+'^';
    	}
    }else{
    	table="TS_ORG_POSTION_K";
    }
   
	var configStr = table+",{'TARGET':'','SOURCE':'POSTION_SEQUENCE~POSTION_NAME'," +
	"'HIDE':'','EXTWHERE':'"+extwhere+" AND POSTION_LEVEL=^3^','TYPE':'single','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
		    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
		    	var ids = idArray.POSTION_NAME;
		    	$("#TS_BMSH_RULE_POST-POST_DUTIES").val(ids);
			}
		};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event,[],[0,495]);
    })
 /*   $('#TS_BMSH_RULE_POST-POST_TYPE').unbind("click").bind('change', function() {  
    	alert("a");
    	$("#TS_BMSH_RULE_POST-POST_DUTIES").val("");
    	$("#TS_BMSH_RULE_POST-POST_XL__NAME").val("");
});  
*/
   /* $("#TS_BMSH_RULE_POST-POST_TYPE").change(function(){
    	
    	$("#TS_BMSH_RULE_POST-POST_DUTIES").val("");
    	$("#TS_BMSH_RULE_POST-POST_XL__NAME").val("");
    })
    */
    
    
    