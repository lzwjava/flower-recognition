package com.lzw.flower.draw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.lzw.flower.R;
import com.lzw.flower.activity.PhotoActivity;
import com.lzw.flower.base.App;
import com.lzw.flower.base.ImageLoader;
import com.lzw.flower.fragment.RecogFragment;
import com.lzw.flower.fragment.WaitFragment;
import com.lzw.flower.material.MaterialActivity;
import com.lzw.flower.result.ResultActivity;
import com.lzw.flower.utils.*;
import com.lzw.flower.web.UploadImage;
import com.lzw.flower.web.Web;
import org.json.JSONObject;

import java.io.File;

public class DrawActivity extends Activity implements View.OnClickListener {
  public static final int CAMERA_RESULT = 1;
  public static final int CROP_RESULT = 2;

  public static final int DRAW_FRAGMENT = 0;
  public static final int RECOG_FRAGMENT = 1;
  public static final int RESULT_FRAGMENT = 2;
  public static final int WAIT_FRAGMENT = 3;
  public static final int MATERIAL_RESULT = 4;
  public static final String RESULT_JSON = "resultJson";
  public static final int INIT_FLOWER_ID = R.drawable.flower_b;
  public static final int LOGOUT = 0;
  String baseUrl;

  DrawView drawView;
  Bitmap originImg;
  public static DrawActivity instance;
  View dir, clear, cameraView, materialView, scale;
  ImageView undoView, redoView;
  View upload;

