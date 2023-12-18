package com.example.TimeTable;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.activitydemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Settings extends AppCompatActivity{

    private static final boolean DEBUG = true;
    private static long time;
    private static String date;
    private static int week;
    private static int total;
    private static int show;
    private Calendar calendar;
    private final List<String> courseList = new ArrayList<>();
    private int current_curriculum_id;
    private String curriculum_name;
    private DBHelper dbHelper;

    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        setContentView(R.layout.settings);
        SetStatusBar.setStatusBar(this);

        FloatingActionButton setting_flt_button = findViewById(R.id.setting_flt_button);
        setting_flt_button.setVisibility(View.INVISIBLE);

        FloatingActionButton back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(v -> {
            if(!DEBUG)
            {
                dbHelper.close();
            }
            finish();
        });

        dbHelper = new DBHelper(this, "Curriculum.db", null, 1);
        getSettings();
        LinearLayout timeLayout = findViewById(R.id.start_time_linear_layout);
        TextView timeText = findViewById(R.id.start_time_text_view);
        LinearLayout weekLayout = findViewById(R.id.now_week_number_linear_layout);
        TextView weekText = findViewById(R.id.now_week_number_text_view);
        LinearLayout totalLayout = findViewById(R.id.total_week_number_linear_layout);
        TextView totalText = findViewById(R.id.total_week_number_text_view);

        timeText.setText(date);
        timeLayout.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        int weeks = GetWeeksSinceStartTime.getWeeksSinceStartTime(calendar.getTimeInMillis());
                        long timestamp = calendar.getTimeInMillis();
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("time", timestamp);
                        values.put("week",weeks);
                        db.update("Settings", values, "settings_id=1", null);
                        String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(timestamp);
                        timeText.setText(date);
                        weekText.setText(String.valueOf(weeks));
                        getSettings();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
            datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
            datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
        });

        weekText.setText(String.valueOf(week));
        weekLayout.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(Settings.this);
            View view = inflater.inflate(R.layout.enter_number_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            TextView dialog_title = view.findViewById(R.id.enter_number_dialog_title);
            dialog_title.setText("当前周数");
            EditText edt_number = view.findViewById(R.id.edt_number);
            edt_number.setHint(String.valueOf(week));
            edt_number.setHintTextColor(getResources().getColor(R.color.dark_gray));
            Button cancelButton = view.findViewById(R.id.enter_number_btn_cancel);
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());


            Button confirmButton = view.findViewById(R.id.enter_number_btn_confirm);
            confirmButton.setOnClickListener(v12 -> {
                String week_raw = edt_number.getText().toString();
                if(week_raw.equals(""))
                {
                    week_raw = String.valueOf(week);
                }
                int weeks = Integer.parseInt(week_raw);
                SQLiteDatabase db;
                if (weeks<1||weeks>total) {
                    Toast.makeText(Settings.this, "当前周数不可大于本学期总周数", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    long timestamp = GetWeeksSinceStartTime.getMondayTimestampForWeeksAgo(weeks);
                    values.put("time", timestamp);
                    values.put("week", weeks);
                    db.update("Settings", values, "settings_id=1", null);
                    weekText.setText(String.valueOf(weeks));
                    String date = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(timestamp);
                    timeText.setText(date);
                    getSettings();
                }
                dialog.dismiss();
            });
            dialog.show();
        });


        totalText.setText(String.valueOf(total));
        totalLayout.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(Settings.this);
            View view = inflater.inflate(R.layout.enter_number_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            TextView dialog_title = view.findViewById(R.id.enter_number_dialog_title);
            dialog_title.setText("总周数");
            EditText edt_number = view.findViewById(R.id.edt_number);
            edt_number.setHint(String.valueOf(total));
            edt_number.setHintTextColor(getResources().getColor(R.color.dark_gray));
            Button cancelButton = view.findViewById(R.id.enter_number_btn_cancel);
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());


            Button confirmButton = view.findViewById(R.id.enter_number_btn_confirm);
            confirmButton.setOnClickListener(v12 -> {
                String total_raw = edt_number.getText().toString();
                if(total_raw.equals(""))
                {
                    total_raw = String.valueOf(total);
                }
                int totals = Integer.parseInt(total_raw);
                SQLiteDatabase db;
                if (totals<week) {
                    Toast.makeText(Settings.this, "本学期总周数不可小于当前周数", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("total", totals);
                    db.update("Settings", values, "settings_id=1", null);
                    totalText.setText(String.valueOf(totals));
                    getSettings();
                }
                dialog.dismiss();
            });
            dialog.show();
        });

        /*Switch switch1 = findViewById(R.id.switch1);*/
        SwitchCompat switch1 = findViewById(R.id.switch1);
        switch1.setChecked(show == 1);
        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("show", isChecked ? 1 : 0);
            db.update("Settings", values, "settings_id=1", null);
            switch1.setChecked(isChecked);
            getSettings();
        });

        LinearLayout exportLayout = findViewById(R.id.export_timetable_layout);
        exportLayout.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(Settings.this);
            //引入自定义的对话框布局
            View view = inflater.inflate(R.layout.export_dialog, null);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Settings.this);
            //设置对话框布局
            builder.setView(view);
            android.app.AlertDialog dialog = builder.create();

            Button cancelButton = view.findViewById(R.id.export_cancel);
            cancelButton.setOnClickListener(v1 -> {
                //取消dialog
                dialog.dismiss();
            });

            Button confirmButton = view.findViewById(R.id.export_confirm);
            confirmButton.setOnClickListener(v12 -> {
                Log.e("export", "exportToExcel: " + "正在导出");
                dialog.dismiss();
                checkPermissionAndExport();


            });
            dialog.show();
        });
    }

    private void exportToExcel() {
        Toast.makeText(Settings.this, "正在导出...", Toast.LENGTH_SHORT).show();
        // 获取课程表数据
        loadCourseList();

        // 创建Excel文件
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(curriculum_name));

        CellStyle style_center = workbook.createCellStyle();
        style_center.setAlignment(HorizontalAlignment.CENTER); // 设置水平居中
        style_center.setVerticalAlignment(VerticalAlignment.CENTER); // 设置垂直居中

        CellStyle style_top = workbook.createCellStyle();
        style_top.setAlignment(HorizontalAlignment.LEFT);
        style_top.setVerticalAlignment(VerticalAlignment.TOP);
        style_top.setWrapText(true);

        // 设置表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("节次");
        headerRow.createCell(1).setCellValue("星期一");
        headerRow.createCell(2).setCellValue("星期二");
        headerRow.createCell(3).setCellValue("星期三");
        headerRow.createCell(4).setCellValue("星期四");
        headerRow.createCell(5).setCellValue("星期五");

        Row Row_1 = sheet.createRow(1);
        Cell Cell_1 = Row_1.createCell(0);
        Cell_1.setCellValue("1-2");
        Cell_1.setCellStyle(style_center);
        Row Row_2 = sheet.createRow(2);
        Cell Cell_2 = Row_2.createCell(0);
        Cell_2.setCellValue("3-4");
        Cell_2.setCellStyle(style_center);
        Row Row_3 = sheet.createRow(3);
        Cell Cell_3 = Row_3.createCell(0);
        Cell_3.setCellValue("5-6");
        Cell_3.setCellStyle(style_center);
        Row Row_4 = sheet.createRow(4);
        Cell Cell_4 = Row_4.createCell(0);
        Cell_4.setCellValue("7-8");
        Cell_4.setCellStyle(style_center);

        // 将课程信息写入Excel文件
        for (String courseInfo : courseList) {
            String[] courseInfoArray = courseInfo.split("\\|");
            int time = Integer.parseInt(courseInfoArray[3]);
            int day = Integer.parseInt(courseInfoArray[4]);
            RichTextString richTextString = new XSSFRichTextString(courseInfoArray[0] + "\n" + "@" + courseInfoArray[1] + "\n" + courseInfoArray[2] + "\n" + courseInfoArray[5] + "-" + courseInfoArray[6] + "周");

            Row row = sheet.getRow(time);
            if (row == null) {
                row = sheet.createRow(time);
            }
            Cell cell = row.getCell(day);
            if (cell == null) {
                cell = row.createCell(day);
                cell.setCellValue(richTextString);

            }else{
                String old_course = cell.getStringCellValue();
                cell.setCellValue(old_course + "\n" + richTextString);
            }
            cell.setCellStyle(style_top);

            // 自动调整行高度
            cell.getRow().setHeight((short)-1);
            //row.setHeight((short) 1000);
        }

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), curriculum_name + ".xlsx");
            FileOutputStream outputStream = new FileOutputStream(file);
            workbook.write(outputStream);

            // 发送广播通知系统媒体库更新文件
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            Toast.makeText(this, "已导出到 " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.i("export", "exportToExcel: " + "导出完成" + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
            Log.e("export", "exportToExcel: " + "导出失败");
        }
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
            curriculum_name = cursor.getString(cursor.getColumnIndex("curriculum_name"));
        }
        cursor.close();
        selection = "curriculum_id = ?";
        selectionArgs = new String[]{String.valueOf(current_curriculum_id)};
        cursor = db.query("Courses", null, selection, selectionArgs, null, null,
                "course_id ASC");
        if (cursor.moveToFirst()) {
            do {
                String course_name = cursor.getString(cursor.getColumnIndex("course_name"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                int time = cursor.getInt(cursor.getColumnIndex("time"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int week_start = cursor.getInt(cursor.getColumnIndex("week_start"));
                int week_end = cursor.getInt(cursor.getColumnIndex("week_end"));

                String CourseInfo =
                                course_name + "|" + classroom + "|" + teacher + "|" + time + "|" + day + "|" + week_start + "|" + week_end ;
                courseList.add(CourseInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (!DEBUG) {
            dbHelper.close();
        }
    }

    private static final int PERMISSION_REQUEST_CODE = 1;

    private void checkPermissionAndExport() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("export", "checkPermissionAndExport: " + "没有存储权限");
            Toast.makeText(this, "请授予存储权限", Toast.LENGTH_SHORT).show();
            // 如果没有存储权限，则请求该权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            Log.e("export", "checkPermissionAndExport: " + "有存储权限");
            // 如果已经有存储权限，则导出Excel文件
            exportToExcel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("export", "onRequestPermissionsResult: " + "用户授予了存储权限");
                // 如果用户授予存储权限，则导出Excel文件
                exportToExcel();
            } else {
                Log.e("export", "onRequestPermissionsResult: " + "用户拒绝了存储权限");
                // 如果用户拒绝了存储权限，则显示一个提示信息
                Toast.makeText(this, "需要存储权限才能导出Excel文件", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("Range")
    private void getSettings() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Settings", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            time = cursor.getLong(cursor.getColumnIndex("time"));
            week = cursor.getInt(cursor.getColumnIndex("week"));
            total = cursor.getInt(cursor.getColumnIndex("total"));
            show = cursor.getInt(cursor.getColumnIndex("show"));
        }
        cursor.close();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        date = simpleDateFormat.format(time);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
    }
}
