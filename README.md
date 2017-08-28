#数据库信息
IP：172.16.0.2
SID：orclcn
账号：icbc_ks
密码：icbc_ks

#建表规范
1.普通表名称 TS_XXX_XXX
2.流程表名称 TS_XXX_XXX_APPLY
说明：TS + 模块名 + 作用点
建表时加上平台系统字段
普通表下面8个常用字段加上
S_USER	VARCHAR2(40)	Y			
S_TDEPT	VARCHAR2(40)	Y			
S_ODEPT	VARCHAR2(40)	Y			
S_MTIME	VARCHAR2(40)	Y			
S_FLAG	NUMBER(4)	Y			
S_DEPT	VARCHAR2(40)	Y			
S_CMPY	VARCHAR2(40)	Y			
S_ATIME	VARCHAR2(40)	Y
流程表上面8个字段+下面4个流程系统字段			
S_WF_STATE	NUMBER(4)	Y			
S_WF_INST	VARCHAR2(40)	Y			
S_WF_NODE	VARCHAR2(2000)	Y			
S_WF_USER	VARCHAR2(2000)	Y	

列表按钮排序显示顺序：1.刷新    2. 添加 3.删除 4.逻辑删除   5.导入 6.导出     

代码范例：
服务编码 TS_JKGL   列表js文件：TS_JKGL_list.js
1.列表代码添加按钮，绑定事件
2.删除增加验证码
3.修改按钮，删除按钮调用公共方法的实现
		
