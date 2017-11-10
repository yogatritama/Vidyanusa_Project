package com.example.vidyanusa;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ManInBack on 12/2/2016.
 */

public class LihatPortalActivity extends Fragment {

    private String username;
    private String access_token;
    private String pengguna;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.lihat_blog_2,container,false);

//        final ProgressDialog pDialog = new ProgressDialog(getActivity());
//        pDialog.setMessage("Silahkan tunggu");
//        pDialog.setCancelable(false);
//        pDialog.show();

        final SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        username = pref.getString("username", null);
        access_token = pref.getString("access_token", null);
        pengguna = pref.getString("id_pengguna", null);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.LIHAT_PORTAL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //              progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("success")){
                                JSONArray arrData = new JSONArray(obj.getString("data"));
                                JSONObject objData;
                                for(int num = 0; num < arrData.length(); num++) {
                                    objData = arrData.getJSONObject(num);
                                    Log.d("log", objData.getString("judul"));
                                }
                            } else {
                                Log.d("log", obj.toString());
                                Toast.makeText(getActivity(),
                                        obj.getString("message"),
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

        return v;
    }

    private int getDP(int num) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, num, getResources()
                        .getDisplayMetrics());
    }

}