package com.lzw.flower.material;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.lzw.flower.R;
import com.lzw.flower.utils.BitmapUtils;
import com.lzw.flower.utils.PathUtils;
import com.lzw.flower.utils.Utils;
import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

import java.io.File;

/**
 * Created by lzw on 14-5-19.
 */
public class MaterialActivity extends Activity {
  HListView listView;
  int[] flowerIds = new int[]{R.drawable.flower_a, R.drawable.flower_b,
      R.drawable.flower_c, R.drawable.flower_d, R.drawable.flower_e};
  private Context cxt;
  ImageAdapter imageAdapter;
  Bitmap[] imgs=new Bitmap[0];
  String[] imgPaths=new String[0];
  File[] fs;
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
    loadData();
  }

  private void loadData() {
    new AsyncTask<Void,Void,Void>(){
      ProgressDialog dialog;


      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        dialog=Utils.showSpinnerDialog(MaterialActivity.this);
      }

      @Override
      protected Void doInBackground(Void... params) {
        checkAndCopyToImageDir();
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        imageAdapter.notifyDataSetChanged();
      }
    }.execute();
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
        bm.recycle();
      }
      pref.edit().putBoolean("isFirst", false).commit();
      checkAndCopyToImageDir();
    } else {
      fs = dir.listFiles();
      len=fs.length;
      imgs=new Bitmap[len];
      imgPaths=new String[len];
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
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
      Intent intent = new Intent();
      Uri uri =Uri.fromFile(new File(imgPaths[pos]));
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
    public View getView(int pos, View conView, ViewGroup parent) {
      if (conView == null) {
        LayoutInflater inflater = LayoutInflater.from(cxt);
        conView = inflater.inflate(R.layout.material_item, null, false);
      }
      ImageView imageView = (ImageView) conView.findViewById(R.id.imageView);
      //Bitmap bm = BitmapFactory.decodeResource(getResources(),flowerIds[pos]);
      if(imgs[pos]==null){
        imgs[pos]=decodeImageFile(fs[pos]);
        imgPaths[pos]=fs[pos].getAbsolutePath();
      }
      imageView.setImageBitmap(imgs[pos]);
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
