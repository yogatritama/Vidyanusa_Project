package com.example.vidyanusa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText eEmail, ePassword;
    private Button login;
    private TextView registrasi;
    private String email;
    private String password;
    private Cursor check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        eEmail = (EditText)findViewById(R.id.etemail);
        ePassword = (EditText)findViewById(R.id.etpass);
        login = (Button)findViewById(R.id.btlogin);
        registrasi= (TextView)findViewById(R.id.signup);

        registrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    /*private void start() {

        if (!validasi()) return;

        email = eEmail.getText().toString();
        password = ePassword.getText().toString();
        Intent intent =new Intent(getApplicationContext(),PresensiActivity.class);
        intent.putExtra("IDENT",email);
        startActivity(intent);
        finish();


//        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.MaterialBaseTheme);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Iniciando...");
//        progressDialog.show();


        eEmail.getText().clear();
        ePassword.getText().clear();

    }
*/
    private boolean validasi() {
        boolean valid = true;

        String email = eEmail.getText().toString();
        String password = ePassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            eEmail.setError("\n" +"Enter a valid email address");
            valid = false;
        } else {
            eEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            ePassword.setError("Enter 4 - 10 characters");
            valid = false;
        } else {
            ePassword.setError(null);
        }

        return valid;
    }

    private void userLogin(){
        if (!validasi()) return;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        final String email = eEmail.getText().toString().trim();
        final String password = ePassword.getText().toString().trim();
        //progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //              progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject objData = new JSONObject(obj.getString("data"));
                            if(obj.getBoolean("success")){
                                if(objData.getString("peran") == "3") {
                                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                                    SharedPreferences.Editor editor = pref.edit();
                                    editor.putString("access_token", objData.getString("access_token"));
                                    editor.putString("id_pengguna", objData.getString("id_pengguna"));
                                    editor.putString("username", objData.getString("username"));
                                    editor.putString("peran", objData.getString("peran"));
                                    editor.commit();
                                    pDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    pDialog.hide();
                                    Toast.makeText(getApplicationContext(), "Silahkan gunakan aplikasi untuk versi guru", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),
                                        objData.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();

                        }

                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                error.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("sandi",password);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }


}