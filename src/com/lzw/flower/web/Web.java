package com.lzw.flower.web;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.lzw.flower.result.FlowerData;
import com.lzw.flower.base.ImageLoader;
import com.lzw.flower.utils.Logger;
import com.lzw.flower.utils.PathUtils;
import com.lzw.flower.base.App;
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
  public static String ID="id";
  public static String STATUS="status";
  public static String FORE="fore";

  public static List<FlowerData> getDatas() {
    List<FlowerData> datas = new ArrayList<FlowerData>();
    try {
      JSONObject json = new JSONObject(App.json);
      boolean valid = json.getBoolean("valid");
      if (valid) {
        JSONArray arr = json.getJSONArray("images");
        for (int i = 0; i < arr.length(); i++) {
          FlowerData data = new FlowerData();
          JSONObject image = arr.getJSONObject(i);
          data.typeName = image.getString("typeName");
          data.typeDesc = image.getString("typeDesc");
          data.imageUrl = image.getString("ImageUrl");
          data.infoUrl = image.getString("InfoUrl");
          datas.add(data);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return datas;
  }

  public static void downloadBitmaps(List<FlowerData> datas) throws IOException {
    for (FlowerData data : datas) {
      String imageUrl = data.imageUrl;
      data.flower=getBitmapCacheNet(imageUrl,0);
    }
  }

  public static Bitmap getBitmapFromUrlByStream(String urlStr) throws IOException {
    HttpURLConnection conn;
    URL url = new URL(urlStr);
    conn = (HttpURLConnection) url.openConnection();
    BufferedInputStream bif = Web.getBufferedInput(conn);
    return BitmapFactory.decodeStream(bif);
  }

  public static Bitmap getBitmapFromUrlByStream1(String urlStr) throws IOException {
    String tmpPath= PathUtils.getBitmapPath();
    downloadUrlToPath(urlStr,tmpPath);
    Logger.d("download succeed");
    return BitmapFactory.decodeFile(tmpPath);
  }

  public static void downloadUrlToPath(String url2, String path) {
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
      if(conn!=null){
        conn.disconnect();
      }
    } catch (Exception e) {
      e.printStackTrace();
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

  public static Bitmap getBitmapCacheNet(String imgUrl, int width) throws IOException {
    Bitmap bitmap = null;
    ImageLoader imageLoader=ImageLoader.getInstance();
    if (imgUrl != null) {
      bitmap = imageLoader.getBitmapFromMemoryCache(imgUrl);
      if (bitmap == null) {
        bitmap = getBitmapFromUrlByStream(imgUrl);
      }
    }
    return bitmap;
  }
}
