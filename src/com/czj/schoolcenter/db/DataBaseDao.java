package com.czj.schoolcenter.db;

import java.util.ArrayList;

import com.czj.schoolcenter.domain.Grade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseDao {
	private ArrayList<Grade> gradelist;
	private String dbname = "SchoolCenter";
	private DatabaseHelper databaseHelper;

	public DataBaseDao(Context context) {
		databaseHelper = new DatabaseHelper(context, dbname, null, 1);

	}

	public void insert(ContentValues values) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		database.insert("grade", null, values);
		database.close();
	}

	public ArrayList<Grade> findall() {
		gradelist = new ArrayList<Grade>();
		SQLiteDatabase database = databaseHelper.getReadableDatabase();
		Cursor cursor = database.query("grade", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Grade grade = new Grade();
			grade.id = cursor.getString(0);
			grade.term = cursor.getString(1);
			grade.number = cursor.getString(2);
			grade.name = cursor.getString(3);
			grade.score = cursor.getString(4);
			grade.credit = cursor.getString(5);
			gradelist.add(grade);
		}
		database.close();
		return gradelist;
	}

	public boolean deledatabase(Context context) {
		return context.deleteDatabase(dbname);
	}
}
