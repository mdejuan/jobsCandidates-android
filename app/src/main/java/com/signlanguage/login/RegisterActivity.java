package com.signlanguage.login;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.signlanguage.R;
import com.signlanguage.rest.RestApi;
import com.signlanguage.rest.ServerJsonResponse;
import java.io.IOException;
import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {
    private UserRegisterTask userBackend = null;
    private EditText etEmail;
    private EditText etName;
    private EditText etPassword;
    private EditText etPasswordRepeat;
    private EditText etTelephone;
    private EditText etAddress;
    private Spinner city_spinner;
    private Button btnRegister;
    private View mProgressView;
    private View mRegisterFormView;
    private TextView textMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUI();
        Spinner spinner = (Spinner) findViewById(R.id.city_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.city_array, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);

    }

    private void initUI() {
        etEmail = (EditText) findViewById(R.id.etEmail);
        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPasswordRepeat = (EditText) findViewById(R.id.etPasswordRepeat);
        etTelephone = (EditText) findViewById(R.id.etTelephone);
        city_spinner = (Spinner)findViewById(R.id.city_spinner);
        etAddress = (EditText) findViewById(R.id.etAddress);
        textMessage = (TextView)findViewById(R.id.textViewMessage);
        textMessage.setVisibility(View.INVISIBLE);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegisterButtonClicked();
            }
        });
    }

    private void onRegisterButtonClicked() {
        if (etEmail.getText().toString().equals("") || !etEmail.getText().toString().contains("@")) {
            etEmail.setError(getString(R.string.error_email));
            return;
        }
        if (etPassword.getText().toString().equals("")) {
            etPassword.setError(getString(R.string.error_pass));
            return;
        }
        if (etPassword.getText().toString().length() < 5 ) {
            etPasswordRepeat.setError(getString(R.string.error_pass_short));
            return;
        }
        if (!etPassword.getText().toString().equals(etPasswordRepeat.getText().toString())) {
            etPasswordRepeat.setError(getString(R.string.error_pass_repeat));
            return;
        }
        if (etTelephone.getText().toString().equals("")) {
            etTelephone.setError(getString(R.string.error_tlf));
            return;
        }
        if (city_spinner.getSelectedItem().toString().equals("County")) {
            ((TextView)city_spinner.getSelectedView()).setError(getString(R.string.error_county));
            return;
        }

        userBackend = new UserRegisterTask(etEmail.getText().toString(), etName.getText().toString(), etPassword.getText().toString(),
                etTelephone.getText().toString(), etAddress.getText().toString(), city_spinner.getSelectedItem().toString());
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private String email;
        private String name;
        private String pass;
        private String tlf;
        private String address;
        private String county;


        UserRegisterTask(String email, String name, String pass, String tlf, String address, String county) {
            this.email = email;
            this.name = name;
            this.pass = pass;
            this.tlf = tlf;
            this.address = address;
            this.county = county;
        }


        @Override
        protected Boolean doInBackground(Void... params) {

            ServerJsonResponse resp = null;

            RestApi restApi = new RestApi();
            RestApi.ClientService service = restApi.getService();
            Call<ServerJsonResponse> call = service.registerUser(this.email,this.name,this.pass,this.tlf,this.address,this.county);
            try {
                resp = call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (resp != null && resp.getMessage().equals("ok")) {
                return true;
            } else {
                userBackend = null;
                etEmail.setText(resp.getMessage());
                return false;
            }

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            userBackend = null;
            showProgress(false);

            textMessage.setVisibility(View.VISIBLE);
            if (success) {
                Toast.makeText(RegisterActivity.this, R.string.register_success,
                        Toast.LENGTH_LONG).show();
                textMessage.setText(getString(R.string.register_success));
            } else {
                textMessage.setText(getString(R.string.register_fail));
                Toast.makeText(RegisterActivity.this, R.string.register_fail,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            userBackend = null;
            cancel(true);
            showProgress(false);
        }


    }
}