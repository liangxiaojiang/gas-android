package com.joe.oil.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {
	private URL url = null;

	/**
	 * 根据URL下载文件，前提是这个文件当中的内容是文本，函数的返回值就是文件当中的内容
	 * 1.创建一个URL对象
	 * 2.通过URL对象，创建一个HttpURLConnection对象
	 * 3.得到InputStram
	 * 4.从InputStream当中读取数据
	 *
	 * @param
	 * @return
	 */
	public String download(String urlstr) {
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		try {
			// 创建一个URL对象
			url = new URL(urlstr);
			// 创建一个Http连接
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			// 使用IO流读取数据
			buffer = new BufferedReader(new InputStreamReader(
					urlConn.getInputStream(), "gb2312")); // 防止中文出现乱码  gb2312
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				buffer.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 可以下载字节流文件到SD卡中
	 *
	 * @param urlstr  要下载文件的URI地址
	 * @param Path  在SD卡上文件夹的路径
	 * @param FileName  在SD卡上文件的名称
	 * @return 该函数返回整型：-1代表下载失败，0代表下载成功，1代表文件已经存在
	 */
	public int download(String urlstr, String Path, String FileName) {
		InputStream inputstream = null;
		BufferedReader buffer = null;
		try {
			FileUtils fileUitls = new FileUtils();
			System.out.println(Path + FileName);
			if (fileUitls.isFileExist(Path + FileName)) {
				return 1;
			} else {
				// 获取URI中的字节流
				inputstream = getInputStreamFromUrl(urlstr);
				// 把字节流转换成字符流
				buffer = new BufferedReader(new InputStreamReader(inputstream,
						"gb2312")); // 防止中文出现乱码   UTF-8
				File resultFile = fileUitls.write2SDFromWrite(Path, FileName,
						buffer);
				if (resultFile == null) {
					return -1;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		} finally {
			try {
				if(buffer != null)
					buffer.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 可以下载字符流和字节流文件到SD卡中
	 *
	 * @param urlstr
	 * @param Path
	 * @param FileName
	 * @return 该函数返回整型：-1代表下载失败，0代表下载成功，1代表文件已经存在
	 */
	public int downFile(String urlstr, String Path, String FileName) {
		InputStream inputstream = null;
		try {
			FileUtils fileUitls = new FileUtils();
			if (fileUitls.isFileExist(Path + FileName)) {
				return 1;
			} else {
				inputstream = getInputStreamFromUrl(urlstr);
				File resultFile = fileUitls.write2SDFromInput(Path, FileName,
						inputstream);
				if (resultFile == null) {
					return -1;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return -1;
		} finally {
			try {
				inputstream.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * 根据URL得到输入流
	 *
	 * @param urlstr
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private InputStream getInputStreamFromUrl(String urlstr)
			throws MalformedURLException, IOException {
		// TODO Auto-generated method stub
		url = new URL(urlstr);
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		InputStream inputStream = urlConn.getInputStream();
		return inputStream;
	}

	/**
	 *
	 * @param strurl 文件url地址
	 * @param path 需要保存文件的路径
	 * @param fileName 保存到本地的文件名
	 * @return
	 */
	public String download1(String strurl,String path,String fileName){

		InputStream is = null;
		OutputStream os = null;
		URL url = null;
		try {
			//创建文件夹
			File f = new File(path);
			if(!f.exists()){
				f.mkdir();
			}
			//创建文件
			File file = new File(path+fileName);
			//判断是否存在文件
			if(file.exists()){
				//创建新文件
				file.createNewFile();
			}else{
				file.delete();
				file.createNewFile();
			}
			//创建并打开连接
			url = new URL(strurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//文件输入流
			is = conn.getInputStream();
			//输出流
			os = new FileOutputStream(file);
			byte buffer[] = new byte[1024];
			int len =0 ;
			while( (len = is.read(buffer))!= -1){
				os.write(buffer,0,len);
			}
			os.flush();
			return "success";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failure";
		}finally{
			try{
				os.close();
				is.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}

	}
}
