package com.rh.core.comm.mind;

import com.rh.core.base.Bean;
import com.rh.core.org.DeptBean;
import com.rh.core.org.UserBean;
import com.rh.core.org.mgr.OrgMgr;
import com.rh.core.serv.ParamBean;

/**
 * 
 * @author yangjy
 *
 */
public class DefaultUserMind extends UserMind {
    
    /**
     * 
     * @param userBean 查看意见用户的用户Bean
     */
    public DefaultUserMind(ParamBean paramBean, UserBean userBean) {
        super(userBean);
    }

    
    /**
     * MIND_DIS_RULE:意见显示规则：1,部门内可见,2,机构内可见,3,机构外可见
     * 
     * @param mindBean 意见记录Bean
     * @return 是否能查看此条意见
     */
    @Override
    protected boolean canView(Bean mindBean) {
        int disRule = mindBean.getInt("MIND_DIS_RULE");
        if (disRule == DISPLAY_RULE_DEPT) {
            String tDeptCode = mindBean.getStr("S_TDEPT");
            if (tDeptCode.equals(this.getViewUser().getTDeptCode())) {
                return true;
            }
        } else if (disRule == DISPLAY_RULE_ORG) {
            String sDeptCode = mindBean.getStr("S_ODEPT");
            if (sDeptCode.equals(this.getViewUser().getODeptCode())) {
                return true;
            }
        } else if (disRule == DISPLAY_RULE_PARENT) {
            String sDeptCode = mindBean.getStr("S_ODEPT");
            // 意见的机构与意见查看人是同一个机构
            if (sDeptCode.equals(this.getViewUser().getODeptCode())) {
                return true;
            }

            DeptBean deptBean = OrgMgr.getDept(sDeptCode);
            String mindOdeptPath = "";
            if (deptBean != null) {
                mindOdeptPath = deptBean.getCodePath();
            }
            String userOdeptPath = this.getViewUser().getODeptCodePath();

            /** 如果意见的机构Path包含当前用户的机构Path，则表示当前用户为上级机构 **/
            if (mindOdeptPath.startsWith(userOdeptPath)) {
                return true;
            }

        } else if (disRule == DISPLAY_RULE_ALL) {
            return true;
        }

        return false;
    }
    
}
