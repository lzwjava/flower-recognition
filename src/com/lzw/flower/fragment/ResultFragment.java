package com.lzw.flower.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.lzw.flower.Data;
import com.lzw.flower.DrawActivity;
import com.lzw.flower.FlowerAdapter;
import com.lzw.flower.R;

import java.util.List;

/**
 * Created by lzw on 14-4-30.
 */
public class ResultFragment extends Fragment {
  View flowerListLayout;
  ListView flowerList;
  List<Data> datas;
  View resultNo;
  public ResultFragment(List<Data> datas) {
    this.datas=datas;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.result_layout,null);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    flowerListLayout=getView().findViewById(R.id.flowerListLayout);
    flowerList= (ListView) flowerListLayout.findViewById(R.id.flowerList);
    flowerList.setAdapter(new FlowerAdapter(getActivity(),datas));
    resultNo=getView().findViewById(R.id.resultNo);
    resultNo.setOnClickListener((View.OnClickListener) getActivity());
  }
}
