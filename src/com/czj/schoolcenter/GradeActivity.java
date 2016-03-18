package com.czj.schoolcenter;

import java.io.File;
import java.util.ArrayList;

import com.czj.schoolcenter.db.DataBaseDao;
import com.czj.schoolcenter.domain.Grade;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GradeActivity extends Activity {
	private ListView lv_grade;
	private ArrayList<Grade> gradelist;
	private DataBaseDao dao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grade);
		dao = new DataBaseDao(this);
		lv_grade = (ListView) findViewById(R.id.lv_grade);
		gradelist = dao.findall();
		if (gradelist!=null) {
			lv_grade.setAdapter(new MyAdapter());
		}
		
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.out.println("ondestroy...........");
		dao.deledatabase(getApplicationContext());
		
	}



	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return gradelist.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			View view = View.inflate(getApplicationContext(), R.layout.lvitem, null);
			TextView grade_id = (TextView) view.findViewById(R.id.grade_id);
			TextView grade_score = (TextView) view.findViewById(R.id.grade_score);
			TextView grade_name = (TextView) view.findViewById(R.id.grade_name);
			Grade grade = gradelist.get(position);
			grade_id.setText(grade.id);
			grade_name.setText(grade.name);
			grade_score.setText(grade.score);
			return view;
		}

	}
}
