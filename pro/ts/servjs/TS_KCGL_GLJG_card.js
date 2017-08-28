var _viewer = this;
if(_viewer.opts.act == "cardAdd"){
	var handler = _viewer.getParHandler();
	var extWhere = handler._extWhere;
	if(typeof(extWhere.split("'")[1])!="undefined"){ 
		_viewer.getItem("KC_ID").setValue(extWhere.split("'")[1]);
	}
}