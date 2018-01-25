var _viewer = this;

_viewer.getItem("AD_LB").change(function(){
	 _viewer.getItem("AD_XL").clear();//类别
	 _viewer.getItem("AD_MK").clear();
	 _viewer.getItem("AD_TYPE").clear();
});

_viewer.getItem("AD_XL").change(function(){
	 _viewer.getItem("AD_MK").clear();
	 _viewer.getItem("AD_TYPE").clear();
});
_viewer.getItem("AD_MK").change(function(){
	
	 _viewer.getItem("AD_TYPE").clear();
});

