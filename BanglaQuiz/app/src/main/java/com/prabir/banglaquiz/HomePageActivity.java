package com.prabir.banglaquiz;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import com.prabir.banglaquiz.R;

public class HomePageActivity extends Activity {

	Intent menu = null;
	BufferedReader bReader = null;
	static List <JSONArray> quizSubjectList;
	static  JSONObject labels;
	static JSONArray subjectList = null;	
	static Typeface fontBengali; 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		 
		//fontBengali= Typeface.createFromAsset(this.getAssets(),
		//		"fonts/siyamrupali.ttf");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			fontBengali = getResources().getFont( R.font.siyamrupali);
		}
		try {
			loadSubjects();

			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setSubjectTitle();
		Button go = (Button)findViewById(R.id.goForQuizButton);
		go.setOnClickListener(goTosubjectPageListener);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void loadSubjects() throws Exception {
		try {

			InputStream subjects = this.getBaseContext().getResources()
					.openRawResource(R.raw.subjects);
			bReader = new BufferedReader(new InputStreamReader(subjects));
			StringBuilder subjectString = new StringBuilder();
			String aJsonLine = null;
			while ((aJsonLine = bReader.readLine()) != null) {
				subjectString.append(aJsonLine);
			}
			Log.d(this.getClass().toString(), subjectString.toString());
			JSONObject subjectObj = new JSONObject(subjectString.toString());
			subjectList = subjectObj.getJSONArray("Subjects");
			labels = subjectObj.getJSONObject("Labels");
			Log.d(this.getClass().getName(),
					"Num Subject " + subjectList.length());
		} 
		catch (Exception e){
			Log.d(this.getClass().getName(), e.getMessage(), e.getCause());
		} 
		finally {

			try {
				bReader.close();
			} catch (Exception e) {
				Log.e("", e.getMessage() , e.getCause());
			}

		}


	}

	private OnClickListener goTosubjectPageListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(HomePageActivity.this, SubjectSelectActivity.class);
			HomePageActivity.this.startActivity(intent);
			 
		}
	};
	public static JSONArray getSubjectList() {
		return subjectList;
	}

	public static JSONObject  getLabels() {
		// TODO Auto-generated method stub
		return labels;
	}

	public static Typeface getBengaliFont() {
		return fontBengali;
	}

	private void setSubjectTitle() {
		JSONObject labels =  getLabels();
		try {
			String titleLabel = labels.getString("Title");
			SpannableStringBuilder SS = new SpannableStringBuilder(titleLabel);
	        SS.setSpan (new CustomTypefaceSpan("",  getBengaliFont()), 0, 
	        		titleLabel.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);	      			
			// Update the action bar title with the TypefaceSpan instance
			ActionBar actionBar = getActionBar();
            if (actionBar  != null)
               actionBar.setTitle(SS);
		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e("JSON Parsing Error", e.getMessage() , e.getCause());
		}

	}
}

