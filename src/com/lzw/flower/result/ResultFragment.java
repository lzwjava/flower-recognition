package com.lzw.flower.result;

import android.app.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import com.lzw.flower.*;
import com.lzw.flower.utils.Utils;
import com.lzw.flower.base.ImageLoader;
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

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.result_layout,null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    cxt=getActivity();
    flowerListLayout=getView().findViewById(R.id.flowerListLayout);
    flowerList= (ListView) flowerListLayout.findViewById(R.id.flowerList);
    resultNo = getView().findViewById(R.id.resultNo);
    resultNo.setOnClickListener(this);
    addHeader();
    setOriginView();
    beginDownload();
    initAnim();
  }

  private void addHeader() {
    LayoutInflater inflater=LayoutInflater.from(getActivity());
    View header=inflater.inflate(R.layout.list_header,null,false);
    flowerList.addHeaderView(header);
  }

  void initAnim(){
    adapter=new FlowerAdapter(getActivity(),datas);
    SwingBottomInAnimationAdapter swingBottomInAnimationAdapter
        = new SwingBottomInAnimationAdapter(adapter);
    swingBottomInAnimationAdapter.setInitialDelayMillis(300);
    swingBottomInAnimationAdapter.setAbsListView(flowerList);
    flowerList.setAdapter(swingBottomInAnimationAdapter);
  }

  public void setOriginView() {
    originView= (ImageView) getView().findViewById(R.id.originImgView);
    ImageLoader imageLoader=ImageLoader.getInstance();
    originView.setImageBitmap(imageLoader.getBitmapFromMemoryCache("origin"));
  }


  public void beginDownload() {
    dialog=Utils.showSpinnerAlert(getActivity());
    new Thread(new Runnable() {
      @Override
      public void run() {
        List<FlowerData> datas = Web.getDatas();
        try {
          Web.downloadBitmaps(datas);
          Message msg = handler.obtainMessage();
          msg.what = GOT_DATA;
          msg.obj = datas;
          handler.sendMessage(msg);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
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
}
