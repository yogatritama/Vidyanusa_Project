package com.example.vidyanusa;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.example.vidyanusa.service.GPSTracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PortalActivity extends AppCompatActivity {

    private EditText judulportal;
    private Spinner spkategori;
    private ImageView imageView;
    private String sJudul, sKategori;
    private Button kirim;

    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;

    private String username;
    private String access_token;
    private String pengguna;

    private List<String> listKegiatan = new ArrayList<>();
    private List<String> item = new ArrayList<>();
    private String idKategori;
    private String photoPath;

    private final int GALLERY = 1, CAMERA = 2;

    private GPSTracker gps;

    private Double latitude;
    private Double longitude;

    private ProgressDialog pDialog;

    private Bitmap bitmap;

    private String timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sample_actionbar);
        setContentView(R.layout.portal);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        username = pref.getString("username", null);
        access_token = pref.getString("access_token", null);
        pengguna = pref.getString("id_pengguna", null);

        gps = new GPSTracker(PortalActivity.this);

//        // Handle Toolbar
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

      //  upload = (EditText) findViewById(R.id.eduploadportal);
        judulportal = (EditText)findViewById(R.id.etjudulportal);
        imageView = (ImageView) findViewById(R.id.uploadfoto_portal);

        spkategori = (Spinner) findViewById(R.id.spsikap);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_DAFTAR_KATEGORI_KEGIATAN,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //  progressDialos.dismiss();
                    try{
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("success")) {
                            JSONArray arrData = new JSONArray(obj.getString("data"));
                            Integer num;
                            JSONObject objData;
                            for(num = 0; num < arrData.length(); num++) {
                                objData = arrData.getJSONObject(num);
                                listKegiatan.add(objData.getString("_id"));
                                item.add(objData.getString("nama_kategori"));
                            }

                            //untuk membuat adapter list kota
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(PortalActivity.this,android.R.layout.simple_spinner_dropdown_item,
                                    item);

                            //untuk menentukan model adapter nya
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spkategori.setAdapter(
                                    new NothingSelectedKategori(
                                            adapter,
                                            R.layout.contact_spinner_row_nothing_selected2,
                                            // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                                            getApplicationContext())
                            );
                            pDialog.hide();
                        }
                        else{
                            JSONObject objData = new JSONObject(obj.getString("data"));
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
                    // progressDialog.hide();
                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("access_token",access_token);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.logoprofile);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        kirim = (Button) findViewById(R.id.buttonkirim);

        kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambah_post();
//                up();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });
                /*Intent i = null;
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
                startActivityForResult(i, request_code);*/


    }

    public void tambah_post() {
        if (!validasi()) return;

        if (spkategori.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Silahkan pilih kegiatan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (gps.canGetLocation()) {
            kirim.setText("Memproses");
            kirim.setEnabled(false);

            pDialog.show();

            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            photoPath = "http://filehosting.vidyanusa.id/mobile/portal/" + timeStamp + ".png";

            idKategori = listKegiatan.get(spkategori.getSelectedItemPosition() - 1);

            sJudul = judulportal.getText().toString();
            sKategori = spkategori.getSelectedView().toString();

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TAMBAH_KEGIATAN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //  progressDialos.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                JSONObject objData = new JSONObject(obj.getString("data"));
                                Toast.makeText(getApplicationContext(), objData.getString("message"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            pDialog.hide();
                            kirim.setText("Kirim");
                            kirim.setEnabled(true);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // progressDialog.hide();
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("access_token", access_token);
                    params.put("judul", sJudul);
                    params.put("kategori", idKategori);
                    params.put("file_berkas", photoPath);
                    params.put("latitude", latitude.toString());
                    params.put("longitude", longitude.toString());
                    params.put("pengguna", pengguna);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

            up();
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
    }

    private boolean validasi() {
        boolean valid = true;

        String sJudul = judulportal.getText().toString();

        TextView errorText = (TextView)spkategori.getSelectedView();

        if (sJudul.isEmpty() || sJudul.length() < 3) {
            judulportal.setError("Inputkan minimal 3 karakter");
            valid = false;
        } else {
            judulportal.setError(null);
        }


        //String nama = null;
        if (spkategori != null && spkategori.getSelectedItem()!=null){
            sKategori = (String)spkategori.getSelectedItem();
        } else {
            errorText.setText("Pilih Salah Satu");
            errorText.setError("");
            errorText.setTextColor(Color.RED);
            valid=false;
        }


        return valid;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK){
            switch(requestCode) {
                case GALLERY: {
                    final Uri imageUri = data.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case CAMERA: {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    imageView.setImageBitmap(bitmap);
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }public void up(){
        String address="167.205.7.230",u="ftp.vidyanusa.id|vidyanusaftp",p="VvIiDdYyAa123!",directory="mobile/portal";
        uploadTask async=new uploadTask();
        async.execute(address,u,p,directory);
    }

    class uploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                com.adeel.library.easyFTP ftp = new com.adeel.library.easyFTP();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
                ftp.connect(params[0],params[1],params[2]);
                boolean status=false;
                if (!params[3].isEmpty()){
                    status=ftp.setWorkingDirectory(params[3]);
                }
                ftp.uploadFile(bs,timeStamp +".png");
                ftp.disconnect();
                return new String("http://filehosting.vidyanusa.id/tes_upload_dari_android/" + timeStamp + ".png");
            }catch (Exception e){
                String t="Failure : " + e.getLocalizedMessage();
                return t;
            }
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Pilih photo dari galeri",
                "Ambil photo dengan kamera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }






}