package com.lzw.flower.material;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.lzw.flower.R;
import com.lzw.flower.utils.BitmapUtils;
import com.lzw.flower.utils.PathUtils;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

import java.io.File;

/**
 * Created by lzw on 14-5-19.
 */
public class MaterialActivity extends Activity {
  HListView listView;
  int[] flowerIds = new int[]{R.drawable.flower_1, R.drawable.flower_bufferfly,
      R.drawable.flower_month, R.drawable.flower_mudan, R.drawable.flower_2,
      R.drawable.flower_normal};
  private Context cxt;
  ImageAdapter imageAdapter;
  Bitmap[] imgs=new Bitmap[0];
  File dir;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cxt = this;
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.material_layout);
    findView();
    initActionBar();
    initListView();
    checkAndCopyToImageDir();
  }

  private void checkAndCopyToImageDir() {
    String dirStr = PathUtils.getImageDir();
    dir = new File(dirStr);
    SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
    boolean isFirst = pref.getBoolean("isFirst", true);
    int len;
    if (isFirst) {
      len = flowerIds.length;
      for (int i = 0; i < len; i++) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(),flowerIds[i]);
        File f = new File(dir, i + "");
        BitmapUtils.saveBitmapToPath(bm, f.getPath());
      }
      pref.edit().putBoolean("isFirst", false).commit();
      checkAndCopyToImageDir();
    } else {
      File[] fs = dir.listFiles();
      len=fs.length;
      imgs=new Bitmap[len];
      for(int i=0;i<len;i++){
        imgs[i]=decodeImageFile(fs[i]);
      }
      imageAdapter.notifyDataSetChanged();
    }
  }

  private void initActionBar() {
    ActionBar actionBar = getActionBar();
    actionBar.setTitle(R.string.pleaseChooseOneFlower);
    actionBar.setDisplayHomeAsUpEnabled(true);
  }

  private void initListView() {
    listView = (HListView) findViewById(R.id.hListView1);
    imageAdapter = new ImageAdapter();
    listView.setAdapter(imageAdapter);
    listView.setHeaderDividersEnabled(true);
    listView.setFooterDividersEnabled(true);
    listView.setOnItemClickListener(new ImageClickListener());
  }

  class ImageClickListener implements
      it.sephiroth.android.library.widget.AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Intent intent = new Intent();
      int resId = flowerIds[position];
      Uri uri = BitmapUtils.getResourceUri(resId);
      intent.setData(uri);
      setResult(RESULT_OK, intent);
      finish();
    }
  }

  private void findView() {
  }

  class ImageAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return imgs.length;
    }

    @Override
    public Object getItem(int position) {
      return imgs[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View conView, ViewGroup parent) {
      if (conView == null) {
        LayoutInflater inflater = LayoutInflater.from(cxt);
        conView = inflater.inflate(R.layout.material_item, null, false);
      }
      ImageView imageView = (ImageView) conView.findViewById(R.id.imageView);
      //Bitmap bm = BitmapFactory.decodeResource(getResources(),flowerIds[position]);
      imageView.setImageBitmap(imgs[position]);
      return conView;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  Bitmap decodeImageFile(File file) {
    int toH = getResources().getDimensionPixelSize(R.dimen.listViewHeight);
    return BitmapUtils.decodeFileByHeight(file.getPath(), toH);
  }
}
