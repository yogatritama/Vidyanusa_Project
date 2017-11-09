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
    private String sJudul, sKategori, sfile_berkas;
    private Button kirim;
    private final int SELECT_PHOTO = 1;
    private ProgressDialog prg;

    private ImageView imageView;
    private  Bitmap selectedImage;
    private InputStream imageStream;
    private ClipDrawable drawable;

    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;

    private String access_token;
    private String pengguna;

    private List<String> listKegiatan = new ArrayList<>();
    private List<String> item = new ArrayList<>();
    private String idKategori;

    private int GALLERY = 1, CAMERA = 2;

    private GPSTracker gps;

    private Double latitude;
    private Double longitude;

    private static final String IMAGE_DIRECTORY = "/vidyanusa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sample_actionbar);
        setContentView(R.layout.portal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
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

            idKategori = listKegiatan.get(spkategori.getSelectedItemPosition() - 1);

            sJudul = judulportal.getText().toString();
            sKategori = spkategori.getSelectedView().toString();




            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            sfile_berkas =



            final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TAMBAH_KEGIATAN,
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
                    params.put("file_berkas",stringRequest);
                    params.put("latitude", latitude.toString());
                    params.put("longitude", longitude.toString());
                    params.put("pengguna", pengguna);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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


    private byte[] imageToByte(ImageView image) {
        Bitmap bitmapFoto = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        /*if(resultCode == RESULT_OK && requestCode == request_code){
//            imageView.setImageURI(data.getData());
//            bytes = imageToByte(imageView);
//
//            // para que se vea la imagen en circulo
//            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
//            roundedBitmapDrawable.setCircular(true);
//            imageView.setImageDrawable(roundedBitmapDrawable);
//        }
//        super.onActivityResult(requestCode, resultCode, data);*/
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == this.RESULT_CANCELED) {
//            return;
//        }
//        if (requestCode == GALLERY) {
//            if (data != null) {
//                Uri contentURI = data.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    String path = saveImage(bitmap);
//                    Toast.makeText(PortalActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//                    imageView.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(PortalActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        } else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            imageView.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
//            Toast.makeText(PortalActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//        }
//    }

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
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(galleryIntent, GALLERY);

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = imageReturnedIntent.getData();
                        imageStream = getContentResolver().openInputStream(imageUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                        imageView.setImageBitmap(selectedImage);
                        //drawable = (ClipDrawable) imageView.getDrawable();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                }
        }
    }

    public  void up(View v){
        String address="167.205.7.230",u="ftp.vidyanusa.id|vidyanusaftp",p="VvIiDdYyAa123!",directory="tes_upload_dari_android";
        //String address="167.205.7.230",u="vidyanusa_projects",p="123456!a",directory="BackEnd";
        uploadTask async=new uploadTask();
        async.execute(address,u,p,directory);//Passing arguments to AsyncThread
    }

    class uploadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            prg = new ProgressDialog(PortalActivity.this);
            prg.setMessage("Uploading...");
            prg.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                easyFTP ftp = new easyFTP();

                //Convert Bitmap to Input Stream
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);


                InputStream is=getResources().openRawResource(+R.drawable.easyftptest);


                ftp.connect(params[0],params[1],params[2]);
                boolean status=false;
                if (!params[3].isEmpty()){
                    status=ftp.setWorkingDirectory(params[3]);
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                ftp.uploadFile(bs,timeStamp +".png");


                return new String("Upload Successful");
            }catch (Exception e){
                String t="Failure : " + e.getLocalizedMessage();
                return t;
            }
        }



        @Override
        protected void onPostExecute(String str) {
            prg.dismiss();
            Toast.makeText(PortalActivity.this,str, Toast.LENGTH_LONG).show();
        }
    }






}