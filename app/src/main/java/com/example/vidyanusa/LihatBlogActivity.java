package com.example.vidyanusa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ManInBack on 12/2/2016.
 */

public class LihatBlogActivity extends Fragment {

    private Button tulis_blog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.lihat_blog_2,container,false);

        tulis_blog=(Button) v.findViewById(R.id.btn_blog);

        tulis_blog.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BlogActivity.class);
                startActivity(intent);
                //finish();
            }
        });

        final LinearLayout listBlog = (LinearLayout) v.findViewById(R.id.listBlog);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.URL_DAFTAR_BLOG,
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

                                    TextView text1 = new TextView(getActivity());
                                    text1.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    text1.setText(objData.getString("title"));
                                    text1.setTextAppearance(getContext(), R.style.TextAppearance_AppCompat_Title);

                                    TextView text2 = new TextView(getActivity());
                                    text2.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    String data = Html.fromHtml(objData.getString("body")).toString();
                                    if(data.length() > 50) {
                                        data = data.substring(0, 49) + " . . . . ";
                                    }
                                    text2.setText(data);

                                    JSONObject objAuthor = objData.getJSONObject("author");
                                    JSONObject objProfil = objAuthor.getJSONObject("profil");

                                    TextView text3 = new TextView(getActivity());
                                    text1.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    text3.setText("dibuat oleh " + objProfil.getString("username"));

                                    Space space = new Space(getActivity());
                                    space.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            getDP(15)));

                                    LinearLayout layoutText = new LinearLayout(getActivity());
                                    layoutText.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    layoutText.setOrientation(LinearLayout.VERTICAL);
                                    layoutText.setPadding(24, 16, 24, 16);
                                    layoutText.addView(text1);
                                    layoutText.addView(text3);
                                    layoutText.addView(space);
                                    layoutText.addView(text2);

                                    CardView layoutCard = new CardView(getActivity());
                                    layoutCard.setLayoutParams(new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT));
                                    ViewGroup.MarginLayoutParams layoutParams =
                                            (ViewGroup.MarginLayoutParams) layoutCard.getLayoutParams();
                                    layoutParams.setMargins(getDP(16), getDP(16), getDP(16), getDP(16));
                                    layoutCard.addView(layoutText);

                                    listBlog.addView(layoutCard);
                                }
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
        );

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