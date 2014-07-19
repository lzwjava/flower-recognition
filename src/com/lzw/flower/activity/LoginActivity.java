package com.lzw.flower.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.avos.avoscloud.*;
import com.lzw.commons.Utils;
import com.lzw.flower.R;
import com.lzw.flower.draw.DrawActivity;

public class LoginActivity extends Activity {
  EditText userName, password;
  Button button;
  Class<?> nextActivity = DrawActivity.class;
  ProgressDialog dialog;
  private Activity cxt;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    cxt = this;
    setContentView(R.layout.login_activity);
    AVUser currentUser = AVUser.getCurrentUser();

    if (currentUser != null) {
      loginSucceed();
    }

    userName = (EditText) findViewById(R.id.userName);
    password = (EditText) findViewById(R.id.password);
    button = (Button) findViewById(R.id.loginBtn);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Intent intent = getIntent();
    AVAnalytics.trackAppOpened(intent);
  }

  public boolean hasAnyEmpty(String txt) {
    for (int i = 0; i < txt.length(); i++) {
      if (txt.charAt(i) == ' ') {
        return true;
      }
    }
    return false;
  }

  public static boolean hasAnyWrongChar(String txt) {
    for (int i = 0; i < txt.length(); i++) {
      char ch = txt.charAt(i);
      if (!Character.isLetterOrDigit(ch)) {
        return true;
      }
      if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')
          || (ch >= '0' && ch <= '9')) {
      } else {
        return true;
      }
    }
    return false;
  }

  boolean isWrong(String txt) {
    if (Utils.isEmpty(this, txt, getString(R.string.empty_prompt))) {
      return true;
    }
    if (hasAnyEmpty(txt)) {
      Utils.toast(cxt, getString(R.string.has_empty_char));
      return true;
    }
    if (hasAnyWrongChar(txt)) {
      Utils.toast(cxt, getString(R.string.has_wrong_char));
      return true;
    }
    return false;
  }

  public void login(View v) {
    final String userNameStr = userName.getText().toString();
    final String passwordStr = password.getText().toString();
    if (isWrong(userNameStr) || isWrong(passwordStr)) {
      return;
    }
    dialog = Utils.showSpinnerDialog(this);
    dialog.show();
    AVUser.logInInBackground(userNameStr, passwordStr, new LogInCallback() {
      public void done(AVUser user, AVException e) {
        dialog.dismiss();
        if (e == null) {
          loginSucceed();
        } else {
          Utils.toast(cxt, getString(R.string.fail_login_tips));
        }
      }
    });
  }

  void loginSucceed() {
    //toast(getString(R.string.login_succeed));
    Utils.goActivity(LoginActivity.this, nextActivity);
    finish();
  }

  public void register(View v) {
    final String userNameStr = userName.getText().toString();
    final String passwordStr = password.getText().toString();
    if (isWrong(userNameStr) || isWrong(passwordStr)) {
      return;
    }

    AVUser user = new AVUser();
    user.setUsername(userNameStr);
    user.setPassword(passwordStr);
    dialog = Utils.showSpinnerDialog(this);
    dialog.show();
    user.signUpInBackground(new SignUpCallback() {
      public void done(AVException e) {
        dialog.dismiss();
        if (e == null) {
          // successfully
          loginSucceed();
          finish();
        } else {
          Utils.toast(cxt, getString(R.string.have_been_registered));
        }
      }
    });
  }
}
