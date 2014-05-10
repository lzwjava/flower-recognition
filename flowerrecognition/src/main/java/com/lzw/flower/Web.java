package com.lzw.flower;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-4-30.
 */
public class Web {
  public static List<Data> getDatas() {
    List<Data> datas = new ArrayList<Data>();
    try {
      JSONObject json = new JSONObject(App.json);
      boolean valid = json.getBoolean("valid");
      if (valid) {
        JSONArray arr = json.getJSONArray("images");
        for (int i = 0; i < arr.length(); i++) {
          Data data = new Data();
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

  public static void downloadBitmaps(List<Data> datas) throws IOException {
    for (Data data : datas) {
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
