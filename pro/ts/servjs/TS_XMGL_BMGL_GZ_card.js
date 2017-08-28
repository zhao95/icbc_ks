var _viewer = this;
var way = _viewer.getItem("GZ_WAY_CODE").getValue();
var value1 = _viewer.getItem("GZ_VALUE1");
switch(way)
{
    case "001":
    	value1.hide();
        break;
    case "002":
    	value1.hide();
        break;
    case "003":
    	value1.hide();
        break;
    case "004":
    	value1.hide();
        break;
    case "005":
//    	准入测试规则  限定准入测试成绩
    	$("#KS_BMGL_GZ-GZ_VALUE1_label .container .name").html('成绩>=');
        break;
    case "006":
    	$("#KS_BMGL_GZ-GZ_VALUE1_label .container .name").html('次数<=');
        break;
    default:
}

//测试用途