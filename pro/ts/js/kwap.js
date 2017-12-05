/*
 * 特此声明：
 * 本文件中  kc   对应表   ts_xmgl_kcap_dapcc_cc
 *          cc   对应表   ts_xmgl_kcap_dapcc_ccsj
 **/

/*
 * 文件结构
 *
 * bindHeaderAction 绑定按钮事件
 *
 * ZdfpccModal 自动分配场次modal
 *
 * LookJkModal 查看借考人员modal
 *
 * UpdateCCModal 更改场次modal
 *
 * KcObject 考场场次相关方法
 *
 * KsObject 考生相关方法
 *
 * Utils
 * */


var ListPage = function () {
    // 构建页码所需参数
    this.showPageNum = 5; // 最多显示的页码
    this.startNum = 1; // 中间页码的第一个页码
    this.endNum = this.startNum; // 中间页码的最后一个页码
};
/*根据条件获取数据*/
ListPage.prototype.getListData = function (num) {
    var showNum = 2;//parseInt(jQuery('#showNumSelect').find("option:selected").val());
    var data = {};
    data._PAGE_ = {};
    data._PAGE_.NOWPAGE = num;
    data._PAGE_.SHOWNUM = showNum;
    return null;
};
/*根据listdata构建表格*/
ListPage.prototype.bldTable = function (listData) {
};
/*构建主体内容（表格和分页）*/
ListPage.prototype._bldBody = function (num) {

    var listData = this.getListData(num);
    this._lPage = listData._PAGE_;
    this._lData = listData._DATA_;
    this.bldTable(listData);
    this.bldPage(/*listData._PAGE_*/);
};
/*查询更新*/
ListPage.prototype.search = function () {
    this.gotoPage(1);
};
/*跳转到指定页*/
ListPage.prototype.gotoPage = function (num) {
    this._bldBody(num);
};
/*上一页*/
ListPage.prototype.prePage = function () {
    var prePage = parseInt(this._lPage.NOWPAGE) - 1;
    var nowPage = "" + ((prePage > 0) ? prePage : 1);
    this.gotoPage(nowPage);
};
/*下一页*/
ListPage.prototype.nextPage = function () {
    var nextPage = parseInt(this._lPage.NOWPAGE) + 1;
    var pages = parseInt(this._lPage.PAGES);
    var nowPage = "" + ((nextPage > pages) ? pages : nextPage);
    this.gotoPage(nowPage);
};
/*首页*/
ListPage.prototype.firstPage = function () {
    this.gotoPage(1);
};
/*末页*/
ListPage.prototype.lastPage = function () {
    this.gotoPage(this._lPage.PAGES);
};
/*构建分页*/
ListPage.prototype.bldPage = function () {
    this._buildPageFlag = true;
    var _self = this;
//            this._page = jQuery("<div class='rhGrid-page'></div>");
    this._page = jQuery(".rhGrid-page");
    this._page.html('');
    //判断是否构建分页
    if (this._buildPageFlag === "false" || this._buildPageFlag === false) {
        this._page.addClass("rhGrid-page-none");
    } else if (this._lPage.PAGES === null) {//没有总条数的情况
        if (this._lPage.NOWPAGE > 1) {//上一页 {"ALLNUM":"1","SHOWNUM":"1000","NOWPAGE":"1","PAGES":"1"}
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                _self.prePage();
            }));
        } else {
//			this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
            this._page.append("<span class='disabled ui-corner-4'><</span>");
        }
        this._page.append("<span class='current ui-corner-4'>" + this._lPage.NOWPAGE + "</span>");	//当前页
        if (this._lData.length === this._lPage.SHOWNUM) {//下一页
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                _self.nextPage();
            }));
        } else {
//			this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>></span>");
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
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>上一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'><</a>").click(function () {
                _self.prePage();
            }));
        } else {
//			this._page.append("<span class='disabled ui-corner-4'>上一页</span>");
            this._page.append("<span class='disabled ui-corner-4'><</span>");
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
//			this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>下一页</a>").click(function(){
            this._page.append(jQuery("<a href='javascript:_parent.window.scroll(0,0);' class='ui-corner-4'>></a>").click(function () {
                _self.nextPage();
            }));
        } else {
//			this._page.append("<span class='disabled ui-corner-4'>下一页</span>");
            this._page.append("<span class='disabled ui-corner-4'>></span>");
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
//		jQuery("<span class='allNum'></span>").text("共" + allNum + "条").appendTo(this._page);
//                jQuery("<span class='allNum'></span>").text(Language.transArr("rh_ui_grid_L1", [allNum])).appendTo(this._page);
    }
    // _PAGE_ :{ALLNUM: "2", NOWPAGE: "1", PAGES: "1", SHOWNUM: "50"}
    //上一页
//            if () {
//            }
//            pageBean.NOWPAGE;
//            pageBean.PAGES;
    return this._page;
};


function initData(xmId) {
    $("#topContent").resizable({
        animate: false,
        autoHide: false,
        delay: 20,
        handles: 's',
        maxHeight: 700,
        minHeight: 290,
        alsoResize: '#ksOrgTreeContent,#ksContent',
        stop: function (/*event, ui*/) {
            $("#topContent").css("width", "");
            $("#ksContent").css("width", "");
        }
    });
    HeaderBtn.initData(xmId);
    ZdfpccModal.initData(xmId);
    SubmissionArrangementModal.initData(xmId);
    KcObject.initData(xmId);
    KsObject.initData(xmId);
}

