package com.signlanguage.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.signlanguage.R;
import com.signlanguage.rest.RestApi;
import com.signlanguage.rest.ServerJsonResponse;

import java.io.IOException;

import retrofit2.Call;

public class RestorePasswordActivity extends AppCompatActivity {
    private UserRestoreTask userBackend = null;
    private EditText etEmail;
    private Button btnRestore;
    private View mProgressView;
    private View mRestoreFormView;

    private TextView textMessage;
    ServerJsonResponse resp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        mRestoreFormView = findViewById(R.id.restore_form);
        mProgressView = findViewById(R.id.restore_progress);

        initUI();
    }

    private void initUI() {

        etEmail = (EditText) findViewById(R.id.etEmail);
        textMessage = (TextView)findViewById(R.id.textViewMessage);
        textMessage.setVisibility(View.INVISIBLE);
        btnRestore = (Button) findViewById(R.id.btnRestore);
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRestorePasswordButtonClicked();
            }
        });
    }

    private void onRestorePasswordButtonClicked() {
        if (etEmail.getText().toString().equals("")) {
            etEmail.setError(getString(R.string.error_email));
            return;
        }

        userBackend = new UserRestoreTask(etEmail.getText().toString());
        showProgress(true);
        userBackend.execute((Void) null);



    }
    /**
     * Shows the progress UI and hides the restore form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRestoreFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRestoreFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRestoreFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRestoreFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }





    public class UserRestoreTask extends AsyncTask<Void, Void, Boolean> {


        private String email;
        private String message;

        UserRestoreTask(String email) {
            this.email = email;

        }


        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO: attempt restore against a network service.
            boolean isrestore = false;

            RestApi restApi = new RestApi();
            RestApi.ClientService service = restApi.getService();

            Call<ServerJsonResponse> call = service.restorePass(this.email);
            try {
                resp = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resp != null && resp.getMessage().equals("ok")) {
                return true;
            } else {
                userBackend = null;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userBackend = null;
            textMessage.setVisibility(View.VISIBLE);
            if (success) {
                showProgress(false);
                textMessage.setText(getString(R.string.restore_email_sent));
                Toast.makeText(RestorePasswordActivity.this, R.string.restore_email_sent,
                        Toast.LENGTH_LONG).show();
            } else {
                showProgress(false);
                textMessage.setText(resp.getId());
                Toast.makeText(RestorePasswordActivity.this, R.string.restore_email_fail,
                        Toast.LENGTH_SHORT).show();
            }
            //finish();
        }

        @Override
        protected void onCancelled() {
            userBackend = null;

            cancel(true);
        }





    }
}