package common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FTPUtil {
	
	public static boolean uploadFile(InputStream is, String fileName, String path) {
		boolean storeResult = false;
		//1、实例化ftp客户端
		FTPClient f = new FTPClient();
		try {
			//2、连接服务器
			f.connect(Constant.FTP_HOST, Constant.FTP_PORT);
			//3、登录验证
			f.login(Constant.FTP_USER, Constant.FTP_PWD);
			//4、判断是否登陆成功
			int reply = f.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				//切换到根路径
				f.changeWorkingDirectory("/");
				//判断是否存在
				//创建文件夹(返回true:服务器上没有，已被创建；返回false:服务器上有，创建失败)
				boolean d = f.makeDirectory(path);
				//切换到刚刚创建完的路径下
				f.changeWorkingDirectory(path);
				//在这个路径下保存文件
				//f.storeFile(new String("黑名单.txt".getBytes("GBK"), "ISO-8859-1"), is);
				//设置文件类型为二进制文件
				f.setFileType(FTPClient.BINARY_FILE_TYPE);
				storeResult = f.storeFile(fileName, is);
				f.logout();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				f.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return storeResult;
	}
	
	
	public static boolean downLoadFile(String path, OutputStream os) {
		boolean boo = false;
		//1、创建ftpclient
		FTPClient ftp = new FTPClient();
		//2、连接服务器
		try {
			ftp.connect(Constant.FTP_HOST, Constant.FTP_PORT);
			//3、登录
			ftp.login(Constant.FTP_USER, Constant.FTP_PWD);
			//4、判断是否登陆成功
			int reply = ftp.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				//5、设置编码
				ftp.setControlEncoding(System.getProperty("file.encoding"));
	            //6、切换路径
				ftp.changeWorkingDirectory("/");
				// 设置文件传输类型为二进制
	            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
				//7、获取某文件
	            boo = ftp.retrieveFile(path, os);
	            ftp.logout();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return boo;
	}
	
	public static boolean downFileListFromFTP(String tempPath, List<String> pathList, List<String> fileNameList) {
		boolean boo = false;
		//1、创建ftpclient
		FTPClient ftp = new FTPClient();
		//2、连接服务器
		try {
			ftp.connect(Constant.FTP_HOST, Constant.FTP_PORT);
			//3、登录
			ftp.login(Constant.FTP_USER, Constant.FTP_PWD);
			//4、判断是否登陆成功
			int reply = ftp.getReplyCode();
			if (FTPReply.isPositiveCompletion(reply)) {
				//5、设置编码
				ftp.setControlEncoding(System.getProperty("file.encoding"));
	            //6、切换路径
				ftp.changeWorkingDirectory("/");
				// 设置文件传输类型为二进制
	            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
	            
	            OutputStream os = null;
	            //循环文件路径集合
	            for (int i = 0; i < pathList.size(); i++) {
					InputStream is = ftp.retrieveFileStream(pathList.get(i));
					//判断文件是否存在
		            if (null == is || ftp.getReplyCode() == 550) {
		            	is = null;
		            	continue;
	                }
	            	os = new FileOutputStream(tempPath + fileNameList.get(i));
	            	boo = ftp.retrieveFile(pathList.get(i), os);
	            	is.close();
	            	os.close();
				}
	            ftp.logout();
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ftp.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return boo;
	}
	
	public static void downLoadZipFile(String tempPath, String tempZipName, List<String> pathList, List<String> fileNameList, HttpServletResponse response) {
		File f = new File(tempPath);
        if (!f.exists()) {
        	//创建临时路径
        	f.mkdirs();
        }
        
        ZipOutputStream zos = null;
        try {
        	// 设置response内容的类型
        	response.setCharacterEncoding("utf-8");
			// 设置头部信息
			response.setHeader("Content-Type", "application/zip");
			response.setHeader("Content-disposition", "attachment;filename="
                            + UUID.randomUUID().toString() + ".zip");
        	//实例化zip输出流
        	zos = new ZipOutputStream(response.getOutputStream());
        	//调用ftp的方法，获取文件输入流的集合
        	boolean boo = downFileListFromFTP(tempPath, pathList, fileNameList);
        	if (boo) {
        		//读取临时路径下所有文件
        		File[] listFiles = f.listFiles();
        		if (null != listFiles && 0 < listFiles.length) {
        			for (int j = 0; j < listFiles.length; j++) {
        				File file = listFiles[j];
        				if (file.isFile() && !file.getName().endsWith(".zip")) {
        					InputStream is = new FileInputStream(file);
        					zos.putNextEntry(new ZipEntry(file.getName()));
        					int len;
        					byte[] buffer = new byte[1024];
        					// 读入需要下载的文件的内容，打包到zip文件
        					while ((len = is.read(buffer)) != -1) {
        						zos.write(buffer);
        					}
        					//文件放置完毕，关闭当前文件
        					zos.closeEntry();
        					is.close();
        				}
        			}
        		}
        	}
        	zos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				zos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//删除临时文件夹及其所有文件
        	deleteDirAndFile(f);
		}
	}
	
//	public static void downLoadZipFile(String tempPath, String tempZipName, List<String> pathList, List<String> fileNameList, HttpServletResponse response) {
//		File f = new File(tempPath);
//        if (!f.exists()) {
//        	//创建临时路径
//        	f.mkdirs();
//        }
//        
//        String strZipPath = tempPath + tempZipName;
//        
//        ZipOutputStream zos = null;
//        try {
//        	// 设置response内容的类型
//        	response.setCharacterEncoding("utf-8");
//			// 设置头部信息
//			response.setHeader("Content-Type", "application/zip");
//			response.setHeader("Content-disposition", "attachment;filename="
//                            + UUID.randomUUID().toString() + ".zip");
//        	//实例化zip输出流
////        	zos = new ZipOutputStream(new FileOutputStream(strZipPath));
//        	zos = new ZipOutputStream(response.getOutputStream());
//        	//调用ftp的方法，获取文件输入流的集合
//        	boolean boo = downFileListFromFTP(tempPath, pathList, fileNameList);
//        	if (boo) {
//        		//读取临时路径下所有文件
//        		File[] listFiles = f.listFiles();
//        		if (null != listFiles && 0 < listFiles.length) {
//        			for (int j = 0; j < listFiles.length; j++) {
//        				File file = listFiles[j];
//        				if (file.isFile() && !file.getName().endsWith(".zip")) {
//        					InputStream is = new FileInputStream(file);
//        					zos.putNextEntry(new ZipEntry(file.getName()));
//        					int len;
//        					byte[] buffer = new byte[1024];
//        					// 读入需要下载的文件的内容，打包到zip文件
//        					while ((len = is.read(buffer)) != -1) {
////        						zos.write(buffer, 0, len);
//        						zos.write(buffer);
//        					}
//        					//文件放置完毕，关闭当前文件
//        					zos.closeEntry();
//        					is.close();
//        				}
//        			}
//        		}
//        	}
//        	zos.flush();
//        	//文件下载
//        	//downLoadZipFileTOClient(response, strZipPath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				zos.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			//删除临时文件夹及其所有文件
//        	deleteDirAndFile(f);
//		}
//	}
	
//	private static void downLoadZipFileTOClient(HttpServletResponse response, String serverFilePath) {
//		InputStream is = null;
//		BufferedInputStream bis = null;
//		OutputStream os = null;
//		BufferedOutputStream bos = null;
//		try {
//			//判断服务器上是否有要下载的zip文件
//			File file = new File(serverFilePath);
//			if (file.exists()) {
//				is = new FileInputStream(serverFilePath);
//				// 放到缓冲流里面
//				bis = new BufferedInputStream(is);
//				
//				response.setCharacterEncoding("utf-8");
//				// 设置response内容的类型
////				response.setContentType("application/x-download");
////				response.setContentType("multipart/form-data");
//				// 设置头部信息
//				response.setHeader("Content-Type", "application/zip");
//				response.setHeader("Content-disposition", "attachment;filename="
//                                + UUID.randomUUID().toString() + ".zip");
//				// 获取文件输出IO流
//				os = response.getOutputStream();
//				bos = new BufferedOutputStream(os);
//				int bytesRead = 0;
//				byte[] buffer = new byte[8192];
//				// 开始向网络传输文件流
//				while ((bytesRead = is.read(buffer)) != -1) {
//					os.write(buffer);
//					// 这里一定要调用flush()方法
//					os.flush();
//				}
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		} finally {
//			try {
//				if(null != bos) {
//					bos.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				if(null != os) {
//					os.flush();
//					os.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				if (null != bis) {
//					bis.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			try {
//				if (null != is) {
//					is.close();
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
	
	/**
	 * 删除文件和路径
	 * @param f
	 */
	public static void deleteDirAndFile(File f) {
		if (f.isDirectory()) {
			File[] listFiles = f.listFiles();
			for (File file : listFiles) {
				if (f.isDirectory()) {
					deleteDirAndFile(file);
				} else {
					file.delete();
				}
			}
		}
		f.delete();
	}

}
