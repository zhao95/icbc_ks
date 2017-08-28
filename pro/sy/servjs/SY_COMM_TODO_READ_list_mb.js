var _viewer = this;

_viewer.resetGridClickParams = function(node) {
	var params = {};
	if (node) {
		params.sId = node.SERV_ID;
		params.pId = node.TODO_OBJECT_ID1;
		params.byIdParams = {
			"SEND_ID" : node.TODO_OBJECT_ID2
		};
	}
	return params;
};
