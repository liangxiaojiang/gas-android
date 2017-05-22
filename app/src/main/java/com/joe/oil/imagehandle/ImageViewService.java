package com.joe.oil.imagehandle;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ImageViewService {
    public static byte[] getImage(String netUrl) {
       byte[] data = null;
       try {
           //建立URL
           URL url = new URL(netUrl);
           HttpURLConnection conn = (HttpURLConnection) url.openConnection();
           conn.setRequestMethod("GET");
           conn.setReadTimeout(5000);
          
           InputStream input = conn.getInputStream();
           data = Util.readInputStream(input);
           input.close();
          
           System.out.println("下载完毕！");
       } catch (MalformedURLException e) {
           e.printStackTrace();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return data;
    }
}
