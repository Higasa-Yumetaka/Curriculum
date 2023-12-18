package com.example.TimeTable;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;

public class SetStatusBar {
    public static void setStatusBar(Activity activity) {
        Window window = activity.getWindow();
        //设置状态栏文字颜色及图标为深色
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //设置状态栏颜色为透明
        window.setStatusBarColor(Color.TRANSPARENT);
    }
}