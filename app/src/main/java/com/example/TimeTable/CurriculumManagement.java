package com.example.TimeTable;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activitydemo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CurriculumManagement extends AppCompatActivity {
    private static final boolean DEBUG = true;
    private final List<String> curriculumList = new ArrayList<>();
    private DBHelper dbHelper;
    private ListView lvCurriculum;
    private TextView curriculum_name_on_use;

    FloatingActionButton curriculum_delete_btn;
    FloatingActionButton curriculum_edit_btn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        SetStatusBar.setStatusBar(this);
        dbHelper = new DBHelper(this, "Curriculum.db", null, 1);
        setContentView(R.layout.curriculum_manage_layout);

        curriculum_delete_btn = findViewById(R.id.curriculum_delete_btn);
        curriculum_edit_btn = findViewById(R.id.curriculum_edit_btn);

        FloatingActionButton back_btn = findViewById(R.id.back_button);
        back_btn.setOnClickListener(v -> {
            if(!DEBUG)
            {
                dbHelper.close();
            }
            finish();
        });

        curriculum_name_on_use = findViewById(R.id.curriculum_name_on_use);
        lvCurriculum = findViewById(R.id.curriculum_list);
        loadCurriculum();
        lvCurriculum.setOnItemClickListener((parent, view, position, id) -> {
            String curriculum_name_raw = curriculum_name_on_use.getText().toString();
            String curriculum_name = curriculumList.get(position);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("isOnUse", 1);
            db.update("Curriculum", values, "curriculum_name = ?", new String[]{curriculum_name});
            values.clear();
            values.put("isOnUse", 0);
            db.update("Curriculum", values, "curriculum_name = ?", new String[]{curriculum_name_raw});
            loadCurriculum();
            finish();
        });

        FloatingActionButton add_btn = findViewById(R.id.add_button);
        add_btn.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(CurriculumManagement.this);
            View view = inflater.inflate(R.layout.add_curriculum_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(CurriculumManagement.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            Button cancelButton = view.findViewById(R.id.btn_cancel);
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());



            Button confirmButton = view.findViewById(R.id.btn_confirm);
            confirmButton.setOnClickListener(v12 -> {
                EditText edt_name = view.findViewById(R.id.edt_name);
                String curriculum_name = edt_name.getText().toString();
                SQLiteDatabase db;
                long result;
                if (curriculum_name.equals("")) {
                    Toast.makeText(CurriculumManagement.this, "课程名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    db = dbHelper.getReadableDatabase();
                    String[] projection = {
                            "curriculum_name"
                    };
                    String selection = "curriculum_name = ?";
                    String[] selectionArgs = {curriculum_name};
                    Cursor cursor = db.query("Curriculum", projection, selection, selectionArgs, null, null, null);
                    if(cursor.getCount() != 0){
                        Toast.makeText(CurriculumManagement.this, "课表名已存在", Toast.LENGTH_SHORT).show();
                        cursor.close();
                        return;
                    }else{
                        db = dbHelper.getWritableDatabase();
                        result = db.insert("Curriculum", null,getContentValues(curriculum_name,0));
                        if(result == -1){
                            Toast.makeText(CurriculumManagement.this, "添加失败", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(CurriculumManagement.this, "添加成功", Toast.LENGTH_SHORT).show();
                            loadCurriculum();
                        }
                    }

                }
                if(!DEBUG)
                {
                    db.close();
                }
                dialog.dismiss();
            });
            dialog.show();
        });

        curriculum_delete_btn.setOnClickListener(v -> {
            //弹出确认删除对话框
            LayoutInflater inflater = LayoutInflater.from(CurriculumManagement.this);
            //引入自定义的对话框布局
            View view = inflater.inflate(R.layout.delete_dialog, null);
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CurriculumManagement.this);
            //设置对话框布局
            builder.setView(view);
            android.app.AlertDialog dialog = builder.create();

            Button cancelButton = view.findViewById(R.id.delete_cancel);
            cancelButton.setOnClickListener(v1 -> {
                //取消dialog
                dialog.dismiss();
            });
            //确认删除
            Button confirmButton = view.findViewById(R.id.delete_confirm);
            confirmButton.setOnClickListener(v12 -> {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String C = curriculum_name_on_use.getText().toString();
                int result = db.delete("Curriculum", "curriculum_name =?", new String[]{C});
                if (result == -1){
                    Toast.makeText(CurriculumManagement.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(CurriculumManagement.this, "课表已删除", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //setResult(RESULT_OK);
                db.close();
                dialog.dismiss();
            });
            dialog.show();
        });

        curriculum_edit_btn.setOnClickListener(v -> {
            LayoutInflater inflater = LayoutInflater.from(CurriculumManagement.this);
            View view = inflater.inflate(R.layout.add_curriculum_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(CurriculumManagement.this);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            EditText edt_name = view.findViewById(R.id.edt_name);
            edt_name.setText(curriculum_name_on_use.getText().toString());
            Button cancelButton = view.findViewById(R.id.btn_cancel);
            cancelButton.setOnClickListener(v1 -> dialog.dismiss());

            Button confirmButton = view.findViewById(R.id.btn_confirm);
            confirmButton.setOnClickListener(v12 -> {

                String curriculum_name = edt_name.getText().toString();
                SQLiteDatabase db;
                if (curriculum_name.equals("")) {
                    Toast.makeText(CurriculumManagement.this, "课程名不能为空", Toast.LENGTH_SHORT).show();
                }
                else{
                    db = dbHelper.getReadableDatabase();
                    String[] projection = {
                            "curriculum_name"
                    };
                    String selection = "curriculum_name = ?";
                    String[] selectionArgs = {curriculum_name};
                    Cursor cursor = db.query("Curriculum", projection, selection, selectionArgs, null, null, null);
                    if(cursor.getCount() != 0){
                        Toast.makeText(CurriculumManagement.this, "课表名已存在", Toast.LENGTH_SHORT).show();
                        cursor.close();
                    }else{
                        db = dbHelper.getWritableDatabase();
                        db.update("Curriculum", getContentValues(curriculum_name,1),"curriculum_name = ?",new String[]{curriculum_name_on_use.getText().toString()});
                        loadCurriculum();
                    }

                }
                if(!DEBUG)
                {
                    db.close();
                }
                dialog.dismiss();
            });
            dialog.show();
        });
    }

    @SuppressLint("Range")
    protected void loadCurriculum() {

        curriculumList.clear();
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.query("Curriculum", null, null, null, null, null,
                "curriculum_id DESC");
        boolean load = false;
        if (cursor.moveToFirst()) {
            do {
                String curriculum_name = cursor.getString(cursor.getColumnIndex("curriculum_name"));
                int isOnUse = cursor.getInt(cursor.getColumnIndex("isOnUse"));
                if (isOnUse == 0) {
                    curriculumList.add(curriculum_name);
                }
                else if(isOnUse == 1) {
                    if(!curriculum_name.equals("")){
                        load = true;
                        curriculum_name_on_use.setText(curriculum_name);
                        curriculum_name_on_use.setTextColor(Color.parseColor("#0E77FF"));
                    }
                }
            } while (cursor.moveToNext());
            if (!DEBUG) {
                dbHelper.close();
            }
        }cursor.close();
        if(!load){
            curriculum_delete_btn.setVisibility(View.GONE);
            curriculum_edit_btn.setVisibility(View.GONE);
        }
        CurriculumManagement.CurriculumListAdapter adapter = new CurriculumManagement.CurriculumListAdapter(this, R.layout.curriculum_item, curriculumList);
        lvCurriculum.setAdapter(adapter);
    }

    public static class CurriculumListAdapter extends ArrayAdapter<String> {
        private final int resourceId;
        public CurriculumListAdapter(Context context, int ResourceId, List<String> noteList) {
            super(context, ResourceId, noteList);
            resourceId = ResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String curriculum = getItem(position);
            View view;
            CurriculumManagement.CurriculumListAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
                viewHolder = new CurriculumManagement.CurriculumListAdapter.ViewHolder();
                viewHolder.curriculum_name = view.findViewById(R.id.curriculum_name);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (CurriculumManagement.CurriculumListAdapter.ViewHolder) view.getTag();
            }

            String curriculumName = curriculum.trim();

            if (curriculumName.equals("")) {
                viewHolder.curriculum_name.setText("无标题");
            } else {
                viewHolder.curriculum_name.setText(curriculumName);
                viewHolder.curriculum_name.setWidth(1000);
            }
            return view;
        }

        private static class ViewHolder {
            TextView curriculum_name;
        }
    }

    private ContentValues getContentValues(String name, int isOnUse) {

        ContentValues values = new ContentValues();
        values.put("curriculum_name", name);
        values.put("isOnUse", isOnUse);
        return values;
    }
}
