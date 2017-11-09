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
import android.media.MediaScannerConnection;
import android.net.Uri;
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
import com.example.vidyanusa.service.GPSTracker;
//import com.mikepenz.aboutlibraries.Libs;
//import com.mikepenz.aboutlibraries.LibsBuilder;
//import com.mikepenz.fontawesome_typeface_library.FontAwesome;
//import com.mikepenz.google_material_typeface_library.GoogleMaterial;
//import com.vidyanusa.siswa.AccountHeader;
//import com.vidyanusa.siswa.AccountHeaderBuilder;
//import com.vidyanusa.siswa.Drawer;
//import com.vidyanusa.siswa.DrawerBuilder;
//import com.vidyanusa.siswa.holder.StringHolder;
//import com.vidyanusa.siswa.model.ExpandableDrawerItem;
//import com.vidyanusa.siswa.model.PrimaryDrawerItem;
//import com.vidyanusa.siswa.model.ProfileDrawerItem;
//import com.vidyanusa.siswa.model.SecondaryDrawerItem;
//import com.vidyanusa.siswa.model.interfaces.IDrawerItem;
//import com.vidyanusa.siswa.model.interfaces.IProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BlogActivity extends AppCompatActivity {
   // private static final int PROFILE_SETTING = 100000;

    private EditText judul;
    private Spinner spmapel, spmateri, sptag;
    private ImageView imageView;
    private EditText deskripsi;
    private Button posting_blog;

    private String access_token;
    private String author;

    private List<String> listMapel = new ArrayList<>();
    private List<String> item2 = new ArrayList<>();
    private String idMapel;

    private List<String> listMateri = new ArrayList<>();
    private List<String> item3 = new ArrayList<>();
    private String idMateri;

    private String sMapel, sMateri, sTag, sJudul, sUpload, sDeskripsi ;
    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;
    private int GALLERY = 1, CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/vidyanusa";

    private GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        access_token = pref.getString("access_token", null);
        author = pref.getString("id_pengguna", null);

        gps = new GPSTracker(BlogActivity.this);

        judul = (EditText)findViewById(R.id.etjudul);

        //List jenis postingan
        spmapel = (Spinner) findViewById(R.id.spmapel);

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
                                    listMapel.add(objData.getString("_id"));
                                    item2.add(objData.getString("mapel"));
                                }

                                //untuk membuat adapter list kota
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(BlogActivity.this,android.R.layout.simple_spinner_dropdown_item,
                                        item2);

                                //untuk menentukan model adapter nya
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spmapel.setAdapter(
                                        new NothingSelectedMataPelajaran(
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

        spmateri = (Spinner) findViewById(R.id.spmateri);


        StringRequest stringRequestmateri = new StringRequest(Request.Method.POST, Constant.URL_DAFTAR_KATEGORI_KEGIATAN,
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
                                    listMateri.add(objData.getString("_id"));
                                    item3.add(objData.getString("materi"));
                                }

                                //untuk membuat adapter list kota
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(BlogActivity.this,android.R.layout.simple_spinner_dropdown_item,
                                        item2);

                                //untuk menentukan model adapter nya
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spmapel.setAdapter(
                                        new NothingSelectedMataPelajaran(
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





//        //List jenis mapel
//        spmapel = (Spinner) findViewById(R.id.spmapel);
//        List<String> item3 = new ArrayList<String>();
//        item3.add("Matematika");
//        item3.add("Bahasa Indonesia");
//        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(BlogActivity.this,android.R.layout.simple_spinner_dropdown_item,
//                item3);
//        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spmapel.setAdapter(adapter2);
//        spmapel.setAdapter(
//                new NothingSelectedMataPelajaran(
//                        adapter3,
//                        R.layout.contact_spinner_row_nothing_selected4,
//                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
//                        this)
//        );
//
//        //List jenis mapel
//        spkategori = (Spinner) findViewById(R.id.spkategori);
//        List<String> item4 = new ArrayList<String>();
//        item4.add("Spiritual");
//        item4.add("Tanggung Jawab");
//        item4.add("Disiplin dan Konsisten");
//        item4.add("Percaya Diri dan Inisiatif");
//        item4.add("Jujur dan Mandiri");
//        item4.add("Gotong Royong dan Peduli");
//        item4.add("Belajar");
//
//        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(BlogActivity.this,android.R.layout.simple_spinner_dropdown_item,
//                item4);
//        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spkategori.setAdapter(adapter4);
//        spkategori.setAdapter(
//                new NothingSelectedKategori(
//                        adapter4,
//                        R.layout.contact_spinner_row_nothing_selected2,
//                        // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
//                        this)
//        );


        imageView = (ImageView) findViewById(R.id.uploadfoto_blog);
        deskripsi = (EditText) findViewById(R.id.etdeskripsi);

        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.logoprofile);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        posting_blog = (Button) findViewById(R.id.buttonkirimblog);
        posting_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tambah_post_blog();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent i = null;
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
                showPictureDialog();
            }
        });


    }

    public void tambah_post_blog() {
        if (!validasi()) return;
        sMapel = spmapel.getSelectedItem().toString();
        sMateri = spmateri.getSelectedItem().toString();
        sTag = sptag.getSelectedItem().toString();
        sJudul = judul.getText().toString();
        sDeskripsi = deskripsi.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TAMBAH_BLOG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  progressDialos.dismiss();
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "Berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),BlogActivity.class));
                                finish();
                            }
                            else{
                                JSONArray arrFail = new JSONArray(obj.getString("data"));
                                JSONObject objFail = arrFail.getJSONObject(0);
                                Toast.makeText(getApplicationContext(),
                                        objFail.getString("message"),
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
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG);
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();

                sMapel = spmapel.getSelectedItem().toString();
                sMateri = spmateri.getSelectedItem().toString();
                sTag = sptag.getSelectedItem().toString();
                sJudul = judul.getText().toString();
                sDeskripsi = deskripsi.getText().toString();

                params.put("mapel",sMapel);
                params.put("materi",sMateri);
                params.put("tag",sTag);
                params.put("judul",sJudul);
                params.put("deskripsi",sDeskripsi);
                return params;
            }
        };
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private boolean validasi() {
        boolean valid = true;

        String sMapel = spmapel.getSelectedView().toString();
        String sMateri = spmateri.getSelectedView().toString();
        String sTag = sptag.getSelectedView().toString();
        String sJudul = judul.getText().toString();

        TextView errorText2 = (TextView)spmapel.getSelectedView();
        if (spmapel != null && spmapel.getSelectedItem()!=null){
            sMapel = (String)spmapel.getSelectedItem();
        } else {
            errorText2.setText("Pilih Salah Satu");
            errorText2.setError("");
            errorText2.setTextColor(Color.RED);
            valid=false;
        }

        TextView errorText1 = (TextView)spmateri.getSelectedView();
        if (spmateri != null && spmateri.getSelectedItem()!=null){
            sMateri = (String)spmateri.getSelectedItem();
        } else {
            errorText1.setText("Pilih Salah Satu");
            errorText1.setError("");
            errorText1.setTextColor(Color.RED);
            valid=false;
        }

        TextView errorText3 = (TextView)sptag.getSelectedView();
        if (sptag != null && sptag.getSelectedItem()!=null){
            sTag = (String)sptag.getSelectedItem();
        } else {
            errorText3.setText("Pilih Salah Satu");
            errorText3.setError("");
            errorText3.setTextColor(Color.RED);
            valid=false;
        }

        if (sJudul.isEmpty() || sJudul.length() < 3) {
            judul.setError("Inputkan minimal 3 karakter");
            valid = false;
        } else {
            judul.setError(null);
        }

        if (sDeskripsi.isEmpty() || sDeskripsi.length() < 10) {
            deskripsi.setError("Inputkan minimal 10 karakter");
            valid = false;
        } else {
            deskripsi.setError(null);
        }

        return valid;
    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //add the values which need to be saved from the drawer to the bundle
//        outState = result.saveInstanceState(outState);
//        //add the values which need to be saved from the accountHeader to the bundle
//        outState = headerResult.saveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        //handle the back press :D close the drawer first and if the drawer is closed close the activity
//        if (result != null && result.isDrawerOpen()) {
//            result.closeDrawer();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.fragment_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //handle the click on the back arrow click
//        switch (item.getItemId()) {
//            case R.id.menu_1:
//                /*Fragment f = DrawerFragment.newInstance("Demo");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();
//                return true;*/
//               // Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
//                //startActivity(intent);
//                //finish();
//
//                Intent intent = new Intent(BlogActivity.this, LoginActivity.class);
//                startActivity(intent);
//                finish();
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    private byte[] imageToByte(ImageView image) {
        Bitmap bitmapFoto = ((BitmapDrawable)image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapFoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        /*if(resultCode == RESULT_OK && requestCode == request_code){
            imageView.setImageURI(data.getData());
            bytes = imageToByte(imageView);

            // para que se vea la imagen en circulo
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
            roundedBitmapDrawable.setCircular(true);
            imageView.setImageDrawable(roundedBitmapDrawable);
        }
        super.onActivityResult(requestCode, resultCode, data);*/
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    Toast.makeText(BlogActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageView.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(BlogActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(thumbnail);
            saveImage(thumbnail);
            Toast.makeText(BlogActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
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
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

}