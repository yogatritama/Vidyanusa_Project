package com.example.vidyanusa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by ManInBack on 12/2/2016.
 */

public class tab1 extends Fragment {

    private EditText nama_lengkap, email, bio, password, kelas;
    private Button ubahdata, tambahkelas;
    private ImageView imageView;
    private int request_code = 1;
    private Bitmap bitmap_foto;
    private RoundedBitmapDrawable roundedBitmapDrawable;
    private byte[] bytes;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.activity_pengaturan_dasar,container,false);

        imageView = (ImageView) getActivity().findViewById(R.id.image_user);
        nama_lengkap =(EditText) getActivity().findViewById(R.id.etnamalengkap);
        bio =(EditText) getActivity().findViewById(R.id.etbio);
        email =(EditText) getActivity().findViewById(R.id.etemail);
        password =(EditText) getActivity().findViewById(R.id.etpassword);
        kelas =(EditText)  getActivity().findViewById(R.id.etkelas);
        tambahkelas=(Button) getActivity().findViewById(R.id.btn_tambahkelas);
        ubahdata=(Button) getActivity().findViewById(R.id.btn_ubahpengaturan);

        bitmap_foto = BitmapFactory.decodeResource(getResources(),R.drawable.logoprofile);
        roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap_foto);
        roundedBitmapDrawable.setCircular(true);
//        imageView.setImageDrawable(roundedBitmapDrawable);
//

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = null;
//                //verificacion de la version de plataforma
//                if(Build.VERSION.SDK_INT < 19){
//                    //android 4.3  y anteriores
//                    i = new Intent();
//                    i.setAction(Intent.ACTION_GET_CONTENT);
//                }else {
//                    //android 4.4 y superior
//                    i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                    i.addCategory(Intent.CATEGORY_OPENABLE);
//                }
//                i.setType("image/*");
//                startActivityForResult(i, request_code);
//            }
//        });


        return v;

    }

    public void ubahdata (){

    }
}