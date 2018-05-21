package com.signlanguage.jobs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.signlanguage.R;
import com.signlanguage.rest.JobJson;
import com.signlanguage.rest.RestJobsApi;
import com.signlanguage.rest.ServerJsonResponse;

import retrofit2.Call;

/**
 * Created by marcos on 19/03/18.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> _listDataChild;
    ServerJsonResponse resp = null;
    private ApplyJobsTask applyJobsTask;
    private LayoutInflater mLayoutInflater;
    private int lastClick = -1;
    public ExpandableListAdapter(Context context, List<String> listDataHeader,
                                 HashMap<String, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        if (context != null) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.list_item_body, null);
        }


        if(childPosition == 1)
        {

            List<String> tokens = new ArrayList<>();
            StringTokenizer tokenizer = new StringTokenizer(childText, ":");
            while (tokenizer.hasMoreElements()) {
                tokens.add(tokenizer.nextToken());
            }
            final String idC = tokens.get(0);
            final String idJ = tokens.get(1);
            final Button btnApply =  convertView.findViewById(R.id.btnApply);

            if(tokens.get(2).equals("true"))
            {
                btnApply.setVisibility(View.INVISIBLE);
            }
            else
            {
                btnApply.setText("APPLY");
                btnApply.setVisibility(View.VISIBLE);
                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        onApplyButtonClicked(idC, idJ);
                        btnApply.setText("APPLIED");
                        btnApply.setEnabled(false);
                    }
                });
            }

        }
        else {
            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            txtListChild.setText(childText);

       }

        return convertView;
    }
    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {

            convertView = mLayoutInflater.inflate(R.layout.list_group_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
    public void onApplyButtonClicked(String idUser, String idJob) {
        applyJobsTask = new ApplyJobsTask(idUser, idJob);
        //showProgress(true);

        applyJobsTask.execute((Void) null);
    }

    public class ApplyJobsTask extends AsyncTask<Void, Void, Boolean> {


        private String idUser;
        private String idJob;

        ApplyJobsTask(String idUser, String idJob) {
            this.idUser = idUser;
            this.idJob = idJob;
        }


        @Override
        protected Boolean doInBackground(Void... params) {


            RestJobsApi restApi = new RestJobsApi();
            RestJobsApi.ClientService service = restApi.getService();
            Call<ServerJsonResponse> call = service.applyJob(this.idUser, this.idJob);
            try {
                resp = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resp != null && resp.getMessage().equals("ok")) {

                return true;
            } else {
                applyJobsTask = null;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            applyJobsTask = null;

            if (success) {

            } else {

            }
        }

        @Override
        protected void onCancelled() {
            applyJobsTask = null;
            //showProgress(false);
        }


    }

}
