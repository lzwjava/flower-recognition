package com.lzw.flower.base;

import android.app.Application;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.lzw.flower.avobject.Photo;

/**
 * Created by lzw on 14-4-25.
 */
public class App extends Application {
  public static final int DRAW_BACK = 0;
  public static final int DRAW_FORE = 1;
  public static final int ALL_INFO = 3;
  public static boolean debug = false;
  public static String json = "{     \"valid\" : true, \t\"images\": [ \t{ \"typeName\": \"牡丹\", " +
      "\"typeDesc\" : \"洛阳市花\", \"ImageUrl\" : \"http://paas-files.qiniudn.com/Ex0tsNkAPXoopBf77vwwuJY4vtnX7SCSOtPuaY1D.jpg\", \"InfoUrl\" : \"http://www.baidu.com/洛阳牡丹.html\"}, \t{ \"typeName\": \"蝴蝶兰\", \"typeDesc\" : \"特别好看的花\", \"ImageUrl\" : \"http://paas-files.qiniudn.com/WFlEbl7G0R7PWcjyA34jCO0qmphkaUTcwDiLG0vh.jpg\", \"InfoUrl\" : \"http://www.baidu.com/蝴蝶兰.html\"}, \t{ \"typeName\": \"月季\", \"typeDesc\" : \"洛阳市花\", \"ImageUrl\" : \"http://paas-files.qiniudn.com/NqDVEacbFKgbtqoU1clKEeK5ih48WJF3AEWCVhh9.jpg\", \"InfoUrl\" : \"http://www.baidu.com\"}, \t{ \"typeName\": \"普通花\", \"typeDesc\" : \"洛阳市花\", \"ImageUrl\" : \"http://paas-files.qiniudn.com/JaBVo2WkrFsMnqEtK7QxqLIC1wCAMNEEUcxK2826.jpg\", \"InfoUrl\" : \"http://www.baidu.com\"}, \t{ \"typeName\": \"花3\", \"typeDesc\" : \"洛阳市花\", \"ImageUrl\" : \"http://paas-files.qiniudn.com/V3iz5xDeLx9HdCigbFOKAACuieI3B5ozRjSqN3Uw.jpg\", \"InfoUrl\" : \"http://www.baidu.com\"} \t] }";

  public static int drawWidth = 800;
  public static int drawHeight = 600;
  public static String IP_ID = "537a64b0e4b01dde2abf2a07";

  @Override
  public void onCreate() {
    super.onCreate();
    AVOSCloud.initialize(this,
        "8mbwu81vrbpxhpmlbl9zdbdums10v4qts8idao9tpvybsscu",
        "dkvdvprur6v12pzk1fko07o20whb3nyb8gn2qxvier1xr8us");
    AVObject.registerSubclass(Photo.class);
    AVAnalytics.enableCrashReport(this, !debug);
  }
}
