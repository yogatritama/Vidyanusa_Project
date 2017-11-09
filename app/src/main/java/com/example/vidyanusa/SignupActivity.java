package com.example.vidyanusa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextView loginLink;
    private ImageView imageView;

    private EditText email;
    private EditText username;
    private EditText nama_lengkap;
    private Spinner jenkel;
    private EditText password;
    private EditText cfmpassword;
    private EditText sekolah;
    private Button registrasi;
    private String sEmail, sUsername, sNamaLengkap, sJenkel, sPassword, sConfirm, sSekolah;
    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        imageView = (ImageView) findViewById(R.id.image_user);
        loginLink = (TextView)findViewById(R.id.link_login);
        email = (EditText)findViewById(R.id.etemail);
        username = (EditText)findViewById(R.id.etusername);
        nama_lengkap = (EditText) findViewById(R.id.etnamalengkap);
        //List jenis kelamin
        jenkel = (Spinner) findViewById(R.id.spjenkel);
        List<String> item = new ArrayList<String>();
        item.add("Laki-laki");
        item.add("Perempuan");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,android.R.layout.simple_spinner_dropdown_item,
                item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jenkel.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected,
                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                        this)
        );

        password = (EditText)findViewById(R.id.etpassword);
        cfmpassword = (EditText)findViewById(R.id.etcfmpassword);
        sekolah = (EditText)findViewById(R.id.sekolah);
        registrasi = (Button)findViewById(R.id.btnregitrasi);
        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.logoprofilewhite);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        registrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrasi();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = null;
                //verificacion de la version de plataforma
                if(Build.VERSION.SDK_INT < 19){
                    //android 4.3  y anteriores
                    i = new Intent();
                    i.setAction(Intent.ACTION_GET_CONTENT);
                }else {
                    //android 4.4 y superior
                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                }
                i.setType("image/*");
                startActivityForResult(i, request_code);
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void registrasi() {
        if (!validasi()) return;
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();
        sEmail = email.getText().toString();
        sUsername = username.getText().toString();
        sNamaLengkap = nama_lengkap.getText().toString();
        sJenkel = jenkel.getSelectedView().toString();
        sPassword = password.getText().toString();
        sConfirm = cfmpassword.getText().toString();
        sSekolah = sekolah.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_REGISTER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  progressDialos.dismiss();
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Silahkan login", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                                finish();
                            }
                            else{
                                JSONObject objData = obj.getJSONObject("data");
                                pDialog.hide();
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
                        pDialog.hide();
                        Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("nama_lengkap",sNamaLengkap);
                params.put("email",sEmail);
                params.put("sandi",sPassword);
                params.put("username",sUsername);
                params.put("jenis_kelamin",sJenkel);
                params.put("sekolah",sSekolah);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean validasi() {
        boolean valid = true;

        String sEmail = email.getText().toString();
        String sUsername = username.getText().toString();
        String sNamaLengkap = nama_lengkap.getText().toString();

        TextView errorText = (TextView)jenkel.getSelectedView();

        String sPassword = password.getText().toString();
        String sConfirm = cfmpassword.getText().toString();
        String sSekolah = sekolah.getText().toString();

        if (sEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Format e-mail salah");
            valid = false;
        } else {
            email.setError(null);
        }

        if (sUsername.isEmpty() || sUsername.length() < 3) {
            username.setError("Inputkan minimal 3 karakter");
            valid = false;
        } else {
            username.setError(null);
        }

        if (sNamaLengkap.isEmpty() || sNamaLengkap.length() < 3) {
            nama_lengkap.setError("Inputkan minimal 3 karakter");
            valid = false;
        } else {
            nama_lengkap.setError(null);
        }

        //String nama = null;
        if (jenkel != null && jenkel.getSelectedItem()!=null){
            sJenkel = (String)jenkel.getSelectedItem();
        } else {
            errorText.setText("Pilih Salah Satu");
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            valid=false;
        }

        if (sPassword.isEmpty() || password.length() < 4 || password.length() > 10) {
            password.setError("Inputkan 4 - 10 karakter");
            valid = false;
        } else {
            password.setError(null);
        }

        if(password.length() > 0 && cfmpassword.length()>0) {
            if (sPassword.equals(sConfirm)) {
                cfmpassword.setError(null);
            } else {
                cfmpassword.setError("Tidak sesuai dengan password");
                valid = false;
            }
        }else {
            cfmpassword.setError("Konfirmasi sandi tidak boleh kosong");
            valid = false;
        }

        if (sSekolah.isEmpty() || sSekolah.length() < 5) {
            sekolah.setError("Inputkan minimal 5 karakter");
            valid = false;
        } else {
            sekolah.setError(null);
        }

        return valid;
    }


    private byte[] imageToByte(ImageView image) {
        Bitmap bitmapFoto = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == request_code){
            imageView.setImageURI(data.getData());
            bytes = imageToByte(imageView);

            // para que se vea la imagen en circulo
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCircular(true);
            imageView.setImageDrawable(roundedBitmapDrawable);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
