package com.joe.oil.imagehandle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static byte[] readInputStream(InputStream input) {
       ByteArrayOutputStream output = new ByteArrayOutputStream();
       try {
           byte[] buffer = new byte[1024];
           int len = 0;
           while((len = input.read(buffer)) != -1) {
              output.write(buffer, 0, len);
           }
       } catch (IOException e) {
           // TODO Auto-generated catch block
           e.printStackTrace();
       }
       return output.toByteArray();
    }
}
