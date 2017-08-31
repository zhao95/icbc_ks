$(function() {
	// 用于查询对应的项目状态的对象
	var typeMap = {
		"未开启" : "c1",
		"已结束" : "c3",
		"报名中" : "c2",
		"审核中" : "c2",
		"考前请假开放中" : "c2",
		"考后请假开放中" : "c2",
		"异地借考开放中" : "c2",
		"未设置" : "c1",
		"已设置" : "c3"
	}
	// 获取当前登录用户的人力资源编码(目前使用的是用户编码)
	// var user_work_num = System.getUser("USER_WORK_NUM");
	var user_work_num = System.getUser("USER_CODE");
	var paramBM = {};
	paramBM["_extWhere"] = "AND BM_CODE = '" + user_work_num + "'";
	// 查询出该人员报名的所有项目
	var resultBM = FireFly.doAct("TS_BMSH_PASS", "query", paramBM);
	// 报名前端去重的数组
	var xm_distinct_arr = [];
	var xm_rowNum1 = 1;
	var xm_rowNum2 = 1;
	// 遍历输出在tab当前进行的项目中
	for (var i = 0; i < resultBM._DATA_.length; i++) {
		var xm_id = resultBM._DATA_[i].XM_ID;
		var paramXM = {};
		paramXM["_extWhere"] = "and XM_ID = '" + xm_id + "'";
		var resultXM = FireFly.doAct("TS_XMGL", "query", paramXM);
		// 判断项目是否已经存在，-1 不存在 >0 存在
		var distinct_num = xm_distinct_arr.indexOf(xm_id);
		if (distinct_num < 0) {
			xm_distinct_arr.push(xm_id);
			// 判断项目进度是否小于100%
			if (resultXM._DATA_[0].XM_JD < 10) {
				var xm_rowNum = resultBM._DATA_[i].ROWNUM_;
				// 获取项目相关的数据， 用于拼接到状态更多页面 xmzt_info.jsp
				var xm_name = resultXM._DATA_[0].XM_NAME;
				var xm_type = resultXM._DATA_[0].XM_TYPE;
				// 根据返回值可以找到查询数据字典后的结果，直接拿来用即可。数据为发起单位。
				var xm_dept = resultXM._DATA_[0].XM_FQDW_NAME__NAME;
				var xm_start = resultXM._DATA_[0].XM_START;
				var xm_end = resultXM._DATA_[0].XM_END;
				var xm_jd = resultXM._DATA_[0].XM_JD;
				var xm_currentState = "";

				// 通过项目ID和用户人力资源编码查询到标志，设置按钮的内容
				var paramObj1 = {};
				paramObj1["_extWhere"] = "and STR1='" + user_work_num
						+ "' AND DATA_ID ='" + xm_id + "'";
				var resultObj = FireFly.doAct("TS_XMZT", "query", paramObj1);
				if (resultObj._DATA_.length != 0) {
					if (resultObj._DATA_[0].INT1 === "1") {
						var xm_opera = "首页显示中";
					}
				} else {
					var xm_opera = "首页未显示";
				}
				// 获取到项目挂接所有模块姓名
				var paramXMSZ = {};
				paramXMSZ["_extWhere"] = "and XM_ID = '" + xm_id + "'";
				var resultXMSZ = FireFly
						.doAct("TS_XMGL_SZ", "query", paramXMSZ);
				// 遍历所有模块名字，取出满足要求的模块状态
				for (var j = 0; j < resultXMSZ._DATA_.length; j++) {
					var gj_name = resultXMSZ._DATA_[j].XM_SZ_TYPE;
					// 如果项目挂接模块处于进行中，则给页面赋值
					if (typeMap[gj_name] === "c2") {
						xm_currentState = gj_name;
					}
				}
				// 数据输入到页面
				jQuery("#table1_tbody").append(
								'<tr class="rhGrid-td-left" XM_ID="'+ xm_id+ '" style="height: 50px">'+
								'<td class="indexTD" style="text-align: center">'+ xm_rowNum1+ '</td>'+
								'<td class="rhGrid-td-left " id="xm_currentState"style="text-align: center">'+ xm_name+'</td>'+ 
								'<td class="rhGrid-td-left " id="xm_type" style="text-align: center">'+ xm_type+'</td>'+
								'<td class="rhGrid-td-left " id="xm_dept" style="text-align: center">'+ xm_dept+'</td>'+
								'<td class="rhGrid-td-left " id="xm_start"style="text-align: center" >'+ xm_start+'</td>'+
								'<td class="rhGrid-td-left " id="S_MTIME" style="text-align: center" >'+ xm_end+'</td>'+ 
								'<td class="rhGrid-td-left " id="xm_end" style="text-align: center" >'+ xm_jd+'0.0%</td>'+ 
								'<td class="rhGrid-td-left " id="xm_currentState" style="text-align: center">'+ xm_currentState+'</td>'+
								'<td id="BM_OPTIONS" style="text-align: center;">'+
									'<input type="button" class="opera_btn" style="margin:0 auto;display:block;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="'+ xm_opera + '"></input>'+
								'</td>'+'</tr>');
				xm_rowNum1++;

			}
			// 项目进度大于100%的显示在已完成中
			else {
				var xm_rowNum = resultBM._DATA_[i].ROWNUM_;
				var xm_name = resultXM._DATA_[0].XM_NAME;
				var xm_type = resultXM._DATA_[0].XM_TYPE;
				// 根据返回值可以找到查询数据字典后的结果，直接拿来用即可。
				var xm_dept = resultXM._DATA_[0].XM_FQDW_NAME__NAME;
				var xm_start = resultXM._DATA_[0].XM_START;
				var xm_end = resultXM._DATA_[0].XM_END;
				var xm_jd = resultXM._DATA_[0].XM_JD;
				var xm_currentState = "";
				// 数据输入到页面
				jQuery("#table2_tbody").append(
						'<tr class="rhGrid-td-left" XM_ID="'+ xm_id+ '" style="height: 50px">'+
						'<td class="indexTD" style="text-align: center">'+ xm_rowNum2+ '</td>'+
						'<td class="rhGrid-td-left " id="xm_currentState"style="text-align: center">'+ xm_name+'</td>'+ 
						'<td class="rhGrid-td-left " id="xm_type" style="text-align: center">'+ xm_type+'</td>'+
						'<td class="rhGrid-td-left " id="xm_dept" style="text-align: center">'+ xm_dept+'</td>'+
						'<td class="rhGrid-td-left " id="xm_start"style="text-align: center" >'+ xm_start+'</td>'+
						'<td class="rhGrid-td-left " id="S_MTIME" style="text-align: center" >'+ xm_end+'</td>'+ 
						'<td class="rhGrid-td-left " id="xm_end" style="text-align: center" >'+ xm_jd+'0.0%</td>'+ 
						'<td class="rhGrid-td-left " id="xm_currentState" style="text-align: center">已结束</td>'+
//						'<td id="BM_OPTIONS" style="text-align: center;">'+
//							'<input type="button" class="opera_btn" style="margin:0 auto;display:block;color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:80px" value="'+ xm_opera + '"></input>'+
//						'</td>'+
						'</tr>');
				xm_rowNum2++;
			}
		}
		// 若项目已经存在页面中
		else if (distinct_num >= 0) {
			// 不做任何操作
		}
	}
	// 渲染隔行背景色
	var table1 = jQuery("#table1_tbody");
	var table2 = jQuery("#table2_tbody");
	rowscolor(table1);
	rowscolor(table2);
	// 背景色渲染方法
	function rowscolor(table) {
		var rows1 = jQuery(table).find("tr");
		for (i = 0; i < rows1.length; i++) {
			if (i % 2 == 1) {
				rows1[i].style.backgroundColor = "Azure";
			}
		}
	}

	// 判断点击按钮以后，将要显示的项目显示到首页
	jQuery("#table1_tbody").find(".opera_btn").unbind("click").click(
			function() {
				// 获取到对应的XM_ID
				var tr_xm_id = $(this).parent().parent().attr("XM_ID");

				// 通过项目ID和用户人力资源编码查询到show_type
				var paramObj = {};
				paramObj["_extWhere"] = "and STR1='" + user_work_num + "'";
				var resultObj = FireFly.doAct("TS_OBJECT", "query", paramObj);

				// 如果ts_object表中无此数据，则添加一条对应用户的数据，再次查询结果
				if (resultObj._DATA_.length == 0) {
					var saveParam = {};
					saveParam["DATA_ID"] = xm_id;
					saveParam["STR1"] = user_work_num;
					saveParam["INT1"] = 1;
					saveParam["SERV_ID"] = "TS_XMZT";
					FireFly.doAct("TS_XMZT", "save", saveParam);
					resultObj = FireFly.doAct("TS_OBJECT", "query", paramObj);
				}

				$(this).val("首页显示中");

				var paramStrs = {};
				paramStrs["USER_WORK_NUM"] = user_work_num;
				paramStrs["XM_ID"] = tr_xm_id;
				FireFly.doAct("TS_XMZT", "modifyShowType", paramStrs, false,
						false, function(data) {
							if (data.INT1 === "1") {
								location.reload();
							} else {
								location.reload();
							}
							// window.location.reload();
						});
			});
})