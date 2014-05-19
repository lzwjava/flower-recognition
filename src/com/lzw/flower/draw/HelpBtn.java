package com.lzw.flower.draw;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import com.lzw.flower.R;
import com.lzw.flower.utils.Utils;

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
    Context cxt = getContext();
    if(isInEditMode()){
      return;
    }
    setOnClickListener((DrawActivity) cxt);
  }

  @Override
  public void onClick(View v) {
    Utils.alertDialog((Activity) getContext(), R.string.info);
  }
}
