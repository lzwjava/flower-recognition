package com.lzw.flower.deprecated;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.lzw.flower.utils.Logger;
import com.lzw.flower.web.Web;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by lzw on 14-5-17.
 */
public class Deprecated {
  public static void uploadToAV(String baseUrl, String originPath, String handPath,int serverId) throws IOException,
      AVException {
    AVFile origin = AVFile.withAbsoluteLocalPath("origin.png", originPath);
    origin.save();
    AVFile hand = AVFile.withAbsoluteLocalPath("hand.png", handPath);
    hand.save();
    String originUrl = origin.getUrl();
    String handUrl = hand.getUrl();
    HttpClient client = new DefaultHttpClient();
    String content = Web.doPost(client, baseUrl, "origin", originUrl, "hand", handUrl, "id", serverId + "");
    Logger.d(content + "");
  }
}
