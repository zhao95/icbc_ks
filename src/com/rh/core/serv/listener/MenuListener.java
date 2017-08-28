package com.rh.core.serv.listener;

import java.util.ArrayList;
import java.util.List;
import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.MenuServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.serv.dict.DictMgr;
import com.rh.core.serv.util.ServUtils;
import com.rh.core.util.Constant;
import com.rh.core.util.device.Device;
import com.rh.core.util.device.DeviceUtils;
import com.rh.core.util.device.Device.DeviceType;

/**
 * 监听获取菜单
 * 
 * @author wangchen
 */
public class MenuListener {
    /**
     * 执行后
     * 
     * @param act 操作
     * @param paramBean 参数bean
     * @param menu 结果
     */
    public void after(String act, ParamBean paramBean, OutBean menu) {
        if (menu.getStr(Constant.RTN_MSG).indexOf(Constant.RTN_MSG_ERROR) < 0) {
            if (act.equals("menu")) {
                // 当前客户端
                Device dev = DeviceUtils.getCurrentDevice(Context.getRequest());
                List<Bean> menuTree;
                // 根据user-agent判断当前客户端能否显示菜单
                if (!paramBean.contains(MenuServ.LEFTMENU)) {
                    menuTree = menu.getList(MenuServ.TOPMENU);
                } else {
                    menuTree = menu.getList(MenuServ.LEFTMENU);
                }
                List<Bean> newMenuTree = new ArrayList<Bean>();
                // 递归过滤菜单
                this.filterMenu(menuTree, newMenuTree, dev);
            }
        }
    }

    /**
     * 访问当前系统的设备与该菜单的适用的设备不符合时剔除该菜单、替换系统配置
     * 
     * @param menuTree 菜单数据
     * @param newMenuTree 过滤之后的菜单数据
     * @param dev 访问当前系统的设备
     * 
     */
    private void filterMenu(List<Bean> menuTree, List<Bean> newMenuTree, Device dev) {
        DeviceType devType = dev.getDeviceType();

        for (Bean menuBean : menuTree) {

            // 1 访问当前系统的设备与该菜单的适用的设备不符合时剔除该菜单
            String menuDevTypes = menuBean.getStr("MENU_DEV_TYPES");
            if (!menuDevTypes.isEmpty() && !menuDevTypes.contains(devType.toString())) {
                continue;
            }

            // 2 替换系统配置
            String menuInfo = menuBean.getStr("INFO");
            if (!menuInfo.isEmpty()) {
                menuInfo = ServUtils.replaceSysVars(menuInfo);
                menuBean.set("INFO", menuInfo);
            }

            if (menuBean.contains(DictMgr.CHILD_NODE)) { // 如果存在子节点
                List<Bean> subChilds = menuBean.getList(DictMgr.CHILD_NODE);
                menuBean.remove(DictMgr.CHILD_NODE);

                newMenuTree.add(menuBean);

                if (subChilds.size() > 0) { // 存在子节点
                    List<Bean> newMenus = new ArrayList<Bean>();
                    menuBean.set(DictMgr.CHILD_NODE, newMenus);
                    filterMenu(subChilds, newMenus, dev);
                }
            } else { // 不存在子节点，直接添加
                newMenuTree.add(menuBean);
            }
        }
    }
}
