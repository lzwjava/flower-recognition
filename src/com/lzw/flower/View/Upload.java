package com.lzw.flower.View;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lzw on 14-5-6.
 */
public class Upload {
  public static void uploadFile(String actionUrl, String uploadPath) {
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";
    try {
      URL url = new URL(actionUrl);
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setDoInput(true);
      con.setDoOutput(true);
      con.setUseCaches(false);
      con.setRequestMethod("POST");
      con.setRequestProperty("Connection", "Keep-Alive");
      con.setRequestProperty("Charset", "UTF-8");
      con.setRequestProperty("Content-Type",
          "multipart/form-data;boundary=" + boundary);
      DataOutputStream ds =
          new DataOutputStream(con.getOutputStream());
      ds.writeBytes(twoHyphens + boundary + lineEnd);
      String newName = "newName";
      ds.writeBytes("Content-Disposition: form-data; " +
          "name=\"file1\";filename=\"" +
          newName + "\"" + lineEnd);
      ds.writeBytes(lineEnd);

      File uploadFile = new File(uploadPath);
      FileInputStream fStream = new FileInputStream(uploadFile);
      int bufferSize = 1024;
      byte[] buffer = new byte[bufferSize];
      int length = -1;
      while ((length = fStream.read(buffer)) != -1) {
        ds.write(buffer, 0, length);
      }
      ds.writeBytes(lineEnd);
      ds.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
      fStream.close();
      ds.flush();
      InputStream is = con.getInputStream();
      int ch;
      StringBuffer b = new StringBuffer();
      while ((ch = is.read()) != -1) {
        b.append((char) ch);
      }
      ds.close();
    } catch (Exception e) {
    }
  }
}
