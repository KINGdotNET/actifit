package io.actifit.fitnesstracker.actifitfitnesstracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class StepHistoryActivity extends BaseActivity {
    private ListView mStepsListView;
    private StepsDBHelper mStepsDBHelper;
    private ArrayList<DateStepsModel> mStepCountList;
    private ArrayList<DateStepsModel> mStepFinalList;
    private ActivityEntryAdapter listingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        mStepsListView = findViewById(R.id.steps_list);
        //mStepFinalList = new ArrayList<String>();
        mStepFinalList = new ArrayList<>();

        StepHistoryAsyncTask stepHistoryAsyncTask = new StepHistoryAsyncTask();
        stepHistoryAsyncTask.execute();

        //hook chart activity button
        Button BtnViewChart = findViewById(R.id.chart_view);

        BtnViewChart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(StepHistoryActivity.this, HistoryChartActivity.class);
                startActivity(intent);

            }
        });

    }

    /**
     * function handles preparing the proper data to the mStepCountList ArrayList
     */
    public void getDataForList() {
        mStepsDBHelper = new StepsDBHelper(this);
        mStepCountList = mStepsDBHelper.readStepsEntries();
    }

    private class StepHistoryAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //grab the data to be displayed in the list
            getDataForList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //initializing date conversion components
            String dateDisplay;
            //existing date format
            SimpleDateFormat dateFormIn = new SimpleDateFormat("yyyyMMdd");
            //output format
            SimpleDateFormat dateFormOut = new SimpleDateFormat("MM/dd/yyyy");

            //loop through the data to prepare it for proper display
            for (int position=0;position<mStepCountList.size();position++){
                try {
                    //grab date entry according to stored format
                    Date feedingDate = dateFormIn.parse((mStepCountList.get(position)).mDate);
                    //convert it to new format for display
                    dateDisplay = dateFormOut.format(feedingDate);

                    //initiate a new entry
                    DateStepsModel newEntry = new DateStepsModel(dateDisplay, mStepCountList.get(position).mStepCount, mStepCountList.get(position).mtrackingDevice);

                    mStepFinalList.add(newEntry);

                }catch(ParseException txtEx){
                    Log.d(MainActivity.TAG,txtEx.toString());
                    txtEx.printStackTrace();
                }
            }
            //reverse the list for descending display
            Collections.reverse(mStepFinalList);

            // Create the adapter to convert the array to views
            listingAdapter = new ActivityEntryAdapter(getApplicationContext(), mStepFinalList);


            mStepsListView.setAdapter(listingAdapter);

        }
    }
}
