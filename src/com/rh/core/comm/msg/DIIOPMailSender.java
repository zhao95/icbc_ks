package com.rh.core.comm.msg;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.rh.core.base.Bean;
import com.rh.core.base.Context;
import com.rh.core.comm.ConfMgr;
import com.rh.core.comm.FileMgr;
import com.rh.core.comm.logs.RHLog;
import com.rh.core.org.UserBean;
import com.rh.core.plug.IMailSender;
import com.rh.core.serv.ServDao;
import com.rh.core.serv.ServMgr;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.MIMEEntity;
import lotus.domino.MIMEHeader;
import lotus.domino.NotesException;
import lotus.domino.NotesFactory;
import lotus.domino.Session;
import lotus.domino.Stream;

public class DIIOPMailSender implements IMailSender {
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * 静态发送邮件方法
	 * @param subject 标题
	 * @param content 内容
	 * @param mailTo 发送给
	 */
	public static void sendMail(String subject, String content, String mailTo) {
		DIIOPMailSender sender = new DIIOPMailSender();
		sender.setSubject(subject);
		sender.setBody(content);
		sender.setMailTo(mailTo);
		sender.send();
	}
	
	private String nsfPath; // 类似于mail/admin.nsf
	private String server; // domino服务器IP或者域名
	private String notesID; // 发件人notesID也就是邮箱
	private String loginName; // 发件人登录名
	private String password; // 发件人登录密码
	
	/**
	 * 构造函数
	 */
	public DIIOPMailSender() {
		server = Context.getSyConf("TS_SPECIAL_MAIL_IP", "122.18.173.78");
		nsfPath = Context.getSyConf("TS_SPECIAL_MAIL_DB", "mail/测试应用1.nsf");
		loginName = Context.getSyConf("TS_SPECIAL_MAIL_NAME", "test1");
		password = Context.getSyConf("TS_SPECIAL_MAIL_PSW", "password");
	}
	
	private String content;
	private String cc;
	private String bcc;
	private String mailTo;
	private String subject;
	private boolean important;
	private boolean isReplySign;
	private List<Bean> fileList = new ArrayList<Bean>();

