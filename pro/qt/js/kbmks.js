 $(function () {
//    $('#ksTable a:last').tab('show');
    $('#ksTable a').click(function (e) {
    	  e.preventDefault();
    	  $(this).tab('show');
    	});
  });