  public static final int IMAGE_RESULT = 0;
  String cropPath;
  Tooltip toolTip;
  int curFragmentId = -1;
  int serverId = -1;
  private Bitmap resultBitmap;
  private RadioGroup radioGroup;
  Fragment curFragment;
  int curDrawMode;
  RadioButton drawBackBtn;
  private Activity cxt;
  Uri curPicUri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    instance = this;
    cxt = this;
    cropPath = PathUtils.getCropPath();
    setContentView(R.layout.draw_layout);
    findView();
    setSize();
    initOriginImage();
    toolTip = new Tooltip(this);
    if (App.debug) {
      //recogOk();
      //materialView.performClick();
      //showRecogFragment(null,null);
      //goResult();
    }
    initUndoRedoEnable();
    setIp();
    initDrawmode();
  }

  private void setImageByUri(final Uri uri) {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        curPicUri = uri;
        Bitmap bitmap = null;
        try {
          if (uri != null) {
            String path = uri.getPath();
            bitmap = BitmapUtils.getBitmapByUri(DrawActivity.this, uri);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        int originW, originH;
        originW = bitmap.getWidth();
        originH = bitmap.getHeight();
        if (originW != App.drawWidth || originH != App.drawHeight) {
          float originRadio = originW * 1.0f / originH;
          float radio = App.drawWidth * 1.0f / App.drawHeight;
          if (Math.abs(originRadio - radio) < 0.01) {
            Bitmap originBm = bitmap;
            Logger.d("create bitmap");
            bitmap = Bitmap.createScaledBitmap(originBm, App.drawWidth, App.drawHeight, false);
            originBm.recycle();
          } else {
            cropIt(uri);
            return;
          }
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.addOrReplaceToMemoryCache("origin", bitmap);
        originImg = bitmap;
        serverId = -1;

        Logger.d("origin w=%d h=%d", originImg.getWidth(), originImg.getHeight());
        Logger.d("drawview w=%d h=%d", drawView.getWidth(), drawView.getHeight());
        drawView.setSrcBitmap(originImg);
        showDrawFragment(App.ALL_INFO);
        curDrawMode = App.DRAW_FORE;
        if (App.debug) {
          //goResult();
        }
      }
    }, 500);
  }

  public void cropIt(Uri uri) {
    Crop.startPhotoCrop(this, uri, cropPath, CROP_RESULT);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(0, LOGOUT, 0, R.string.logOut);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    int id = item.getItemId();
    if (id == LOGOUT) {
      AVUser.logOut();
      finish();
    }
    return super.onMenuItemSelected(featureId, item);
  }

  private void initDrawmode() {
    curDrawMode = App.DRAW_FORE;
  }

  private void initOriginImage() {
    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), INIT_FLOWER_ID);
    String imgPath = PathUtils.getCameraPath();
    BitmapUtils.saveBitmapToPath(bitmap, imgPath);
    Uri uri1 = Uri.fromFile(new File(imgPath));
    setImageByUri(uri1);
  }

  private void setIp() {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          AVQuery q = new AVQuery("Constant");
          AVObject obj = q.get(App.IP_ID);
          String ip = obj.getString("ip");
          baseUrl = "http://" + ip + ":8080/file";
          Logger.d(baseUrl + " baseUrl");
        } catch (AVException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  private void goResult() {
    Intent intent = new Intent(DrawActivity.this, ResultActivity.class);
    intent.putExtra(RESULT_JSON, App.json);
    startActivity(intent);
  }

  private void findView() {
    drawView = (DrawView) findViewById(R.id.drawView);
    undoView = (ImageView) findViewById(R.id.undo);
    redoView = (ImageView) findViewById(R.id.redo);
    scale = findViewById(R.id.scale);
    upload = findViewById(R.id.upload);
    clear = findViewById(R.id.clear);
    dir = findViewById(R.id.dir);
    materialView = findViewById(R.id.material);
    cameraView = findViewById(R.id.camera);

    dir.setOnClickListener(this);
    materialView.setOnClickListener(this);
    undoView.setOnClickListener(this);
    scale.setOnClickListener(this);
    redoView.setOnClickListener(this);
    clear.setOnClickListener(this);
    cameraView.setOnClickListener(this);
    upload.setOnClickListener(this);
    initRadio();
  }

  private void initRadio() {
    radioGroup = (RadioGroup) findViewById(R.id.radioRroup);
    radioGroup.setOnCheckedChangeListener(new RadioListener());
  }


  private class RadioListener implements RadioGroup.OnCheckedChangeListener {
    @Override
    public void onCheckedChanged(RadioGroup group, int id) {
      if (id == R.id.drawBack) {
        drawView.drawBack();
        drawView.setScaleMode(false);
        curDrawMode = App.DRAW_BACK;
      } else if (id == R.id.drawFore) {
        drawView.drawFore();
        drawView.setScaleMode(false);
        drawView.setVisibility(View.VISIBLE);
        curDrawMode = App.DRAW_FORE;
      }else if(id==R.id.scaleBtn){
        drawView.setScaleMode(true);
      }
      updateDrawFragmentByMode();
    }
  }

  private void updateDrawFragmentByMode() {
    showDrawFragment(curDrawMode);
  }

  void initUndoRedoEnable() {
    drawView.history.setCallBack(new History.CallBack() {
      @Override
      public void onHistoryChanged() {
        setUndoRedoEnable();
        if (curFragmentId != DRAW_FRAGMENT) {
          showDrawFragment(curDrawMode);
        }
      }
    });
  }

  void setUndoRedoEnable() {
    redoView.setEnabled(drawView.history.canRedo());
    undoView.setEnabled(drawView.history.canUndo());
  }

  private void showDrawFragment(int infoId) {
    curFragmentId = DRAW_FRAGMENT;
    curFragment = new DrawFragment(infoId);
    showFragment(curFragment);
  }

  private void setSize() {
    setSizeByResourceSize();
    setViewSize(drawView);
  }

  private void setSizeByResourceSize() {
    int width = getResources().getDimensionPixelSize(R.dimen.draw_width);
    int height = getResources().getDimensionPixelSize(R.dimen.draw_height);
    App.drawWidth = width;
    App.drawHeight = height;
  }

  private void setViewSize(View v) {
    ViewGroup.LayoutParams lp = v.getLayoutParams();
    lp.width = App.drawWidth;
    lp.height = App.drawHeight;
    v.setLayoutParams(lp);
  }

  private void showFragment(Fragment fragment) {
    FragmentTransaction trans = getFragmentManager().beginTransaction();
    trans.replace(R.id.rightLayout, fragment);
    trans.commit();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    if (id == R.id.drawOk) {
      if (drawView.isDrawFinish()) {
        //showRecogFragment();
        saveBitmap();
      } else {
        Utils.alertDialog(this, R.string.please_draw_finish);
      }
    } else if (id == R.id.recogOk) {
      recogOk();
    } else if (id == R.id.recogNo) {
      recogNo();
    } else if (id == R.id.dir) {
      Utils.getGalleryPhoto(this, IMAGE_RESULT);
    } else if (id == R.id.clear) {
      clearEverything();
    } else if (id == R.id.help) {
      if (curFragmentId == DRAW_FRAGMENT) {
        toolTip.startWhenDraw();
      }
    } else if (id == R.id.undo) {
      drawView.undo();
    } else if (id == R.id.redo) {
      drawView.redo();
    } else if (id == R.id.camera) {
      Utils.takePhoto(cxt, CAMERA_RESULT);
    } else if (id == R.id.material) {
      goMaterial();
    } else if (id == R.id.upload) {
      com.lzw.commons.Utils.goActivity(cxt, PhotoActivity.class);
    } else if (id == R.id.scale) {
      cropIt(curPicUri);
    }
  }

  private void goMaterial() {
    Intent intent = new Intent(this, MaterialActivity.class);
    startActivityForResult(intent, MATERIAL_RESULT);
  }

  private void showRecogFragment(Bitmap foreBitmap, Bitmap backBitmap) {
    curFragmentId = RECOG_FRAGMENT;
    RecogFragment recogFragment = new RecogFragment();
    recogFragment.setForeBitmap(foreBitmap);
    recogFragment.setBackBitmap(backBitmap);
    showFragment(recogFragment);
  }

  public void clearEverything() {
    if (curPicUri != null) {
      setImageByUri(curPicUri);
    }
    showDrawFragment(curDrawMode);
  }

  public void saveBitmap() {
    Bitmap handBitmap = drawView.getHandBitmap();
    Bitmap originBitmap = drawView.getSrcBitmap();
    //showDrawPicture(handBitmap,originBitmap);
    saveBitmapToFileAndUpload(handBitmap, originBitmap);
  }

  private void saveBitmapToFileAndUpload(Bitmap handBitmap, Bitmap originBitmap) {
    final String originPath = PathUtils.getOriginPath();
    BitmapUtils.saveBitmapToPath(originBitmap, originPath);
    final String handPath = PathUtils.getHandPath();
    BitmapUtils.saveBitmapToPath(handBitmap, handPath);
    new AsyncTask<Void, Void, Void>() {
      boolean res;
      Bitmap foreBitmap;
      Bitmap backBitmap;

      @Override
      protected void onPreExecute() {
        super.onPreExecute();
        showWaitFragment();
      }

      @Override
      protected Void doInBackground(Void... params) {
        try {
          //uploadToAV(baseUrl, originPath, handPath);
          if (baseUrl == null) {
            throw new Exception("baseUrl is null");
          }
          String jsonRes = UploadImage.upload(baseUrl, serverId, Web.STATUS_CONTINUE,
              originPath, handPath, null, false);
          getJsonData(jsonRes);
          res = true;
        } catch (Exception e) {
          res = false;
          e.printStackTrace();
        }
        return null;
      }

      private void getJsonData(String jsonRes) throws Exception {
        JSONObject json = new JSONObject(jsonRes);
        if (serverId == -1) {
          serverId = json.getInt(Web.ID);
        }
        String foreUrl = json.getString(Web.FORE);
        String backUrl = json.getString(Web.BACK);
        String resultUrl = json.getString(Web.RESULT);
        foreBitmap = Web.getBitmapFromUrlByStream1(foreUrl, 0);
        backBitmap = Web.getBitmapFromUrlByStream1(backUrl, 0);
        resultBitmap = Web.getBitmapFromUrlByStream1(resultUrl, 0);
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (res) {
          Logger.d("lzw", "get json ok then show recogFragment");
          showRecogFragment(foreBitmap, backBitmap);
        } else {
          Utils.toast(DrawActivity.this, R.string.server_error);
          recogNo();
        }
      }
    }.execute();
  }

  private void showWaitFragment() {
    curFragmentId = WAIT_FRAGMENT;
    showFragment(new WaitFragment());
  }

  public void showDrawPicture(Bitmap handBitmap, Bitmap originBitmap) {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    Bitmap bm = drawView.getCacheBm();
    LayoutInflater inflater = LayoutInflater.from(this);
    View dialog = View.inflate(this, R.layout.dialog, null);
    ImageView originImgView = (ImageView) dialog.findViewById(R.id.origin),
        handImg = (ImageView) dialog.findViewById(R.id.hand),
        hand1Img = (ImageView) dialog.findViewById(R.id.hand1);
    originImgView.setImageBitmap(originBitmap);
    handImg.setImageBitmap(handBitmap);
    hand1Img.setImageBitmap(handBitmap);
    builder.setView(dialog);
    builder.show();
  }

  private void recogNo() {
    showDrawFragment(curDrawMode);
  }

  public void ok(View v) {
    Utils.showImage(this, drawView.getCacheBm());
  }

  public void recogOk() {
    new AsyncTask<Void, Void, Void>() {
      boolean res;
      String jsonRes;

      @Override
      protected Void doInBackground(Void... params) {
        try {
          jsonRes = UploadImage.upload(baseUrl, serverId, Web.STATUS_OK,
              null, null, null, true);
          res = true;
        } catch (Exception e) {
          e.printStackTrace();
          res = false;
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (res) {
          Intent intent = new Intent(DrawActivity.this, ResultActivity.class);
          intent.putExtra(RESULT_JSON, jsonRes);
          startActivity(intent);
        } else {
          Utils.toast(getApplicationContext(), R.string.server_error);
        }
      }
    }.execute();
  }

  @Override
  protected void onActivityResult(int requesetCode, int resultCode, Intent data) {
    if (resultCode != RESULT_CANCELED) {
      Uri uri;
      switch (requesetCode) {
        case IMAGE_RESULT:
          if (data != null) {
            Uri data1 = data.getData();
            setImageByUri(data1);
          }
          break;
        case CAMERA_RESULT:
          setImageByUri(Utils.getCameraUri());
          break;
        case CROP_RESULT:
          uri = Uri.fromFile(new File(cropPath));
          setImageByUri(uri);
          break;
        case MATERIAL_RESULT:
          uri = data.getData();
          setImageByUri(uri);
      }
    }
  }
}
