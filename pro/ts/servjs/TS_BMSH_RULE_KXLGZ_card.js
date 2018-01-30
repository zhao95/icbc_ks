_viewer=this;
$("#TS_BMSH_RULE_KXLGZ-POSTION_NAME").css("cursor","pointer");
$("#TS_BMSH_RULE_KXLGZ-POSTION_NAME").unbind('click').bind('click',function(){
    var xls = $("#TS_BMSH_RULE_KXLGZ-POSTION_SEQUENCE__NAME").val();
    var lb = $("#TS_BMSH_RULE_KXLGZ-POSTION_TYPE__NAME").val();
    var extwhere = "";
    if(lb==""){
    	alert("请先选择类别");
    	return false;
    }
    if(xls == ""){
    	extwhere = 'AND POSTION_TYPE_NAME =^'+lb+'^';
    }else{
    	extwhere = 'AND POSTION_TYPE_NAME =^'+lb+'^ AND POSTION_SEQUENCE=^'+xls+'^';
    }
	var configStr = "TS_ORG_POSTION,{'TARGET':'','SOURCE':'POSTION_SEQUENCE~POSTION_NAME'," +
	"'HIDE':'','EXTWHERE':'"+extwhere+" AND POSTION_LEVEL=^3^','TYPE':'single','HTMLITEM':''}";
	var options = {
			"config" :configStr,
			"parHandler":_viewer,
			"formHandler":_viewer.form,
		    "replaceCallBack":function(idArray) {//回调，idArray为选中记录的相应字段的数组集合
		    	var ids = idArray.POSTION_NAME;
		    	$("#TS_BMSH_RULE_KXLGZ-POSTION_NAME").val(ids);
			}
		};
	var queryView = new rh.vi.rhSelectListView(options);
	queryView.show(event,[],[0,495]);
    })