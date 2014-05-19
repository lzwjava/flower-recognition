package com.lzw.flower.base;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.util.Log;
import android.util.LruCache;

@SuppressLint("NewApi")
public class ImageLoader {
  static LruCache<String, Bitmap> mMemoryCache; 
  static ImageLoader mImageLoader;
  
  public ImageLoader() {
    // TODO Auto-generated constructor stub
    int maxMemory=(int)Runtime.getRuntime().maxMemory();
    int cacheSize=maxMemory/8;
    mMemoryCache=new LruCache<String, Bitmap>
     (cacheSize){
      @Override
      protected int sizeOf(String key,Bitmap bitmap){
        return bitmap.getByteCount();
      }
    };
  }
  
  public static ImageLoader getInstance() {
    if(mImageLoader==null){
      mImageLoader=new ImageLoader();
    }
    return mImageLoader;
  }
  
  public void addBitmapToMemoryCache
   (String key,Bitmap bitmap){ 
    if(getBitmapFromMemoryCache(key)==null){
      mMemoryCache.put(key, bitmap);
    }
  }
  
  public Bitmap getBitmapFromMemoryCache(String key){
    Bitmap bm=null;
    if(key!=null){
      bm=mMemoryCache.get(key);
    }else{
      Log.i("lzw", "get bitmap from cache but key=" + key);
    }
    return bm;
  }
  
  public static Bitmap decodeSampledBitmapFromPath(String path,int reqWidth){
    Options options=new Options();
    options.inJustDecodeBounds=true;
    BitmapFactory.decodeFile(path,options);
    int inSampleSize=calInSampleSize(options,reqWidth);
    options.inJustDecodeBounds=false;
    options.inSampleSize=inSampleSize;
    return BitmapFactory.decodeFile(path,options);
  }

  private static int calInSampleSize(Options options, int reqWidth) {
    // TODO Auto-generated method stub
    int w=options.outWidth;
    int h=options.outHeight;
    int inSampleSize=1;
    if(w>reqWidth){
      inSampleSize=Math.round(w/reqWidth);
    }
    return inSampleSize;
  }
}

