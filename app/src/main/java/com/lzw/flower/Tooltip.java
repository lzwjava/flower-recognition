package com.lzw.flower;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;
import com.lzw.flower.R;

/**
 * Created by lzw on 14-5-4.
 */
public class Tooltip implements View.OnClickListener {
  public static final String IS_FIRST_OPEN = "isFirstOpen";
  Activity cxt;
  ToolTipRelativeLayout mToolTipFrameLayout;
  ToolTipView dirTooltip;
  ToolTipView drawTooltip;
  ToolTipView okTooltip;
  LayoutInflater inflater;
  View draw;
  SharedPreferences pref;
  public Tooltip(Activity cxt) {
    this.cxt = cxt;
    mToolTipFrameLayout = (ToolTipRelativeLayout)
        cxt.findViewById(R.id.activity_main_tooltipframelayout);
    inflater=LayoutInflater.from(cxt);
    mToolTipFrameLayout.setOnClickListener(this);
    pref=PreferenceManager.getDefaultSharedPreferences(cxt);
    boolean isFirstOpen=pref.getBoolean(IS_FIRST_OPEN,true);
    if(isFirstOpen){
      start();
      pref.edit().putBoolean(IS_FIRST_OPEN,false).commit();
    }
  }


  public void start() {
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        mToolTipFrameLayout.removeAllViews();
        mToolTipFrameLayout.setVisibility(View.VISIBLE);
        addDirtip();
        addDrawtip();
        addOktip();
      }
    }, 500);
  }

  public void restart(){
    mToolTipFrameLayout.setVisibility(View.VISIBLE);
  }

  private void addOktip() {
    okTooltip=getToolTipView(R.id.ok,R.layout.tooltip_ok, android.R.color.holo_green_light);
  }

  private void addDrawtip() {
    int hookId = R.id.drawPos;
    int tooltip_view = R.layout.tooltip_draw;
    int colorId = android.R.color.holo_orange_light;
    draw=cxt.findViewById(hookId);
    drawTooltip = getToolTipView(hookId,tooltip_view, colorId);
  }

  private ToolTipView getToolTipView(int hookId,int tooltip_view, int colorId) {
    ToolTipView toolTipView;View drawTipView = inflater.inflate(tooltip_view, null, false);
    View hookView=cxt.findViewById(hookId);
    ToolTip drawTip=    new ToolTip().withColor
        (cxt.getResources().getColor(colorId))
        .withContentView(drawTipView);
    toolTipView=mToolTipFrameLayout.showToolTipForView(drawTip
        ,hookView);
    //toolTipView.setOnToolTipViewClickedListener(new MyToolTipListener());
    return toolTipView;
  }

  private void addDirtip() {
    dirTooltip=getToolTipView(R.id.dirBtn,R.layout.tooltip_dir,
        android.R.color.holo_red_light);
  }

  @Override
  public void onClick(View v) {
    mToolTipFrameLayout.setVisibility(View.GONE);
  }

  private class MyToolTipListener implements ToolTipView.OnToolTipViewClickedListener {
    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
      if(toolTipView==dirTooltip){
        dirTooltip=null;
      }else if(toolTipView==drawTooltip){
        drawTooltip=null;
        draw.setVisibility(View.GONE);
      }else if(toolTipView==okTooltip){
        okTooltip=null;
      }
    }
  }
}
