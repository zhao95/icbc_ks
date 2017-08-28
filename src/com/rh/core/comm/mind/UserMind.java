package com.rh.core.comm.mind;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.rh.core.base.Bean;
import com.rh.core.base.BeanUtils;
import com.rh.core.base.Context;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.util.Constant;
import com.rh.core.util.Lang;
import com.rh.core.util.Strings;

/**
 * 用于获取指定用户可以查看的审批单的意见
 * 
 * @author yangjy
 */
public abstract class UserMind {

    /**
     * 部门内可见
     */
    public static final int DISPLAY_RULE_DEPT = 1;

    /**
     * 机构内可见
     */
    public static final int DISPLAY_RULE_ORG = 2;

    /**
     * 是否可以完全公开
     */
    public static final int DISPLAY_RULE_ALL = 3;

    /**
     * 本机构及以上机构可见
     */
    public static final int DISPLAY_RULE_PARENT = 4;

    /**
     * 意见类型数据字典
     */
    public static final String DICT_MIND_TYPE = "SY_COMM_MIND_TYPE";

    /**
     * 完整地意见列表
     */
    private List<Bean> mindList = null;

    private UserBean viewUser = null;
    
    private String dataId = "";
    
    private List<Bean> canViewMindList = null;


    /**
     * @param userBean 意见查看人
     */
    public UserMind(UserBean userBean) {
        this.viewUser = userBean;
    }

    /**
     * @return 获取表单主键
     */
    public String getDataId() {
        return this.dataId;
    }
    
    /**
     * 查询符合条件的意见
     * 
     * @param servId 服务ID
     * @param dataId 审批单ID
     */
    public void query(String servId, String dataId) {
        this.dataId = dataId;
        mindList = MindUtils.getMindList(servId, this.dataId, "");
    }

    /**
     * 查询符合条件的意见
     * 
     * @param servId 服务ID
     * @param dataId 审批单ID
     * @param sortType 排序类型
     */
    public void query(String servId, String dataId, String sortType) {
        this.dataId = dataId;
        mindList = MindUtils.getMindList(servId, this.dataId, sortType);
    }

