package com.example.vidyanusa.portal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.vidyanusa.Constant;
import com.example.vidyanusa.PortalActivity;
import com.example.vidyanusa.R;
import com.example.vidyanusa.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LenovoG4080 on 15/09/2016.
 */
public class LihatPortal extends Fragment {
    private static final String TAG=LihatPortal.class.getName();
    private RecyclerView recyclerViewMakanan;

    private ProgressDialog pDialog;                                  // membuat loading
    //private String url ="https://private-109fde-tutorialdummydata.apiary-mock.com/listmakanan";     // link JSON API dari IBACOR
    private TextView txtJsonData;
    private ListView listView;
    private ArrayList<Portal> portalList;
    private PortalAdapter adapter;
    private String[] itemsMenu = new String[]{"nama_makanan","harga","stok_barang","sisa_barang","owner"};
    private String[] itemsMenuShow = new String[]{"Nama Makanan","Harga","Stok Barang", "Sisa Barang", "Owner"};
    private Spinner spinnerMenu;
    private EditText edCari;
    private Button portal;

    private String username;
    private String access_token;
    private String pengguna;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lihat_portal, container, false);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        SharedPreferences pref = getActivity().getSharedPreferences("MyPref", 0); // 0 - for private mode
        username = pref.getString("username", null);
        access_token = pref.getString("access_token", null);
        pengguna = pref.getString("id_pengguna", null);

        portal=(Button) view.findViewById(R.id.btn_portal);
        portal.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PortalActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        portalList = new ArrayList<>();
        recyclerViewMakanan = (RecyclerView) view.findViewById(R.id.recyclerview);
        getDataVolley();

//        spinnerMenu = (Spinner) view.findViewById(R.id.spinnerMenu);
//       ArrayAdapter<String> adapterMenu = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, itemsMenuShow);
//        spinnerMenu.setAdapter(adapterMenu);
//
//        edCari = (EditText) view.findViewById(R.id.edCari);
//        btnCari = (Button) view.findViewById(R.id.btnCari);
//        btnCari.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String cari=edCari.getText().toString();
//                if (cari.isEmpty()){
//                    Toast.makeText(getActivity(), "Data tidak boleh kosong", Toast.LENGTH_LONG).show();
//                }else{
//                    try {
//                        portalList.clear();
//                        String menu= itemsMenu[(int)spinnerMenu.getSelectedItemId()].toString();
//                        cari = URLEncoder.encode(cari, "UTF-8");

//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });


        return view;
    }

    private void getDataVolley(){



        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LIHAT_PORTAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        iniData(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),
                                error.toString(),
                                Toast.LENGTH_LONG).show();
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
        RequestHandler.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void iniData(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);

            // ini utk mengambil attribute array yg ada di json (yaitu attribute data)
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            //looping utk array
            for(int i=0; i<jsonArray.length(); i++){
                //get json berdasarkan banyaknya data (index i)
                JSONObject objectMakanan = jsonArray.getJSONObject(i);

                //get data berdasarkan attribte yang ada dijsonnya (harus sama)
                String namaMakanan = objectMakanan.getString("judul");
                String harga = objectMakanan.getJSONObject("pengguna").getJSONObject("profil").getString("username");
                String stok = objectMakanan.getJSONObject("kategori").getString("nama_kategori");
                String sisa = objectMakanan.getString("file_berkas");
                String owner = "admin";

                //add data ke modelnya
                Portal Portal = new Portal();
                Portal.setNama_makanan(namaMakanan);
                Portal.setHarga(harga);
                Portal.setStok_barang(stok);
                Portal.setSisa_barang(sisa);
                Portal.setOwner(owner);

                //add model ke list
                portalList.add(Portal);

                //passing data list ke adapter
                adapter = new PortalAdapter(portalList, getActivity());
                adapter.notifyDataSetChanged();
                recyclerViewMakanan.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewMakanan.setItemAnimator(new DefaultItemAnimator());
                recyclerViewMakanan.setAdapter(adapter);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}