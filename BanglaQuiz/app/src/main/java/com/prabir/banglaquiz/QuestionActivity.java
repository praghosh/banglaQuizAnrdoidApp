package com.prabir.banglaquiz;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

public class QuestionActivity extends Activity {
	/** Called when the activity is first created. */

	EditText question = null;
	RadioButton answer1 = null;
	RadioButton answer2 = null;
	RadioButton answer3 = null;
	RadioButton answer4 = null;
	RadioGroup answers = null;
	Button finish = null;
	int selectedAnswer = -1;
	int quesIndex = 0;
	int numEvents = 0;
	int selected[] = null;
	int correctAns[] = null;
	boolean review =false;
	Button prev, next = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		TableLayout quizLayout = (TableLayout) findViewById(R.id.quizLayout);
		quizLayout.setVisibility(View.INVISIBLE);
		Typeface fontBengali = HomePageActivity.getBengaliFont();
		
		
		try {
			question = (EditText) findViewById(R.id.quizquestion);
			question.setTypeface(fontBengali);
			answer1 = (RadioButton) findViewById(R.id.a0);
			answer1.setTypeface(fontBengali);
			answer2 = (RadioButton) findViewById(R.id.a1);
			answer2.setTypeface(fontBengali);
			answer3 = (RadioButton) findViewById(R.id.a2);
			answer3.setTypeface(fontBengali);
			answer4 = (RadioButton) findViewById(R.id.a3);
			answer4.setTypeface(fontBengali);
			answers = (RadioGroup) findViewById(R.id.answers);
			RadioGroup questionLayout = (RadioGroup)findViewById(R.id.answers);
			Button finish = (Button)findViewById(R.id.finish);
			finish.setOnClickListener(finishListener);
			
			prev = (Button)findViewById(R.id.Prev);
			prev.setOnClickListener(prevListener);
			next = (Button)findViewById(R.id.Next);
			next.setOnClickListener(nextListener);

			
			selected = new int[SubjectSelectActivity.getQuesList().length()];
			Arrays.fill(selected, -1);
			correctAns = new int[SubjectSelectActivity.getQuesList().length()];
			Arrays.fill(correctAns, -1);


			this.showQuestion(0,review);

			quizLayout.setVisibility(View.VISIBLE);
			
		} catch (Exception e) {
			Log.e("", e.getMessage().toString(), e.getCause());
		} 

	}
	
	
	private void showQuestion(int qIndex,boolean review) {
		try {
			JSONObject aQues = SubjectSelectActivity.getQuesList().getJSONObject(qIndex);
			int answerColor = getResources().getColor(R.color.answer_color); 
			String quesValue = aQues.getString("Question");
			if (correctAns[qIndex] == -1) {
				String correctAnsStr = aQues.getString("CorrectAnswer");
				correctAns[qIndex] = Integer.parseInt(correctAnsStr);
			}
			question.setTextColor(getResources().getColor(R.color.question_color)); 
			question.setText(quesValue.toCharArray(), 0, quesValue.length());
			answers.check(-1);
			answer1.setTextColor(answerColor); 
			answer2.setTextColor(answerColor); 
			answer3.setTextColor(answerColor); 
			answer4.setTextColor(answerColor);
			JSONArray ansList = aQues.getJSONArray("Answers");
			String aAns = ansList.getJSONObject(0).getString("Answer");
			answer1.setText(aAns.toCharArray(), 0, aAns.length());
			aAns = ansList.getJSONObject(1).getString("Answer");
			answer2.setText(aAns.toCharArray(), 0, aAns.length());
			aAns = ansList.getJSONObject(2).getString("Answer");
			answer3.setText(aAns.toCharArray(), 0, aAns.length());
			aAns = ansList.getJSONObject(3).getString("Answer");
			answer4.setText(aAns.toCharArray(), 0, aAns.length());
			Log.d("",selected[qIndex]+"");
			if (selected[qIndex] == 0)
				answers.check(R.id.a0);
			if (selected[qIndex] == 1)
				answers.check(R.id.a1);
			if (selected[qIndex] == 2)
				answers.check(R.id.a2);
			if (selected[qIndex] == 3)
				answers.check(R.id.a3);
			
			setScoreTitle();
			if (quesIndex == (SubjectSelectActivity.getQuesList().length()-1)) 
			{
				 next.setEnabled(false);
				 next.setTextColor( Color.parseColor("#C2C6A4"));
			}
			
			if (quesIndex == 0)
			{
				prev.setEnabled(false);
				prev.setTextColor( Color.parseColor("#C2C6A4"));
			}
			
			if (quesIndex > 0)
			{
				prev.setEnabled(true);
				prev.setTextColor( Color.parseColor("#FFFF33"));
			}
			
			if (quesIndex < (SubjectSelectActivity.getQuesList().length()-1))
			{
				next.setEnabled(true);
				next.setTextColor( Color.parseColor("#FFFF33"));
			}

			
			if (review) {
				Log.d("review",selected[qIndex]+""+correctAns[qIndex]);;	
				if (selected[qIndex] != correctAns[qIndex]) {
					if (selected[qIndex] == 0)
						answer1.setTextColor(Color.RED);
					if (selected[qIndex] == 1)
						answer2.setTextColor(Color.RED);
					if (selected[qIndex] == 2)
						answer3.setTextColor(Color.RED);
					if (selected[qIndex] == 3)
						answer4.setTextColor(Color.RED);
				}
				if (correctAns[qIndex] == 0)
					answer1.setTextColor(Color.parseColor("#00B422"));
				if (correctAns[qIndex] == 1)
					answer2.setTextColor(Color.parseColor("#00B422"));
				if (correctAns[qIndex] == 2)
					answer3.setTextColor(Color.parseColor("#00B422"));
				if (correctAns[qIndex] == 3)
					answer4.setTextColor(Color.parseColor("#00B422"));
				
			}
		} catch (Exception e) {
			Log.e(this.getClass().toString(), e.getMessage(), e.getCause());
		}
	}
	

	private OnClickListener finishListener = new OnClickListener() {
		public void onClick(View v) {
			setAnswer();
			//Calculate Score
			int score = 0;
			for(int i=0; i<correctAns.length; i++){
				if ((correctAns[i] != -1) && (correctAns[i] == selected[i]))
					score++;
			}
			AlertDialog alertDialog;
			alertDialog = new Builder(QuestionActivity.this).create();
			alertDialog.setTitle("Score");
			alertDialog.setMessage((score) +" out of " + (SubjectSelectActivity.getQuesList().length()));
			
			alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Retake", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					review = false;
					quesIndex=0;
					QuestionActivity.this.showQuestion(0, review);
				}
			});

			alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Review", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					review = true;
					quesIndex=0;
					QuestionActivity.this.showQuestion(0, review);
				}
			});

			alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Quit", new DialogInterface.OnClickListener(){

				public void onClick(DialogInterface dialog, int which) {
					review = false;
					finish();
				}
			});

			alertDialog.show();
			
		}
	};

	private void setAnswer() {
		if (answer1.isChecked())
			selected[quesIndex] = 0;
		if (answer2.isChecked())
			selected[quesIndex] = 1;
		if (answer3.isChecked())
			selected[quesIndex] = 2;
		if (answer4.isChecked())
			selected[quesIndex] = 3;
		
		Log.d("",Arrays.toString(selected));
		Log.d("",Arrays.toString(correctAns));
		
	}
	
	private OnClickListener nextListener = new OnClickListener() {
		public void onClick(View v) {
			setAnswer();
			quesIndex++;
			if (quesIndex >= SubjectSelectActivity.getQuesList().length())
				quesIndex = SubjectSelectActivity.getQuesList().length() - 1;
			
			showQuestion(quesIndex,review);
		}
	};

	private OnClickListener prevListener = new OnClickListener() {
		public void onClick(View v) {
			setAnswer();
			quesIndex--;
			if (quesIndex < 0)
				quesIndex = 0;

			showQuestion(quesIndex,review);
		}
	};
	
	private void setScoreTitle() {
		this.setTitle("SciQuiz3     " + (quesIndex+1)+ "/" + SubjectSelectActivity.getQuesList().length());
	}

	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // do something useful
	            return(true);
	    }

	    return(super.onOptionsItemSelected(item));
	}

}