    /**
     * 取得指定意见类型的意见
     * 
     * @param type 意见编码
     * @return 符合条件的意见列表
     */
    public List<Bean> getMindListByType(String type) {
        List<Bean> rtnList = new ArrayList<Bean>();
        type = type + "-";
        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);
            String mindCode = mindBean.getStr("MIND_CODE");
            if (mindCode.startsWith(type) && canView(mindBean)) {
                appendFileID(mindBean);
                rtnList.add(mindBean);
            }
        }

        return rtnList;
    }

    /**
     * 取得指定意见类型的意见
     * 
     * @param type 意见编码
     * @param odeptCode 意见编码
     * @return 符合条件的意见列表
     */
    public List<Bean> getMindListByType(String type, String odeptCode) {
        List<Bean> rtnList = new ArrayList<Bean>();
        final String type1 = type + "-";
        final String type2 = type + "_";
        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);
            if (odeptCode.equals(mindBean.getStr("S_ODEPT"))) {
                String mindCode = mindBean.getStr("MIND_CODE");
                if ((mindCode.startsWith(type1) || mindCode.startsWith(type2)) 
                        && canView(mindBean)) {
                    appendFileID(mindBean);
                    rtnList.add(mindBean);
                }
            }
        }

        return rtnList;
    }

    /**
     * 
     * @param mindCode 意见编码
     * @return 根据意见编码 获取意见的列表
     */
    public List<Bean> getMindListByMindCode(String mindCode) {
        // mindCode可能时逗号分隔的多个意见类型
        Set<String> codeSet = Strings.toSet(mindCode);
        List<Bean> rtnList = new ArrayList<Bean>();
        if (this.canViewMindList != null) {
            for (Bean mindBean : canViewMindList) {
                if (codeSet.contains(mindBean.getStr("MIND_CODE"))) {
                    rtnList.add(mindBean);
                }
            }
        } else {
            for (int i = 0; i < mindList.size(); i++) {
                Bean mindBean = mindList.get(i);
                if (codeSet.contains(mindBean.getStr("MIND_CODE")) && canView(mindBean)) {
                    appendFileID(mindBean);
                    rtnList.add(mindBean);
                }
            }
        }
        return rtnList;
    }

    /**
     * 根据意见编码和组织编码获取意见列表
     * 
     * @param mindCode 意见编码
     * @param odeptCode 机构编码
     * @return 意见Bean的List列表
     */
    public List<Bean> getMindListByMindCode(String mindCode, String odeptCode) {
        List<Bean> rtnList = new ArrayList<Bean>();
        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);
            if (odeptCode.equals(mindBean.getStr("S_ODEPT"))) {
                if (mindCode.equals(mindBean.getStr("MIND_CODE")) && canView(mindBean)) {
                    appendFileID(mindBean);
                    rtnList.add(mindBean);
                }
            }
        }

        return rtnList;
    }

    /**
     * 按照意见编码顺序显示意见
     * @param sCmpy 公司编码
     * @return 取得所有意见编码
     */
    public List<Bean> getMindCodeList(String sCmpy) {
        Bean queryBean = new Bean();

        if (sCmpy.length() > 0) {
            String strWhere = " and S_CMPY = '" + sCmpy + "'";
            queryBean.set(Constant.PARAM_WHERE, strWhere);
        }

        queryBean.set(Constant.PARAM_ORDER, "CODE_SORT ASC");

        return ServDao.finds("SY_COMM_MIND_CODE", queryBean);
    }

    /**
     * 解析MIND_FILE字段，根据逗号分隔成MIND_FILE_ID和MIND_FILE_NAME属性。
     * @param mindBean 意见Bean
     */
    public static void appendFileID(Bean mindBean) {
        if (!mindBean.isEmpty("MIND_FILE")) {
            String mindFile = mindBean.getStr("MIND_FILE");
            mindBean.put("_MIND_FILE_LIST", parseFileInfo(mindFile));
        }
    }

    /**
     * 
     * @param fileInfo 文件信息字符串
     * @return 解析文件信息结果
     */
    private static List<Bean> parseFileInfo(String fileInfo) {
        List<Bean> list = new ArrayList<Bean>();
        String[] files = fileInfo.split(";");
        for (String strFile : files) {
            String[] file = strFile.split(",");
            if (file.length == 2 && StringUtils.isNotEmpty(file[0])
                    && StringUtils.isNotEmpty(file[1])) {
                Bean bean = new Bean();
                bean.put("FILE_ID", file[0]);
                bean.put("FILE_NAME", file[1]);
                list.add(bean);
            }
        }

        return list;
    }
    
    /**
     * 
     * @param mindBean 意见Bean
     * @return 是否能查看此意见的内容
     */
    protected abstract boolean canView(Bean mindBean);

    /**
     * @return 取得所有能包含的意见类型
     */
    public List<Bean> getMindTypeList() {
        return getMindTypeList("");
    }

    /**
     * @param odeptCode 机构编码
     * @return 取得所有能包含的意见类型
     */
    public List<Bean> getMindTypeList(String odeptCode) {
        List<String> types = new ArrayList<String>();
        for (int i = 0; i < mindList.size(); i++) {
            Bean mind = mindList.get(i);

            /** 如果有机构值就过滤 给定机构值的意见列表 */
            if (odeptCode.equals(mind.getStr("S_ODEPT")) || odeptCode.length() == 0) {
                String mindCode = mind.getStr("MIND_CODE");
                if (mindCode.indexOf("-") > 0) {
                    final int pos = mindCode.indexOf("-");
                    final String type = mindCode.substring(0, pos);
                    if (!types.contains(type)) {
                        types.add(type);
                    }
                } else if (mindCode.indexOf("_") > 0) {
                    final int pos = mindCode.indexOf("_");
                    final String type = mindCode.substring(0, pos);
                    if (!types.contains(type)) {
                        types.add(type);
                    }
                }
            }
        }

        List<Bean> list = DictMgr.getItemList(DICT_MIND_TYPE);
        List<Bean> result = new ArrayList<Bean>();

        for (Bean bean : list) {
            String itemCode = bean.getStr("ID");
            if (types.contains(itemCode)) {
                result.add(bean);
            }
        }

        return result;
    }

    /**
     * @return 获取机构的列表 , 按照 dept_sort 顺序 排列
     */
    public List<Bean> getOdeptList() {
        List<Bean> deptList = new ArrayList<Bean>();

        HashMap<String, Bean> odepts = new HashMap<String, Bean>();
        for (int i = 0; i < mindList.size(); i++) {
            Bean mind = mindList.get(i);

            if (canView(mind)) {
                String odeptCode = mind.getStr("S_ODEPT");

                DeptBean deptBean = OrgMgr.getDept(odeptCode);

                if (!odepts.containsKey(odeptCode)) {
                    odepts.put(odeptCode, deptBean);
                    deptList.add(deptBean);
                }
            }
        }

        if (deptList.size() <= 1) { // 小于一个机构， 不用排序了
            return deptList;
        }

        BeanUtils.sort(deptList, "DEPT_LEVEL");

        return deptList;
    }

    /**
     * 
     * @return 返回意见条目的数量
     */
    public int getMindCount() {
        return this.mindList.size();
    }

    /**
     * 
     * @return 意见列表
     */
    public List<Bean> getMindList() {
        if (this.canViewMindList != null) {
            return canViewMindList;
        }
        this.canViewMindList = new ArrayList<Bean>();
        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);
            if (canView(mindBean)) {
                appendFileID(mindBean);
                this.canViewMindList.add(mindBean);
            }
        }
        
        return this.canViewMindList;
    }
    
    /**
     * 取得指定NIID的意见
     * @param niId 流程节点实例ID
     * @return 指定NI_ID的意见Bean，没有则返回NULL。
     */
    public List<Bean> getMindByNIID(String niId) {
        List<Bean> result = new ArrayList<Bean>();
        for (Bean mind : mindList) {
            if (mind.getStr("WF_NI_ID").equals(niId)) {
                appendFileID(mind);
                result.add(mind);
            }
        }
        return result;
    }
    
    /**
     * 取得指定NIID的意见(移动端流程跟踪用)
     * @param niId 流程节点实例ID
     * @return 指定NI_ID的意见Bean，没有则返回NULL。
     */
    public List<Bean> getMindForMBByNIID(String niId) {
        List<Bean> result = new ArrayList<Bean>();
        for (Bean mind : mindList) {
            if (mind.getStr("WF_NI_ID").equals(niId) && canView(mind)) {
                appendFileID(mind);
                result.add(mind);
            }
        }
        return result;
    }

    /**
     * @param odeptCode 机构编码
     * @return 意见列表
     */
    public List<Bean> getMindList(String odeptCode) {
        List<Bean> rtnList = new ArrayList<Bean>();
        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);
            if (mindBean.getStr("S_ODEPT").equals(odeptCode)) {
                if (canView(mindBean)) {
                    appendFileID(mindBean);
                    rtnList.add(mindBean);
                }
            }
        }

        return rtnList;
    }

    /**
     * 
     * @return 所有的意见编码
     */
    public HashSet<String> getMindCodeList() {
        HashSet<String> mindCodeset = new HashSet<String>();

        for (int i = 0; i < mindList.size(); i++) {
            Bean mindBean = mindList.get(i);

            String mindCode = mindBean.getStr("MIND_CODE");
            if (!mindCodeset.contains(mindCode)) {
                mindCodeset.add(mindCode);
            }
        }

        return mindCodeset;
    }

    /**
     * 
     * @return 意见查看用户对象
     */
    public UserBean getViewUser() {
        return viewUser;
    }

    /**
     * 获取所有意见编码下的意见
     * @param code 意见编码
     * @return 意见字符串多个意见用逗号分割
     */
    public String getPrintData(String code) {
        StringBuilder sb = new StringBuilder();
        List<Bean> minds = this.getMindListByMindCode(code);
        int size = minds.size();
        String comm = "<div>";
        String endcomm = "</div>";
        String commbr = "</br>";
        sb.append(comm);
        for (Bean bean : minds) {
            String strbegin = "<p style='line-height:18px;'>";
            String strend = "</p>"; // 两条以上需要添加换行
            sb.append(strbegin).append(bean.getStr("MIND_CONTENT"))
                    .append(",");
            if (bean.isNotEmpty("BD_UNAME")) {
                sb.append(bean.getStr("BD_UNAME"));
                sb.append("(").append(bean.getStr("S_UNAME"));
                sb.append("授权)");
            } else {
                sb.append(bean.get("S_UNAME"));
            }
            sb.append("(").append(bean.get("MIND_TIME"))
                    .append(")").append(strend);
            if (size > 1) {
                sb.append(commbr);
            }
            size--;
        }
        sb.append(endcomm);
        return sb.toString();
    }
   /**
    * 获取不同机构的的意见
    * @param code 意见编码
    * @param odeptLevel 机构级别
    * @return 意见列表
    */
    public List<Bean> getMindListByOdeptLevel(String code, int odeptLevel) {
        List<Bean> result = new ArrayList<Bean>();
        List<Bean> minds = this.getMindListByMindCode(code);
        for (Bean mind : minds) {
            String odeptCode = mind.getStr("S_ODEPT");
            DeptBean deptBean = OrgMgr.getDept(odeptCode);
            if (deptBean.getLevel() == odeptLevel) {
                result.add(mind);
            }
        }

        return result;
    }
   /**
    *  获取意见不同级别机构的意见
    * @param code 意见编码
    * @param odeptLevel 机构级别
    * @return 意见字符串多个以逗号分割
    */
    public String getPrintData(String code, int odeptLevel) {
        StringBuilder sb = new StringBuilder();
        List<Bean> minds = this.getMindListByOdeptLevel(code, odeptLevel);
        int size = minds.size();
        String comm = "<div>";
        String endcomm = "</div>";
        String commbr = "</br>";
        sb.append(comm);
        for (Bean bean : minds) {
            String strbegin = "<p style='line-height:18px;'>";
            String strend = "</p>"; // 两条以上需要添加换行
            sb.append(strbegin).append(bean.getStr("MIND_CONTENT"))
                    .append(",").append(bean.get("S_UNAME"))
                    .append("(").append(bean.get("MIND_TIME"))
                    .append(")").append(strend);
            if (size > 1) {
                sb.append(commbr);
            }
            size--;
        }
        sb.append(endcomm);
        return sb.toString();
    }
    
    /**
     * 创建UserMind实例
     * 
     * @param userBean 用户Bean
     * @return UserMind实例
     */
    public static UserMind create(ParamBean paramBean, UserBean userBean) {
        String conf = Context.getSyConf("SY_USER_MIND_IMPL_CLS", "");
        if (StringUtils.isEmpty(conf)) {
            return new DefaultUserMind(paramBean, userBean);
        }
        
        @SuppressWarnings("unchecked")
        Class<UserMind> cls = (Class<UserMind>) Lang.loadClass(conf);
        try {
            Constructor<UserMind> con = cls.getConstructor(ParamBean.class, UserBean.class);
            UserMind userMind = con.newInstance(paramBean, userBean);
            return userMind;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
