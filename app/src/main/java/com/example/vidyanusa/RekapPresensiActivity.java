package com.example.vidyanusa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import com.mikepenz.aboutlibraries.Libs;
//import com.mikepenz.aboutlibraries.LibsBuilder;
//import com.mikepenz.fontawesome_typeface_library.FontAwesome;
//import com.mikepenz.google_material_typeface_library.GoogleMaterial;
//import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RekapPresensiActivity extends AppCompatActivity {

    private TextView username;
    private Spinner bulan;
    private Spinner tahun;
    private TextView tgl;
    private TextView jam_masuk;
    //jam_boleh_masuk
    private TextView lokasi;
    //jam_boleh_pulang
    private TextView ket;

    private String usernameString;
    private String access_token;
    private String pengguna;

    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sample_actionbar);
        setContentView(R.layout.activity_rekap_presensi);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        usernameString = pref.getString("username", null);
        access_token = pref.getString("access_token", null);
        pengguna = pref.getString("id_pengguna", null);

        username = (TextView) findViewById(R.id.textView1);
        bulan = (Spinner) findViewById(R.id.spinner1);
        //untuk membuat list kota, atau bisa menggunaan String[]
        List<String> item = new ArrayList<String>();
            item.add("Januari");
            item.add("Februari");
            item.add("Maret");
            item.add("April");
            item.add("Mei");
            item.add("Juni");
            item.add("Juli");
            item.add("Agustus");
            item.add("September");
            item.add("Oktober");
            item.add("November");
            item.add("Desember");

        //untuk membuat adapter list kota
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(RekapPresensiActivity.this,android.R.layout.simple_spinner_dropdown_item,
                item);

        //untuk menentukan model adapter nya
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //menerapkan adapter pada spinner tahun
        bulan.setAdapter(adapter);

        tahun = (Spinner) findViewById(R.id.spinner2);
        //untuk membuat list kota, atau bisa menggunaan String[]
        List<String> item2 = new ArrayList<String>();
        item2.add("2017");
        item2.add("2018");
        item2.add("2019");
        item2.add("2020");

        //untuk membuat adapter list kota
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RekapPresensiActivity.this,android.R.layout.simple_spinner_dropdown_item,
                item2);

        //untuk menentukan model adapter nya
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //menerapkan adapter pada spinner sp
        tahun.setAdapter(adapter2);

        tgl = (TextView) findViewById(R.id.textView4);
        jam_masuk = (TextView) findViewById(R.id.textView5);
        //lokasi = (TextView) findViewById(R.id.textView6);
        ket = (TextView) findViewById(R.id.textView7);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_GET_ABSENSI_BY_USERNAME,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //              progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.getBoolean("success")){
                            JSONArray arrData = new JSONArray(obj.getString("data"));
                            JSONObject objData;

                            table = (TableLayout) findViewById(R.id.tableLayout1);

                            String[] temp1;
                            String[] temp2;
                            String tanggal = "";
                            String jam_masuk;

                            for(int num = 0; num < arrData.length(); num++) {
                                objData = arrData.getJSONObject(num);
                                temp1 = objData.getString("created_at").split("T");
                                temp2 = temp1[0].split("-");
                                if(tanggal.equals(temp2[2] + "-" + temp2[1] + "-" +  temp2[0])) {
                                    continue;
                                }
                                tanggal = temp2[2] + "-" + temp2[1] + "-" + temp2[0];

                                TextView text1 = new TextView(RekapPresensiActivity.this);
                                text1.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                text1.setTextSize(16);
                                text1.setPadding(10, 10, 10, 10);
                                text1.setGravity(Gravity.CENTER);
                                text1.setText(tanggal);

                                TextView text2 = new TextView(RekapPresensiActivity.this);
                                text2.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                text2.setTextSize(16);
                                text2.setPadding(10, 10, 10, 10);
                                text2.setGravity(Gravity.CENTER);
                                temp2 = temp1[1].split("\\.");
                                jam_masuk = temp2[0];
                                text2.setText(jam_masuk);

                                TextView text3 = new TextView(RekapPresensiActivity.this);
                                text3.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.WRAP_CONTENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                text3.setTextSize(16);
                                text3.setPadding(10, 10, 10, 10);
                                text3.setGravity(Gravity.CENTER);
                                text3.setText(objData.getString("keterangan"));

                                TableRow table1 = new TableRow(RekapPresensiActivity.this);
                                table1.setLayoutParams(new TableRow.LayoutParams(
                                        TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT));
                                table1.setPadding(getDP(10), getDP(10), getDP(10), getDP(10));
                                table1.setGravity(Gravity.CENTER);
                                setOnClick(table1, objData.getJSONObject("lokasi").getString("latitude") + " - " + objData.getJSONObject("lokasi").getString("longitude"));
                                table1.addView(text1);
                                table1.addView(text2);
                                table1.addView(text3);

                                table.addView(table1);
                            }
                        }
                        else{
                            JSONObject objData = obj.getJSONObject("data");
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
                    Toast.makeText(getApplicationContext(),
                            error.toString(),
                            Toast.LENGTH_LONG).show();
                }
            }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("access_token",access_token);
                params.put("pengguna",pengguna);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

