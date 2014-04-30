package com.lzw.flower;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by lzw on 14-4-29.
 */
public class HelpBtn extends ImageButton implements View.OnClickListener {
  public HelpBtn(Context context) {
    super(context);
    init();
  }


  public HelpBtn(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public HelpBtn(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }


  private void init() {
    setOnClickListener(this);
  }


  @Override
  public void onClick(View v) {

  }
}