var HeaderBtn = {
    xmId: '',

    initData: function (xmId) {
        this.xmId = xmId;
        this.setTjFbBtn();
        this.bindHeaderAction(xmId);
    },
    //设置提交发布按钮
    setTjFbBtn: function () {
        var xmId = this.xmId;
        var deptCodeStr = Utils.getUserYAPAutoCode();
        //unPublish
        var xmBean = FireFly.doAct("TS_XMGL", "byid", {"_PK_": xmId}, false);
        // xmBean.S_USER;
        var typeBean = FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getTjOrPublish", {
            XM_ID: xmId,
            deptCodeStr: deptCodeStr
        }, false);

        if (typeBean.type === 'publish') {
            $("#tjccap").css('display', 'none');
            var userYAPPublishCode = Utils.getUserYAPPublishCode();
            var canPublishBean = FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getCanPublish", {
                XM_ID: xmId,
                UserYAPPublishCode: userYAPPublishCode
            }, false);
            if (canPublishBean.canPublish === 'true') {
                if (xmBean.XM_KCAP_PUBLISH_TIME === '') {
                    //项目未发布 显示发布
                    $("#publish").css('display', 'block');
                } else {
                    $("#unPublish").css('display', 'block');
                }


            } else {
                //没有发布权限,不显示发布 取消发布按钮
                $("#publish").css('display', 'none');
                $("#unPublish").css('display', 'none');
            }
        } else {
            $("#tjccap").css('display', 'block');
            $("#publish").css('display', 'none');
        }
        // var deptCodeStr = Utils.getUserYAPAutoCode();
        // var split = deptCodeStr.split(',');
        // for (var i = 0; i < split.length; i++) {
        //     var itemCode = split[i];
        //     if (itemCode !== '') {
        //         var dept = FireFly.doAct('SY_ORG_DEPT', 'byid', {'_PK_': itemCode});
        //         if (dept.DEPT_PCODE) {
        //             //有父级机构
        //             $("#tjccap").css('display', 'block');
        //             $("#publish").css('display', 'none');
        //         } else if (dept._MSG_ && dept._MSG_.indexOf('ERROR') >= 0) {
        //             //机构不存在
        //         } else {
        //             //机构存在，父id为空 ->为总行 获取总行下的所有机构安排考位
        //             $("#tjccap").css('display', 'none');
        //             $("#publish").css('display', 'block');
        //         }
        //
        //     }
        // }
    },

    /*提交场次安排后不能 安排座位*/
    zdfpccDisableAftertjccap: function () {
        if (Utils.getCanDraggable() === false) {
            this.setNoCanArrange();
        } else {
            this.setCanArrange();
        }
    },

    //可以安排
    setCanArrange: function () {
        $("#zdfpcc").removeClass('rh-icon-disable').addClass('rh-icon');
        $("#updatecc").removeClass('rh-icon-disable').addClass('rh-icon');
        $("#tjccap").removeClass('rh-icon-disable').addClass('rh-icon');
        $("#publish").removeClass('rh-icon-disable').addClass('rh-icon');
        $("#clearYapzw").removeClass('rh-icon-disable').addClass('rh-icon');

        $("#zdfpcc").unbind('click').bind('click', function () {
            $('#zdfpccModal').modal({backdrop: false, show: true});
        });
        $("#updatecc").unbind('click').bind('click', function () {
            UpdateCCModal.show();
        });
        $("#tjccap").unbind('click').bind('click', function () {
            var kcCountBean = Utils.getRemainingKsInfo();
            var $tjccapModal = $('#tjccapModal');
            if (kcCountBean.count > 0) {
                $tjccapModal.find(".modal-body").html('是否提交场次安排？' +
                    '<div style="color:red;">有' + kcCountBean.remainCount + '个未安排的考生' +
                    '</div>');
            } else {
                $tjccapModal.find(".modal-body").html('是否提交场次安排？');
            }
            $tjccapModal.modal({backdrop: false, show: true});
        });
        $("#publish").unbind('click').bind('click', function () {
            var kcCountBean = Utils.getRemainingKsInfo();
            var $publishModal = $('#publishModal');
            if (kcCountBean.count > 0) {
                $publishModal.find(".modal-body").html('是否发布场次安排？' +
                    '<div style="color:red;">有' + kcCountBean.remainCount + '个未安排的考生' +
                    '</div>');
            } else {
                $publishModal.find(".modal-body").html('是否发布场次安排？');
            }
            $publishModal.modal({backdrop: false, show: true});
        });
        $("#clearYapzw").unbind('click').bind('click', function () {
            $('#clearYapzwModal').modal({backdrop: false, show: true});
        });

    },

    //不可以安排
    setNoCanArrange: function () {
        $("#zdfpcc").removeClass('rh-icon').addClass('rh-icon-disable');
        $("#updatecc").removeClass('rh-icon').addClass('rh-icon-disable');
        $("#zdfpcc").unbind('click');
        $("#updatecc").unbind('click');
        $("#tjccap").removeClass('rh-icon').addClass('rh-icon-disable');
        $("#tjccap").unbind('click');
        $("#publish").removeClass('rh-icon').addClass('rh-icon-disable');
        $("#publish").unbind('click');
        $("#clearYapzw").removeClass('rh-icon').addClass('rh-icon-disable');
        $("#clearYapzw").unbind('click');
    },
    /**
     * 绑定页首按钮事件
     */
    bindHeaderAction: function (xmId) {
        var self = this;
        this.setCanArrange();
        $("#submissionArrangement").click(function () {
            SubmissionArrangementModal.show();
        });
        $("#lookJk").click(function () {
            LookJkModal.show(xmId);
        });
        $("#xngs").click(function () {
            var kcCountBean = Utils.getRemainingKsInfo();
            var $xngsModal = $('#xngsModal');
            if (kcCountBean.count > 0) {
                $xngsModal.find(".modal-body").html('是否辖内公示座位信息？' +
                    '<div style="color:red;">有' + kcCountBean.remainCount + '个未安排的考生' +
                    '</div>');
            } else {
                $xngsModal.find(".modal-body").html('是否辖内公示座位信息？');
            }
            $xngsModal.modal({backdrop: false, show: true});
        });

        $("#unPublish").click(function () {
            $('#unPublishModal').modal({backdrop: false, show: true});
        });

        //提交确定按钮事件
        $("#tjccapModal").find('button[class="btn btn-success"]').bind('click', function () {
            showVerifyCallback(function () {
                var deptCodeStr = '';//Utils.getUserYAPAutoCode();
                var deptCodes = deptCodeStr.split(',');
                deptCodes = Utils.arrayUnique(deptCodes.concat(KcObject.getAllKcODeptCode()));
                var kcIdStr = '';
                for (var i = 0; i < KcObject.kcArr.length; i++) {
                    var kc = KcObject.kcArr[i];
                    kcIdStr += ',' + kc.KC_ID;
                }
                if (kcIdStr.length > 0) {
                    kcIdStr = kcIdStr.substring(1);
                }
                FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'submit', {
                    data: JSON.stringify({
                        XM_ID: xmId,
                        DEPT_CODES: deptCodes,
                        KC_ID_STR: kcIdStr
                    })
                }, false, false);
                // for (var i = 0; i < deptCodes.length; i++) {
                //     var itemDeptCode = deptCodes[i];
                //     if (itemDeptCode !== '') {
                //         //避免重复提交 保存前先查询
                //         var queryBean = FireFly.doAct("TS_XMGL_KCAP_TJJL", "query", {
                //             '_NOPAGE_': true,
                //             '_extWhere': " and TJ_DEPT_CODE ='" + itemDeptCode + "' and XM_ID ='" + xmId + "'"
                //         });
                //         if (queryBean._DATA_.length <= 0) {
                //             FireFly.doAct('TS_XMGL_KCAP_TJJL', 'save', {
                //                 TJ_DEPT_CODE: itemDeptCode,
                //                 XM_ID: xmId
                //             }, false);
                //         }
                //     }
                // }
                Utils.getCanDraggable(true);
                KcObject.reloadCCInfo();
                KsObject.setDfpKsContent();
                self.zdfpccDisableAftertjccap();
            });
        });

        //发布确定按钮事件
        $("#publishModal").find('button[class="btn btn-success"]').bind('click', function () {
            // var xmBean = {
            //     XM_ID: xmId,
            //     _PK_: xmId,
            //     XM_KCAP_PUBLISH_USER_CODE: System.getUser("USER_CODE"),
            //     XM_KCAP_PUBLISH_TIME: rhDate.getTime()
            // };
            // FireFly.doAct("TS_XMGL", "save", xmBean, false, false, function () {
            //     alert("发布成功！");
            // });
            showVerifyCallback(function () {
                FireFly.doAct("TS_XMGL_KCAP_DAPCC", "publish", {XM_ID: xmId}, false, false, function () {
                    alert("发布成功！");
                });
                Utils.getCanDraggable(true);
                KcObject.reloadCCInfo();
                KsObject.setDfpKsContent();
                self.zdfpccDisableAftertjccap();
                $("#publish").css('display', 'none');
                $("#unPublish").css('display', 'block');
            });

        });

        //取消发布确定按钮事件
        $("#unPublishModal").find('button[class="btn btn-success"]').bind('click', function () {
            var xmBean = {
                XM_ID: xmId,
                _PK_: xmId,
                XM_KCAP_PUBLISH_USER_CODE: System.getUser("USER_CODE"),
                XM_KCAP_PUBLISH_TIME: ''
            };
            /*= FireFly.doAct("TS_XMGL", "byid", {"_PK_": xmId}, false);*/
            // xmBean.XM_KCAP_PUBLISH_USER_CODE = System.getUser("USER_CODE");
            // xmBean.XM_KCAP_PUBLISH_TIME = rhDate.getTime();

            showVerifyCallback(function () {
                FireFly.doAct("TS_XMGL", "save", xmBean, false, false, function () {
                    alert("取消发布成功！");
                });
                Utils.getCanDraggable(true);
                KcObject.reloadCCInfo();
                KsObject.setDfpKsContent();
                self.zdfpccDisableAftertjccap();
                $("#publish").css('display', 'block');
                $("#unPublish").css('display', 'none');
            });
        });

        //清除座位安排确定按钮事件
        $("#clearYapzwModal").find('button[class="btn btn-success"]').bind('click', function () {
            var kcIdStr = '';
            for (var i = 0; i < KcObject.kcArr.length; i++) {
                var kc = KcObject.kcArr[i];
                kcIdStr += kc.KC_ID + ',';
            }
            FireFly.doAct("TS_XMGL_KCAP_DAPCC", "clearYapzw", {KC_ID_STR: kcIdStr}, false, false, function () {
                alert("清除座位安排成功！");
            });
            KcObject.reloadCCInfo();
            KsObject.setDfpKsContent();
        });


        function sidebarFunction() {
            var $mainSidebar = $('.main-sidebar');
            var $i = $('#toggle-sidebar').find('i');
            if ($mainSidebar.width() < 20) {
                //收缩状态
                $('.content-wrapper').animate({marginLeft: "250px"}, speed);
                $mainSidebar.animate({width: "250px"}, speed, function () {
                    $i.removeClass("fa-angle-right");
                    $i.addClass("fa-angle-left");
                    $('#topContent').resize();
                });
            } else {
                $('.content-wrapper').animate({marginLeft: "16px"}, speed);
                $mainSidebar.animate({width: "16px"}, speed, function () {
                    $i.removeClass("fa-angle-left");
                    $i.addClass("fa-angle-right");
                    $('#topContent').resize();
                });
            }
        }

        var speed = 200;
        //伸缩按钮
        $("#toggle-sidebar").click(function () {
            sidebarFunction();
        });

        $("#maximize").click(function () {
            var $mainSidebar = $('.main-sidebar');
            if ($(this).find('img').attr('src').indexOf('maximize') > 0) {
                if ($mainSidebar.width() >= 20) {
                    sidebarFunction();
                }

                $(this).find('img').attr('src', FireFly.getContextPath() + '/ts/image/fit-size.png');

                var number = document.documentElement.clientHeight - 127;
                if (number < 557) {
                    number = 557;
                }
                $('#ksOrgTreeContent').animate({height: number + 'px'}, speed);
                $('#ksContent').animate({height: number + 'px'}, speed, function () {
                    $('#bottom-content').css('display', 'none');
                });
            } else {
                if ($mainSidebar.width() < 20) {
                    sidebarFunction();
                }
                var topHeight = $('#topContent').height() - 45;
                $(this).find('img').attr('src', FireFly.getContextPath() + '/ts/image/maximize.png');
                $('#ksOrgTreeContent').animate({height: topHeight + 'px'}, speed);
                $('#ksContent').animate({height: topHeight + 'px'}, speed, function () {
                    $('#bottom-content').css('display', 'block');
                });
            }
        });
        //底部伸缩按钮
        $("#toggle-bottom-sidebar").click(function () {
            var $bottomContent = $('#bottom-content');
            $bottomContent.animate({
                height: 'toggle'
            });
            // $bottomContent.slideToggle("slow");
            // var speed = 200;
            // var $i = $(this).find('i');
            // if ($bottomContent.height() === 15) {
            //     //收缩状态
            //     // $('.content-wrapper').animate({marginLeft: "250px"}, speed);
            //     $bottomContent.animate({height: "auto"}, speed, function () {
            //         $i.removeClass("fa-angle-up");
            //         $i.addClass("fa-angle-down");
            //     });
            // } else {
            //     // $('.content-wrapper').animate({marginLeft: "16px"}, speed);
            //     $bottomContent.animate({height: "16px"}, speed, function () {
            //         $i.removeClass("fa-angle-down");
            //         $i.addClass("fa-angle-up");
            //     });
            // }
        });


        $("#xngsModal").find('button[class="btn btn-success"]').bind('click', function () {
            showVerifyCallback(function () {
                if (KcObject.kcArr !== null && KcObject.kcArr !== undefined) {
                    //考场不为空
                    var kcStr = '';
                    for (var i = 0; i < KcObject.kcArr.length; i++) {
                        var kc = KcObject.kcArr[i];
                        kcStr += ',' + kc.KC_ID;
                    }
                    if (kcStr.length > 0) {
                        kcStr = kcStr.substring(1);
                    }
                    FireFly.doAct("TS_XMGL_KCAP_DAPCC", "xngs", {
                        XM_ID: xmId,
                        KC_ID_STR: kcStr
                    }, false, false, function () {
                        alert('辖内场次安排已公示');
                    });
                }
            });
        });

        this.zdfpccDisableAftertjccap();
    }


};


/**
 * 自动分配模态框相关方法
 *
 * 配置项settingArray:GZK
 * 配置项
 */
