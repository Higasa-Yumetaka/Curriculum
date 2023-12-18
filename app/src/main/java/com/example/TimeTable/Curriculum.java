package com.example.TimeTable;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activitydemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Curriculum extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private final List<String> courseList = new ArrayList<>();
    private int current_curriculum_id;
    private int current_week;
    private int choose_week;
    private DBHelper dbHelper;
    LinearLayout table;
    TextView week;
    int show;
    int total;

    public Curriculum() {
    }

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.curriculum);
        SetStatusBar.setStatusBar(this);
        dbHelper = new DBHelper(this, "Curriculum.db", null, 1);
        week = findViewById(R.id.week);


        table = findViewById(R.id.curriculum);

        FloatingActionButton btn1 = findViewById(R.id.curriculum_back_button);
        FloatingActionButton btn2 = findViewById(R.id.curriculum_button);
        btn1.setVisibility(View.INVISIBLE);
        btn2.setVisibility(View.INVISIBLE);

        FloatingActionButton setting_btn = findViewById(R.id.setting_button);
        setting_btn.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(Curriculum.this, Settings.class);
            startActivity(intent);
        });

        FloatingActionButton switch_btn = findViewById(R.id.switch_button);
        switch_btn.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(Curriculum.this, CurriculumManagement.class);
            startActivity(intent);
        });

        getSettings();
        choose_week = current_week;
        week.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(Curriculum.this);
            View view = inflater.inflate(R.layout.enter_number_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(Curriculum.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            TextView dialog_title = view.findViewById(R.id.enter_number_dialog_title);
            dialog_title.setText("周数");
            EditText edt_number = view.findViewById(R.id.edt_number);
            edt_number.setHint(String.valueOf(current_week));
            edt_number.setHintTextColor(getResources().getColor(R.color.dark_gray));
            Button cancelButton = view.findViewById(R.id.enter_number_btn_cancel);
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());
            Button confirmButton = view.findViewById(R.id.enter_number_btn_confirm);
            confirmButton.setOnClickListener(v12 -> {
                String week_raw = edt_number.getText().toString();
                if(TextUtils.isEmpty(week_raw)){
                    week_raw = String.valueOf(current_week);
                }
                int weeks = Integer.parseInt(week_raw);
                if (weeks < 1 || weeks > total) {
                    Toast.makeText(Curriculum.this, "请输入周数", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    choose_week = weeks;
                    //Log.e("choose_week", String.valueOf(choose_week));
                    loadAll();
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAll();
    }

    @SuppressLint("Range")
    protected void loadCourseList() {
        String selection = "isOnUse = ?";
        String[] selectionArgs = new String[]{"1"};
        courseList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.query("Curriculum", null, selection, selectionArgs, null, null,
                "curriculum_id DESC");
        if (cursor.moveToFirst()) {
            current_curriculum_id = cursor.getInt(cursor.getColumnIndex("curriculum_id"));
        }
        cursor.close();
        selection = "curriculum_id = ?";
        selectionArgs = new String[]{String.valueOf(current_curriculum_id)};
        cursor = db.query("Courses", null, selection, selectionArgs, null, null,
                "course_id ASC");
        if (cursor.moveToFirst()) {
            do {
                int course_id = cursor.getInt(cursor.getColumnIndex("course_id"));
                String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int week_start = cursor.getInt(cursor.getColumnIndex("week_start"));
                int week_end = cursor.getInt(cursor.getColumnIndex("week_end"));
                int color = cursor.getInt(cursor.getColumnIndex("color"));
                String CourseInfo =
                        course_id + "|"
                                + course_name + "|"
                                + teacher + "|"
                                + classroom + "|"
                                + time + "|"
                                + day + "|"
                                + week_start + "|"
                                + week_end + "|"
                                + color;
                courseList.add(CourseInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (!DEBUG) {
            dbHelper.close();
        }
    }

    private void loadAll() {

        //Log.e("choose_week", String.valueOf(choose_week));
        table.removeAllViews();
        getSettings();
        String week_text = "第" + choose_week + "周";
        week.setText(week_text);
        loadCourseList();
        //Log.e("courseList", courseList.toString());

        int rowCount = 5; // 行数
        int columnCount = 6; // 列数
// 获取屏幕高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

// 计算每个单元格的高度
        int cellHeight = (screenHeight - 100) / rowCount;

        for (int i = 0; i < rowCount; i++) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < columnCount; j++) {
                /*TextView cellView = new TextView(this);*/
                LinearLayout cellView = new LinearLayout(this);
                //(0,0)不绘制
                if (i == 0 && j == 0) {
                    cellView.setGravity(Gravity.CENTER);
                    cellView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            cellHeight / 4,
                            0.4f));
                } else if (i == 0) {//第一行，绘制星期

                    TextView dayOfWeekView = new TextView(this);
                    dayOfWeekView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f));
                    dayOfWeekView.setGravity(Gravity.CENTER);
                    dayOfWeekView.setTextSize(12);
                    // 获取当前日期和时间
                    Calendar calendar = Calendar.getInstance();
                    // 获取今天的星期
                    int weekdayIndex = calendar.get(Calendar.DAY_OF_WEEK);
                    if (j == 1)
                        dayOfWeekView.setText("周一");
                    else if (j == 2)
                        dayOfWeekView.setText("周二");
                    else if (j == 3)
                        dayOfWeekView.setText("周三");
                    else if (j == 4)
                        dayOfWeekView.setText("周四");
                    else
                        dayOfWeekView.setText("周五");
                    dayOfWeekView.setTextColor(getResources().getColor(R.color.black));

                    cellView.addView(dayOfWeekView);

                    if (j == weekdayIndex - 1) {//今天
                        dayOfWeekView.setTextColor(getResources().getColor(R.color.blue));
                    }
                    cellView.setGravity(Gravity.CENTER);
                    //cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_drawable);
                    cellView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            cellHeight / 4,
                            1.0f));
                } else if (j == 0) {//第一列，绘制节数
                    TextView timeView = new TextView(this);
                    String time_text = 2 * i -1 + "-" + 2 * i;
                    timeView.setText(time_text);
                    timeView.setTextColor(getResources().getColor(R.color.black));
                    cellView.setGravity(Gravity.CENTER);
                    cellView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            cellHeight,
                            0.4f));
                    timeView.setGravity(Gravity.CENTER);
                    timeView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f));
                    cellView.addView(timeView);
                } else {
                    //绘制课程

                    cellView.setLayoutParams(new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            1.0f));

                    //cellView.setText("课程");
                    int finalI = i;//节数
                    int finalJ = j;//星期几
                    int course_id;
                    String course_name;
                    String teacher;
                    String classroom;
                    int time;
                    int day;
                    int week_start;
                    int week_end;
                    boolean anotherCourse = false;

                    String loaded_course_name = "";
                    String loaded_teacher = "";
                    String loaded_classroom = "";
                    int loaded_week_start = 0;
                    int loaded_week_end = 0;
                    int loaded_course_id = 0;

                    boolean loaded = false;

                    for (String item : courseList) {

                        String[] courseInfo = item.split("\\|");
                        course_id = Integer.parseInt(courseInfo[0]);
                        course_name = courseInfo[1];
                        teacher = courseInfo[2];
                        classroom = courseInfo[3];
                        time = Integer.parseInt(courseInfo[4]);
                        day = Integer.parseInt(courseInfo[5]);
                        week_start = Integer.parseInt(courseInfo[6]);
                        week_end = Integer.parseInt(courseInfo[7]);

                        if (day == finalJ && time == finalI) {
                            //Log.e("course_name", "row: " + i + " col: " + j + " loaded: " + loaded);
                            if (loaded) {
                                anotherCourse = true;
                                if (choose_week >= week_start) {
                                    loaded_course_id = course_id;
                                    loaded_course_name = course_name;
                                    loaded_classroom = classroom;
                                    loaded_teacher = teacher;
                                    loaded_week_start = week_start;
                                    loaded_week_end = week_end;
                                }
                            }else{
                                loaded = true;
                                loaded_course_id = course_id;
                                loaded_course_name = course_name;
                                loaded_classroom = classroom;
                                loaded_teacher = teacher;
                                loaded_week_start = week_start;
                                loaded_week_end = week_end;
                            }
                        }
                    }
                    //Log.e("course_name", "row: " + i + " col: " + j + " name: " + course_name);
                    if(loaded) {
                        cellView.removeAllViews();
                        RelativeLayout courseLayout = new RelativeLayout(this);
                        courseLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.MATCH_PARENT,
                                RelativeLayout.LayoutParams.MATCH_PARENT));
                        int padding = 25;
                        courseLayout.setPadding(padding, (int) (1.5 * padding), padding, (int) (0.5 * padding));
                        boolean be_gray = loaded_week_start > choose_week || loaded_week_end < choose_week;
                        TextView courseView = new TextView(this);
                        courseView.setGravity(Gravity.START);
                        courseView.setId(View.generateViewId()); // 设置一个唯一的ID
                        if(show == 0 && be_gray){
                            courseView.setText("");
                        }else{
                            courseView.setText(loaded_course_name);
                        }
                        courseView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                        //Log.e("loaded course_name", "loaded course_name: " + course_name);
                        courseView.setMaxLines(4);
                        courseView.setEllipsize(TextUtils.TruncateAt.END);
                        courseView.setTextSize(14);
                        RelativeLayout.LayoutParams courseViewParams = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        courseViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP); // 将视图顶部对齐
                        courseViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT); // 将视图左侧对齐
                        courseLayout.addView(courseView, courseViewParams);

                        TextView courseView2 = new TextView(this);
                        courseView2.setGravity(Gravity.START);
                        courseView2.setId(View.generateViewId()); // 设置一个唯一的ID
                        if(loaded_classroom.equals("")||(show == 0 && be_gray)){
                            courseView2.setText("");
                        }else {
                            String classroom_text = "@" + loaded_classroom;
                            courseView2.setText(classroom_text);
                        }

                        courseView2.setMaxLines(2);
                        courseView2.setEllipsize(TextUtils.TruncateAt.END);
                        courseView2.setTextSize(10);
                        RelativeLayout.LayoutParams courseView2Params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        //courseView2Params.addRule(RelativeLayout.ALIGN_TOP, courseView.getId()); // 将视图顶部对齐
                        courseView2Params.addRule(RelativeLayout.BELOW, courseView.getId()); // 将视图放置在 courseView 下方
                        //courseView2Params.addRule(RelativeLayout.RIGHT_OF, courseView.getId()); // 将视图放置在 courseView 右侧
                        courseLayout.addView(courseView2, courseView2Params);

                        TextView courseView3 = new TextView(this);
                        courseView3.setGravity(Gravity.START);
                        courseView3.setId(View.generateViewId()); // 设置一个唯一的ID
                        if(show == 0 && be_gray){
                            courseView3.setText("");
                        }else {
                            courseView3.setText(loaded_teacher);
                        }
                        courseView3.setMaxLines(2);
                        courseView3.setEllipsize(TextUtils.TruncateAt.END);
                        courseView3.setTextSize(10);
                        if (be_gray) {
                            courseView.setTextColor(getColor(R.color.text_color_2));
                            courseView2.setTextColor(getColor(R.color.text_color_2));
                            courseView3.setTextColor(getColor(R.color.text_color_2));

                        } else {
                            courseView.setTextColor(getColor(R.color.text_color_1));
                            courseView2.setTextColor(getColor(R.color.text_color_1));
                            courseView3.setTextColor(getColor(R.color.text_color_1));
                        }

                        RelativeLayout.LayoutParams courseView3Params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        //courseView3Params.addRule(RelativeLayout.ALIGN_TOP, courseView2.getId()); // 将视图顶部对齐
                        courseView3Params.addRule(RelativeLayout.BELOW, courseView2.getId()); // 将视图放置在 courseView 下方
                        //courseView3Params.addRule(RelativeLayout.RIGHT_OF, courseView2.getId()); // 将视图放置在 courseView2 右侧
                        courseLayout.addView(courseView3, courseView3Params);



                        TextView courseView4 = new TextView(this);
                        courseView4.setGravity(Gravity.START);
                        courseView4.setId(View.generateViewId()); // 设置一个唯一的ID
                        if(!anotherCourse||(show == 0 && be_gray)){
                            courseView4.setText("");
                        }else {
                            courseView4.setText("▲");
                        }
                        if(be_gray) {
                            courseView4.setTextColor(getColor(R.color.text_color_2));
                        }else {
                            courseView4.setTextColor(getColor(R.color.text_color_1));
                        }

                        courseView4.setMaxLines(2);
                        courseView4.setEllipsize(TextUtils.TruncateAt.END);
                        courseView4.setTextSize(10);
                        RelativeLayout.LayoutParams courseView4Params = new RelativeLayout.LayoutParams(
                                RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        courseView4Params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // 将视图顶部对齐
                        courseView4Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // 将视图左侧对齐
                        courseLayout.addView(courseView4, courseView4Params);


                        cellView.addView(courseLayout);
                        if (!be_gray) {
                            cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_color_1_drawable);
                        } else {
                            if(show == 1){
                                cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_color_2_drawable);
                            }else {
                                cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_default_drawable);
                            }
                        }
                    }else{
                        cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_default_drawable);
                    }
                    if (courseList.isEmpty()) {
                        loaded_course_id = 0;
                        cellView.setBackgroundResource(R.drawable.curriculum_item_shape_layer_list_default_drawable);
                    }
                    int finalCourse_id = loaded_course_id;
                    cellView.setGravity(Gravity.CENTER);
                    cellView.setOnClickListener(v -> {
                        android.content.Intent intent = new android.content.Intent(Curriculum.this, Course.class);
                        intent.putExtra("row", finalI);
                        intent.putExtra("column", finalJ);
                        intent.putExtra("course_id", finalCourse_id);
                        intent.putExtra("curriculum_id", current_curriculum_id);
                        startActivity(intent);
                    });
                }
                rowLayout.addView(cellView);
            }
            table.addView(rowLayout);
        }
    }

    @SuppressLint("Range")
    private void getSettings () {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Settings", null, null, null, null, null, null);
        cursor.moveToFirst();
        current_week = cursor.getInt(cursor.getColumnIndex("week"));
        total = cursor.getInt(cursor.getColumnIndex("total"));
        show = cursor.getInt(cursor.getColumnIndex("show"));
        cursor.close();
    }
}

