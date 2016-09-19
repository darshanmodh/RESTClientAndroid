package rest.darshanmodh.com.restfulwebservice;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Darshan on 019 9/19/2016.
 */
public class RegisterActivity extends Activity {
    ProgressDialog prgDialog;
    TextView errorMsg;
    EditText nameET;
    EditText emailET;
    EditText pwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        errorMsg = (TextView)findViewById(R.id.register_error);
        nameET = (EditText)findViewById(R.id.registerName);
        emailET = (EditText)findViewById(R.id.registerEmail);
        pwdET = (EditText)findViewById(R.id.registerPassword);
        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(false);
    }

    public void registerUser(View view) {
        String name = nameET.getText().toString();
        String email = emailET.getText().toString();
        String password = pwdET.getText().toString();
        RequestParams params = new RequestParams();
        if(Utility.isNotNull(name) && Utility.isNotNull(email) && Utility.isNotNull(password)) {
            if(Utility.validate(email)) {
                params.put("name", name);
                params.put("username", email);
                params.put("password", password);
                invokeWebService(params);
            } else {    // email validation
                Toast.makeText(getApplicationContext(), "Please enter valid email ID.", Toast.LENGTH_LONG).show();
            }
        } else {    // null values
            Toast.makeText(getApplicationContext(), "Please fill the form completely.", Toast.LENGTH_LONG).show();
        }
    }

    private void invokeWebService(RequestParams params) {
        prgDialog.show();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://10.0.0.3:8080/Android_Web_Service/register/doregister", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                prgDialog.hide();
                try {
                    String str = new String(responseBody, "UTF-8");
                    JSONObject obj = new JSONObject(str);
                    if(obj.getBoolean("status")) {
                        setDefaultValues();
                        Toast.makeText(getApplicationContext(), "You are successfully registered.", Toast.LENGTH_LONG).show();
                    } else {    //error
                        errorMsg.setText(obj.getString("error_msg"));
                        Toast.makeText(getApplicationContext(), obj.getString("error_msg"), Toast.LENGTH_LONG).show();
                    }
                } catch(UnsupportedEncodingException encodingE) {
                    encodingE.printStackTrace();
                } catch(JSONException jsonE) {
                    Toast.makeText(getApplicationContext(), "Server's internal error!", Toast.LENGTH_LONG).show();
                    jsonE.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                prgDialog.hide();
                if(statusCode == 404)
                    Toast.makeText(getApplicationContext(), "Requested resource not found!", Toast.LENGTH_LONG).show();
                else if(statusCode == 500)
                    Toast.makeText(getApplicationContext(), "Server's internal error!", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getApplicationContext(), "Unexpected error!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void navigateToLoginActivity(View view) {
        Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    public void setDefaultValues() {
        nameET.setText("");
        emailET.setText("");
        pwdET.setText("");
    }
}
