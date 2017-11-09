package com.example.vidyanusa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

//import com.vidyanusa.siswa.Drawer;

public class PengaturanDasarActivity extends AppCompatActivity {

    //private Drawer result = null;

    private EditText nama_lengkap, email, bio, password, kelas;
    private Button ubahdata, tambahkelas;
    private ImageView imageView;
    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan_dasar);
        setTitle("Pengaturan Dasar");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(false);

        imageView = (ImageView) findViewById(R.id.image_user);
        nama_lengkap =(EditText) findViewById(R.id.etnamalengkap);
        bio =(EditText) findViewById(R.id.etbio);
        email =(EditText) findViewById(R.id.etemail);
        password =(EditText) findViewById(R.id.etpassword);
        kelas =(EditText)  findViewById(R.id.etkelas);
        tambahkelas=(Button) findViewById(R.id.btn_tambahkelas);
        ubahdata=(Button) findViewById(R.id.btn_ubahpengaturan);

        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.logoprofile);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
        imageView.setImageDrawable(roundedBitmapDrawable);

        ubahdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ubahdata();
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



    }


//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        //add the values which need to be saved from the drawer to the bundle
//        outState = result.saveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public void onBackPressed() {
//        //handle the back press :D close the drawer first and if the drawer is closed close the activity
//        if (result != null && result.isDrawerOpen()) {
//            result.closeDrawer();
//        } else {
//            super.onBackPressed();
//        }
//    }

    public void ubahdata (){

    }



}
