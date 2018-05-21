package com.signlanguage.jobs;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.signlanguage.R;
import com.signlanguage.rest.JobJson;
import com.signlanguage.rest.RestJobsApi;
import com.signlanguage.rest.ServerJsonResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;


public class JobsActivity extends AppCompatActivity {
    private UserJobsTask userJobsTask;

    ServerJsonResponse resp = null;
    List<JobJson> jobs = new ArrayList<JobJson>();
    int idUser;
    ExpandableListView expListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobs);
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        Bundle bundle = getIntent().getExtras();
        idUser = bundle.getInt("idUser");
        userJobsTask = new UserJobsTask(idUser, this);
        //showProgress(true);

        userJobsTask.execute((Void) null);

    }


    public class UserJobsTask extends AsyncTask<Void, Void, Boolean> {


        private int id;
        Context context;

        UserJobsTask(int id, Context context) {
            this.id = id;
            this.context = context;
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            RestJobsApi restApi = new RestJobsApi();
            RestJobsApi.ClientService service = restApi.getService();
            Call<ServerJsonResponse> call = service.getCandidateJobs(new Integer(this.id).toString());
            try {
                resp = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resp != null && resp.getMessage().equals("ok")) {

                return true;
            } else {
                userJobsTask = null;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            ExpandableListAdapter listAdapter;

            List<String> listDataHeader;
            HashMap<String, List<String>> listDataChild;
            userJobsTask = null;

            if (success) {
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<String, List<String>>();

                for(JobJson job : resp.getJobs())
                {
                    String applied = "";

                    if(job.isApplied()) {
                        listDataHeader.add(job.getTitle() + " - Applied");
                        applied = "true";
                    }
                    else {
                        listDataHeader.add(job.getTitle());
                        applied= "false";
                    }
                    // Adding child data
                    List<String> body = new ArrayList<String>();
                    //body.add(job.getShortDescription());
                    body.add(job.getContent());
                    body.add(new Integer(idUser).toString() +":"+ job.getId()+":"+ applied);
                    if(job.isApplied())
                        listDataChild.put(job.getTitle()+" - Applied", body);
                    else
                        listDataChild.put(job.getTitle(), body);


                }
                listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

                // setting list adapter

                expListView.setAdapter(listAdapter);
            } else {

                Toast.makeText(JobsActivity.this, R.string.jobs_fail,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            userJobsTask = null;
            //showProgress(false);
        }


    }
}

