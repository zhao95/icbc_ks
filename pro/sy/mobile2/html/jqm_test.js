$(function() {
	
	
	/*FireFly.mb_doAct('SY_ORG_USER', 'finds', {'S_FLAG': 1}, false, false).then(function(data) {
		console.debug(data);
	});
	console.log('finish');*/
	var data = FireFly.mb_doAct('SY_ORG_USER', 'finds', {'S_FLAG': 1}, false, false);
	console.debug(data);
	console.log('finish');
});