var ZdfpccModal = {

    xmId: '',
    settingArray: [],
    hasSettingArray: [],
    /*  {id: 'setting1', name: '相同考试前后左右不相邻', checked: true, disabled: true},
     {id: 'setting2', name: '同一考生同一考场场次连排'},
     {id: 'setting3', name: '距离远近规则（里面配置，场次可以后可选择）'},
     {id: 'setting4', name: '同一网点级机构考生均分安排'},
     {id: 'setting5', name: '来自同一机构考生不连排'},
     {id: 'setting6', name: '考生人数少于机器数一半时，考生左右间隔不低于2个座位，前后不低于1个'},
     {id: 'setting7', name: '特定机构考生场次靠后安排'},
     {id: 'setting8', name: '领导职务考生座位靠前安排'},
     {id: 'setting9', name: '特定考试仅限于省分行安排（考试－考生，领导不能在当地考试，省分行）'}
     ],*/

    initData: function (xmId) {
        this.xmId = xmId;
        this.setSettingArray();
        this.setZdfpccModalContent();
    },

    reloadData: function () {
        this.setSettingArray();
        this.setZdfpccModalContent();
    },

    setSettingArray: function () {
        var currentUserCode = System.getUser("USER_CODE");

        var settingArrayBean = FireFly.doAct("TS_XMGL_KCAP_GZK", 'query', {'_NOPAGE_': true, '_ORDER_': ' GZ_SORT'});
        this.settingArray = settingArrayBean._DATA_;
        var hasSettingBean = FireFly.doAct("TS_XMGL_KCAP_GZ", 'query', {
            '_NOPAGE_': true,
            '_ORDER_': ' GZ_SORT',
            '_extWhere': " and XM_ID ='" + this.xmId + "' and S_USER ='" + currentUserCode + "'"
        });
        this.hasSettingArray = hasSettingBean._DATA_;
        for (var i = 0; i < this.settingArray.length; i++) {
            var setting = this.settingArray[i];
            for (var j = 0; j < this.hasSettingArray.length; j++) {
                //如果hasSetting有值，说明已经配置了  checked设置为true
                var hasSetting = this.hasSettingArray[j];
                if (hasSetting.GZ_CODE === setting.GZ_CODE) {
                    setting.checked = true;
                    setting.GZ_VALUE2 = hasSetting.GZ_VALUE2;
                }
            }
        }
    },

    /**
     * 设置考生场次自动分配内容
     */
    setZdfpccModalContent: function () {
        var self = this;
        var $allocationRule = $('#allocation-rule');
        $allocationRule.html('');

        var $selectXLTbody = $('#selectXL-table').find('tbody');

        for (var i = 0; i < this.settingArray.length; i++) {
            var setting = this.settingArray[i];
            if (setting.GZ_TYPE === '1') {
                //GZ_TYPE为1  默认选择，不可变更
                setting.disabled = true;
                setting.checked = true;
            }

            if (setting.GZ_CODE === 'S001') {
                //最少考场，最少场次 为单选框特殊处理
                if (setting.GZ_VALUE2 === '0') {
                    $('input[value="leastKc"]').attr("checked", true);
                } else {
                    $('input[value="leastCc"]').attr("checked", true);
                }
            }
            else /*if (setting.GZ_CODE !== 'S001')*/ {
                var $item = jQuery([
                    '<div class="checkbox">',
                    '   <label>',
                    '       <input id="' + setting.GZ_ID + '" type="checkbox" '
                    + (setting.checked ? 'checked ' : ' ') + (setting.disabled ? 'disabled ' : ' ') + '>',
                    '       <span class="setting-name">' + setting.GZ_NAME + '</span>',
                    '   </label>',
                    '</div>'
                ].join(''));

                if (setting.GZ_CODE === 'R008') {
                    //特定机构考生场次靠后安排
                    var settingOrgIndex = i;
                    var settingOrg = self.settingArray[settingOrgIndex];
                    if (!settingOrg.GZ_VALUE2) {
                        settingOrg.GZ_VALUE2 = '{"values":"","direction":"back"}';
                    }
                    var settingOrgValue2Obj = JSON.parse(settingOrg.GZ_VALUE2);
                    var $itemSettingName = $item.find('.setting-name');
                    if (settingOrgValue2Obj.direction === 'back') {
                        $itemSettingName.html($itemSettingName.html().replace('前', '后'));
                    } else {
                        $itemSettingName.html($itemSettingName.html().replace('后', '前'));
                    }

                    var $btn = jQuery([
                        '   <span style=" position: relative;top: -5px;cursor: pointer;">',
                        '       <span class="rh-icon-img btn-edit"></span>',
                        '   </span>'
                    ].join(''));
                    $btn.bind('click', function () {
                            var deptCodeStr = Utils.getUserYAPAutoCode();
                            var data = FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getOrgTreeByDeptCode', {deptCodeStr: deptCodeStr});
                            //jstree
                            var root = {
                                id: data.DEPT_CODE,
                                text: data.DEPT_NAME,
                                data: {id: data.DEPT_CODE, text: data.DEPT_NAME},
                                state: {opened: true},
                                children: []
                            };
                            var $selectOrgTreeContent = $('#selectOrg-tree').find('.content-navTree');

                            var putChildren = function (parent, childs) {
                                childs = childs ? childs : [];
                                for (var i = 0; i < childs.length; i++) {
                                    var child = childs[i];
                                    var id = child.DEPT_CODE ? child.DEPT_CODE : child.ID;
                                    var text = child.DEPT_NAME ? child.DEPT_NAME : child.NAME;
                                    var item = {
                                        id: id,
                                        text: text,
                                        data: child,
                                        children: []
                                    };
                                    parent.children.push(item);
                                    putChildren(item, child.CHILD);
                                }
                            };

                            var childs = data.CHILD;
                            putChildren(root, childs);

                            try {
                                $selectOrgTreeContent.jstree('destroy');//已经初始化tree，先destroy
                            } catch (e) {
                            }
                            $selectOrgTreeContent.jstree({
                                'core': {
                                    "multiple": false,
                                    'data': [root]
                                },
                                plugins: ["search", "sort", "types", "checkbox", "themes", "html_data"],
                                "checkbox": {
                                    "keep_selected_style": false,//是否默认选中
                                    "three_state": false,//父子级别级联选择
                                    "tie_selection": false
                                }
                            });

                            if (settingOrgValue2Obj.direction === 'back') {
                                $("#org-direction").val("back");
                            } else {
                                $("#org-direction").val("forward");
                            }

                            var deptCodes = settingOrgValue2Obj.values.split(',');
                            var resultCheckedNodes = [];
                            for (var j = 0; j < deptCodes.length; j++) {
                                var deptCode = deptCodes[j];
                                resultCheckedNodes.push('#' + deptCode);
                            }

                            $selectOrgTreeContent.on("ready.jstree", function (e, data) {
                                $selectOrgTreeContent.jstree("check_node", resultCheckedNodes);
                            });

                            var selectOrgModal = $('#selectOrgModal');
                            selectOrgModal.css('z-index', '999999');

                            selectOrgModal.find('button[class="btn btn-success"]').unbind('click').bind('click', function () {
                                settingOrgValue2Obj.values = $selectOrgTreeContent.jstree("get_checked").join(',');
                                settingOrgValue2Obj.direction = $("#org-direction").val();
                                var $settingName = $('#' + settingOrg.GZ_ID).parent().find('.setting-name');
                                if (settingOrgValue2Obj.direction === 'back') {
                                    $settingName.html($settingName.html().replace('前', '后'));
                                } else {
                                    $settingName.html($settingName.html().replace('后', '前'));
                                }
                                settingOrg.GZ_VALUE2 = JSON.stringify(settingOrgValue2Obj);
                            });

                            //添加选择机构搜索事件
                            var to = false;
                            var $treeSearchName = selectOrgModal.find('#select-org-tree-search-name');
                            // var $treeSearch = selectOrgModal.find('#select-org-tree-search');
                            var searchF = function () {
                                if (to) {
                                    clearTimeout(to);
                                }
                                to = setTimeout(function () {
                                    var v = $treeSearchName.val();
                                    $selectOrgTreeContent.jstree(true).search(v, false, true);
                                }, 250);
                            };
                            // $treeSearch.unbind('click').bind('click', searchF);
                            $treeSearchName.unbind('keyup').bind('keyup', searchF);

                            selectOrgModal.modal({backdrop: false, show: true});
                        }
                    );
                    $item.append($btn);

                } else if (setting.GZ_CODE === 'R009') {
                    //特定考试仅限于省分行安排
                    var settingXLIndex = i;
                    var settingXL = self.settingArray[settingXLIndex];
                    var $btn = jQuery([
                        '   <span style=" position: relative;top: -5px;cursor: pointer;">',
                        '       <span class="rh-icon-img btn-edit"></span>',
                        '   </span>'
                    ].join(''));
                    $btn.bind('click', function () {
                        var list = FireFly.doAct("TS_XMGL_BM_KSLB", 'query', {
                            '_NOPAGE_': true,
                            "_AFTER_SELECT_KEYWORDS": "distinct",
                            "_SELECT_": 'KSLB_XL_CODE,KSLB_XL,KSLB_NAME',
                            '_extWhere': " and XM_ID ='" + self.xmId + "'"
                        });
                        var dataList = list._DATA_;
                        $selectXLTbody.html('');
                        var $selectXLModal = $('#selectXLModal');
                        $selectXLModal.css('z-index', '999999');

                        for (var j = 0; j < dataList.length; j++) {
                            var item = dataList[j];
                            var $tr = jQuery([
                                '<tr id="' + item.KSLB_XL_CODE + '">',
                                '   <td><input type="checkbox" code="' + item.KSLB_XL_CODE + '"></td>',
                                '   <td>' + (j + 1) + '</td>',
                                '   <td>' + item.KSLB_NAME + '</td>',
                                '   <td>' + item.KSLB_XL + '</td>',
                                '</tr>'
                            ].join(''));
                            if (settingXL.GZ_VALUE2 === undefined) {
                                settingXL.GZ_VALUE2 = '{"values":""}';
                            }
                            var settingXLValues2Obj = JSON.parse(settingXL.GZ_VALUE2);
                            if (settingXLValues2Obj.values.indexOf(item.KSLB_XL_CODE) >= 0) {
                                $tr.find('input[type="checkbox"]').attr('checked', 'checked');//.= true;
                            }
                            $selectXLTbody.append($tr);
                        }
                        $selectXLModal.find('button[class="btn btn-success"]').unbind('click').bind('click', function () {
                            settingXLValues2Obj.values = '';
                            var result = '';
                            var checkeids = $selectXLTbody.find('input[type=checkbox]:checked');
                            for (var k = 0; k < checkeids.length; k++) {
                                var checked = checkeids[k];
                                result += $(checked).attr('code') + ',';
                            }
                            //去掉最后的逗号
                            result = result.substring(0, result.length - 1);
                            settingXLValues2Obj.values = result;
                            settingXL.GZ_VALUE2 = JSON.stringify(settingXLValues2Obj);
                        });
                        $selectXLModal.modal({backdrop: false, show: true});
                    });
                    $item.append($btn);
                }

                $allocationRule.append($item);
            }
        }
    },

    getZdfpccModalValue: function () {
        var result = [];
        // result.least =

        for (var i = 0; i < this.settingArray.length; i++) {
            var setting = this.settingArray[i];
            //特殊处理一些项
            var cloneSetting = this.clone(setting);
            delete cloneSetting.GZ_ID;
            delete cloneSetting._PK_;
            delete cloneSetting.S_USER;
            delete cloneSetting.S_TDEPT;
            delete cloneSetting.S_ODEPT;
            delete cloneSetting.S_MTIME;
            delete cloneSetting.S_FLAG;
            delete cloneSetting.S_DEPT;
            delete cloneSetting.S_CMPY;
            delete cloneSetting.S_ATIME;
            cloneSetting.XM_ID = this.xmId;
            if (cloneSetting.GZ_CODE === 'S001') {
                cloneSetting.GZ_VALUE2 = $('input[name="least"]').filter(':checked').val() === 'leastKc' ? '0' : '1';
                result.push(cloneSetting);
            } else if ($('#' + setting.GZ_ID)[0].checked) {
                result.push(cloneSetting);
            }
        }
        return result;
    },

    /**
     * 自动安排考位
     */
    doArrangeSeat: function () {
        this.saveBeanList(function () {
            var deptCodeStr = Utils.getUserYAPAutoCode();
            var codes = deptCodeStr.split(',');
            for (var i = 0; i < codes.length; i++) {
                var itemCode = codes[i];
                if (itemCode !== '') {
                    var dept = FireFly.doAct('SY_ORG_DEPT', 'byid', {'_PK_': itemCode});
                    if (dept.DEPT_PCODE) {
                        //有父级机构
                        FireFly.doAct("TS_XMGL_KCAP_YAPZW", "doArrangeSeat", {XM_ID: this.xmId, ODEPT_CODE: itemCode});
                    } else if (dept._MSG_ && dept._MSG_.indexOf('ERROR') >= 0) {
                        //机构不存在
                    } else {
                        //机构存在，父id为空 ->为总行 获取总行下的所有机构安排考位
                        var ccTreeNodes = KcObject.getCCTreeNodes();
                        var children = ccTreeNodes[0].children;
                        // var childs = FireFly.getDict('SY_ORG_ODEPT_ALL', itemCo de)["0"].CHILD;
                        for (var j = 0; j < children.length; j++) {
                            var child = children[j];
                            FireFly.doAct("TS_XMGL_KCAP_YAPZW", "doArrangeSeat", {
                                XM_ID: this.xmId,
                                ODEPT_CODE: child.id
                            });
                        }
                    }
                }
            }
            KsObject.search();
            KcObject.reloadCCInfo();
        });

    },

    /**
     * 保存配置信息
     **/
    saveBeanList: function (callback) {
        //  xmid  {id,value}
        var self = this;
        var settings = this.getZdfpccModalValue();
        //
        FireFly.doAct("TS_XMGL_KCAP_GZ", "saveBeanList", {
            'data': JSON.stringify(
                {XM_ID: this.xmId, BATCHDATAS: settings}
            )
        }, false, false, function () {
            if (callback) {
                callback.apply(self);
            }
        });
    },
    clone: function (obj) {
        var o;
        if (typeof obj === "object") {
            if (obj === null) {
                o = null;
            } else {
                if (obj instanceof Array) {
                    o = [];
                    for (var i = 0, len = obj.length; i < len; i++) {
                        o.push(this.clone(obj[i]));
                    }
                } else {
                    o = {};
                    for (var j in obj) {
                        o[j] = this.clone(obj[j]);
                    }
                }
            }
        } else {
            o = obj;
        }
        return o;
    }

};

var SubmissionArrangementModal = {
    xmId: '',
    submissionArrangementModal: '',
    initData: function (xmId) {
        this.submissionArrangementModal = $('#submissionArrangementModal');
        this.xmId = xmId;
    },
    setData: function () {
        var $arrangementTbody = $('#submissionArrangement-table').find('tbody');
        $arrangementTbody.html('');
        var data = this.getData();
        var dataList = data._DATA_;

        $('#totalCount').html(data.totalCount);
        $('#hasCount').html(data.hasCount);
        $('#noCount').html(data.noCount);
        for (var i = 0; i < dataList.length; i++) {
            var item = dataList[i];
            $arrangementTbody.append([
                '<tr>',
                '   <td>' + ( i + 1) + '</td>',
                '   <td>' + item.DEPT_NAME + '</td>',
                '   <td>' + (item.isTj === 'true' ? '已提交' : '<span style="color:red">未提交</span>') + '</td>',
                '</tr>'
            ].join(''));
        }
    },
    getData: function () {
        var userYAPAutoCode = Utils.getUserYAPAutoCode();
        return FireFly.doAct("TS_XMGL_KCAP_TJJL", "getKcOrgStatus", {
            XM_ID: this.xmId,
            pvlgDeptCodeStr: userYAPAutoCode
        }, false);
    },
    show: function () {
        this.setData();
        this.submissionArrangementModal.modal({backdrop: false, show: true});
    }

};

