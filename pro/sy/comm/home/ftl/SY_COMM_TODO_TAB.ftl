<script type="text/javascript">
    var result = FireFly.doAct("TS_XMGL_SZ","getStayApXm",{});
    var datalist = result._DATA_;
    var j = 0;
    for(var i=0;i<datalist.length;i++){
    	j++;
   var xmszid = datalist[i].XM_ID;
   var newTr='<tr style="height:30px">'+
   '<td width="5%" align="center">			'+j+'		</td>'+
   '<td width="5%" align="center">'+datalist[i].xm_name+'</td>'+
   '<td width="5%" align="center">'+datalist[i].xm_start+'</td>'+
   '<td width="5%" align="center">'+datalist[i].xm_end+'</td>'+
    	'<td align="center">未安排</td>'+
    '<td align="center"><span style="color:lightblue"><a href="#" style="text-decoration:none;" id='+xmszid+'>安排</a></span></td>'+
    '<tr>';
   $("#kctable").append(newTr);
   $("#"+xmszid).click(function(){
	   var height = jQuery(window).height()-80;
   	var width = jQuery(window).width()-200;
   	var temp = {"act":"cardModify","sId":"TS_XMGL_KCAP","widHeiArray":[width,height],"xyArray":[100,100]};
   	temp["_PK_"] = $(this).attr("id");//修改时，必填
       var cardView = new rh.vi.cardView(temp);
       cardView.show();
   });
    } 
    $("td").css("border","solid 1px #dddddd");
    	$("#alranpai").click(function(){
		var opts={'url':'TS_ANPAI_PASS.list.do?','tTitle':'已安排考场','menuFlag':3};
		Tab.open(opts);
    	})
</script>
<div id='TS_COMM_TODO' class='portal-box'>
<div class='portal-box-title'><span class='portal-box-title-icon icon_portal_todo'></span><span class="portal-box-title-label">待我安排的考场</span><span class="portal-box-hideBtn  conHeanderTitle-expand"></span><span class="portal-box-more"><a href="#" id="alranpai"></a></span></div>
<div>
            <table id="kctable"  style="border:solid 1px  #dddddd; width:100%">
                  <tr style="background:f4fbff;height:30px;color:#999999"><td width="5%" align="center">序号</td><td width="35%" align="center">名称</td><td width="15%" align="center">安排开始时间</td><td width="15%" align="center">安排截止日期</td><td width="15%" align="center">状态</td><td width="15%" align="center">操作</td></tr>
           
            </table>
        
    </div>
</div>
