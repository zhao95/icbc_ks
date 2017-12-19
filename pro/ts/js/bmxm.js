var user_code = System.getVar("@USER_CODE@");
var bmbq = $("#bmbq").val();
//隔一行 进行 背景颜色 渲染
function rowscolor(table) {
    var rows = table.getElementsByTagName("tr");
    for (i = 1; i < rows.length; i++) {
        if (i % 2 == 0) {

            rows[i].style.backgroundColor = "Azure";
        }
    }


}
//每页多少条 添加onchange事件
function xmfenye() {
    //跟 级别 按钮 的onchange时间一样都要 筛选所有条件下的数据
    new listPages().gotoPage(1);
}

//加载完毕  显示第一个 tab active  显示隐藏
//报名项目列表调用(初始化后展示)
/*function ksqxm() {
   
    var pageEntity = result.list;
   
    var table = document.getElementById("table");
    rowscolor(table);
}*/

//分页+查询
var listPages = function () {
    // 构建页码所需参数
    this.showPageNum = 5; // 最多显示的页码
    this.startNum = 1; // 中间页码的第一个页码
    this.endNum = this.startNum; // 中间页码的最后一个页码
};
listPages.prototype.getListData = function (num) {
    //每页条数
    var param = {};
    var showNum = parseInt(jQuery('#xmyema').find("option:selected").val());
    param._PAGE_ = {};
    param._PAGE_.NOWPAGE = num;
    param._PAGE_.SHOWNUM = showNum;
    param["xmname"]= $("#xmnamesearch").val();
    param["user_code"] = user_code;
    param["shownum"]=showNum;
    param["nowpage"]=num;
    return  FireFly.doAct("TS_XMGL", "getUserXm", param);
};
//全局变量  sql查询条件(页面输入的搜索条件)
var sqlWhere = "";
// 创建页面显示数据的主体
listPages.prototype._bldBody = function (num) {
    var listData = this.getListData(num);
    this._lPage = listData._PAGE_;
    this.bldTable(listData);
    this.bldPage();
    var listPages = this;
    //查询条件按钮（设置查询考试名称和年份的条件）
};

