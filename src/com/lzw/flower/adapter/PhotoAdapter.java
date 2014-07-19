package com.lzw.flower.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.lzw.flower.R;
import com.lzw.flower.avobject.Photo;
import com.lzw.flower.utils.Utils;

import java.util.List;

public class PhotoAdapter extends BaseAdapter {
  private final int width;
  List<Photo> photos;
  Activity cxt;

  class Holder {
    ImageView photoView;
    View deleteView;
  }

  public PhotoAdapter(Activity cxt) {
    this.cxt = cxt;
    width = Utils.getWindowWidth(cxt);
  }

  DelClickListener delClickListener = new DelClickListener();

  @Override
  public int getCount() {
    return photos.size();
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
    if (conView == null) {
      LayoutInflater inflater = LayoutInflater.from(cxt);
      conView = inflater.inflate(R.layout.person_photo_item, null, false);
      Holder holder = new Holder();
      holder.photoView = (ImageView) conView.findViewById(R.id.photo);
      holder.photoView.setLayoutParams(new FrameLayout.LayoutParams(width, width));
      holder.deleteView = conView.findViewById(R.id.delete);
      conView.setTag(holder);
    }
    Holder holder = (Holder) conView.getTag();
    Photo photo = photos.get(position);
    holder.photoView.setImageBitmap(photo.getBitmap());
    return conView;
  }

  private void setDeleteView(View deleteView, int position) {
    deleteView.setOnClickListener(delClickListener);
  }

  class DelClickListener implements View.OnClickListener {
    @Override
    public void onClick(final View v) {
    }
  }
}