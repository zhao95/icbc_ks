var _viewer = this;

_viewer.resetGridClickParams = function(node) {
	//console.log($.toJSON(node));
	var params = {};
	if (node) {
		params.sId = node.SERV_ID;
		params.pId = node.TODO_OBJECT_ID1;
		params.byIdParams = {
			"NI_ID" : node.TODO_OBJECT_ID2,
			"_AGENT_USER_" : node.OWNER_CODE //AGT_USER_CODE暂时，先以OWNER_CODE替代
		};
	}
	return params;
};