var LookJkModal = {
    lookJkModal: '',
    $inJkKsTable: '',
    $outJkKsTable: '',

    initData: function (xmId) {
        this.xmId = xmId;
        this.lookJkModal = $('#lookJkModal');
        this.$inJkKsTable = $('#in-jkKs-table');
        this.$outJkKsTable = $('#out-jkKs-table');

        var $inTbody = this.$inJkKsTable.find('tbody');
        var $outTbody = this.$outJkKsTable.find('tbody');

        var data = this.getData();
        var inJkKsContent = data.inJkKsContent;
        var outJkKsContent = data.outJkKsContent;
        for (var i = 0; i < inJkKsContent.length; i++) {
            var inJkKs = inJkKsContent[i];
            $inTbody.append([
                '<tr>',
                '<td>' + inJkKs.USER_NAME + '</td>',//姓名
                '<td>' + inJkKs.USER_CODE + '</td>',//人力资源编码
                '<td>' + inJkKs.DEPT_NAME + '</td>',//所属机构
                '<td>' + inJkKs.JK_DEPT_NAME + '</td>',//借考分行

                '</tr>'
            ].join(''));
        }

        for (var i = 0; i < outJkKsContent.length; i++) {
            var outJkKs = outJkKsContent[i];
            $outTbody.append([
                '<tr>',
                '<td>' + outJkKs.USER_NAME + '</td>',//姓名
                '<td>' + outJkKs.USER_CODE + '</td>',//人力资源编码
                '<td>' + outJkKs.DEPT_NAME + '</td>',//所属机构
                '<td>' + outJkKs.JK_DEPT_NAME + '</td>',//借考分行
                '</tr>'
            ].join(''));
        }
    },

    getData: function () {
        return FireFly.doAct('TS_XMGL_KCAP_DAPCC', 'getJkKsContent', {
            xmId: this.xmId,
            deptCodeStr: Utils.getUserYAPAutoCode()
        });
    },
    show: function (xmId) {
        if (this.lookJkModal === '') {
            this.initData(xmId);
        }
        this.lookJkModal.modal({backdrop: false, show: true});
    }
};

var UpdateCCModal = {
    updateccModal: '',//modal
    updateCCTree1: '',//tree1
    updateCCTree2: '',//tree2

    initData: function () {
        this.updateccModal = $('#updateccModal');
        this.setUpdateCCTree1();
        this.setUpdateCCTree2();
    },

    /*确定按钮事件*/
    ensure: function () {
        var self = this;
        var tree1Selected = UpdateCCModal.updateCCTree1.jstree().get_selected();
        var tree2Selected = UpdateCCModal.updateCCTree2.jstree().get_selected();

        if (tree1Selected.length <= 0 || tree2Selected.length <= 0) {
            alert('请选择场次');
        } else if (KcObject.getSjById(tree1Selected[0]) === null || KcObject.getSjById(tree2Selected[0]) === null) {
            alert('选中非场次，请重新选择');
        } else if (KcObject.getSjById(tree1Selected[0]).CC_ID !== KcObject.getSjById(tree2Selected[0]).CC_ID) {
            alert('选中考场不相同，请重新选择');
        } else if (tree1Selected[0] === tree2Selected[0]) {
            alert('两边选择的为同一场次，请重新选择');
        } else if (
            (Date.parse(KcObject.getSjById(tree1Selected[0]).SJ_END.replace('-', '/')) - Date.parse(KcObject.getSjById(tree1Selected[0]).SJ_START.replace('-', '/'))) / 1000 / 60
            !== (Date.parse(KcObject.getSjById(tree2Selected[0]).SJ_END.replace('-', '/')) - Date.parse(KcObject.getSjById(tree2Selected[0]).SJ_START.replace('-', '/'))) / 1000 / 60) {
            // var sj1BmKsTime = ;
            // var sj2BmKsTime = ;
            alert('场次时长不同，请重新选择');
        } else {
            FireFly.doAct("TS_XMGL_KCAP_DAPCC", "changeCc",
                {sjId: tree1Selected[0], sjId2: tree2Selected[0]},
                true, false, function () {
                    KcObject.reloadCCInfo();
                    self.updateccModal.modal('hide');
                }
            );
        }
    },

    /*显示modal*/
    show: function () {
        if (this.updateccModal === '') {
            this.initData();
        }
        this.updateccModal.modal({backdrop: false, show: true});
    },

    getTreeData: function () {
        var ccTreeNodes = KcObject.getCCTreeNodes();
        if (ccTreeNodes === null || ccTreeNodes.length === 0) {
            ccTreeNodes = [];
        }
        return ccTreeNodes;
    },

    setUpdateCCTree1: function () {
        var self = this;

        var $ccJstree = $('#updateCCTree1');
        $ccJstree.html('');
        var kcTreeNodes = this.getTreeData();//jsTree构造jstree数据结构
        self.updateCCTree1 = $ccJstree.jstree({
            'core': {
                "multiple": false,
                'data': kcTreeNodes
            }
        });

        $ccJstree.on("changed.jstree", function (e, data) {
//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
        });

    },

    setUpdateCCTree2: function () {
        var self = this;

        var $ccJstree = $('#updateCCTree2');
        $ccJstree.html('');

        var kcTreeNodes = this.getTreeData();//jsTree构造jstree数据结构
        self.updateCCTree2 = $ccJstree.jstree({
            'core': {
                "multiple": false,
                'data': kcTreeNodes
            }
        });

        $ccJstree.on("changed.jstree", function (e, data) {
//                if (data.node.data.KC_ID) {
//                    //选中考场
//                    self.setKcInfo(data.node.data);
//                } else {
//                    var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
//                    //选中场次
//                    self.setCcInfo(data.node.data, parentKcNode.data);
//                }
        });
    }
};

/**
 * 考场场次操作集合
 */
