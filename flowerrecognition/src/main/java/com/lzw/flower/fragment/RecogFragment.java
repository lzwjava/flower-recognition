package com.lzw.flower.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzw.flower.R;

/**
 * Created by lzw on 14-4-30.
 */
public class RecogFragment extends Fragment {
  View recogOk,recogNo;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.recog_layout,container,false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    recogOk=getView().findViewById(R.id.recogOk);
    recogNo=getView().findViewById(R.id.recogNo);
    View.OnClickListener listener=(View.OnClickListener) getActivity();
    recogOk.setOnClickListener(listener);
    recogNo.setOnClickListener(listener);
  }
}
