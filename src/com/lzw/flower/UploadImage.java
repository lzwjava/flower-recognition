package com.lzw.flower;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UploadImage {
  String multipart_form_data = "multipart/form-data";
  String twoHyphens = "--";
  String boundary = "****************fD4fH3gL0hK7aI6";    // 数据分隔符
  String lineEnd = "\r\n";    // The value is "\r\n" in Windows.

  private void addImageContent(Image[] files, DataOutputStream output) {
    for (Image file : files) {
      StringBuilder split = new StringBuilder();
      split.append(twoHyphens + boundary + lineEnd);
      split.append("Content-Disposition: form-data; " +
          "name=\"" + file.getTag() + "\"; filename=\"" + file.getFilename() + "\"" + lineEnd);
      split.append("Content-Type: " + file.getContentType() + lineEnd);
      split.append(lineEnd);
      try {
        // 发送图片数据
        output.writeBytes(split.toString());
        output.write(file.getData(), 0, file.getData().length);
        output.writeBytes(lineEnd);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void addFormField(Set<Map.Entry<Object, Object>> params, DataOutputStream output) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Object, Object> param : params) {
      sb.append(twoHyphens + boundary + lineEnd);
      sb.append("Content-Disposition: form-data; name=\"" + param.getKey() + "\"" + lineEnd);
      sb.append(lineEnd);
      sb.append(param.getValue() + lineEnd);
    }
    try {
      output.writeBytes(sb.toString());// 发送表单字段数据
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public String post(String actionUrl, Set<Map.Entry<Object, Object>> params, Image[] files) throws Exception {
    HttpURLConnection conn = null;
    DataOutputStream output = null;
    BufferedReader input = null;
    try {
      URL url = new URL(actionUrl);
      conn = (HttpURLConnection) url.openConnection();
      conn.setConnectTimeout(120000);
      conn.setDoInput(true);        // 允许输入
      conn.setDoOutput(true);        // 允许输出
      conn.setUseCaches(false);    // 不使用Cache
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Connection", "keep-alive");
      conn.setRequestProperty("Content-Type", multipart_form_data +
          "; boundary=" + boundary);

      conn.connect();
      output = new DataOutputStream(conn.getOutputStream());
      Logger.d("add image");
      addImageContent(files, output);    // 添加图片内容

      Logger.d("add params");
      addFormField(params, output);    // 添加表单字段内容

      output.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);// 数据结束标志
      output.flush();

      int code = conn.getResponseCode();
      if (code != 200) {
        throw new RuntimeException("response " + actionUrl + " failed");
      }
      Logger.d("response is="+code);

      input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      StringBuilder response = new StringBuilder();
      String oneLine;
      while ((oneLine = input.readLine()) != null) {
        response.append(oneLine + lineEnd);
      }

      return response.toString();
    } catch (Exception e) {
      throw e;
    } finally {
      // 统一释放资源
      try {
        if (output != null) {
          output.close();
        }
        if (input != null) {
          input.close();
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      if (conn != null) {
        conn.disconnect();
      }
    }
  }


  public static String upload(String actionUrl, int id, String originPath,
                            String handPath) throws Exception {
    Set<Map.Entry<Object, Object>> params = new HashSet<Map.Entry<Object, Object>>();
    JSONObject obj=new JSONObject();
    obj.accumulate(Web.ID,id);
    obj.accumulate(Web.STATUS,"continue");
    Map<String, String> map = new HashMap<String, String>();
    String jsonStr = obj.toString();
    map.put(Web.TEXT, jsonStr);
    for (Map.Entry entry : map.entrySet()) {
      params.add(entry);
    }
    Image[] files = new Image[2];
    files[0] = new Image(originPath, "origin.png", Web.ORIGIN);
    files[1] = new Image(handPath, "hand.png", Web.HAND);
    String jsonRes = new UploadImage().post(actionUrl, params, files);
    Logger.d("result =" + jsonRes);
    return jsonRes;
  }
}
