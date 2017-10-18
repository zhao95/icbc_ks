var _viewer = this;
$(".rhGrid").find("tr").unbind("dblclick");

// _viewer._advancedSearchBtn.unbind("click").bind("click", function(event) {
// alert(1);
// });

_viewer.getBtn("addFun").unbind("click").bind("click", function(event) {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	alert(pkCodes);
});

_viewer.getBtn("delFun").unbind("click").bind("click", function(event) {
	var pkCodes = _viewer.grid.getSelectPKCodes();//获取主键值
	alert(pkCodes);
});

$(".rh-advSearch-table").find("label[value='ROLE_ID']").text("角色功能");
$(".rh-advSearch-table").find("label[value='ROLE_PID']").text("已有功能");
$(".rh-advSearch-table").find("label[value='ROLE_DCODE']").text("机构");

$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-btn").unbind("click").bind("click",function(event) {
			var roleName = $("#rh-advanceSearch-TS_PVLG_ROLE_UPDATEROLE_NAME")
					.val();
			// var isHave
			// =$(".rh-advSearch-check").find("input[type='checkbox']:checked").val();
			// alert(isHave);
});

var height = jQuery(window).height()-200;
var width = jQuery(window).width()-200;
$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-table").find("a.rh-advSearch-sel").eq(0).unbind("click").bind("click",function(event){
    var temp = {"act":UIConst.ACT_CARD_ADD,"sId":"TS_PVLG_ROLE_UPDATE_FUNS","parHandler":_viewer,"widHeiArray":[width,height],"xyArray":[100,100]};
    var cardView = new rh.vi.cardView(temp);
    cardView.show();
});

$("#TS_PVLG_ROLE_UPDATE .rh-advSearch-table").find("a.rh-advSearch-sel").eq(1).unbind("click").bind("click",function(event){
	alert(3);
});