var KcObject = {
    xmId: '',//项目id
    rootData: '',//后端树数据
    rootNodes: '',//处理后的树数据
    kcArr: [],//所有待安排考场
    ccArr: [],//所有待安排场时间
    kctree: {},//场次树jstree对象
    currentCc: '',//当前显示的场次
    currentParentKc: '',//当前显示的考场
    currentYapzwArr: [],//当前场次已安排好的座位信息
    currentType: 'view',//当前场次展示方式 list view
    isRefreshKsContent: true,//是否刷新考生列表

    /*初始化界面数据*/
    initData: function (xmId) {
        this.xmId = xmId;
        this.getInitData(function () {
            this.setCCTree();
            this.setAllKcInfo();
        });//setCCTree setAllKcInfo方法在加载完数据后执行

    },
    /*从后端获取初始化数据并处理*/
    getInitData: function (callback) {
        var self = this;//self指向KcObject
        var deptCodeStr = Utils.getUserYAPAutoCode();
        FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getKcAndCc", {
                "xmId": self.xmId,
                "deptCodeStr": deptCodeStr
            }, false, false, function (data) {
                //处理数据
                if (data !== "") {
                    self.rootData = data.root;
                    self.getCCTreeNodes();
                }
                if (callback) {
                    callback.apply(self);
                }
            }
        );
    },

    /**
     * 数据类型:dept kc cc
     * 获取数据对应的类型
     **/
    getDataType: function (data) {
        if (data.DEPT_ENNAME && data.DEPT_CODE) {
            //有DEPT_ENNAME、DEPT_CODE属性为dept
            return "dept";
        } else if (data.KC_NAME && data.KC_ID) {
            //有KC_NAME、KC_ID属性为kc
            return "kc";
        } else if (data.SJ_CC && data.SJ_ID) {
            return "cc";
        } else {
            return '';
        }
    },

    /**
     * 获取所有考场所属机构数组(未去重)
     */
    getAllKcODeptCode: function () {
        var res = [];
        for (var i = 0; i < this.kcArr.length; i++) {
            var kc = this.kcArr[i];
            res.push(kc.KC_ODEPTCODE);
        }
        return res;
    },

    getCcById: function (id) {
        var result = null;
        for (var i = 0; i < this.ccArr.length; i++) {
            var cc = this.ccArr[i];
            if (cc.CC_ID === id) {
                result = cc;
            }
        }
        return result;
    },

    getSjById: function (id) {
        var result = null;
        for (var i = 0; i < this.ccArr.length; i++) {
            var cc = this.ccArr[i];
            if (cc.SJ_ID === id) {
                result = cc;
            }
        }
        return result;
    },

    getYapzwById: function (id) {
        var result = null;
        for (var i = 0; i < this.currentYapzwArr.length; i++) {
            var yapzw = this.currentYapzwArr[i];
            if (yapzw.YAPZW_ID === id) {
                result = yapzw;
            }
        }
        return result;
    },


    getCCTreeNodes: function () {
        if (this.rootNodes) {
            return this.rootNodes;
        }
        var rootData = this.rootData;
        this.recursiveTreeData(rootData);
        return this.rootNodes;
    },

    /**
     * 递归遍历数据，生成jstree数据
     * @param data
     * @param parentNode
     */
    recursiveTreeData: function (data, parentNode) {
        var node;
        var dataType = this.getDataType(data);
        switch (dataType) {
            case 'dept':
                node = {id: data.DEPT_CODE, text: data.DEPT_NAME, data: data, state: {opened: true}, children: []};
                break;
            case 'kc':
                this.kcArr.push(data);
                node = {id: data.KC_ID, text: data.KC_NAME, data: data, children: []};
                break;
            case 'cc':
                var cc = data;
                var date = cc.SJ_START.substring(0, 10);
                var start = cc.SJ_START.substring(11);
                var end = cc.SJ_END.substring(11);
                var dateStr = date + "(" + start + "-" + end + ")";
                cc.ccTime = dateStr;
                this.ccArr.push(data);
                node = {id: data.SJ_ID, text: data.ccTime, data: data, children: []};
                break;
        }
        if (parentNode) {
            parentNode.children.push(node);
        } else {
            this.rootNodes = [node];
        }
        if (data.CHILD) {
            for (var i = 0; i < data.CHILD.length; i++) {
                var item = data.CHILD[i];
                this.recursiveTreeData(item, node);
            }
        }
    },

    /**
     * 加载考场场次数据
     * _________________                             _________________
     * ---kc
     * -----------------      mergeCells(grid,[0])   -----------------
     * ---cc   |  张三 |              =>             |       |  张三 |
     * -----------------                             -  18   ---------
     * ---dept   |  王五 |                             |       |  王五 |
     * -----------------                             -----------------
     **/
    setCCTree: function () {
        var self = this;

        var $ccJstree = $('#ccJstree');
        var kcTreeNodes = this.getCCTreeNodes();//jsTree构造jstree数据结构
        var jstree = $ccJstree.jstree({
            'core': {
                'data': kcTreeNodes
            }
        });
        self.kctree = jstree;

        $ccJstree.on("changed.jstree", function (e, data) {
            var dataType = self.getDataType(data.node.data);
            if (dataType === 'kc') {
                //选中考场
                self.setKcInfo(data.node.data);
                //显示考场关联机构人员
                KsObject.setKcRelateOrg(data.node.data.KC_ID, '');
            } else if (dataType === 'cc') {
                var parentKcNode = $.jstree.reference(jstree).get_node(data.node.parent);
                //选中场次
                self.setCcInfo(data.node.data, parentKcNode.data);
                //显示考场关联机构人员
                KsObject.setKcRelateOrg(parentKcNode.data.KC_ID, data.node.data.SJ_ID);
            } else if (dataType === 'dept') {
                var parent = data.node.parent;
                KsObject.clearData();
                //去除currentParentKc currentCc
                self.currentParentKc = null;
                self.currentCc = null;
                KsObject.reloadKsOrgTip();
                if (parent === '#') {
                    self.setOrgKcInfo(data.node.data.DEPT_CODE);
                    // self.setKcArrInfo(self.kcArr);
                } else {
                    self.setOrgKcInfo(data.node.data.DEPT_CODE);
                }
//                    var parent = data.node.parent;
//                    if (parent !== '#') {
//                        var deptCode = data.node.data.DEPT_CODE;
//                        KsObject.setInitData(deptCode);
//                    } else {
//                        KcObject.setAllKcInfo();
//                    }
            }
        });
    },

    /**
     * 机构下的考场信息
     */
    setOrgKcInfo: function (deptCode) {
        var kcArrFilter = [];
        for (var i = 0; i < this.kcArr.length; i++) {
            var kc = this.kcArr[i];
            if (kc.KC_ODEPTCODE === deptCode) {
                kcArrFilter.push(kc);
            }
        }
        this.setKcArrInfo(kcArrFilter);
    },

    /**
     * 全考场信息
     */
    setAllKcInfo: function () {
        this.setKcArrInfo(this.kcArr);
    },

    /**
     * 根据kcArr渲染考场信息
     */
    setKcArrInfo: function (kcArr) {
        var $kcTip = $('#kcTip');
        $kcTip.html('');
        var $kcInfo = $('#kcInfo');
        var $kcInfoThead = $kcInfo.find('thead');
        $kcInfoThead.html('');
        var $kcInfoTbody = $kcInfo.find('tbody');
        $kcInfoTbody.html('');

        var ccCount = 0;//场次数

        var ccTimes = [];//kcArrhz中对应的所有场次时间
        //获取所有的场次时间
        for (var i = 0; i < kcArr.length; i++) {
            //考场
            var kc = kcArr[i];
            var ccList = kc.CHILD;

            for (var j = 0; j < ccList.length; j++) {
                //场次
                var cc = ccList[j];
                if (ccTimes.indexOf(cc.ccTime) < 0) {
                    ccTimes.push(cc.ccTime);
                }
            }
        }

        //thead(表头)
        var $headTr = jQuery('<tr style="background-color: #e3e6ea"></tr>');
        $headTr.append('<th>考场</th>');
        for (var i = 0; i < ccTimes.length; i++) {
            var ccTime = ccTimes[i];
            $headTr.append('<th>' + ccTime + '</th>');
        }
        $kcInfoThead.append($headTr);

        var yapTotalCount = 0;

        //tbody
        for (var i = 0; i < kcArr.length; i++) {
            var kc = kcArr[i];
            var $bodyTr = jQuery('<tr style=""></tr>');
            $bodyTr.append('<td>' + kc.KC_NAME + '</td>');
            for (var j = 0; j < ccTimes.length; j++) {
                var ccTime = ccTimes[j];

                function hasCcTime(ccList, ccTime) {
                    var result = null;
                    for (var i = 0; i < ccList.length; i++) {
                        var cc = ccList[i];
                        if (cc.ccTime === ccTime) {
                            result = cc;
                        }
                    }
                    return result;
                }

                var cc;
                if (cc = hasCcTime(kc.CHILD, ccTime)) {
                    ccCount++;
                    var kcZwInfoBean = FireFly.doAct("TS_XMGL_KCAP_YAPZW", "getKcZwInfo", {
                        KC_ID: kc.KC_ID,
                        SJ_ID: cc.SJ_ID
                    });
                    yapTotalCount += parseInt(kcZwInfoBean.yapNum);

                    var yapNumStr = '';
                    if (parseInt(kcZwInfoBean.yapNum) < parseInt(kcZwInfoBean.total)) {
                        yapNumStr = '<span style="color:red;">' + kcZwInfoBean.yapNum + '</span>'
                    } else {
                        yapNumStr = kcZwInfoBean.yapNum + '';
                    }
                    $bodyTr.append('<td>' + yapNumStr + ' / ' + kcZwInfoBean.total + '</td>');
                } else {
                    $bodyTr.append('<td></td>');
                }
            }
            $kcInfoTbody.append($bodyTr);
        }

        var kcIdStr = '';
        for (var i = 0; i < kcArr.length; i++) {
            var kc = kcArr[i];
            kcIdStr += kc.KC_ID + ',';
        }
        if (kcIdStr.length > 0) {
            kcIdStr = kcIdStr.substring(0, kcIdStr.length - 1);
        }
        var kcCountBean = FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getDeptKcCount", {
            XM_ID: xmId,
            KC_ID: kcIdStr
        }, false, false);

        //kcTip
        $kcTip.append([
            '<span style="color:#fff;">当前考场及场次：</span>',
            '考场数:<span id="kcCount" class="tip-yellow">' + kcArr.length + '</span>&nbsp;',
            '场次数：<span id="ccCount" class="tip-yellow">' + ccCount + '</span>&nbsp;',
            '已安排：<span id="yapCount" class="tip-yellow">' + yapTotalCount + '</span>&nbsp;',
            '考生数：<span id="ksCount" class="tip-yellow">' + kcCountBean.remainCount + '/' + kcCountBean.count + '</span>&nbsp;',
        ].join(''));
        /*
         '（借入：<span id="jieruCount" class="tip-yellow">3</span>&nbsp;',
         '借出：<span id="jiechuCount" class="tip-yellow">2</span>&nbsp;',
         '请假：<span id="qjCount" class="tip-yellow">1</span>）',*/

    },

    /**
     * 单个考场的信息
     */
    setKcInfo: function (kc) {
        this.currentCc = {};
        this.currentParentKc = kc;

        var $kcTip = $('#kcTip');
        $kcTip.html('');
        var $kcInfoThead = $('#kcInfo').find('thead');
        $kcInfoThead.html('');
        var $kcInfoTbody = $('#kcInfo').find('tbody');
        $kcInfoTbody.html('');

        //kcTip
        $kcTip.append([
            '<span style="color:#fff;">当前考场及场次：</span>' + kc.KC_NAME,
        ].join(''));
        KsObject.reloadKsOrgTip();

        //thead
        var $headTr = jQuery('<tr style="background-color: #e3e6ea"></tr>');
        $headTr.append('<th>考场</th>');
        for (var i = 0; i < kc.CHILD.length; i++) {
            var cc = kc.CHILD[i];
            $headTr.append('<th>' + cc.ccTime + '</th>');
        }
        $kcInfoThead.append($headTr);

        //tbody
        var $bodyTr = jQuery('<tr style=""></tr>');
        $bodyTr.append('<td>' + kc.KC_NAME + '</td>');
        for (var i = 0; i < kc.CHILD.length; i++) {
            var cc = kc.CHILD[i];
            var kcZwInfoBean = FireFly.doAct("TS_XMGL_KCAP_YAPZW", "getKcZwInfo", {KC_ID: kc.KC_ID, SJ_ID: cc.SJ_ID});
            var yapNumStr = '';
            if (parseInt(kcZwInfoBean.yapNum) < parseInt(kcZwInfoBean.total)) {
                yapNumStr = '<span style="color:red;">' + kcZwInfoBean.yapNum + '</span>'
            } else {
                yapNumStr = kcZwInfoBean.yapNum + '';
            }
            $bodyTr.append('<td>' + yapNumStr + ' / ' + kcZwInfoBean.total + '</td>');
        }
        $kcInfoTbody.append($bodyTr);
    },

    reloadCCInfo: function () {
        this.setCcInfo(this.currentCc, this.currentParentKc, this.currentType);
    },

    _setCcInfoType: function (type) {
        this.currentType = type;
        this.setCcInfo(this.currentCc, this.currentParentKc, type);
    },
    /**
     * 具体场次信息
     */
    setCcInfo: function (cc, parentKc, type) {
        var self = this;
        this.currentCc = cc;
        this.currentParentKc = parentKc;
        var sjId = cc.SJ_ID;
        var kcId = this.currentParentKc.KC_ID;
        if (type !== 'view' && type !== 'list') {
            type = 'view';
        }
        var $kcTip = $('#kcTip');
        $kcTip.html('');
        var $kcInfoThead = $('#kcInfo').find('thead');
        $kcInfoThead.html('');
        var $kcInfoTbody = $('#kcInfo').find('tbody');
        $kcInfoTbody.html('');

        //kcTip
        $kcTip.append([
            '<span style="color:#fff;">当前考场及场次：</span>',
            '' + parentKc.KC_NAME,
            '&nbsp;&nbsp;' + cc.ccTime,
            '&nbsp;最优数：<span id="optimal-number" class="tip-yellow"></span>',
            '&nbsp;已安排：<span id="cc-info-yap-count" class="tip-yellow">' + this.currentYapzwArr.length + '</span>',
            '&nbsp;借考：<span id="cc-info-jk-count" class="tip-yellow">0</span>',
            /*',请假：<span id="cc-info-qj-count" class="tip-yellow">0</span>',*/
            '<div onclick="KcObject._setCcInfoType(\'list\')" style="cursor:pointer;padding: 3px;float: right"><i class="fa fa fa-list-ul"></i></div>',
            '<div onclick="KcObject._setCcInfoType(\'view\')" style="cursor:pointer;padding: 3px;float: right"><i class="fa fa-dashboard" aria-hidden="true"></i></div>',
        ].join(''));

        if (type === 'view') {
            //tbody
            // var ccId = cc.CC_ID;
//                var kcData = FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'byid', {
//                    _PK_: ccId
//                }, false, false);
            //获取考场座位信息
            var zwListBean = FireFly.doAct("TS_KCGL_ZWDYB", 'query', {
                _NOPAGE_: true,
                _WHERE_: " and KC_ID = '" + kcId + "'",
                _ORDER_: " ZW_ZWH_XT asc"
            }, false, false);
            var zwList = zwListBean._DATA_;
            var tData = [], trData, preLetter = null;

            var rows = [];
            var cols = [];
            var zwObject = {};
            for (var i = 0; i < zwList.length; i++) {
                var zw = zwList[i];
                var split = zw.ZW_ZWH_XT.split('-');
                try {
                    var row = split[0];
                    var col = split[1];
                    rows.push(row);
                    cols.push(col);
                    zwObject['' + row + '-' + col] = zw;
                } catch (e) {
                }
            }
            var max_row = Math.max.apply(null, rows);
            var max_col = Math.max.apply(null, cols);

            for (var i = 1; i <= max_row; i++) {
                trData = [];
                for (var j = 1; j <= max_col; j++) {
                    trData.push(zwObject['' + i + '-' + j])
                }
                tData.push(trData);
            }

            // for (var i = 0; i < zwList.length; i++) {
            //     var zw = zwList[i];
            //     if (preLetter !== zw.ZW_ZWH_XT.substring(0, 1)) {
            //         if (trData !== undefined) {
            //             tData.push(trData);
            //         }
            //         trData = [];
            //         preLetter = zw.ZW_ZWH_XT.substring(0, 1);
            //     }
            //     trData.push(zw);
            // }
            // if (trData !== undefined) {
            //     tData.push(trData);
            // }

            for (var i = 0; i < tData.length; i++) {
                trData = tData[i];
                var $tr = jQuery("<tr></tr>");
                for (var j = 0; j < trData.length; j++) {
                    var tdData = trData[j];
                    var $td;
                    if (tdData && tdData.ZW_ID) {
                        if (tdData.ZW_KY === '1') {
                            $td = jQuery('<td id="' + tdData.ZW_ID + '" style="width:10%;" class="can-arrange">' +
                                '   <span style="font-size: 12px;position: relative;top: -8px;left: -6px;">' + tdData.ZW_ZWH_SJ + '</span>' +
                                '   <span class="userName"></span>' +
//                            '   <span class="close">x</span>' +
                                '</td>');
                        } else {
                            $td = jQuery('<td id="' + tdData.ZW_ID + '" style="width:10%;background-color: #efcaba;" ' +
                                '   <span style="font-size: 12px;position: relative;top: -8px;left: -6px;">' + tdData.ZW_ZWH_SJ + '</span>' +
                                '   <span class="userName"></span>' +
//                            '   <span class="close">x</span>' +
                                '</td>');
                        }
                    } else {
                        $td = jQuery('<td id="" style="width:10%;background-color: #efcaba;" >' +
                            '   <span style="font-size: 12px;position: relative;top: -8px;left: -6px;"></span>' +
                            '   <span class="userName"></span>' +
//                            '   <span class="close">x</span>' +
                            '</td>');
                    }

                    $tr.append($td);
                }
                $kcInfoTbody.append($tr);
            }
            //添加放置事件
            this.addDroppableEvent($("#kcInfo").find(".can-arrange"));
            //设置座位信息
            this.setZwForView(sjId);

        } else if (type === 'list') {
            if (Utils.getCanDraggable()) {
                var contextPath = FireFly.getContextPath();
                $kcTip.append('<div style="margin:0 10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>');
                var $addTo = jQuery('<div style="cursor:pointer;padding: 3px 10px;float: right;"><img src="' + contextPath + '/ts/image/down.png" alt="">&nbsp;<span>移入</span></div>');
                <!-- class="fa fa-arrow-down" style="color:green;" aria-hidden="true"-->
                var $remove = jQuery('<div style="cursor:pointer;padding: 3px 10px;float: right;"><img src="' + contextPath + '/ts/image/up.png" alt="">&nbsp;<span>移出</span></div>');
                $remove.unbind('click').bind('click', function () {
                    var $trs = Utils.getTableTbodyCheckedTrs("kcInfo");
                    var idStr = '';
                    for (var i = 0; i < $trs.length; i++) {
                        var $tr = $trs[i];
                        var id = $tr.attr('id');
                        idStr += ',' + id;
                    }
                    idStr.substring(1, idStr.length);
                    FireFly.doAct("TS_XMGL_KCAP_YAPZW", "delete", {_PK_: idStr}, false, false, function (data) {
                        if (data._MSG_.indexOf("ERROR") >= 0) {
                            //后端错误
                        } else {
                            self.setZwListContent(sjId, $kcInfoTbody);
                            KsObject.search();
                        }
                    });
                });
                $addTo.unbind('click').bind('click', function (e) {
                    if (KsObject.searchDeptCode === 'qj') {
                        alert('已请假考生不安排');
                        return;
                    }
                    var tableTbodyCheckedTrs = Utils.getTableTbodyCheckedTrs('ksTable');
                    if (tableTbodyCheckedTrs.length <= 0) {
                        return;
                    }
                    var shIdStr = '';
                    for (var i = 0; i < tableTbodyCheckedTrs.length; i++) {
                        var tr = tableTbodyCheckedTrs[i];
                        shIdStr += ',' + tr.attr('shid');
                    }
                    if (shIdStr.length > 1) {
                        shIdStr = shIdStr.substring(1);
                    }
                    var sjid = self.currentCc.SJ_ID;
                    var ccid = self.currentCc.CC_ID;
                    var sjCC = self.currentCc.SJ_CC;
                    var sjDate = self.currentCc.SJ_START.substring(0, 10);
                    var kcid = self.currentParentKc.KC_ID;
                    // var zwId = $(this).attr('id');

                    FireFly.doAct("TS_XMGL_KCAP_YAPZW", 'saveBeanFromList', {
                        // ZW_ID: zwId,
                        SJ_ID: sjid,
                        SJ_CC: sjCC,
                        SJ_DATE: sjDate,
                        CC_ID: ccid,
                        KC_ID: kcid,
                        SH_ID_STR: shIdStr,
                        XM_ID: self.xmId
                    }, false, false, function (/*data*/) {
                        KsObject.search();
                        KcObject.reloadCCInfo();
                    });
                });
                $kcTip.append($remove);
                $kcTip.append($addTo);
                $kcTip.append('<div style="margin-right:10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>');
            }
            // $kcTip.append([
            //     '<div style="margin:0 10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>',
            //     '<div style="cursor:pointer;padding: 3px 10px;float: right;"><i class="fa fa-arrow-up" style="color:green;"></i><span>移出</span></div>',
            //     '<div style="cursor:pointer;padding: 3px 10px;float: right;"><i class="fa fa-arrow-down" style="color:green;" aria-hidden="true"></i><span>添加</span></div>',
            //     '<div style="margin-right:10px;float: right;height: 20px;width: 1px;background-color: #fff;border-left: 1px solid #7a7c81;"></div>',
            // ].join(''));

            $kcInfoThead.append([
                '<tr style="background-color: #e3e6ea">',
                '   <th><input type="checkbox"></th>',
                '   <th>座号</th>',
                '   <th>一级机构</th>',
                '   <th>二级机构</th>',
                '   <th>三级机构</th>',
                '   <th>人力资源编码</th>',
                '   <th>姓名</th>',
                '   <th>考试名称</th>',
                '   <th>考试级别</th>',
                '   <th>报考数</th>',
                '   <th>IP地址</th>',
                '   <th>备注</th>',
                '</tr>'
            ].join(''));
            //                '   <th>四级机构</th>',

            this.setZwListContent(sjId, $kcInfoTbody);

            // $kcInfoTbody.append([
            //     '<tr>',
            //     '   <td><input type="checkbox"></td>',
            //     '   <td>1</td>',
            //     '   <td>安徽省分行</td>',
            //     '   <td>合肥市分行</td>',
            //     '   <td>包河区支行</td>',
            //     '   <td>宁国路网点</td>',
            //     '   <td>黄虹</td>',
            //     '   <td>初级营销 (公司业务)</td>',
            //     '   <td>初级</td>',
            //     '   <td>2</td>',
            //     '   <td>10.108.11.1</td>',
            //     '   <td>备注</td>',
            //     '</tr>'
            // ].join(''));
            //添加表头复选框变更事件
            Utils.addTableCheckboxChangeEvent('kcInfo');
        }
    },

    setZwForView: function (sjId) {
        this.currentYapzwArr = [];
        var zwListBean = FireFly.doAct("TS_XMGL_KCAP_YAPZW", "getYapZw", {SJ_ID: sjId});
        var zwList = zwListBean._DATA_;
        this.currentYapzwArr = this.currentYapzwArr.concat(zwList);
        KsObject.reloadKsOrgTipFlag = false;
        for (var i = 0; i < zwList.length; i++) {
            var zw = zwList[i];
            var $zw = $('#' + zw.ZW_ID);
            $zw.attr('shid', zw.SH_ID);
            $zw.find('.userName').html(zw.BM_NAME);
            this.setZwItemForView(zw.YAPZW_ID);
        }
        KsObject.reloadKsOrgTipFlag = true;
        this.reloadCCTip();
        KsObject.reloadKsOrgTip();
    },

    /**
     * 刷新tip信息
     */
    reloadCCTip: function () {
        $('#cc-info-yap-count').html(this.currentYapzwArr.length);
        var jkCount = 0/*, qjCount = 0*/;
        for (var i = 0; i < this.currentYapzwArr.length; i++) {
            var currentYapzw = this.currentYapzwArr[i];
            if (currentYapzw.BM_STATUS === '2' || currentYapzw.BM_STATUS === '3') {
                jkCount++;
            }
            /*if (currentYapzw.BM_STATUS === '1' || currentYapzw.BM_STATUS === '3') {
             qjCount++;
             }*/
        }
        // $('#cc-info-qj-count').html(qjCount);
        $('#cc-info-jk-count').html(jkCount);
        // $('optimal-number').html();//最优数
    },

    /**
     * 设置已安排座位列表
     **/
    setZwListContent: function (sjId, $kcInfoTbody) {
        $kcInfoTbody.html('');
        var zwListBean = FireFly.doAct("TS_XMGL_KCAP_YAPZW", "getYapzwContent", {SJ_ID: sjId});
        var zwList = zwListBean._DATA_;
        for (var i = 0; i < zwList.length; i++) {
            var zw = zwList[i];
            $kcInfoTbody.append([
                '<tr id="' + zw.YAPZW_ID + '">',
                '   <td><input type="checkbox"></td>',
                '   <td>' + zw.ZW_ZWH_SJ + '</td>',
                '   <td>' + zw.org1 + '</td>',//一级机构
                '   <td>' + zw.org2 + '</td>',//二级机构
                '   <td>' + zw.org3 + '</td>',//三级机构
                '   <td>' + zw.BM_CODE + '</td>',//四级机构
                '   <td>' + zw.BM_NAME + '</td>',//姓名
                '   <td>' + zw.BM_XL + '-' + (zw.BM_MK === '无模块' ? '' : ('-' + zw.BM_MK )) + '</td>',//考试名称
                '   <td>' + FireFly.getDictNames(FireFly.getDict('TS_XMGL_BM_KSLBK_LV'), zw.BM_TYPE) + '</td>',//级别
                '   <td>' + zw.COUNT + '</td>',//报考数
                '   <td>' + zw.IPZ_IP + '</td>',//ip
                '   <td></td>',//备注
                '</tr>'
            ].join(''));
            // '   <td>' + zw.org4 + '</td>',//四级机构
        }
    },

    /**
     * 根据已安排座位id设置座位属性
     **/
    setZwItemForView: function (yapzwId) {
        var self = this;
        var yapzw = this.getYapzwById(yapzwId);
        var zwId = yapzw.ZW_ID;
        var userName = yapzw.BM_NAME;
        var userCode = yapzw.BM_CODE;
        var shId = yapzw.SH_ID;
        var $zw = $('#' + zwId);

        var deptCode = yapzw.S_ODEPT;
        // FireFly.get
        var deptName = FireFly.getDictNames(FireFly.getDict("TS_ORG_DEPT_ALL"), deptCode);
        // BM_LB BM_XL BM_MK

        var typeName = FireFly.getDictNames(FireFly.getDict("TS_XMGL_BM_KSLBK_LV"), yapzw.BM_TYPE);
        var kslbName = yapzw.BM_LB + '-' + yapzw.BM_XL + (yapzw.BM_MK === '无模块' ? '' : ('-' + yapzw.BM_MK )) + "-" + typeName;
        $zw.attr('title', userName + '\n' + kslbName + '\n' + deptName);
        // $zw.attr('yapzwId', yapzwId);
        $zw.find('.userName').html(userName);
        $zw.attr('shId', shId);

        if (yapzw.JK_FLAG === '3') {//借入
            $zw.css('background', '#66CCFF');
        } else if (yapzw.JK_FLAG === '2') {//借考审批中
            $zw.css('background', '#f8eeba');
        } else {
            $zw.css('background', '#c4ffb3');//普通状态
        }

        if (Utils.getCanDraggable()) {
            //允许拖拉设置拖拉事件
            $zw.droppable("disable");
            //$zw.droppable("destroy");
            var $span = jQuery('<span class="close">x</span>');
            $span.unbind('click').bind('click', function () {
                FireFly.doAct("TS_XMGL_KCAP_YAPZW", "delete", {_PK_: yapzwId}, false, false, function (data) {
                    if (data._MSG_.indexOf("ERROR") >= 0) {
                        //后端错误
                    } else {
                        var $zw = $('#' + zwId);
                        $zw.removeAttr('shId');
                        $zw.droppable("enable");
                        $zw.draggable("disable");
                        $zw.find('.userName').html('');
                        $zw.find('.close').remove();
                        $zw.css('background', '');
                        self.currentYapzwArr.splice(self.currentYapzwArr.indexOf(yapzw), 1);
                        self.reloadCCTip();
                        KsObject.reloadKsOrgTip(1);
                        if (self.isRefreshKsContent) {
                            KsObject.search();
                        }
                    }
                });
            });
            $zw.append($span);

            $zw.draggable({
                // cursor: 'move',
                cursorAt: {left: 0, top: 0},
                containment: 'body',
                appendTo: 'body',
                helper: function (/*event*/) {
                    return [
                        '<div style="/*width:30px;height: 30px;*/background-color: #FFF8DC; border:1px solid #999999;border-radius:3px;padding: 3px;opacity: 0.7;">',//
                        '   <div>' + userName + '</div>',
                        '   <div>' + kslbName + '</div>',
                        '   <div>' + userCode + '</div>',
                        '</div>'
                    ].join('');
                },
            });
            $zw.draggable("enable");

        }

    },


    /**
     * 为座位添加拖拉放置事件
     **/
    addDroppableEvent: function ($element) {
        if (Utils.getCanDraggable()) {
            //允许拖拉
            var self = this;
            $element.droppable({
                activeClass: "droppable-active",//"ui-state-default",  droppable-active
                hoverClass: "droppable-hover",//"ui-state-hover",  droppable-hover
                drop: function (event, ui) {
//                    ccId;
                    //YAPZW_ID
//                    CC_ID
//                    ZW_ID
//                    KC_ID
//                    SH_ID
//                    SJ_ID
                    var sjid = self.currentCc.SJ_ID;
                    var ccid = self.currentCc.CC_ID;
                    var sjCC = self.currentCc.SJ_CC;
                    var sjDate = self.currentCc.SJ_START.substring(0, 10);
                    var kcid = self.currentParentKc.KC_ID;
                    var shId = $(ui.draggable[0]).attr('shId');
                    var zwId = $(this).attr('id');
                    // var userCode = ui.draggable[0].cells[11].innerText.trim();

                    //从座位移动到座位 删除原来的座位  不刷新考生列表
                    if (ui.draggable[0].tagName !== 'TR') {
                        self.isRefreshKsContent = false;
                    }
                    $(ui.draggable[0]).find('.close').click();
                    self.isRefreshKsContent = true;
                    // var _this = this;
                    FireFly.doAct("TS_XMGL_KCAP_YAPZW", 'saveBean', {
                        ZW_ID: zwId,
                        SJ_ID: sjid,
                        SJ_CC: sjCC,
                        SJ_DATE: sjDate,
                        CC_ID: ccid,
                        KC_ID: kcid,
                        SH_ID: shId,
                        XM_ID: self.xmId
                    }, false, false, function (data) {
                        if (data._MSG_.indexOf("ERROR") >= 0) {
                            //后端错误
                            if (data._MSG_.indexOf('IDX_DATE_CC_USER') >= 0) {
                                var bean = FireFly.doAct('TS_XMGL_KCAP_YAPZW', 'getIndexInfo', {
                                    SH_ID: shId,
                                    SJ_DATE: sjDate,
                                    SJ_CC: sjCC,
                                    XM_ID: self.xmId
                                });
                                if (bean.MSG_STR === '') {
                                    alert('同一考生同一场次不能有两个座位');
                                } else {
                                    alert(bean.MSG_STR);
                                }
                            } else {
                                alert('安排座位失败');
                            }
                        } else {
                            self.currentYapzwArr.push(data);
                            self.setZwItemForView(data.YAPZW_ID);
                            // $(ui.draggable[0]);
                            if (ui.draggable[0].tagName === 'TR') {
                                //非列表拖拉进来的 不刷新考生列表
                                KsObject.search();
                            }
                            self.reloadCCTip();
                            KsObject.reloadKsOrgTip(-1);
                        }
                    });

                }
            });
        }
    }
};

