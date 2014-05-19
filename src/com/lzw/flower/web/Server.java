package com.lzw.flower.web;

import com.lzw.flower.utils.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-5-6.
 */
public class Server {
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
      if (code == 200 || code == 206 || code==400) {
        entityContent = EntityUtils.toString(response.getEntity());
      }
      Logger.d("%d code", response.getStatusLine().getStatusCode());
    } catch (Exception e) {
      e.printStackTrace();
    }
    return entityContent;
  }
}