	@Override
	public void send() {
		log.debug("---------------- 邮件发送开始 -------------------");
		log.debug("subject:" + subject);
		log.debug("content:" + content);
		log.debug("mailTo:" + mailTo);
		
		Session session = null;
		Database database = null;
		Document doc = null;
		try {
			// 获取session
			String anonymous = ConfMgr.getConf("DOMINO_ANONYMOUS", "true");
			if (anonymous.equals("true")) {
				session = NotesFactory.createSession(server);
			} else {
				if (loginName == null) {
					UserBean userBean = Context.getUserBean();
					loginName = userBean.getLoginName(); // 统一认证号
				}
				
				if (password == null) {
					password = "";
				}
				
				String ior = NotesFactory.getIOR(server, loginName, password);
				session = NotesFactory.createSessionWithIOR(ior, loginName, password);
				//createSession方法不兼容非匿名情况下的访问Ior文件，所以用createSessionWithIOR代替
//						session = NotesFactory.createSession(server, loginName, password);
			}

			// 得到数据库
			database = session.getDatabase("", nsfPath, false);
			doc = database.createDocument();

			doc.appendItemValue("Form", "Memo");
			// 发件人，正式环境不需要加这个，我们的测试环境需要
			if (anonymous.equals("true")) {
				doc.appendItemValue("Principal", notesID);
			}

			// 收件人
			if (StringUtils.isNotBlank(mailTo)) {
				doc.appendItemValue("SendTo", parseMailAddress(mailTo));
			} else {
				throw new RuntimeException("收件人不能为空");
			}

			// 抄送
			if (StringUtils.isNotBlank(cc)) {
				doc.appendItemValue("CopyTo", parseMailAddress(cc));
			}

			// 密送
			if (StringUtils.isNotBlank(bcc)) {
				doc.appendItemValue("BlindCopyTo", parseMailAddress(bcc));
			}

			// 增加标题
			doc.appendItemValue("Subject", subject);

			MIMEEntity body = doc.createMIMEEntity("Body");
			
			// 增加重要性
			if (important) {
				appendImportance(body);
			}
			
			// 回执标志，需要放到header设置的前面否则正文会丢格式以及附件顺序也会乱
			// 回执标志的位置在重要性和标题下面
			if (isReplySign) {
				doc.appendItemValue("ReturnReceipt", "1");
			}
			
			// 增加正文
			MIMEHeader header = body.createHeader("MIME-Version");
			header.setHeaderVal("1.0");
			header = body.createHeader("Content-Type");

			// 替换正文中内嵌图片的路径
			Bean replaceContentBean = replaceContentUrl(content);
			List<Bean> listInfo = null;
			if (replaceContentBean == null) {
				listInfo = new ArrayList<Bean>();
			} else {
				listInfo = replaceContentBean.getList("FILE_LIST");
			}

			if (listInfo.size() > 0) {
				header.setHeaderValAndParams("multipart/related");
			} else {
				header.setHeaderValAndParams("text/html; charset=UTF-8");
			}

			if (replaceContentBean != null && listInfo.size() > 0) {
				// 替换后的正文
				content = replaceContentBean.getStr("CONTENT");
			}
			// 增加HTML格式的正文。
			appendHtmlBody(content, session, body);

			// 增加邮件正文中内嵌的图片
			appendInlineImages(session, body, listInfo);

			// 增加附件
			appendAttachments(session, body, fileList);

			doc.closeMIMEEntities(true, "body"); // 必须关闭，否则正文格式也会丢失
			doc.send(false);
			doc.save(true); // 先发送后保存才能把邮件保存到发件箱
			
			log.debug("---------------- 邮件发送结束 -------------------");
			// 设置回复和转发状态
			/*if (ConfMgr.getConf("DIIOP_RESPONDED_TO", false)) { // 默认禁止设置回复和转发状态
				setRespondedTo(database, mailBean);
			}*/
		} catch (Exception e) {
			RHLog.error(DIIOPMailSender.class, e);
			Throwable t = e.getCause();
			String failDesc = "发送失败";
			if (t != null && t instanceof NotesException) {
				String msg = t.toString();
				if (msg.contains("Invalid IOR null")) {
					//设置认证失败标志
					failDesc = "统一认证号和密码登陆失败，请重新进行绑定认证";
					log.debug(failDesc);
				} 
			}
			log.error("邮件发送失败：", e);
			throw new RuntimeException(e);
		} finally {
			if (doc != null) {
				try {
					doc.recycle();
				} catch (NotesException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (database != null) {
				try {
					database.recycle();
				} catch (NotesException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (session != null) {
				try {
					session.recycle();
				} catch (NotesException e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}
	
	@Override
	public void addFile(String fileName, String filePath) {
		Bean fileBean = new Bean()
				.set("FILE_NAME", fileName)
				.set("FILE_PATH", filePath);
		fileList.add(fileBean);
	}
	
	/**
	 * 替换正文中内嵌图片的SRC为邮件要求的格式
	 * @param sendConent 正文HTML内容
	 * @return
	 */
	private Bean replaceContentUrl(String sendConent) {
		org.jsoup.nodes.Document doc = Jsoup.parse(sendConent);
		Elements es = doc.select("img[cid]");
		
		// 找不到图片，则返回null
		if (es == null || es.size() == 0) {
			return null;
		}

		List<Bean> listInfo = new ArrayList<Bean>();
		for (Element e : es) {
			String cidValue = e.attr("cid"); // 即我们数据库图片存储的主键
			// CID为空，则不处理
			if (StringUtils.isEmpty(cidValue)) {
				continue;
			}
			e.removeAttr("cid");
			
			String strWidth = e.attr("oldwidth");
			if (strWidth != null && NumberUtils.isNumber(strWidth)) {
				e.attr("width", strWidth);
				e.removeAttr("oldwidth");
			}
			
			Element parent = e.parent();
			if (parent != null) {
				String tagName = parent.tagName();
				if (tagName != null && tagName.equalsIgnoreCase("A")) {
					parent.replaceWith(e);
				}
			}

			Bean file = ServDao.find(ServMgr.SY_COMM_FILE, cidValue);
			// 数据库中找不到文件，则不处理
			if (file == null) {
				continue;
			}
			e.attr("src", "cid:_" + file.getId());
			
			Bean bean = new Bean();
			bean.setId(file.getId());
			bean.set("FILE_ID", file.getStr("FILE_ID"));
			bean.set("FILE_NAME", file.getStr("FILE_NAME"));
			bean.set("FILE_PATH", file.getStr("FILE_PATH"));
			listInfo.add(bean);
		}
		
		Bean result = new Bean();
		result.set("CONTENT", doc.html());
		result.set("FILE_LIST", listInfo);
		return result;
	}
	
	/**
	 * 增加HTML格式正文
	 */
	private void appendHtmlBody(String content, Session session, MIMEEntity body) throws NotesException {
		Stream stream = null;
		try {
			MIMEEntity child = body.createChildEntity();
			stream = session.createStream();
			stream.writeText(content);
			child.setContentFromText(stream, "text/html;charset=\"UTF-8\"", Stream.EOL_NONE);
			child.encodeContent(MIMEEntity.ENC_BASE64);
			stream.close();
			stream = null;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}
	
	/**
	 * 在邮件体内，增加嵌入正文的图片列表
	 */
	private void appendInlineImages(Session session, MIMEEntity body, List<Bean> listInfo)
			throws IOException, NotesException {
		for (Bean bean : listInfo) {
			appendInlineImage(session, body, bean);
		}
	}
	
	/**
	 * 在邮件体内，增加嵌入正文的图片
	 */
	private void appendInlineImage(Session session, MIMEEntity body, Bean bean) throws IOException, NotesException {
		if (bean == null) {
			return;
		}
		InputStream imgIn = null;
		Stream imageStream = null;
		try {
			imgIn = (InputStream) FileMgr.download(bean);
			// 添加内嵌图片
			imageStream = session.createStream();
			MIMEEntity mime = body.createChildEntity();
			MIMEHeader header = mime.createHeader("Content-Disposition");
			header.setHeaderVal("inline;filename=" + bean.getStr("FILE_NAME"));
			header = mime.createHeader("Content-ID");
			header.setHeaderVal("<_" + bean.getStr("FILE_ID") + ">"); // 必须要有下划线与替换的对应
			header = mime.createHeader("Content-Transfer-Encoding");
			header.setHeaderVal("Base64");
			imageStream.setContents(imgIn);
			mime.setContentFromBytes(imageStream, "image/gif", MIMEEntity.ENC_NONE);
			mime.encodeContent(MIMEEntity.ENC_BASE64);
			imageStream.close();
			imageStream = null;
		} finally {
			IOUtils.closeQuietly(imgIn);
			if (imageStream != null) {
				try {
					imageStream.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	/**
	 * 在邮件体中添加附件列表
	 */
	private void appendAttachments(Session session, MIMEEntity body, List<Bean> fileList)
			throws NotesException, UnsupportedEncodingException, IOException {
		if (fileList == null || fileList.size() == 0) {
			return;
		}

		for (Bean fileBean : fileList) {
			appendAttachment(session, body, fileBean);
		}
	}

	/**
	 * 在邮件体中添加附件
	 */
	private void appendAttachment(Session session, MIMEEntity body, Bean fileBean)
			throws NotesException, UnsupportedEncodingException, IOException {
		MIMEEntity child = body.createChildEntity();
		String fileName = MimeUtility.encodeText(fileBean.getStr("FILE_NAME"), "UTF-8", "B");
		MIMEHeader header = child.createHeader("Content-Disposition");
		header.setHeaderVal("attachment; filename=\"" + fileName + "\"");
		InputStream in = null;
		Stream st = null;
		try {
			st = session.createStream();
			in = FileMgr.download(fileBean);
			st.setContents(in);
			String fileMtype = fileBean.getStr("FILE_MTYPE") + ";";
			if (st.getBytes() != 0) {
				child.setContentFromBytes(st, fileMtype, MIMEEntity.ENC_IDENTITY_BINARY);
			}
			st.close();
			st = null;
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (Exception e) {
				}
			}
			IOUtils.closeQuietly(in);
		}
	}
	
	/**
	 * 添加重要性
	 */
	private void appendImportance(MIMEEntity body) throws NotesException {
		MIMEHeader header;
		header = body.createHeader("Importance");
		header.setHeaderVal("High");
		header = body.createHeader("X-Priority");
		header.setHeaderVal("1");
	}
	
	/**
	 * 解析邮箱地址
	 */
	private Vector<String> parseMailAddress(String sentToStr) {
		Vector<String> toVect = new Vector<String>();
		String[] sentTos = sentToStr.split(",");
		for (String sentTo : sentTos) {
			// 解析收件人
			Map<String, String> map = parseAddress(sentTo);
			toVect.add(map.get("ADDRESS"));
		}
		return toVect;
	}
	
	/**
	 * 解析邮件地址和姓名
	 * 
	 * @param mailAddress
	 *            陈胜 <chensheng@ruaho.com>
	 * @return
	 */
	 private Map<String, String> parseAddress(String mailAddress) {
		mailAddress = mailAddress.trim();
		String address = "";
		String name = "";
		
		if (mailAddress.indexOf("<") == 0) { // <chensheng@ruaho.com>
			if (mailAddress.contains(">")) {
				address = mailAddress.substring(1, mailAddress.indexOf(">"));
			} else {
				address = mailAddress.substring(1, mailAddress.length());
			}
			
			if (mailAddress.contains("@")) {
				name = mailAddress.substring(1, mailAddress.indexOf("@"));
			} else if (isNotesID(mailAddress)) { // NotesID
				if (mailAddress.contains("/")) {
					name = mailAddress.substring(1, mailAddress.indexOf("/"));
				} else {
					name = mailAddress.substring(1, mailAddress.length());
				}
			}
		} else if (mailAddress.indexOf("<") > 0) { // 陈胜 <chensheng@ruaho.com>
			if (mailAddress.contains(">")) {
				if (mailAddress.indexOf(">") > mailAddress.indexOf("<")) {
					address = mailAddress.substring(mailAddress.indexOf("<") + 1, mailAddress.indexOf(">"));
				} else {
					address = mailAddress;
				}
			} else {
				address = mailAddress.substring(mailAddress.indexOf("<") + 1, mailAddress.length());
			}
			
			name = mailAddress.substring(0, mailAddress.indexOf("<"));
		} else { // chensheng@ruaho.com
			if (mailAddress.contains(">")) {
				address = mailAddress.substring(0, mailAddress.indexOf(">"));
			} else {
				address = mailAddress;
			}
			
			if (mailAddress.contains("@")) {
				if (mailAddress.contains("/")) {
					name = mailAddress.substring(0, mailAddress.indexOf("/"));
				} else {
					name = mailAddress.substring(0, mailAddress.indexOf("@"));
				}
			} else if (isNotesID(mailAddress)) {
				name = mailAddress.substring(0, mailAddress.indexOf("/"));
			}
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("ADDRESS", address.trim());
		map.put("NAME", name.trim());

		return map;
	}
	
	/**
	 * 是否notesID
	 */
	private boolean isNotesID(String notesID) {
		if (!notesID.contains("@")) {
			Pattern pattern = Pattern.compile("/", Pattern.CASE_INSENSITIVE); // 不区分大小写
			Matcher mt = pattern.matcher(notesID);
			int count = 0;
			while (mt.find()) {
				count++;
				if (count >= 2) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public String getHost() {
		return null;
	}

	@Override
	public void setHost(String host) {
		
	}

	@Override
	public boolean isDebug() {
		return true;
	}

	@Override
	public void setDebug(boolean isDebug) {
		
	}

	@Override
	public boolean isAuth() {
		return false;
	}

	@Override
	public void setAuth(boolean b) {
		
	}

	@Override
	public void setUser(String user) {
		
	}

	@Override
	public void setPassword(String password) {
		
	}

	@Override
	public void setMailTo(String mailTo) {
		this.mailTo = mailTo;
	}

	@Override
	public void setMailFrom(String sender) {
		
	}

	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public void setCc(String cc) {
		this.cc = cc;
	}

	@Override
	public void setBody(String body) {
		this.content = body;
	}

	@Override
	public void setBodyIsHTML(boolean bodyIsHTML) {
		
	}

}
