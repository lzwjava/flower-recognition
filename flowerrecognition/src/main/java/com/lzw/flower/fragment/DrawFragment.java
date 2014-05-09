package com.lzw.flower.fragment;

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

/**
 * Created by lzw on 14-4-30.
 */
public class DrawFragment extends Fragment {
  View ok;
  TextView infoView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.draw_right,container,false);
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ok=getView().findViewById(R.id.ok);
    ok.setOnClickListener((View.OnClickListener) getActivity());
    infoView= (TextView) getView().findViewById(R.id.infoView);
    Resources res=getResources();
    infoView.append(res.getString(R.string.please_use));
    infoView.append(getSpannedImage(R.drawable.blue));
    infoView.append(res.getString(R.string.draw_back));
    infoView.append(getSpannedImage(R.drawable.red));
    infoView.append(res.getString
        (R.string.draw_flower));
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
