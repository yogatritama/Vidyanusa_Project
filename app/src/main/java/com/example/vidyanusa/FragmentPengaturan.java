package com.example.vidyanusa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by LenovoG4080 on 15/09/2016.
 */
public class FragmentPengaturan extends Fragment {

    public Button pengaturandasar;
    public Button portofolio;

    private static final String TAG=FragmentPengaturan.class.getName();
    private ProgressDialog pDialog;                                  // membuat loading
    private String url ="https://private-109fde-tutorialdummydata.apiary-mock.com/listmakanan";     // link JSON API dari IBACOR
    private TextView txtJsonData;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pengaturan, container, false);
//        pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Loading...");
//        pDialog.setCancelable(false);

        pengaturandasar=(Button) view.findViewById(R.id.btn_pengaturandasar);
        pengaturandasar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PengaturanDasarActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        portofolio=(Button) view.findViewById(R.id.btn_portofolio);
        portofolio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PortofolioActivity.class);
                startActivity(intent);
                //finish();
            }
        });
//        getDataVolley();

        return view;
    }

}