var KsObject = {
    xmId: '',
//        deptCode: '',//那个部门下的考生
    kcId: '',//考场id
    ksArr: [],//考生信息
    ksOrgTree: '',
    listPage: new ListPage(),
    /**
     * 初始化考生列表界面
     * @param xmId
     */
    initData: function (xmId) {
        var self = this;
        this.xmId = xmId;
//            this.getKsArr(null, function () {
//                this.setDfpKsContent();
////                this.setKsOrgContent();
//            });
        this._initSearchValue();

        self.listPage.getListData = function (num) {
            var showNum = parseInt(jQuery('#showNumSelect').find("option:selected").val());
            var data = {};
            data._PAGE_ = {};
            data._PAGE_.NOWPAGE = num;
            data._PAGE_.SHOWNUM = showNum;
            return self.getKsArr(data);
        };
        self.listPage.bldTable = function (/*listData*/) {
            self.setDfpKsContent();
        };
        //变更每页显示条数时，重新获取数据
        jQuery('#showNumSelect').on('change', function () {
            self.listPage.search();
        });
    },

    /**
     * 清除界面上的考生信息
     */
    clearData: function () {
        $('#ksOrgTreeContent').html('');
        $('#ksTable').find('tbody').html('');
        $('#ksTablePage').css('display', 'none');
    },

    /**
     * 通过id数组获取对应的考生数据
     * @param ids
     * @returns {Array}
     */
    getKsArrByIds: function (ids) {
        //ids this.ksArr
        var result = [];
        for (var i = 0; i < this.ksArr.length; i++) {
            var ks = this.ksArr[i];
            for (var j = 0; j < ids.length; j++) {
                var id = ids[j];
                if (ks.SH_ID === id) {
                    result.push(ks);
                }
            }
        }
        return result;
    },

    /**
     * 根据考场id展示考场关联的机构
     * @param kcId
     * @param sjId
     */
    setKcRelateOrg: function (kcId, sjId) {
        if (this.kcId !== kcId || $('#ksOrgTreeContent').html() === '') {
            this.kcId = kcId;
            this.sjId = sjId;
            this.setKsOrgContent(kcId);
            this.searchDeptCode = ''; //初始化 机构搜索条件
        } else if (this.sjId !== sjId) {
            this.kcId = kcId;
            this.sjId = sjId;
            // this.reloadKsOrgKsCount();
        }
        this.search();

    },

//     setInitData: function (deptCode) {
//         this.setKsOrgContent(deptCode);
//         this.getKsArr(null, function () {
//             this.setDfpKsContent();
// //                this.setKsOrgContent();
//         });
//     },

    /**
     * 根据条件参数params获取考生信息 -> this.ksArr
     * 并执行回调函数
     * @param params1
     * @param callback
     */
    getKsArr: function (params1, callback) {
        var params = this._getExtWhere();
        var self = this;
        var param = {
            _linkWhere: " and XM_ID='" + self.xmId + "' ",
            _linkServQuery: "2",
            XM_ID: self.xmId
        };
        jQuery.extend(param, params);
        jQuery.extend(param, params1);
        return FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsContent', {data: JSON.stringify(param)}, false, false, function (data) {
            self.ksArr = data._DATA_;
            if (callback) {
                callback.apply(self);
            }
        });
    },


    /**
     * 考生机构
     */
    setKsOrgContent: function (kcId) {
        var self = this;
        kcId = kcId ? kcId : '';
        var param = {kcId: kcId};
        FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsOrgTree', param, false, false, function (data) {
            var root = {
                id: data.DEPT_CODE,
                text: data.DEPT_NAME,
                data: {id: data.DEPT_CODE, text: data.DEPT_NAME},
                state: {opened: true},
                children: []
            };
            var $ksOrgTreeContent = $('#ksOrgTree').find('.content-navTree');
//                $ksOrgTreeContent.html('');
//            var data = FireFly.getDict('SY_ORG_ODEPT_ALL', pdeptCode);
//            var deptName = FireFly.getDictNames(FireFly.getDict('SY_ORG_ODEPT_ALL'), pdeptCode);
//            var root = {id: pdeptCode, text: deptName, data: {id: pdeptCode, text: deptName}, children: []};

            var putChildren = function (parent, childs) {
                childs = childs ? childs : [];
                for (var i = 0; i < childs.length; i++) {
                    var child = childs[i];
                    var id = child.DEPT_CODE ? child.DEPT_CODE : child.ID + ' ';
                    if (data.DEPT_CODE === id.trim()) {
                        continue;
                    }
                    var text = child.DEPT_NAME ? child.DEPT_NAME : child.NAME;
                    var item = {
                        id: id,
                        text: text,
                        data: child,
                        children: []
                    };
                    parent.children.push(item);
                    putChildren(item, child.CHILD);
                }
            };

            var childs = data.CHILD;
            putChildren(root, childs);

            var treeData = [root];
            // var jkListBean = FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getKcJk", {kcId: kcId});
            // if (jkListBean._DATA_ && jkListBean._DATA_.length >= 0) {

            // kcId = KcObject.currentParentKc.KC_ID;
            var sjId = KcObject.currentCc.SJ_ID;

            if (parseInt(KsObject.countKsCount({kcId: kcId, sjId: sjId, isJk: true, totalArrange: true})) > 0) {
                treeData.push({
                    id: 'jk',
                    text: '借考',
                    data: {id: 'jk', text: '借考'},
                    state: {opened: true},
                    children: []
                });
            }
            if (parseInt(KsObject.countKsCount({kcId: kcId, sjId: sjId, isQj: true, totalArrange: true})) > 0) {
                treeData.push({
                    id: 'qj',
                    text: '请假',
                    data: {id: 'qj', text: '请假'},
                    state: {opened: true},
                    children: []
                });
            }

            // }

            try {
                self.ksOrgTree.jstree('destroy');//已经初始化tree，先destroy
            } catch (e) {
            }
            self.ksOrgTree = $ksOrgTreeContent.jstree({
                'core': {
                    "multiple": false,
                    'data': treeData
                }
            });

            $ksOrgTreeContent.on("changed.jstree", function (e, data) {
                // id = id ? id : data.node.data.DEPT_CODE;
                self.searchDeptCode = data.node.id.trim();
                self.search();
            });
            // $ksOrgTreeContent.on("ready.jstree", function (/*e, data*/) {
            //     self.reloadKsOrgKsCount();
            // });

            $ksOrgTreeContent.on("after_open.jstree", function (e, node) {
                var kcId = '';
                var sjId = '';
                if (KcObject.currentParentKc || KcObject.currentCc) {
                    kcId = KcObject.currentParentKc.KC_ID;
                    sjId = KcObject.currentCc.SJ_ID;
                    KsObject.setTreeText(node.node, kcId, sjId);
                }
            });

        });

    },

    w: undefined,

    // reloadKsOrgKsCount: function () {
    //     jQuery.ajax({
    //         type: "POST",
    //         async: true,
    //         success: function (/*data*/) {
    //             setTimeout('KsObject.testTime()', 2000);
    //         }
    //     });
    // },

    setTreeText: function (orgJsonObject, kcId, sjId) {
        for (var i = 0; i < orgJsonObject.children.length; i++) {
            var treeNodeId = orgJsonObject.children[i];
            /*var childOrg*/
            // = childOrg.id;
            // var text = childOrg.data.DEPT_NAME ? childOrg.data.DEPT_NAME : childOrg.data.NAME;
            var node = KsObject.ksOrgTree.jstree(true).get_node(treeNodeId);
            var text = KsObject.ksOrgTree.jstree(true).get_text(node);
            text = text.split('(')[0];
            var count = KsObject.countKsCount({kcId: kcId, sjId: sjId, searchDeptCode: treeNodeId.trim()});
            KsObject.ksOrgTree.jstree(true).set_text(node, text + '(<span style="color: red">' + count + '</span>)');
            // KsObject.setTreeText(childOrg, kcId, sjId);
        }
    },

    // testTime: function () {
    //     var kcId = '';
    //     var sjId = '';
    //     if (KcObject.currentParentKc || KcObject.currentCc) {
    //         kcId = KcObject.currentParentKc.KC_ID;
    //         sjId = KcObject.currentCc.SJ_ID;
    //         var orgJsonObject = KsObject.ksOrgTree.jstree(true).get_json('#', {flat: false})[0];
    //         // setTimeout('', 3000);
    //         KsObject.setTreeText(orgJsonObject, kcId, sjId)
    //         // KsObject.ksOrgTree.jstree().getNode();
    //     }
    // },

    reloadKsOrgTipFlag: true,
    reloadKsOrgTipCount: 0,
    ksOrgTipInfo: {},
    /**
     * 刷新ksOrgTip
     **/
    reloadKsOrgTip: function (noReloadCount) {
        //*this.reloadKsOrgTipFlag = false 不做操作
        if (!this.reloadKsOrgTipFlag) {
            return;
        }
        var count = 0;
        var totalCount = 0;
        var kcId = '';
        var sjId = '';
        if (KcObject.currentParentKc || KcObject.currentCc) {
            kcId = KcObject.currentParentKc.KC_ID;
            sjId = KcObject.currentCc.SJ_ID;
            //*this.reloadKsOrgTipFlag = false 不做操作
            //*noReload 为false 向后端请求
            //*noReload 为-2 -1 1 2 3 已安排-2 -1 +1 +2 +3
            if (sjId === this.ksOrgTipInfo.sjId && noReloadCount) {
                count = this.ksOrgTipInfo.count + noReloadCount;
                totalCount = this.ksOrgTipInfo.totalCount;
            } else {
                count += parseInt(this.countKsCount({kcId: kcId, sjId: sjId}));
                count += parseInt(this.countKsCount({kcId: kcId, sjId: sjId, isJk: true}));
                totalCount += parseInt(this.countKsCount({kcId: kcId, sjId: sjId, isJk: false, totalArrange: true}));
                totalCount += parseInt(this.countKsCount({kcId: kcId, sjId: sjId, isJk: true, totalArrange: true}));
            }

            // $('#ksOrgTipKsCount').html(count);
        }
        /*else {
         $('#ksOrgTipKsCount').html('');
         }*/
        if (totalCount === 0) {
            //考生数为0，不显示
            $('#ksOrgTip').html('');
        } else {
            this.ksOrgTipInfo = {kcId: kcId, sjId: sjId, count: count, totalCount: totalCount};
            $('#ksOrgTip').html('[<span class="tip-red" id="ksOrgTipKsCount">' + count + '</span>' +
                '/' +
                '<span class="tip-red" id="ksOrgTipKsTotalCount">' + totalCount + '</span>]');
        }
        //ksOrgTipKsCount   ksOrgTipKsTotalCount
    },

    /**
     * 统计考生数
     * {kcId:kcId,
     * sjId:sjId,
     * isJk:isJk,
     * totalArrange:false}
     * kcId必需项
     *
     */
    countKsCount: function (ksParams) {
        var kcId = ksParams.kcId,
            sjId = ksParams.sjId,
            isJk = ksParams.isJk,
            isQj = ksParams.isQj,
            totalArrange = ksParams.totalArrange,
            searchDeptCode = ksParams.searchDeptCode;
        var self = this;
        var params1 = {};
        params1._PAGE_ = {};
        params1._PAGE_.NOWPAGE = 1;
        params1._PAGE_.SHOWNUM = 1;
        if (isJk) {
            params1.searchDeptCode = 'jk';
        }
        if (isQj) {
            params1.searchDeptCode = 'qj';
        }
        if (totalArrange) {
            params1.isArrange = 'false';
        }
        var params = {
            _linkWhere: " and XM_ID='" + self.xmId + "' ",
            _linkServQuery: "2",
            XM_ID: self.xmId,
            /**/
            searchDeptCode: searchDeptCode,
            searchKcId: kcId,
            searchSjId: sjId
        };
        // jQuery.extend(param, params);
        jQuery.extend(params, params1);
        var doAct = FireFly.doAct("TS_XMGL_KCAP_DAPCC", 'getKsContent', {data: JSON.stringify(params)}, false, false);
        return doAct._PAGE_.ALLNUM;
    },


    /**
     * 考生信息
     **/
    setDfpKsContent: function () {
        var ksArr = this.ksArr;
        var $ksTable = $('#ksTable');
        var $ksTableTbody = $ksTable.find('tbody');
        $ksTableTbody.html('');
        for (var i = 0; i < ksArr.length; i++) {
            var ks = ksArr[i];
//                var bmCode = ks.BM_CODE;
//                var bmMk = ks.BM_MK;
//                var bmMkName = ks.BM_MK__NAME;
//                var bmName = ks.BM_NAME;
//                var bmType = ks.BM_TYPE;
//                var bmTypeName = ks.BM_TYPE__NAME;
//                var bmXl = ks.BM_XL;
//                var shId = ks.SH_ID;
//                var odeptName = ks.S_ODEPT__NAME;
//                var bmxlName = ks.BM_XL__NAME;
            $ksTableTbody.append([
                '<tr scope="row" id="' + ks.SH_ID + '" shId="' + ks.SH_ID + '">',
                '   <td><input type="checkbox"></td>',
                '   <td>' + (i + 1) + '</td>',//序号
                '   <td>' + ks.org1 + '</td>',//一级机构
                '   <td>' + ks.org2 + '</td>',//二级机构
                '   <td>' + ks.org3 + '</td>',//三级机构
                '   <td>' + ks.org4 + '</td>',//四级机构
                '   <td>' + ks.BM_NAME + '</td>',//姓名
                '   <td>' + ks.BM_XL + (ks.BM_MK === '无模块' ? '' : ('-' + ks.BM_MK)) + '</td>',//考试名称
                '   <td>' + FireFly.getDictNames(FireFly.getDict('TS_XMGL_BM_KSLBK_LV'), ks.BM_TYPE) + '</td>',//级别
                '   <td>' + ks.COUNT + '</td>',//报考数
                // '   <td>' + ks.STATUS + '</td>',//状态
                '   <td>' + ks.BM_CODE + '</td>',//人力资源编码
                '</tr>'
            ].join(''));

        }
        //添加表头复选框变更事件
        Utils.addTableCheckboxChangeEvent('ksTable');
        $('#ksTablePage').css('display', 'block');

        if (Utils.getCanDraggable() && KsObject.searchDeptCode !== 'qj') {
            //允许拖拉
            $ksTable.find("tbody tr").draggable({
                // cursor: 'move',
                cursorAt: {left: 33, top: 55},
                containment: 'body',
                appendTo: 'body',
                helper: function (event) {
                    var cells = event.currentTarget.cells;
                    return [
                        '<div style="/*width:30px;height: 30px;*/background-color: #FFF8DC; border:1px solid #999999;border-radius:3px;padding: 3px;opacity: 0.7;">',//
                        '   <div>' + cells[6].innerText + '</div>',
                        '   <div>' + cells[7].innerText + '</div>',
                        '   <div>' + cells[10].innerText + '</div>',
                        '</div>'
                    ].join('');
                },
            });
        }
    },

    search: function () {
        // var self = this;
        this.listPage.search();
        //条件  请求  渲染
        // this.getKsArr(null, function () {
        //     self.setDfpKsContent();
        // });
    },

    /*获取搜索条件*/
    _getExtWhere: function () {
        var searchName = $('#search-name').val();
        var searchLoginName = $('#search-login-name').val();
        var searchBmXl = $('#search-bm-xl').val();
        var searchBmMk = $('#search-bm-mk').val();
        var searchBmJb = $('#search-bm-jb').val();
        var searchBmCount = $('#search-bm-count').val();
        return {
            searchDeptCode: this.searchDeptCode,
            searchKcId: this.kcId,
            searchSjId: this.sjId,
            searchName: searchName,
            searchLoginName: searchLoginName,
            searchBmXl: searchBmXl,
            searchBmMk: searchBmMk,
            searchBmJb: searchBmJb,
            searchBmCount: searchBmCount
        };
//            return [];
//            alert('searchName:' + searchName + '\n'
//                + 'searchLoginName:' + searchLoginName + '\n'
//                + 'searchBmXl:' + searchBmXl + '\n'
//                + 'searchBmMk:' + searchBmMk + '\n'
//                + 'searchBmJb:' + searchBmJb,//FireFly.getDictNames(FireFly.getDict('TS_XMGL_BM_KSLBK_LV'), searchBmJb) + '\n'
//                +'searchBmCount:' + searchBmCount + '\n');
    },

    _initSearchValue: function () {
        var self = this;
        //KSLBK_NAMEDF
//            KSLBK_XL
//            KSLBK_MK
//            TS_XMGL_BM_KSLBK_LV
        //search-bm-xl search-bm-mk search-bm-jb
        var xlDictData = Utils.getDictData("", 'KSLBK_XL');
        var jbDictData = FireFly.getDict('TS_XMGL_BM_KSLBK_LV')[0].CHILD;
        Utils.setOptionData('search-bm-xl', xlDictData);
        var xlSelect = $('#search-bm-xl');
        xlSelect.on('change', function () {
            var option = xlSelect.val();
            var mkDictData = Utils.getDictData(" and KSLBK_XL='" + option + "'", 'KSLBK_MK');
            Utils.setOptionData('search-bm-mk', mkDictData);
        });
        Utils.setOptionData('search-bm-jb', jbDictData);
    },

};

