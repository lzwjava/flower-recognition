package com.lzw.flower.result;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.lzw.flower.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzw on 14-4-30.
 */
public class FlowerAdapter extends BaseAdapter {
  List<FlowerData> datas=new ArrayList<FlowerData>();
  Context cxt;

  public FlowerAdapter(Context cxt,List<FlowerData> datas) {
    this.cxt=cxt;
    this.datas = datas;
  }

  public void setDatas(List<FlowerData> datas) {
    this.datas = datas;
  }

  class Holder{
    ImageView flowerView;
    TextView typeNameView;
    TextView typeDescView;
  }

  @Override
  public int getCount() {
    return datas.size();
  }

  @Override
  public Object getItem(int position) {
    return null;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View conView, ViewGroup parent) {
    if(conView==null){
      LayoutInflater inflater=LayoutInflater.from(cxt);
      conView=inflater.inflate(R.layout.flower_item,null);
      Holder holder=new Holder();
      holder.flowerView= (ImageView) conView.findViewById(R.id.image);
      holder.typeDescView= (TextView) conView.findViewById(R.id.typeDesc);
      holder.typeNameView= (TextView) conView.findViewById(R.id.typeName);
      conView.setTag(holder);
    }
    Holder holder= (Holder) conView.getTag();
    FlowerData data=datas.get(position);
    holder.flowerView.setImageBitmap(data.flower);
    holder.typeNameView.setText(data.typeName);
    holder.typeDescView.setText(data.typeDesc);
    return conView;
  }
}
