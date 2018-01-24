package com.rh.ts.util;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.base.db.Transaction;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 考场安排工具类
 * Created by shenh on 2018/1/24.
 */
public class KcUtils {

    /**
     * 获取自动分配权限编码roleCode
     *
     * @return autoRoleCode
     */
    public static String getAutoPvlgCode() {
        String servId = "TS_XMGL_KCAP_YAPZW";
        Bean tsXmglKcapYapzwPvlg = RoleUtil.getPvlgRole(Context.getUserBean().getCode(), servId);
        Bean auto = tsXmglKcapYapzwPvlg.getBean(servId + "_PVLG").getBean("auto");
        return auto.getStr("ROLE_DCODE");
    }

    public static String getKcIdStr(String xmId) {
        String autoPvlgCode = KcUtils.getAutoPvlgCode();
        List<Bean> kcList = KcUtils.getKcList(xmId, autoPvlgCode);
        List<String> kcIdList = new ArrayList<String>();
        for (Bean bean : kcList) {
            String kcId = bean.getStr("KC_ID");
            kcIdList.add(kcId);
        }
        return StringUtils.join(kcIdList.iterator(), ",");
    }

    /**
     * 获取权限范围内的所有可分配考场
     *
     * @param xmId
     * @param rolePvlgDeptCode
     * @return
     */
    public static List<Bean> getKcList(String xmId, String rolePvlgDeptCode) {
        List<Object> values = new ArrayList<Object>();
        values.add(xmId);

        //根据用户权限code（deptCodeStr）过滤考场
        String[] splitDeptCode = rolePvlgDeptCode.split(",");
        StringBuilder deptBuilder = new StringBuilder("and ( ");
        for (String deptCode : splitDeptCode) {
            values.add("%" + deptCode + "%");
            deptBuilder.append(" e.CODE_PATH like ? ").append("or ");
        }
        String deptSql = deptBuilder.substring(0, deptBuilder.length() - 3) + ")";

        //查询出所有有权限的考场
        // xmId:该项目 a待安排考场 a.XM_ID
        // roleDeptCode:当前用户所拥有的权限  e考场所属机构 e.CODE_PATH like %roleDeptCode%
        //b考场信息 c考场关联机构 d考场关联机构信息
        return Transaction.getExecutor().query("select a.*,d.DEPT_CODE,b.KC_ODEPTCODE from TS_XMGL_KCAP_DAPCC a " +
                "INNER JOIN TS_KCGL b on b.KC_ID =a.KC_ID " +
                "INNER JOIN ts_xmgl_kcap_gljg c on c.KC_ID=a.KC_ID " +
                "LEFT JOIN SY_ORG_DEPT d on d.DEPT_CODE = c.JG_CODE " +
                "LEFT JOIN SY_ORG_DEPT e on b.KC_ODEPTCODE = e.DEPT_CODE " +
                "where a.XM_ID=? " + deptSql, values);
    }
}
