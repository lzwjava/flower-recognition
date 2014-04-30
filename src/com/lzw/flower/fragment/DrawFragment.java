package com.lzw.flower.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lzw.flower.R;

/**
 * Created by lzw on 14-4-30.
 */
public class DrawFragment extends Fragment {
  View ok;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.draw_right,container,false);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    ok=getView().findViewById(R.id.ok);
    ok.setOnClickListener((View.OnClickListener) getActivity());
  }
}
