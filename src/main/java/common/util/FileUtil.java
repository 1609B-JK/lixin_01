/** 
 * <pre>项目名称:ssh-03 
 * 文件名称:FileUtil.java 
 * 包名:common.util 
 * 创建日期:2017年3月20日上午11:59:28 
 * Copyright (c) 2017, chenjh123@gmail.com All Rights Reserved.</pre> 
 */  
package common.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/** 
 * <pre>项目名称：ssh-03    
 * 类名称：FileUtil    
 * 类描述：    
 * 创建人：陈教授 chenjh123@gmail.com    
 * 创建时间：2017年3月20日 上午11:59:28    
 * 修改人：陈教授 chenjh123@gmail.com     
 * 修改时间：2017年3月20日 上午11:59:28    
 * 修改备注：       
 * @version </pre>    
 */
public class FileUtil {
	
	public static String uploadFile(String path, String fileName, File file) {
		String p = "/attach/";
		//判断该路径是否存在
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		//生成新的文件名
		String name = UUID.randomUUID().toString() + 
				fileName.substring(fileName.lastIndexOf("."));
		p = p + name;
		//使用io流进行保存
		InputStream is = null;
		OutputStream os = null;
		try {
			//把上传的文件读取到输入流中
			is = new FileInputStream(file);
			//实例化输出流
			os = new FileOutputStream(path + "/" + name);
			//声明byte数组，用来接收每次读取的数据
			byte[] b = new byte[1024];
			//读取第一行，读不到内容则返回-1
			int i = is.read(b);
			while (-1 != i) {
				//把刚刚读取到的内容写出去
				os.write(b);
				os.flush();
				//当把数组中数据写出后，需要读取下段内容
				i = is.read(b);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return p;
	}
	
	public static void loadByteArray(byte[] data, OutputStream os) {
		try {
			os.write(data);
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static byte[] fileToByteArray(File file) {
		InputStream is = null;
		byte[] data = new byte[(int) file.length()];
		try {
			is = new FileInputStream(file);
			is.read(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return data;
	}
	/**
	 * springmvc文件上传
	 * @param file
	 * @return
	 */
	public static String uploadFile(MultipartFile file, HttpServletRequest request) {
		//获取原始名称
		String originalFilename = file.getOriginalFilename();
		//从原始名称中截取后缀名
		String fileSuffix = originalFilename.substring(originalFilename.lastIndexOf("."));
		//生成新的文件名
		String fileName = UUID.randomUUID().toString() + fileSuffix;
		//获取项目上下文路径
		String realPath = request.getSession().getServletContext().getRealPath("/imgs");
		//生成年月日路径(yyyy/mm/dd)
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String format = sdf.format(date);
		//判断该路径是否存在，不存在就创建
		File f = new File(realPath + "/" + format);
		if (!f.exists()) {
			//创建这个路径
			f.mkdirs();
		}
		try {
			file.transferTo(new File(realPath + "/" + format + "/" + fileName));
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "/movies/" + format + "/" + fileName;
	}
	
	/**
	 * 获取文件指纹（md5序列码）
	 * @param is
	 * @param type
	 * @return
	 */
	public static String getMD5(InputStream is, String type) {
		String result = null;
		 try {
			 MessageDigest md = MessageDigest.getInstance(type);
			 byte[] buffer = new byte[8192];
			 int length = -1;
			 while ((length = is.read(buffer)) != -1) {
			     md.update(buffer, 0, length);
			 }
			 byte[] digest = md.digest();
			 StringBuffer strBuffer = new StringBuffer();
			  for (int i = 0; i < digest.length; i++) {
			   strBuffer.append(Integer.toHexString(0xff & digest[i]));
			  }
			  result = strBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		    try {
		    	is.close();
		    } catch (IOException ex) {
		    	
		    }
		}
		return result;
	}
	//下载
	public static boolean downLoadFile(HttpServletRequest request,HttpServletResponse response,String filename,String path){
		boolean flag = false;
	    FTPClient ftpClient = new FTPClient();
	    try {
	      //连接FTP服务器
	      ftpClient.connect(Constant.FTP_HOST, Constant.FTP_PORT);
	      //登录FTP服务器
	      ftpClient.login(Constant.FTP_USER, Constant.FTP_PWD);
	      //验证FTP服务器是否登录成功
	      int replyCode = ftpClient.getReplyCode();
	      if(!FTPReply.isPositiveCompletion(replyCode)){
	        return flag;
	      }
	      //切换FTP目录
	      ftpClient.changeWorkingDirectory(path);
	      FTPFile[] ftpFiles = ftpClient.listFiles();
	      for(FTPFile file : ftpFiles){
	        if(filename.equalsIgnoreCase(file.getName())){
	          
	        	BufferedOutputStream  os =  new BufferedOutputStream(response.getOutputStream());
	       // 8. 对浏览器进行设置========================================================
				//解决浏览器兼容问题
		        if (request.getHeader("User-Agent").toLowerCase().indexOf("firefox") > 0) {
		        	filename = new String(filename.getBytes("GB2312"),"ISO-8859-1");
		        } else {
		        	// 对文件名进行编码处理中文问题
		        	filename = java.net.URLEncoder.encode(filename, "UTF-8");// 处理中文文件名的问题
		        	filename = new String(filename.getBytes("UTF-8"), "GBK");// 处理中文文件名的问题
		        }
		        response.reset();
		        response.setContentType("application/x-msdownload");// 不同类型的文件对应不同的MIME类型
			     // inline在浏览器中直接显示，不提示用户下载
			        // attachment弹出对话框，提示用户进行下载保存本地
			        // 默认为inline方式
		        response.setHeader("Content-Disposition", "attachment;filename="+filename);
	          
	          
	          ftpClient.retrieveFile(file.getName(), os);
	          os.close();
	        }
	      }
	      ftpClient.logout();
	      flag = true;
	    } catch (Exception e) {
	      e.printStackTrace();
	    } finally{
	      if(ftpClient.isConnected()){
	        try {
	          ftpClient.logout();
	        } catch (IOException e) {
	          
	        }
	      }
	    }
	    return flag;
	}

}
