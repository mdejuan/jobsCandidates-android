package com.signlanguage.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.signlanguage.R;
import com.signlanguage.jobs.JobsActivity;
import com.signlanguage.rest.RestApi;
import com.signlanguage.rest.ServerJsonResponse;

import java.io.IOException;

import retrofit2.Call;


public class LoginActivity extends AppCompatActivity {
    private UserLoginTask userBackend = null;
    private TextView tvRegister;
    private TextView tvRestore;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private CheckBox checkboxRemember;
    private View mProgressView;
    private View mLoginFormView;
    private TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        initViews();
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


    }


    private void initViews() {
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvRestore = (TextView) findViewById(R.id.tvRestore);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        checkboxRemember = (CheckBox) findViewById(R.id.checkboxRemember);
        textMessage = (TextView)findViewById(R.id.textViewMessage);
        textMessage.setVisibility(View.INVISIBLE);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoginButtonClicked();
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        tvRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RestorePasswordActivity.class));
            }
        });

    }



    private void loginSuccess() {
        // TODO: go to home screen
    }



    private void onLoginButtonClicked() {
        if (etEmail.getText().toString().equals("") || !etEmail.getText().toString().contains("@")) {
            etEmail.setError(getString(R.string.error_email));
            return;
        }
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError(getString(R.string.error_pass));
            return;
        }

        userBackend = new UserLoginTask(etEmail.getText().toString(), etPassword.getText().toString());
        showProgress(true);

        userBackend.execute((Void) null);
    }
    /**
     * Shows the progress UI and hides the login-register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private int mID;
        private String email;
        private String pass;


        UserLoginTask(String email, String pass) {
            this.email = email;
            this.pass = pass;

        }

        protected void saveAccout() {
            SharedPreferences sharedPreferences = getSharedPreferences("Account", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("userID", mID);
            editor.putString("username", this.email);
            editor.putString("password", this.pass);
            editor.commit();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            ServerJsonResponse resp = null;

            RestApi restApi = new RestApi();
            RestApi.ClientService service = restApi.getService();
            Call<ServerJsonResponse> call = service.logUser(this.email,this.pass);
            try {
                resp = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resp != null && resp.getMessage().equals("ok")) {
                mID = Integer.valueOf(resp.getId()).intValue();
                saveAccout();
                return true;
            } else {
                userBackend = null;
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userBackend = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(LoginActivity.this, JobsActivity.class);
                intent.putExtra("idUser", this.mID);
                startActivity(intent);
            } else {
                textMessage.setText(getString(R.string.login_fail));
                textMessage.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, R.string.login_fail,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            userBackend = null;
            showProgress(false);
        }


    }
}