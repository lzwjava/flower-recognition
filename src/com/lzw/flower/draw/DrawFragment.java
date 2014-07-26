package com.lzw.flower.draw;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lzw.flower.R;
import com.lzw.flower.base.App;

/**
 * Created by lzw on 14-4-30.
 */
public class DrawFragment extends Fragment {
  View ok;
  TextView infoView;
  int infoId;
  public DrawFragment(int infoId) {
    this.infoId=infoId;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.draw_right,container,false);
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ok=getView().findViewById(R.id.drawOk);
    ok.setOnClickListener((View.OnClickListener) getActivity());
    infoView= (TextView) getView().findViewById(R.id.infoView);
    if(infoId== App.DRAW_BACK){
      appendBlue();
    }else if(infoId== App.DRAW_FORE){
      appendRed();
    }else if(infoId==App.ALL_INFO){
      appendAll();
    }
  }

  private void appendRed() {
    appendAllText(R.drawable.red, R.string.draw_flower_tips);
  }

  private void appendBlue() {
    int imgId = R.drawable.blue;
    int txtId = R.string.draw_back_tips;
    appendAllText(imgId, txtId);
  }

  public void appendAll(){
    infoView.append("\n");
    appendBlue();
    infoView.append("\n");
    appendRed();
    infoView.append("\n");
  }

  public void appendAllText(int imgId, int txtId) {
    Resources res=getActivity().getResources();
    infoView.append(res.getString(R.string.please_use));
    appendImageAndText(res, imgId, txtId);
  }

  public void appendImageAndText(Resources res, int imgId, int txtId) {
    infoView.append(getSpannedImage(imgId));
    infoView.append(res.getString(txtId));
  }

  public Spanned getSpannedImage(int id) {
    String text=id+"";
    return Html.fromHtml("<img src=" + text + ">", imageGetter, null);
  }

  Html.ImageGetter imageGetter=new Html.ImageGetter() {
    @Override
    public Drawable getDrawable(String source) {
      int id=Integer.parseInt(source);
      Drawable d=null;
      d=getResources().getDrawable(id);
      d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
      return d;
    }
  };
}
