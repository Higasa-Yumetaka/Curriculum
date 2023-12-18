package com.example.TimeTable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.activitydemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Course extends AppCompatActivity {
    private final boolean DEBUG = true;
    int row;
    int column;
    int exist;
    private DBHelper dbHelper;
    int course_id;
    String course_name;
    String course_name_;
    String teacher;
    String classroom;
    int time;
    int day;
    int color;
    int week_start;
    int week_start_;
    int week_end;
    int week_end_;
    int curriculum_id;
    TextView course_title;
    private Spinner course_times_spinner;
    private EditText course_name_edit_text;
    private EditText classroom_edit_text;
    private EditText teacher_edit_text;
    private EditText edt_week_start;
    private EditText edt_week_end;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.course);
        SetStatusBar.setStatusBar(this);
        dbHelper = new DBHelper(this, "Curriculum.db", null, 1);


        course_title = findViewById(R.id.course_title);
        course_name_edit_text = findViewById(R.id.course_name_edit_text);
        classroom_edit_text = findViewById(R.id.classroom_edit_text);
        teacher_edit_text = findViewById(R.id.teacher_edit_text);
        edt_week_start = findViewById(R.id.edt_week_start);
        edt_week_end = findViewById(R.id.edt_week_end);
        course_times_spinner = findViewById(R.id.spinner_time);

        //返回
        FloatingActionButton back_btn = findViewById(R.id.course_back_button);
        back_btn.setOnClickListener(v -> {
            if (!DEBUG) {
                dbHelper.close();
            }
            finish();
        });

        //保存课程
        FloatingActionButton save_btn = findViewById(R.id.course_save_button);
        save_btn.setOnClickListener(v -> {
            week_start = Integer.parseInt(edt_week_start.getText().toString());
            week_end = Integer.parseInt(edt_week_end.getText().toString());
            course_name = course_name_edit_text.getText().toString();
            if (course_id == 0) {
                if (exist == 0) {
                    if (week_start > week_end) {
                        Toast.makeText(Course.this, "开始周不能大于结束周", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        addCourse();
                    }
                } else {

                    if (week_start >= week_start_ && week_start <= week_end_) {
                        Toast.makeText(Course.this, "第" + week_start + "周已经存在课程:" + course_name_ + "," + week_start_ + "-" + week_end_ + "周", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (week_end <= week_end_ && week_end >= week_start_) {
                        Toast.makeText(Course.this, "第" + week_end + "周已经存在课程:" + course_name_ + "," + week_start_ + "-" + week_end_ + "周", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (course_name.equals("")) {
                            Toast.makeText(Course.this, "课程名不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            addCourse();
                        }
                    }
                }

            } else {
                if (week_start > week_end) {
                    Toast.makeText(Course.this, "开始周不能大于结束周", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (course_name.equals("")) {
                        Toast.makeText(Course.this, "课程名不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        updateCourse();
                    }

                }
            }
            if (!DEBUG) {
                dbHelper.close();
            }
            finish();
        });

        //删除课程
        FloatingActionButton delete_btn = findViewById(R.id.course_delete_button);
        delete_btn.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(Course.this);
            //引入自定义的对话框布局
            View view = inflater.inflate(R.layout.delete_dialog, null);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Course.this);
            //设置对话框布局
            builder.setView(view);
            android.app.AlertDialog dialog = builder.create();
            TextView message = view.findViewById(R.id.delete_message);
            message.setText("确认要删除该课程吗?");

            Button cancelButton = view.findViewById(R.id.delete_cancel);
            cancelButton.setOnClickListener(v1 -> {
                //取消dialog
                dialog.dismiss();
            });
            //确认删除
            Button confirmButton = view.findViewById(R.id.delete_confirm);
            confirmButton.setOnClickListener(v12 -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int result = db.delete("Courses", "course_id =?", new String[]{String.valueOf(course_id)});
                if (result == -1) {
                    Toast.makeText(Course.this, "删除失败", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Course.this, "课程已删除", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //setResult(RESULT_OK);
                dialog.dismiss();
            });
            dialog.show();
            if (!DEBUG) {
                dbHelper.close();
            }
        });

        //添加课程
        FloatingActionButton add_button = findViewById(R.id.course_add_button);
        add_button.setOnClickListener(v -> {
            course_id = 0;
            exist = 1;
            course_name_ = course_name;
            course_name = "";
            teacher = "";
            classroom = "";
            week_start_ = week_start;
            week_end_ = week_end;

            week_start= 0;
            week_end = 0;
            course_name_edit_text.setText("");
            teacher_edit_text.setText("");
            classroom_edit_text.setText("");
            edt_week_start.setText("");
            edt_week_end.setText("");

            course_title.setText("添加课程");
            add_button.setVisibility(View.GONE);
            delete_btn.setVisibility(View.GONE);

        });


        row = getIntent().getIntExtra("row", 1);
        column = getIntent().getIntExtra("column", 1);
        exist = getIntent().getIntExtra("exist", 0);
        course_id = getIntent().getIntExtra("course_id", 0);
        curriculum_id = getIntent().getIntExtra("curriculum_id", 0);
        course_name_ = getIntent().getStringExtra("course_name_");

        course_title = findViewById(R.id.course_title);
        course_times_spinner.setSelection(row - 1);
        if (course_id == 0) {
            course_title.setText("添加课程");
            delete_btn.setVisibility(android.view.View.GONE);
            add_button.setVisibility(android.view.View.GONE);
        } else {
            course_title.setText("编辑课程");
            loadCourse();
        }
    }

    @SuppressLint("Range")
    protected void loadCourse() {
        String selection;
        String[] selectionArgs;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        selection = "course_id = ?";
        selectionArgs = new String[]{String.valueOf(course_id)};
        cursor = db.query("Courses", null, selection, selectionArgs, null, null,
                "course_id DESC");
        if (cursor.moveToFirst()) {
            course_name = cursor.getString(cursor.getColumnIndex("course_name"));
            teacher = cursor.getString(cursor.getColumnIndex("teacher"));
            classroom = cursor.getString(cursor.getColumnIndex("classroom"));
            time = cursor.getInt(cursor.getColumnIndex("time"));
            day = cursor.getInt(cursor.getColumnIndex("day"));
            week_start = cursor.getInt(cursor.getColumnIndex("week_start"));
            week_end = cursor.getInt(cursor.getColumnIndex("week_end"));
            color = cursor.getInt(cursor.getColumnIndex("color"));
        }
        cursor.close();
        if (!DEBUG) {
            dbHelper.close();
        }
        course_name_edit_text.setText(course_name);
        classroom_edit_text.setText(classroom);
        teacher_edit_text.setText(teacher);
        edt_week_start.setText(String.valueOf(week_start));
        edt_week_end.setText(String.valueOf(week_end));
    }

    private void addCourse(){
        course_name = course_name_edit_text.getText().toString();
        if(TextUtils.isEmpty(course_name)){
            Toast.makeText(this,"课程名不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        teacher = teacher_edit_text.getText().toString();
        classroom = classroom_edit_text.getText().toString();
        if(TextUtils.isEmpty(edt_week_start.getText().toString())){
            week_start = 1;
        }else if(TextUtils.isEmpty(edt_week_end.getText().toString())){
            week_end = 99;
        }else{
            week_start= Integer.parseInt(edt_week_start.getText().toString());
            week_end = Integer.parseInt(edt_week_end.getText().toString());
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(week_start > week_end){
            Toast.makeText(this,"周数输入错误",Toast.LENGTH_SHORT).show();
            return;
        }
        long result = db.insert("Courses",null,getContentValues());
        if(result != -1){
            course_id = (int) result;
            Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        }else{
            Log.e("addCourse","添加失败");
            Toast.makeText(this,"添加失败",Toast.LENGTH_SHORT).show();
        }
        if(!DEBUG){
            dbHelper.close();
        }
        finish();
    }

    private void updateCourse(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        week_start= Integer.parseInt(edt_week_start.getText().toString());
        week_end = Integer.parseInt(edt_week_end.getText().toString());
        if(week_start > week_end){
            Toast.makeText(this,"周数输入错误",Toast.LENGTH_SHORT).show();
            return;
        }
        String selection;
        String[] selectionArgs;
        selection = "course_id = ?";
        selectionArgs = new String[]{String.valueOf(course_id)};
        long result = db.update("Courses",getContentValues(),selection,selectionArgs);
        if(result != -1){
            Toast.makeText(this,"修改成功",Toast.LENGTH_SHORT).show();
        }else{
            Log.e("updateCourse","修改失败");
            Toast.makeText(this,"修改失败",Toast.LENGTH_SHORT).show();
        }

    }

    private ContentValues getContentValues(){
        ContentValues values = new ContentValues();
        course_name = course_name_edit_text.getText().toString();
        values.put("course_name",course_name);
        values.put("curriculum_id",curriculum_id);
        teacher = teacher_edit_text.getText().toString();
        values.put("teacher",teacher);
        classroom = classroom_edit_text.getText().toString();
        values.put("classroom",classroom);
        time=course_times_spinner.getSelectedItemPosition()+1;
        values.put("time",time);
        day=column;
        values.put("day",day);
        week_start = Integer.parseInt(edt_week_start.getText().toString());
        values.put("week_start",week_start);
        week_end = Integer.parseInt(edt_week_end.getText().toString());
        values.put("week_end",week_end);
        color=1;
        values.put("color",color);
        return values;
    }

}
