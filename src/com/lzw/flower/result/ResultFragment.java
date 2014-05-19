package com.lzw.flower.result;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lzw.flower.R;
import com.lzw.flower.draw.DrawActivity;
import com.lzw.flower.utils.Logger;
import com.lzw.flower.utils.Utils;
import com.lzw.flower.web.Web;
import com.nhaarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-4-30.
 */
public class ResultFragment extends Fragment implements View.OnClickListener {
  public static final int GOT_DATA = 0;
  View flowerListLayout;
  ListView flowerList;
  List<FlowerData> datas=new ArrayList<FlowerData>();
  ImageView originView;
  View resultNo;
  AlertDialog dialog;
  FlowerAdapter adapter;
  Activity cxt;
  ImageView showView;
  TextView typeNameView,typeDescView;
  View wikiView;
  WikiListener wikiListener;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.result_layout,null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    cxt=getActivity();
    findView();
    addHeader();
    beginDownload();
    initAnim();
    perfromHeaderClick();
  }

  public void findView() {
    flowerListLayout=getView().findViewById(R.id.flowerListLayout);
    flowerList= (ListView) flowerListLayout.findViewById(R.id.flowerList);
    resultNo = getView().findViewById(R.id.resultNo);
    typeNameView= (TextView) getView().findViewById(R.id.typeName);
    typeDescView= (TextView) getView().findViewById(R.id.typeDesc);
    wikiView=  getView().findViewById(R.id.wiki);
    wikiListener=new WikiListener();
    wikiView.setOnClickListener(wikiListener);
    resultNo.setOnClickListener(this);
    showView= (ImageView) getView().findViewById(R.id.showView);
  }

  private void addHeader() {
    LayoutInflater inflater=LayoutInflater.from(getActivity());
    View header=inflater.inflate(R.layout.list_header,null,false);
    flowerList.addHeaderView(header);

    originView= (ImageView) header.findViewById(R.id.originView);
    Bitmap origin = getOriginBitmap();
    originView.setImageBitmap(origin);
    header.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        perfromHeaderClick();
      }
    });
  }

  public void perfromHeaderClick() {
    typeNameView.setText(R.string.origin_flower);
    typeDescView.setText(null);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        setBitmapByKey("origin");
      }
    }, 500);
    setWiki(false,null);
  }

  private void setWiki(boolean isShow,String key) {
    if(isShow){
      wikiView.setVisibility(View.VISIBLE);
      wikiListener.setKey(key);
    }else{
      wikiView.setVisibility(View.INVISIBLE);
    }
  }

  void initAnim(){
    adapter=new FlowerAdapter(getActivity(),datas);
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter
        = new SwingBottomInAnimationAdapter(adapter);
    swingBottomInAnimationAdapter.setInitialDelayMillis(300);
    swingBottomInAnimationAdapter.setAbsListView(flowerList);
    flowerList.setAdapter(swingBottomInAnimationAdapter);
    flowerList.setOnItemClickListener(new flowerClickListener());
  }

  class flowerClickListener implements android.widget.AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      FlowerAdapter.Holder holder= (FlowerAdapter.Holder) view.getTag();
      FlowerData data = holder.data;
      String url =data.imageUrl;
      typeNameView.setText(data.typeName);
      typeDescView.setText(data.typeDesc);
      setBitmapByKey(url);
      setWiki(true,data.typeName);
    }
  }

  private void setBitmapByKey(String key) {
    int h1= getLeftPicHeight();
    Bitmap bm = Web.getBitmapCacheNetByHeight(key, h1);
    showView.setImageBitmap(bm);
  }

  private int getLeftWidth() {
    View view=getView().findViewById(R.id.recogOkLayout);
    return view.getWidth();
  }

  private int getLeftPicHeight() {
    View view=getView().findViewById(R.id.showViewLayout);
    int h = view.getHeight();
    Resources res = getResources();
    int paddingH=res.getDimensionPixelSize(R.dimen.showViewLayoutPadding)*2;
    int padH=res.getDimensionPixelSize(R.dimen.showViewPadding)*2;
    return h-paddingH-padH;
  }

  public Bitmap getOriginBitmap() {
    return Web.getBitmapCacheNet("origin",getThumbWidth());
  }

  public void beginDownload() {
    dialog=Utils.showSpinnerAlert(getActivity());
    new Thread(new Runnable() {
      @Override
      public void run() {
        String json=getJsonFromIntent();
        List<FlowerData> datas = Web.getDatas(json);
        try {
          int reqWidth;
          reqWidth= getThumbWidth();
          Web.downloadBitmaps(datas, reqWidth);
          Message msg = handler.obtainMessage();
          msg.what = GOT_DATA;
          msg.obj = datas;
          handler.sendMessage(msg);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      private String getJsonFromIntent() {
        Intent intent=getActivity().getIntent();
        String json = intent.getStringExtra(DrawActivity.RESULT_JSON);
        assert json!=null;
        return json;
      }
    }).start();
  }

  public int getThumbWidth() {
    Resources res = getResources();
    int listWidth = res.getDimensionPixelSize(R.dimen.flowerListWidth);
    int pad=res.getDimensionPixelSize(R.dimen.listPadding);
    return listWidth-pad*2;
  }


  Handler handler = new Handler(new Handler.Callback() {
    @Override
    public boolean handleMessage(Message msg) {
      int what = msg.what;
      boolean res = false;
      if (what == GOT_DATA) {
        if(dialog!=null){
          dialog.dismiss();
        }
        List<FlowerData> datas = (List<FlowerData>) msg.obj;
        ResultFragment.this.datas=datas;
        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
        res = true;
      }
      return res;
    }
  });

  @Override
  public void onClick(View v) {
    int id=v.getId();
    if(id==R.id.resultNo){
      getActivity().finish();
    }
  }

  class WikiListener implements View.OnClickListener {
    String key;

    public void setKey(String key) {
      this.key = key;
    }

    @Override
    public void onClick(View v) {
      Intent intent=new Intent();
      intent.setAction(Intent.ACTION_VIEW);
      String url1="http://zh.wikipedia.org/wiki/";
      Uri uri=Uri.parse(url1+key);
      intent.setData(uri);
      startActivity(intent);
    }
  }
}
