package com.lzw.flower.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.lzw.flower.base.ImageLoader;
import com.lzw.flower.result.FlowerData;
import com.lzw.flower.utils.BitmapUtils;
import com.lzw.flower.utils.Logger;
import com.lzw.flower.utils.PathUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-4-30.
 */
public class Web {
  public static final String TEXT = "text";
  public static final String ORIGIN = "origin";
  public static final String HAND = "hand";
  public static final String BACK = "back";
  public static final String STATUS_CONTINUE = "continue";
  public static String ID = "id";
  public static String STATUS = "status";
  public static String FORE = "fore";
  public static String STATUS_OK = "ok";
  public static String RESULT = "result";
  public static String RECT = "rect";

  public static List<FlowerData> getDatas(String jsonStr) {
    List<FlowerData> datas = new ArrayList<FlowerData>();
    try {
      JSONObject json = new JSONObject(jsonStr);
      boolean valid = json.getBoolean("valid");
      if (valid) {
        JSONArray arr = json.getJSONArray("images");
        for (int i = 0; i < arr.length(); i++) {
          FlowerData data = new FlowerData();
          JSONObject image = arr.getJSONObject(i);
          data.typeName = image.getString("typeName");
          data.typeDesc = image.getString("typeDesc");
          data.imageUrl = image.getString("ImageUrl");
          //data.infoUrl = image.getString("InfoUrl");
          datas.add(data);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return datas;
  }

  public static void downloadBitmaps(List<FlowerData> datas, int reqWidth) throws Exception {
    for (FlowerData data : datas) {
      String imageUrl = data.imageUrl;
      data.flower = getBitmapCacheNet(imageUrl, reqWidth);
    }
  }

  public static Bitmap getBitmapFromUrlByStream(String urlStr) throws IOException {
    HttpURLConnection conn;
    URL url = new URL(urlStr);
    conn = (HttpURLConnection) url.openConnection();
    BufferedInputStream bif = Web.getBufferedInput(conn);
    return BitmapFactory.decodeStream(bif);
  }

  public static Bitmap getBitmapFromUrlByStream1(String urlStr, int width) throws Exception {
    String tmpPath = PathUtils.getBitmapPath();
    downloadUrlToPath(urlStr, tmpPath);
    return BitmapUtils.decodeSampledBitmapFromPath(tmpPath, width);
  }

  public static void downloadUrlToPath(String url2, String path) throws Exception {
    // TODO Auto-generated method stub
    BufferedInputStream bInput = null;
    BufferedOutputStream bOutput = null;
    try {
      HttpURLConnection conn = null;
      URL url = new URL(url2);
      conn = (HttpURLConnection) url.openConnection();
      bInput = getBufferedInput(conn);
      bOutput = getBufferedOutput(path);
      byte[] buffer = new byte[1024];
      int cnt;
      while ((cnt = bInput.read(buffer)) != -1) {
        bOutput.write(buffer, 0, cnt);
      }
      bOutput.flush();
      if (conn != null) {
        conn.disconnect();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    } finally {
      try {
        if (bInput != null)
          bInput.close();
        if (bOutput != null)
          bOutput.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public static BufferedOutputStream getBufferedOutput(String path)
      throws IOException, FileNotFoundException {
    BufferedOutputStream bOutput;
    File file = new File(path);
    if (file.getParentFile().exists() == false) {
      file.getParentFile().mkdirs();
    }
    file.createNewFile();
    bOutput = new BufferedOutputStream(new FileOutputStream(file));
    return bOutput;
  }

  public static BufferedInputStream getBufferedInput(HttpURLConnection conn)
      throws MalformedURLException, IOException {
    BufferedInputStream bInput;
    conn.setConnectTimeout(5000);
    conn.setReadTimeout(15000);
    conn.setDoInput(true);
    conn.setDoOutput(true);
    bInput = new BufferedInputStream(conn.getInputStream());
    return bInput;
  }

  public static Bitmap getBitmapCacheNet(String imgUrl, int width, int height) throws Exception {
    Bitmap bitmap = null;
    ImageLoader imageLoader = ImageLoader.getInstance();
    if (imgUrl != null) {
      String scaleUrl = imgUrl;
      if (width > 0) {
        if (imgUrl.contains("filename")) {
          scaleUrl = imgUrl + "&maxWidth=" + width;
        } else if (imgUrl.contains("qiniu")) {
          scaleUrl = imgUrl + "?imageView2/2/w/" + width;
        }
      }
      bitmap = imageLoader.getBitmapFromMemoryCache(scaleUrl);
      if (bitmap == null && imgUrl.startsWith("http")) {
        bitmap = getBitmapFromUrlByStream1(scaleUrl, width);
        Logger.d("return bitmap width " + bitmap.getWidth());
        Logger.d("require width=" + width);
        imageLoader.addBitmapToMemoryCache(scaleUrl, bitmap);
      }
    }
    if (bitmap != null) {
      if (width != 0) {
        bitmap = scaleBitmapByWidth(bitmap, width);
      } else if (height != 0) {
        bitmap = scaleBitmapByHeight(bitmap, height);
      }
    }
    return bitmap;
  }

  public static Bitmap getBitmapCacheNet(String imgUrl, int width) throws Exception {
    return getBitmapCacheNet(imgUrl, width, 0);
  }

  public static Bitmap scaleBitmapByWidth(Bitmap bitmap, int width) {
    int h = bitmap.getHeight();
    int w = bitmap.getWidth();
    int dw = width;
    int dh = Math.round(dw * h * 1.0f / w);
    return Bitmap.createScaledBitmap(bitmap, dw, dh, false);
  }

  public static Bitmap scaleBitmapByHeight(Bitmap bitmap, int height) {
    int h = bitmap.getHeight();
    int w = bitmap.getWidth();
    int dh = height;
    int dw = Math.round(dh * w * 1.0f / h);
    return Bitmap.createScaledBitmap(bitmap, dw, dh, false);
  }

  public static String doPost(HttpClient httpClient, String url, String... pairs) {
    String entityContent = null;
    try {
      HttpPost post = new HttpPost(url);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      for (int i = 0; i < pairs.length / 2; i++) {
        params.add(new BasicNameValuePair(pairs[2 * i], pairs[2 * i + 1]));
      }
      post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
      HttpResponse response = httpClient.execute(post);
      int code = response.getStatusLine().getStatusCode();
      if (code == 200 || code == 206 || code == 400) {
        entityContent = EntityUtils.toString(response.getEntity());
      }
      Logger.d("%d code", response.getStatusLine().getStatusCode());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entityContent;
  }
}
