package com.example.TimeTable;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public static final String CREATE_COURSES = "create table Courses("
            + "course_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "curriculum_id INTEGER,"
            + "course_name TEXT,"
            + "teacher TEXT,"
            + "classroom TEXT,"
            + "time INTEGER,"
            + "day INTEGER,"
            + "week_start INTEGER,"
            + "week_end INTEGER,"
            + "color INTEGER"
            + ")";

    private static final String default_courses = "insert into Courses(curriculum_id,course_name,teacher,classroom,time,day,week_start,week_end,color) values(1,'默认课程','默认教师','默认教室',1,1,1,21,1)";

    public static final String CREATE_CURRICULUM = "create table Curriculum("
            + "curriculum_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "curriculum_name TEXT,"
            + "isOnUse INTEGER"
            + ")";

    public static final String CREATE_SETTINGS = "create table Settings("
            + "settings_id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "time INTEGER,"
            + "week INTEGER,"
            + "total INTEGER,"
            + "show INTEGER"
            + ")";

    private static final String default_settings = "insert into Settings(time, week, total, show) values(1685548800000, 1, 20, 1)";


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static final String default_curriculum = "insert into Curriculum(curriculum_name,isOnUse) values('默认课表',1)";

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_COURSES);db.execSQL(default_courses);
        db.execSQL(CREATE_CURRICULUM);db.execSQL(default_curriculum);
        db.execSQL(CREATE_SETTINGS);db.execSQL(default_settings);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
