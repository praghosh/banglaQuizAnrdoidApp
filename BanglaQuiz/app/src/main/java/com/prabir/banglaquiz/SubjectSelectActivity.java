package com.prabir.banglaquiz;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import android.support.v4.app.NavUtils;

/**
 * 
 */
public class SubjectSelectActivity extends Activity {
	Button buttonSamas;
	ProgressDialog pd;
	TableLayout quizLayout;
	LinearLayout subjectSelectlinearLayout;
	JSONArray  subjectLists;
	BufferedReader bReader;
	static JSONArray quesList;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subject_select);
		quizLayout = (TableLayout) findViewById(R.id.subjectLayout);
		setSubjectTitle();
		//quizLayout.setVisibility(android.view.View.INVISIBLE);
		loadSubjectUi ();
		//	quizLayout.setVisibility(android.view.View.VISIBLE);		

	}


	private void setSubjectTitle() {
		JSONObject labels = HomePageActivity.getLabels();
		try {
			String titleLabel = labels.getString("Title");
			SpannableStringBuilder SS = new SpannableStringBuilder(titleLabel);
	        SS.setSpan (new CustomTypefaceSpan("", HomePageActivity.getBengaliFont()), 0, 
	        		titleLabel.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);	      			
			// Update the action bar title with the TypefaceSpan instance
		 	ActionBar actionBar = getActionBar();
			 if (actionBar!=null) {
				 actionBar.setTitle(SS);
			 }

		}
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void loadSubjectUi () 
	{

		
		Typeface fontBengali = HomePageActivity.getBengaliFont();
		subjectLists = HomePageActivity.getSubjectList();


		try {
			JSONObject labels = HomePageActivity.getLabels();
			String selectSubject = labels.getString("SelectSubject");
			EditText selectSubjectText = (EditText) findViewById(R.id.selectSubject);
			selectSubjectText.setText(selectSubject.toCharArray(), 0, selectSubject.length());
			selectSubjectText.setTypeface(fontBengali);
			int numberOfSubjects = HomePageActivity.getSubjectList().length();

			for (int k = 0; k < numberOfSubjects; k++) {
				
				Button btn = new Button(SubjectSelectActivity.this);						 
				btn.setTextColor(Color.parseColor("#ffff11"));
				JSONObject subject = subjectLists.getJSONObject(k);
				String btnText =  subject.getString("DisplayName");
				btn.setTypeface(fontBengali);
				btn.setText(btnText.toCharArray(), 0, btnText.length());
				btn.setId(k);
				subjectSelectlinearLayout  = (LinearLayout)findViewById(R.id.subjectSelectlinearLayout);
				//	LinearLayout.LayoutParams params =subjectSelectlinearLayout.

				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			//	params.weight = 1.0f;
			//	params.topMargin=15;
				params.setMargins(10, 1,10, 1);
				
				params.gravity = Gravity.CENTER_VERTICAL;		
				btn.setLayoutParams(params);
				btn.setBackground(SubjectSelectActivity.this.getResources().getDrawable(R.drawable.buttonselector));
				btn.setPadding(1, 1, 1, 1);
				btn.setOnClickListener(openSubjectQuizListener);
				subjectSelectlinearLayout.addView(btn);

			}

		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private OnClickListener openSubjectQuizListener = new OnClickListener() {


		public void onClick(View v) {
			System.out.print(v) ;
			int id = v.getId();
			loadSubjectQuiz(id);
			Intent intent = new Intent(SubjectSelectActivity.this, QuestionActivity.class);
			SubjectSelectActivity.this.startActivity(intent);

		}

		private void loadSubjectQuiz(int id) {
			bReader =null;
			try {
				JSONObject subject = subjectLists.getJSONObject(id);
				String subjectName = subject.getString("SubjectName");
				int resId = getResources().getIdentifier("raw/" + subjectName, null, SubjectSelectActivity.this.getPackageName());
				InputStream subjects = SubjectSelectActivity.this.getBaseContext().getResources()
						.openRawResource(resId);
				bReader = new BufferedReader(new InputStreamReader(subjects));
				StringBuilder quesString = new StringBuilder();
				String aJsonLine = null;

				while ((aJsonLine = bReader.readLine()) != null) {
					quesString.append(aJsonLine);
				}
				Log.d(this.getClass().toString(), quesString.toString());
				JSONObject quesObj = new JSONObject(quesString.toString());
				quesList = quesObj.getJSONArray("Questions");
				Log.d(this.getClass().getName(),
						"Num Questions " + quesList.length());
			}
			catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			finally {
				try {
					if (bReader !=null) bReader.close();
				} catch (Exception e) {
					Log.e("", e.getMessage().toString(), e.getCause());
				}

			}

		}

	};


	public static JSONArray getQuesList() {
		return quesList;
	}

}
