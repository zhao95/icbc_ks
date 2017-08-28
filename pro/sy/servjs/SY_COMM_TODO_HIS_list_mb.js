var _viewer = this;

_viewer.resetGridClickParams = function(node) {
	console.log(node);
	var params = {};
	if (node) {
		params.sId = node.SERV_ID;
		params.pId = node.TODO_OBJECT_ID1;
	}
	return params;
};