//        final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460").withIdentifier(100);
//
//        // Create the AccountHeader
//        headerResult = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withTranslucentStatusBar(true)
//                .withHeaderBackground(R.drawable.header)
//                .addProfiles(
//                        profile/*,
//                        profile2,
//                        profile3,
//                        profile4,
//                        profile5,
//                        profile6,
//                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
//                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_plus).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
//                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)*/
//                )
//                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
//                    @Override
//                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
//                        //sample usage of the onProfileChanged listener
//                        //if the clicked item has the identifier 1 add a new profile ;)
//                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
//                            int count = 100 + headerResult.getProfiles().size() + 1;
//                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon(R.drawable.profile5).withIdentifier(count);
//                            if (headerResult.getProfiles() != null) {
//                                //we know that there are 2 setting elements. set the new profile above them ;)
//                                headerResult.addProfile(newProfile, headerResult.getProfiles().size() - 2);
//                            } else {
//                                headerResult.addProfiles(newProfile);
//                            }
//                        }
//
//                        //false if you have not consumed the event and it should close the drawer
//                        return false;
//                    }
//                })
//                .withSavedInstance(savedInstanceState)
//                .build();
//
//        //Create the drawer
//        result = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withHasStableIds(true)
//                .withItemAnimator(new AlphaCrossFadeAnimator())
//                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
//                .addDrawerItems(
//                        new ExpandableDrawerItem().withName("Presensi").withIcon(FontAwesome.Icon.faw_map_marker).withIdentifier(19).withSelectable(false).withSubItems(
//                                new SecondaryDrawerItem().withName("Presensi Harian").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(1),
//                                new SecondaryDrawerItem().withName("Rekap Presensi").withLevel(2).withIcon(GoogleMaterial.Icon.gmd_8tracks).withIdentifier(2)
//                        ),
//                        new PrimaryDrawerItem().withName("Blog").withIcon(GoogleMaterial.Icon.gmd_blogger).withIdentifier(3).withSelectable(false),
//                        new PrimaryDrawerItem().withName("Portal").withIcon(FontAwesome.Icon.faw_archive).withIdentifier(5).withSelectable(false)
//                ) // add the items we want to use with our Drawer
//                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
//                    @Override
//                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
//                        //check if the drawerItem is set.
//                        //there are different reasons for the drawerItem to be null
//                        //--> click on the header
//                        //--> click on the footer
//                        //those items don't contain a drawerItem
//
//                        if (drawerItem != null) {
//                            Intent intent = null;
//                            if (drawerItem.getIdentifier() == 1) {
//                                intent = new Intent(RekapPresensiActivity.this, PresensiActivity.class);
//                            } else if (drawerItem.getIdentifier() == 2) {
//                                intent = new Intent(RekapPresensiActivity.this, RekapPresensiActivity.class);
//                            } else if (drawerItem.getIdentifier() == 3) {
//                                intent = new Intent(RekapPresensiActivity.this, LihatBlogActivity.class);
//                            } else if (drawerItem.getIdentifier() == 4) {
//                                intent = new Intent(RekapPresensiActivity.this, AktivitasActivity.class);
//                            } else if (drawerItem.getIdentifier() == 5) {
//                                intent = new Intent(RekapPresensiActivity.this, PortalActivity.class);
//                            } else if (drawerItem.getIdentifier() == 20) {
//                                intent = new LibsBuilder()
//                                        .withFields(R.string.class.getFields())
//                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                                        .intent(RekapPresensiActivity.this);
//                            }
//                            if (intent != null) {
//                                RekapPresensiActivity.this.startActivity(intent);
//                            }
//                        }
//
//                        return false;
//                    }
//                })
//                .withSavedInstance(savedInstanceState)
//                .withShowDrawerOnFirstLaunch(true)
////              .withShowDrawerUntilDraggedOpened(true)
//                .build();
//
//        //only set the active selection or active profile if we do not recreate the activity
//        if (savedInstanceState == null) {
//            // set the selection to the item with the identifier 11
//            result.setSelection(21, false);
//
//            //set the active profile
//            headerResult.setActiveProfile(profile);
//        }
//
//        result.updateBadge(4, new StringHolder(10 + ""));
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
//                Intent intent =new Intent(getApplicationContext(),LoginActivity.class);
//                startActivity(intent);
//            case R.id.menu_2:
//                /*Fragment f2 = SecondDrawerFragment.newInstance("Demo 2");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f2).commit();
//                return true;*/
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    private int getDP(int num) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, num, getResources()
                        .getDisplayMetrics());
    }

    private void setOnClick(final View v, final String str){
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vv) {
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });
    }

}