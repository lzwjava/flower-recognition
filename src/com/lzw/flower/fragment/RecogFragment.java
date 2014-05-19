package com.lzw.flower.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lzw.flower.R;

/**
 * Created by lzw on 14-4-30.
 */
public class RecogFragment extends Fragment {
  View recogOk,recogNo;
  Bitmap foreBitmap,backBitmap;
  ImageView foreView,backView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.recog_layout,container,false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    recogOk=getView().findViewById(R.id.recogOk);
    recogNo=getView().findViewById(R.id.recogNo);
    foreView= (ImageView) getView().findViewById(R.id.foreground);
    backView= (ImageView) getView().findViewById(R.id.background);
    View.OnClickListener listener=(View.OnClickListener) getActivity();
    recogOk.setOnClickListener(listener);
    recogNo.setOnClickListener(listener);
    setImage();
  }

  private void setImage() {
    setImageView(backView, backBitmap);
    setImageView(foreView,foreBitmap);
  }

  private void setImageView(ImageView imgView, Bitmap bitmap) {
    if(bitmap!=null){
      imgView.setImageBitmap(bitmap);
    }else{
      throw new NullPointerException("bitmap is null");
    }
  }

  public void setBackBitmap(Bitmap backBitmap) {
    this.backBitmap = backBitmap;
  }

  public void setForeBitmap(Bitmap foreBitmap) {
    this.foreBitmap = foreBitmap;
  }
}
