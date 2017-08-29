<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    final String CONTEXT_PATH = request.getContextPath();
%>
<%@ include file="sy/base/view/inHeader.jsp"%>
<%@ page import="com.rh.core.serv.ServDao"%>
<%@ page import="com.rh.core.serv.ServMgr"%>
<%@ page import="com.rh.ts.pvlg.mgr.GroupMgr"%>
<%@ page import="com.rh.core.base.Bean"%>
<%@ page import="java.text.SimpleDateFormat"%>

<!-- /.box -->
<%--可申请的报名列表--%>

<%
    //获取所有项目ID
    String user_code = userBean.getStr("USER_CODE");
    Bean paramBean = new Bean();
    String qz = GroupMgr.getGroupCodes(user_code);
    Bean outBean = ServMgr.act("TS_XMGL","getXmList",paramBean );
    String xmlist = outBean.getStr("xid");
    String[] xmarray = xmlist.split(",");
    //将可见的 项目 ID 放到新的数组中
    List<String>  kjxm = new ArrayList<String>();
    //遍历项目ID  匹配项目和本人的 群组权限
    for(int a=0;a<xmarray.length;a++){
        paramBean.set("xmid", xmarray[a]);
        Bean outBeanCode = ServMgr.act("TS_XMGL_RYGL_V","getCodes",paramBean);
        String codes = outBeanCode.getStr("rycodes");
        Boolean boo = false;
        if(codes==""){
        }else{
            //本人所在的群组编码
            String[] codeArray = codes.split(",");
            String[] qzArray = qz.split(",");
            for(int b=0;b<qzArray.length;b++){
                if(Arrays.asList(codeArray).contains(qzArray[b])){
                    boo=true;
                }
            }
        }
        //可见的项目id
        if(boo==true){
            kjxm.add(xmarray[a]);
        }
    }
    //从已报名的考试中找到已报名的考试信息   判断是否报名了  报的是什么
    String where = "AND BM_CODE="+"'"+user_code+"'";
    List<Bean> baominglist = ServDao.finds("TS_BMLB_BM",where);
    List<String> stringlist = new ArrayList<String>();
    if(baominglist.size()!=0){

        for(int a=0;a<baominglist.size();a++){
            //获取报名的 项目信息  的name  将报名项目名称放到array中
            String XM_ID = baominglist.get(a).getStr("XM_ID");
            if(XM_ID!=""){
                stringlist.add(XM_ID);
            }
        }
    }
%>

<%
    List<Bean> showList =new ArrayList<>();

    String servId = "TS_XMGL";
    List<Bean> list = ServDao.finds(servId,"");

    int j=0;
    for(int i=0;i<list.size();i++) {
//										j++;
        Bean bean = list.get(i);
        String name = bean.getStr("XM_NAME");
        //项目中已存在array的  title  数据  将展示在  已报名信息中
        String id = bean.getStr("XM_ID");
        if (stringlist.contains(id) || !kjxm.contains(id)) {
            //已报名这个考试之后  或者他不能报名这个考试 中断循环 继续开始
//											j--;
            continue;
        }

        String dept = bean.getStr("XM_FQDW_NAME");
        String type = bean.getStr("XM_TYPE");
        String where1 = "AND XM_ID=" + "'" + id + "'";
        List<Bean> listbean = ServDao.finds("TS_XMGL_BMGL", where1);
        Bean bmbean = listbean.get(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = bmbean.getStr("BM_START");
        String endTime = bmbean.getStr("BM_END");
        String state = "未开始";
//											String display = "none";
        if (startTime != "" && endTime != "") {
            Date date1 = sdf.parse(startTime);
            Date date2 = sdf.parse(endTime);
            Date date = new Date();
            if (date.getTime() < date2.getTime() && date.getTime() > date1.getTime()) {
                state = "待报名";
//													display = "block";
            } else if (date.getTime() > date2.getTime()) {
                state = "已结束";
            }
        }


        Bean showBean =new Bean();
        showBean.set("name",name);
        showBean.set("dept",dept);
        showBean.set("startTime",startTime);
        showBean.set("endTime",endTime);
        showBean.set("state", state);
        showList.add(showBean);
    }
%>

<div class="panel panel-default" id="apply-panel">

    <div class="panel-heading" style="background-color: transparent">
        <h3 class="panel-title">
            可申请的报名 (<span id="keshenqingbaomingSum" style="color:red"><%=showList.size()%></span>)
            <a href="<%=CONTEXT_PATH%>/ts/jsp/bm.jsp"
               class="index-list-more-a">
                更多
                <span style="color:red;">></span>
            </a>
        </h3>
        <div style="width: 124px;height: 3px;position: relative;top: 11px;left: -5px;background-color: #ff0000;">
        </div>
    </div>
    <div class="panel-body">
        <table class="rhGrid  JColResizer" id="apply-table">
            <thead class="">
            <tr style="backGround-color:WhiteSmoke; height: 30px">
                <th class="" style="width: 40%;">名称</th>
                <th class="" style="width: 30%;">组织单位</th>
                <th class="" style="width: 30%;">报名时间</th>
            </tr>
            </thead>
            <tbody class="grid-tbody">

            < <tr>
                <td >2017年（第8周）资格考试报名</td>
                <td >中国工商银行总行</td>
                <td>2017-05-07 00:00 － 05-16 00:00</td>
            </tr>
            <tr>
                <td >2017年非资格考试报名</td>
                <td >北京分行</td>
                <td>2017-05-07 00:00 － 05-16 00:00</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>