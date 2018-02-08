package com.rh.ts.qjlb;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.org.UserBean;
import com.rh.core.serv.CommonServ;
import com.rh.core.serv.OutBean;
import com.rh.core.serv.ServDao;
import com.rh.ts.util.TsConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 请假周数和次数 的判断
 *
 * @author shiyun
 */
public class QjNumServ extends CommonServ {

    /**
     * 判断请假次数
     *
     * @param paramBean
     * @return
     */
    public OutBean getFlag(Bean paramBean) {

        String xmId = paramBean.getStr("xm_id");

        //项目详情考试开始时间
        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        String xmKsStartDate = xmBean.getStr("XM_KSSTARTDATA");
        if ("".equals(xmKsStartDate)) {
            return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
        }

        String shids = paramBean.getStr("shids");

        String[] shidArr = shids.split(",");
        //本次想报名的考试次数
        int wannacishu = shidArr.length;
        //总次数
        int cishu = paramBean.getInt("cishu");
        //总周数
        int zhoushu = paramBean.getInt("zhoushu");

        UserBean userBean = Context.getUserBean();

        String code = userBean.getCode();

        SimpleDateFormat simps = new SimpleDateFormat("yyyy");
        Date date = new Date();
        String format = simps.format(date);
        
        List<Bean> finds = ServDao.finds("TS_BM_QJ_NUM", "AND QJ_CODE='" + code + "' AND YEAR(XM_END_TIME) = '"+format+"'");

        if (finds != null && finds.size() != 0) {
            int weeknum = finds.get(0).getInt("WEEK_NUM");
            int cishunum = finds.get(0).getInt("CISHU_NUM");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if (weeknum == zhoushu) {
                //判断是否为同一周
                String starttime = finds.get(0).getStr("XM_START_TIME");

                String endtime = finds.get(0).getStr("XM_END_TIME");

                String ks_time = "";

                //循环遍历  如果不在同一周 不能  进行请假
                for (String shid : shidArr) {
                    List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND SH_ID='" + shid + "'");
                    if (shpasslist != null && shpasslist.size() != 0) {
                        Bean shpassbean = shpasslist.get(0);
                        List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw", "AND SH_ID='" + shpassbean.getId() + "'");
                        if (kslist != null && kslist.size() != 0) {
                            if (!"".equals(kslist.get(0).getStr("SJ_DATE"))) {
                                ks_time = kslist.get(0).getStr("SJ_DATE").split("\\(")[0];
                            } else {
                                ks_time = xmKsStartDate;
//								return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                            }
                        }

                    }

                    try {
                        Date stdate = sdf.parse(starttime);
                        Date endDate = sdf.parse(endtime);


                        if (!"".equals(ks_time)) {
                        } else {
                            ks_time = xmKsStartDate;
//						return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                        }
                        SimpleDateFormat simp = new SimpleDateFormat("yyy-mm-dd");
                        Date ksdate = simp.parse(ks_time);

                        if (ksdate.getTime() > endDate.getTime() || ksdate.getTime() < stdate.getTime()) {
                            //不在项目内
                            return new OutBean().setError("考试周次数已达最大数");
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

            if ((cishunum + wannacishu) > cishu) {
                return new OutBean().setError("考试次数超过上限");
            }
        }
        if (wannacishu > cishu) {
            return new OutBean().setError("考试次数超过上限");
        }
        return new OutBean().set("yes", "true");
    }

    /**
     * 保存请假次数
     *
     * @param paramBean
     * @return
     */
    public OutBean getQx(Bean paramBean) {

        String xmId = paramBean.getStr("xm_id");

        //项目详情考试开始时间
        Bean xmBean = ServDao.find(TsConstant.SERV_XMGL, xmId);
        String xmKsStartDate = xmBean.getStr("XM_KSSTARTDATA");
        if ("".equals(xmKsStartDate)) {
            return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
        }

        String shids = paramBean.getStr("shids");

        String[] shIdArr = shids.split(",");
        //本次想报名的考试次数
        int wannacishu = shIdArr.length;
        //总次数
        int cishu = paramBean.getInt("cishu");
        //总周数
        int zhoushu = paramBean.getInt("zhoushu");

        String code = paramBean.getStr("user_code");

        Bean userBean = ServDao.find("SY_ORG_USER", code);

        String name = userBean.getStr("USER_NAME");

        List<Bean> finds = ServDao.finds("TS_BM_QJ_NUM", "AND QJ_CODE='" + code + "'");

        if (finds != null && finds.size() != 0) {
            //一周  判断是否为同一周
            Bean bean = finds.get(0);

            int pastweeknum = bean.getInt("WEEK_NUM");
            //开始时间
            String startime = bean.getStr("XM_START_TIME");
            //结束时间
            String endtime = bean.getStr("XM_END_TIME");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            String newstartdate = "";

            String newenddate = "";

            String ks_time = "";

            boolean Flag = false;
            if (pastweeknum < zhoushu) {
                //只报考了一周
                //不是同一周   判断 已报名次数 是否超过6   和本次想报名次数
                if ((bean.getInt("CISHU_NUM") + wannacishu) > cishu) {
                    //不能再报名
                    return new OutBean().setError("请假次数超过上限");

                } else {
                    for (String shId : shIdArr) {
                        List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND SH_ID='" + shId + "'");
                        if (shpasslist != null && shpasslist.size() != 0) {
                            Bean shpassbean = shpasslist.get(0);
                            List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw", "AND SH_ID='" + shpassbean.getId() + "'");
                            if (kslist != null && kslist.size() != 0) {
                                if (!"".equals(kslist.get(0).getStr("SJ_DATE"))) {
                                    ks_time = kslist.get(0).getStr("SJ_DATE").split("\\(")[0];
                                } else {
                                    ks_time = xmKsStartDate;
//											return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                                }
                            }


                        }
                        try {
                            Date stdate = sdf.parse(startime);
                            Date endDate = sdf.parse(endtime);
                            if (!"".equals(ks_time)) {
                            } else {
                                ks_time = xmKsStartDate;
//									return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                            }
                            SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd");
                            Date ksdate = simp.parse(ks_time);

                            if (ksdate.getTime() > endDate.getTime() || ksdate.getTime() < stdate.getTime()) {
                                //再加一个考试周
                                Flag = true;
                                //根据报名ID找XM  开始结束时间
                                Bean bmPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shId);
                                String xmid = bmPassBean.getStr("XM_ID");
                                Bean xmbean = ServDao.find("TS_XMGL", xmid);
                                newstartdate = xmbean.getStr("XM_KSSTARTDATA");
                                newenddate = xmbean.getStr("XM_KSENDDATA");
                            }
                        } catch (ParseException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                    }
                    if (Flag) {
                        //可请假  但是第二周了
                        bean.set("WEEK_NUM", pastweeknum + 1);
                        bean.set("CISHU_NUM", bean.getInt("CISHU_NUM") + wannacishu);
                        bean.set("XM_START_TIME", newstartdate);
                        bean.set("XM_END_TIME", newenddate);
                        ServDao.save("TS_BM_QJ_NUM", bean);
                    } else {
                        //同一周  可请假
                        bean.set("CISHU_NUM", bean.getInt("CISHU_NUM") + wannacishu);
                        ServDao.save("TS_BM_QJ_NUM", bean);
                    }


                }
            } else if (pastweeknum == zhoushu) {
                for (String shId : shIdArr) {
                    List<Bean> shpasslist = ServDao.finds("TS_BMSH_PASS", "AND SH_ID='" + shId + "'");
                    if (shpasslist != null && shpasslist.size() != 0) {
                        Bean shpassbean = shpasslist.get(0);
                        List<Bean> kslist = ServDao.finds("ts_xmgl_kcap_yapzw", "AND SH_ID='" + shpassbean.getId() + "'");
                        if (kslist != null && kslist.size() != 0) {
                            if (!"".equals(kslist.get(0).getStr("SJ_DATE"))) {
                                ks_time = kslist.get(0).getStr("SJ_DATE").split("\\(")[0];
                            } else {
                                ks_time = xmKsStartDate;
//									return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                            }
                        }


                    }
                    try {
                        Date stdate = sdf.parse(startime);
                        Date endDate = sdf.parse(endtime);
                        if (!"".equals(ks_time)) {

                        } else {
                            ks_time = xmKsStartDate;
//							return new OutBean().setError("请假失败,获取不到考试时间，请校验！");
                        }
                        SimpleDateFormat simp = new SimpleDateFormat("yyy-mm-dd");
                        Date ksdate = simp.parse(ks_time);

                        if (ksdate.getTime() > endDate.getTime() || ksdate.getTime() < stdate.getTime()) {
                            //再加一个考试周
                            Flag = true;
                            //根据报名ID找XM  开始结束时间
                        }
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }


                }
                if (Flag) {
                    //不可请假
                    return new OutBean().setError("已达最大考试周，不能请假");
                } else {
                    //同一周  可请假
                    bean.set("CISHU_NUM", bean.getInt("CISHU_NUM") + wannacishu);
                    ServDao.save("TS_BM_QJ_NUM", bean);
                }


            }

        } else {
            //没有请过假
            if (wannacishu > cishu) {
                //不能再报名
                return new OutBean().setError("报名次数超过上限");

            } else {
                Bean newbean = new Bean();
                newbean.set("QJ_CODE", code);
                newbean.set("WEEK_NUM", "1");
                newbean.set("CISHU_NUM", wannacishu);
                newbean.set("QJ_NAME", name);
                //根据报名id找项目考试开始结束时间
                Bean bmPassBean = ServDao.find(TsConstant.SERV_BMSH_PASS, shIdArr[0]);
                String xmid = bmPassBean.getStr("XM_ID");
                Bean xmbean = ServDao.find("TS_XMGL", xmid);
                String starttime = xmbean.getStr("XM_KSSTARTDATA");

                String xmendtime = xmbean.getStr("XM_KSENDDATA");

                //开始时间
                newbean.set("XM_START_TIME", starttime);
                //结束时间
                newbean.set("XM_END_TIME", xmendtime);
                ServDao.save("TS_BM_QJ_NUM", newbean);

            }

        }
        OutBean out = new OutBean();
        out.set("yes", "true");
        return out;

    }
}
