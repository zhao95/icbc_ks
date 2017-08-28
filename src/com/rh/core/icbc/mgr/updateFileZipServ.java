package com.rh.core.icbc.mgr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.rh.core.serv.OutBean;
import com.rh.core.serv.ParamBean;
import com.rh.core.util.DateUtils;
import com.rh.core.util.file.Zip;

/**
 * 系统升级文件打包服务类
 * 
 * @author caoyiqing
 *
 */
public class updateFileZipServ {

	private Logger log = Logger.getLogger(getClass());

	/**
	 * 检查所有提交文件是否都是存在的
	 * 
	 * @param fileList
	 *            文件路径
	 * @param absolutePath
	 *            系统的绝对路径
	 * @return 文件结果列表
	 */
	public OutBean checkFiles(String fileList, String absolutePath) {

		OutBean out = new OutBean();
		if (StringUtils.isEmpty(fileList)) {
			return out.setError("文件列表为空！");
		}

		String[] files = fileList.split("\r\n");
		Map<String, String> map = new HashMap<String, String>();
		// 检查文件列表，将结果存到map中，以文件路径为key；以检查结果为value
		for (String file : files) {
			String tempFile = getRelativePath(file);
			String filePath = tempFile.trim();
			if (StringUtils.isEmpty(filePath)) {
				continue;
			}
			File updateFile = new File(absolutePath + "/" + filePath);
			if (updateFile.exists()) {
				map.put(file, "是");
				// 查找内部类
				getInnerClass(file, updateFile, map);
			} else {
				map.put(file, "否");
			}
		}
		return out.setOk().setData(map);

	}

	/**
	 * 查找文件的内部类
	 * 
	 * @param filePath
	 * @param file
	 * @param map
	 */
	private void getInnerClass(String filePath, File file, Map<String, String> map) {
		String fileName = file.getName();
		// 只有源码文件才会有内部类
		if (fileName.endsWith(".class")) {
			// 无后缀文件名
			String fileNameNS = fileName.substring(0, fileName.length() - 6);
			// 查找相同目录下的内部类文件
			File parent = file.getParentFile();
			File[] brother = parent.listFiles();
			for (File bro : brother) {
				String broName = bro.getName();
				if (broName.startsWith(fileNameNS + "$")) {
					if (bro.exists()) {
						int index = filePath.lastIndexOf("/");
						if (index == -1) {
							index = filePath.lastIndexOf("\\");
						}
						// 获取内部类的相对路径
						String broPath = filePath.substring(0, index + 1).concat(bro.getName());
						map.put(broPath, "内部类");
					}
				}
			}
		}
	}

	/**
	 * 检查单个文件是否存在
	 * 
	 * @param param
	 *            文件路径和系统路径
	 * @return 返回测结果 1.文件路径为空 2.文件存在 3.文件不存在
	 */
	public OutBean checkSingleFile(ParamBean param) {
		OutBean outBean = new OutBean();
		String filePath = param.getStr("FILE_PATH").trim();
		String absolutePath = param.getStr("ABS_PATH").trim();
		if (StringUtils.isEmpty(filePath)) {
			return outBean.setError("文件路径为空！");
		} else {
			String tempFile = getRelativePath(filePath);
			File updateFile = new File(absolutePath +"/"+ tempFile);
			if (updateFile.exists()) {
				return outBean.setOk();
			} else {
				return outBean.setError("文件不存在，请查看路径");
			}
		}
	}

	/**
	 * 压缩打包文件
	 * 
	 * @param param
	 *            文件列表
	 * @return
	 */
	public OutBean doZip(ParamBean param) {
		// 文件路径
		String fileList = param.getStr("FILE_PATH");
		// 系统根路径
		String absolutePath = param.getStr("ABS_PATH");
		String[] files = fileList.split(",");
		FileOutputStream fos;
		try {
			// 日志文件
			File logFile = new File(absolutePath + "/log.txt");
			fos = new FileOutputStream(logFile);
			// 压缩包文件 日期为压缩包名
			Zip zip = new Zip(absolutePath + "/" + DateUtils.getDate() + ".zip");
			// 添加压缩文件
			for (String file : files) {
				if (StringUtils.isNotEmpty(file)) {
					// 文件相对路径
					String tempFile = getRelativePath(file);
					int index = (tempFile.lastIndexOf("\\") == -1) ? tempFile.lastIndexOf("/")
							: tempFile.lastIndexOf("\\");
					if (index != -1) {
						// 文件目录
						String fileDir = tempFile.substring(0, index);
						// 添加的文件
						File updateFile = new File(absolutePath + "/" + file);

						// 添加压缩文件
						zip.addFile(updateFile, fileDir);
						// 写日志文件
						fos.write((file + "\r\n").getBytes());
						continue;
					}
					File updateFile = new File(absolutePath + "/" + file);
					zip.addFile(updateFile); // 添加压缩文件
					fos.write((file + "\r\n").getBytes()); // 写日志文件
				}
			}
			fos.close(); // 关闭文件流
			zip.addFile(logFile); // 添加日志文件
			zip.close(); // 关闭压缩流
			logFile.delete(); // 删除日志文件
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return new OutBean().setError();
		}
		return new OutBean().setOk();
	}
	
	public OutBean readFile(ParamBean param){
		OutBean out = new OutBean();
		String filePath = param.getStr("FILE_PATH");
		if(StringUtils.isNotEmpty(filePath)){
			File file = new File(filePath);
			try {
				String fileStr = FileUtils.readFileToString(file, "utf-8");
				out.setData(fileStr);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				return out.setError();
			}
		}else{
			return out.setError();
		}
		return out.setOk();
	}
	
	/**
	 * 匹配路径，返回相对路径
	 * 匹配以pro、/ 或   相对路径
	 * @param file 路径
	 * @return 相对路径
	 */
	private String getRelativePath(String file) {
		String tempFile;
		if (file.startsWith("pro/")) {
			tempFile = file.substring(4).trim();
		} else if (file.startsWith("/")) {
			tempFile = file.substring(1).trim();
		} else {
			tempFile = file.trim();
		}
		return tempFile;
	}
}