var Utils = {

    _canDraggable: undefined,//是否允许拖拉安排考生 外部获取请使用getCanDraggable方法

    /**
     * 是否允许拖拉安排考位
     * @param reloadFlag 是否刷新
     */
    getCanDraggable: function (reloadFlag) {

        if (this._canDraggable === undefined || reloadFlag) {
            this._canDraggable = false;

            var xmBean = FireFly.doAct("TS_XMGL", "byid", {"_PK_": xmId}, false);
            if (xmBean.XM_KCAP_PUBLISH_TIME !== '') {
                //项目发布不允许拖拉
            } else {
                var deptCodeStr = this.getUserYAPAutoCode();
                deptCodeStr = deptCodeStr === undefined ? '' : deptCodeStr;
                var split = deptCodeStr.split(',');
                for (var i = 0; i < split.length; i++) {
                    var itemDeptCode = split[i];
                    if (itemDeptCode !== '') {
                        var queryBean = FireFly.doAct("TS_XMGL_KCAP_TJJL", "query", {
                            '_NOPAGE_': true,
                            '_extWhere': " and TJ_DEPT_CODE ='" + itemDeptCode + "' and XM_ID ='" + xmId + "'"
                        });
                        //提交后不允许拖拉
                        this._canDraggable = queryBean._DATA_.length <= 0;
                    }
                }
            }

        }
        return this._canDraggable;
    },


    /**
     * 获取总的未安排考生数
     * @returns {*}
     */
    getRemainingKsInfo: function () {
        var kcIdStr = '';
        var kcArr = KcObject.kcArr;
        for (var i = 0; i < kcArr.length; i++) {
            var kc = kcArr[i];
            kcIdStr += kc.KC_ID + ",";
        }
        if (kcIdStr.length > 0) {
            kcIdStr = kcIdStr.substring(0, kcIdStr.length - 1);
        }
        return FireFly.doAct("TS_XMGL_KCAP_DAPCC", "getDeptKcCount", {
            XM_ID: xmId,
            KC_ID: kcIdStr
        }, false, false);
    },

    /**
     * 获取当前登录用户考位安排权限
     * @returns {*}
     */
    getUserYAPPvlgCode: function (type) {
        var userPvlg = FireFly.getUserPvlg(System.getUser("USER_CODE"));
        var code;
        try {
            code = userPvlg.TS_XMGL_KCAP_YAPZW_PVLG[type].ROLE_DCODE;
        } catch (e) {
            code = '';
        }
        return code;
    },
    /**
     * 获取当前登录用户考位发布权限
     * @returns {*}
     */
    getUserYAPPublishCode: function () {
        return this.getUserYAPPvlgCode("publish");
    },
    /**
     * 获取当前登录用户考位安排权限
     * @returns {*}
     */
    getUserYAPAutoCode: function () {
        return this.getUserYAPPvlgCode("auto");
        // var userPvlg = FireFly.getUserPvlg(System.getUser("USER_CODE"));
        // var code;
        // try {
        //     code = userPvlg.TS_XMGL_KCAP_YAPZW_PVLG.auto.ROLE_DCODE;
        // } catch (e) {
        //     code = '';
        // }
        // return code;
    },
    /**
     * 表格添加全选/全不选功能（复选框）
     * @param tableId table id
     */
    addTableCheckboxChangeEvent: function (tableId) {
        var $table = $('#' + tableId);
        var $thCheckbox = $table.find('th input[type="checkbox"]');
        if ($thCheckbox.length >= 0) {
            //th checkbox 全选/全不选 事件
            $($thCheckbox[0]).unbind('change').bind('change', function () {
                var $tdCheckboxs = $table.find('td input[type="checkbox"]');
                for (var i = 0; i < $tdCheckboxs.length; i++) {
                    var tdCheckbox = $tdCheckboxs[i];
                    tdCheckbox.checked = this.checked;
                }
            });
            //td checkbox td中checkbox变更，改变th checkbox
            var tdCheckboxs = $table.find('td input[type="checkbox"]');
            tdCheckboxs.unbind('change').bind('change', function () {
                if ($thCheckbox[0].checked && !this.checked) {
                    $thCheckbox[0].checked = false;
                } else {
                    var allChecked = true;
                    for (var i = 0; i < tdCheckboxs.length; i++) {
                        var tdCheckbox = tdCheckboxs[i];
                        if (!tdCheckbox.checked) {
                            allChecked = false;
                        }
                    }
                    $thCheckbox[0].checked = allChecked;
                }
            });
        }
    },

    getTableTbodyCheckedTrs: function (tableId) {
        var result = [];
        var $table = $('#' + tableId);
        var $trs = $table.find('tbody tr');

        for (var i = 0; i < $trs.length; i++) {
            var $tr = jQuery($trs[i]);
            var $checkBox = $tr.find('td input[type="checkbox"]');
            if ($checkBox[0].checked) {
                result.push($tr);
            }
        }
        return result;
    },

    setOptionData: function (selectId, dictData, selectValue) {
        selectValue = selectValue ? selectValue : '';
        var select = $('#' + selectId);
        //移除原有的选项
        select.find('option').remove();
        var opt = [];
        opt.push("<option value=''></option>");
        jQuery.each(dictData, function (i, dictItem) {
            opt.push("<option value='");
            opt.push(dictItem.ID);
            opt.push("'");
            if (dictItem.ID === selectValue) {
                opt.push(" selected ");
            }
            opt.push(">");
            opt.push(dictItem.NAME);
            opt.push("</option>");
        });
        jQuery(opt.join("")).appendTo(select);
    },

    //获取级联下拉框数据 (考试类别库 TS_XMGL_BM_KSLBK)
    getDictData: function (linkWhere, codeName) {
        var data = {
            "_SELECT_": codeName,
            "_ORDER_": codeName,
            "_AFTER_SELECT_KEYWORDS": "distinct",
            "_linkWhere": linkWhere + " and " + codeName + " is not null ",
            "_NOPAGE_": "true"
        };
//            var result = FireFly.getListData("TS_XMGL_BM_KSLBK", data, false);
        var result = FireFly.doAct("TS_XMGL_BM_KSLBK", 'query', data);
        var dictData = [];
        for (var i = 0; i < result._DATA_.length; i++) {
            var dict = result._DATA_[i];
            dictData.push({ID: dict[codeName], NAME: dict[codeName]});
        }
        return dictData;
    },

    /**
     * 数组去重
     * @param arr 数组
     * @returns {Array}
     */
    arrayUnique: function (arr) {
        var res = [];
        var json = {};
        for (var i = 0; i < arr.length; i++) {
            if (!json[arr[i]]) {
                res.push(arr[i]);
                json[arr[i]] = 1;
            }
        }
        return res;
    }

};