/*  跳转到指定页*/
listPages.prototype.gotoPage = function (num) {

    this._bldBody(num);
};
/*  上一页*/
listPages.prototype.prePage = function () {
    var prePage = parseInt(this._lPage.NOWPAGE) - 1;
    var nowPage = "" + ((prePage > 0) ? prePage : 1);
    this.gotoPage(nowPage);
};
/* 下一页*/
listPages.prototype.nextPage = function () {
    var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
    var pages = parseInt(this._lPage.PAGES);
    var nowPage = "" + ((nextPage > pages) ? pages : nextPage);
    this.gotoPage(nowPage);
};
/* 首页*/
listPages.prototype.firstPage = function () {
    this.gotoPage(1);
};
/*末页*/
listPages.prototype.lastPage = function () {
    this.gotoPage(this._lPage.PAGES);
};
listPages.prototype.bldTable = function (listData) {
    var $ybmTableTbody = $("#table tbody");
    $ybmTableTbody.html("");
    // var data = listData.list;
    // var pageEntity=JSON.parse(data);
    var pageEntity = listData.alllist;
    var first = listData.first;
    for (var i = 0; i < pageEntity.length; i++) {
        var name = pageEntity[i].XM_NAME;
        //项目中已存在array的  title  数据  将展示在  已报名信息中
        var id = pageEntity[i].XM_ID;
        var dept = pageEntity[i].XM_FQDW_NAME;
        var type = pageEntity[i].XM_TYPE;
        var state = "未开始";
        var display = "none";

        //获取报名时间判断  报名状态
       /* var param1 = {};
        param1["xmid"] = id;
        var result1 = FireFly.doAct("TS_XMGL_BMGL", "getBMState", param1);
        var pageEntity1 = result1.list;*/
        var startTime = pageEntity[i].START_TIME_BM;
        var endTime = pageEntity[i].END_TIME_BM;
        var state = pageEntity[i].STATE_BM;
        if (state == "待报名") {
            display = "block";
        }
        //append数据
        var j = i + parseInt(first);
        $("#table tbody").append('<tr class="rhGrid-td-left" style="height: 50px"><td class="indexTD" style="text-align: center">' + j + '</td><td class="rhGrid-td-hide" id="BM_TYPE' + i + '" >' + type + '</td><td class="rhGrid-td-hide" id="BM_ID' + i + '" >' + id + '</td><td class="rhGrid-td-left " id="BM_NAME' + i + '" style="text-align: left">' + name + '</td><td class="rhGrid-td-left " id="BM_ODEPT__NAME">' + dept + '</td><td class="rhGrid-td-left " id="S_ATIME">' + endTime + '</td><td  id="BM_STATE__NAME" style="text-align: left">' + state + '</td><td id="BM_OPTIONS"><button class="btn btn-success" type="button" onclick="tiaozhuan(' + i + ')" style="margin-left:30px;display:' + display + ';color:white;font-size:15px;background-color:LightSeaGreen;height:35px;width:70px">报名</button></td></tr>');
    }
    var table = document.getElementById("table");
    rowscolor(table);
};
/* 添加分页展示*/
listPages.prototype.bldPage = function () {
    this._buildPageFlag = true;
    var _self = this;
    this._page = jQuery("#xmfenyediv");
    this._page.html('');
    //判断是否构建分页
    if (this._buildPageFlag === "false" || this._buildPageFlag === false) {
        this._page.addClass("rhGrid-page-none");
    } else if (this._lPage.PAGES === null) {//没有总条数的情况
        if (this._lPage.NOWPAGE > 1) {//上一页 {"ALLNUM":"1","SHOWNUM":"1000","NOWPAGE":"1","PAGES":"1"}
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                _self.prePage();
            }));
        } else {
// 		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
        }
        this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
        if (this._lData.length === this._lPage.SHOWNUM) {//下一页
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function () {
                _self.nextPage();
            }));
        } else {
// 		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
        }
    } else if (!jQuery.isEmptyObject(this._lPage)) {
        // 当前页码
        var currentPageNum = parseInt(this._lPage.NOWPAGE);
        // 总页数
        var sumPage = parseInt(this._lPage.PAGES);

        if (this.startNum + this.showPageNum < sumPage) {
            this.endNum = this.startNum + this.showPageNum
        } else {
            this.endNum = sumPage;
        }

        // 总条数
        var allNum = parseInt(this._lPage.ALLNUM);
        // 显示上一页
        if (currentPageNum !== 1) {
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function () {
                _self.prePage();
            }));
        } else {
// 		this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
        }
        // 移动页码
        if (currentPageNum > this.startNum + Math.floor((this.endNum - this.startNum) / 2)) {// 如果点击了后面的页码，则后移
            if (currentPageNum === sumPage) {// 点击了最后一页
                this.endNum = sumPage;

                if (this.endNum - this.showPageNum > 0) {
                    this.startNum = this.endNum - this.showPageNum;
                } else {
                    this.startNum = 1;
                }
            } else {
                if (currentPageNum > this.showPageNum) {
                    this.endNum = currentPageNum + 1;
                    this.startNum = currentPageNum - this.showPageNum + 1;
                }
            }
        } else {// 否则前移
            if (currentPageNum === 1) {// 点击了第一页
                this.startNum = 1;
            } else {
                this.startNum = currentPageNum - 1;
            }
            if (this.startNum + this.showPageNum < sumPage) {
                this.endNum = this.startNum + this.showPageNum;
            } else {
                this.endNum = sumPage;
            }
        }
        // 显示首页
        if (this.startNum !== 1) {
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>1</a>").click(function () {
                _self.gotoPage(parseInt(jQuery(this).html()));
            })).append("...");
        }
        // 如果总页数小于本页显示的最大页码
        if (sumPage < this.endNum) {
            this.endNum = sumPage;
        }
        // 显示中间页码
        for (var i = this.startNum; i <= this.endNum; i++) {
            if (i === currentPageNum) {// 构建当前页
                this._page.append("<span class='current ui-corner-4'>" + i + "</span>");
            } else {
                this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + i + "</a>").click(function () {
                    _self.gotoPage(parseInt(jQuery(this).html()));
                }));
            }
        }
        // 显示尾页
        if (sumPage > this.endNum) {
            this._page.append("...").append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>" + sumPage + "</a>").click(function () {
                _self.lastPage();
            }));
        }
        // 显示下一页
        if (currentPageNum !== sumPage) {
// 		this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function () {
                _self.nextPage();
            }));
        } else {
// 		this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
        }
        // 显示跳转到指定页码
        if (sumPage > 6) {
            this._page.append("<input class='toPageNum ui-corner-4' type='text' value=''/>").append(jQuery("<input class='toPageBtn' type='button' value='GO' />").click(function () {
                try {
                    var val = parseInt(jQuery(this).prev().val());
                    if (val >= 1 && val <= sumPage) {
                        _self.gotoPage(val);
                    }
                } catch (e) {
                    // 页码转换异常，忽略
                }
            }));
        }
        //总条数显示
// 	jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
        jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
    }
    return this._page;
};
//默认跳转到第一页
/*new listPage().gotoPage(1);*/
//项目查询 功能按钮
$("#searchbut").click(function(){
	  new listPages().gotoPage(1);
})
