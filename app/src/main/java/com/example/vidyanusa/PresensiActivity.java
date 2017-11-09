package com.example.vidyanusa;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextClock;
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
//import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
//import com.vidyanusa.siswa.AccountHeader;
//import com.vidyanusa.siswa.AccountHeaderBuilder;
//import com.vidyanusa.siswa.Drawer;
//import com.vidyanusa.siswa.DrawerBuilder;
//import com.vidyanusa.siswa.app.service.GPSTracker;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PresensiActivity extends AppCompatActivity {

    private Button datang;
    private Button history;
    private TextClock waktu;

    private String username;
    private String access_token;
    private String pengguna;
    private String keterangan;
    private Double latitude;
    private Double longitude;
    private String[] dataWaktu;
    private Integer jam;
    private Integer menit;
    private Integer totalDetik;
    private Spinner spKelas;
    private List<String> listIdKelas = new ArrayList<>();
    private List<String> item = new ArrayList<>();
    private String idKelas;

    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.portal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View v =inflater.inflate(R.layout.activity_presensi,container,false);

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Silahkan tunggu");
        pDialog.setCancelable(false);
        pDialog.show();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        username = pref.getString("username", null);
        access_token = pref.getString("access_token", null);
        pengguna = pref.getString("id_pengguna", null);

        gps = new GPSTracker(PresensiActivity.this);

//        // Handle Toolbar
//        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        spKelas = (Spinner) findViewById(R.id.spKelas);

        waktu = (TextClock) findViewById(R.id.textClock);
        datang = (Button) findViewById(R.id.button);
        history = (Button) findViewById(R.id.button2);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_AMBIL_PROFIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //              progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray arrData = new JSONArray(obj.getString("data"));
                            JSONObject objData = arrData.getJSONObject(0);
                            if(obj.getBoolean("success")){
                                JSONArray arrKelas = new JSONArray(objData.getString("kelas"));
                                Integer num;
                                JSONObject objKelas;
                                if(arrKelas.length() == 0) {
                                    Toast.makeText(getApplicationContext(), "Silahkan hubungi guru untuk mendaftar kelas", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                    finish();
                                }
                                for(num = 0; num < arrKelas.length(); num++) {
                                    objKelas = arrKelas.getJSONObject(num);
                                    listIdKelas.add(objKelas.getString("_id"));
                                    item.add(objKelas.getString("nama_kelas"));
                                }
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spKelas.setAdapter(new NothingSelectedKategori(adapter, R.layout.contact_spinner_row_nothing_kelas, getApplicationContext()));
                                pDialog.hide();
                            }
                            else{
                                Log.d("log", obj.toString());
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
                params.put("username",username);
                params.put("access_token",access_token);

                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        datang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   Intent intent = new Intent(getApplicationContext(), PresensiActivity.class);
                // startActivity(intent);
                //finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                // create class object

                if(spKelas.getSelectedItemPosition() == 0) {
                    Toast.makeText(getApplicationContext(), "Silahkan pilih kelas", Toast.LENGTH_SHORT).show();
                    return;
                }

                idKelas = listIdKelas.get(spKelas.getSelectedItemPosition() - 1);

                // check if GPS enabled

                if(gps.canGetLocation()){

                    datang.setText("Memproses");
                    datang.setEnabled(false);

                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();

                    dataWaktu = waktu.getText().toString().split(":");
                    jam = Integer.parseInt(dataWaktu[0]);
                    menit = Integer.parseInt(dataWaktu[1]);
                    totalDetik = (jam * 3600) + (menit * 60);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.URL_TAMBAH_ABSEN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //              progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        JSONObject objData = new JSONObject(obj.getString("data"));
                                        Toast.makeText(getApplicationContext(),
                                                objData.getString("message"),
                                                Toast.LENGTH_LONG).show();
                                    }
                                    catch (JSONException e){
                                        e.printStackTrace();

                                    }

                                    datang.setText("Datang");
                                    datang.setEnabled(true);

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
                            params.put("pengguna",pengguna);
                            params.put("access_token",access_token);
                            params.put("kelas", idKelas);
                            params.put("totaldetik", totalDetik.toString());
                            params.put("latitude",latitude.toString());
                            params.put("longitude",longitude.toString());
                            return params;
                        }
                    };

                    stringRequest.setRetryPolicy(new DefaultRetryPolicy( 50000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    RequestHandler.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplicationContext(), PresensiActivity.class);
                //startActivity(intent);
                //finish();
                //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


//            final IProfile profile = new ProfileDrawerItem().withName("Mike Penz").withEmail("mikepenz@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/1476232?v=3&s=460").withIdentifier(100);
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
//                                intent = new Intent(PresensiActivity.this, PresensiActivity.class);
//                            } else if (drawerItem.getIdentifier() == 2) {
//                                intent = new Intent(PresensiActivity.this, RekapPresensiActivity.class);
//                            } else if (drawerItem.getIdentifier() == 3) {
//                                intent = new Intent(PresensiActivity.this, LihatBlogActivity.class);
//                            } else if (drawerItem.getIdentifier() == 4) {
//                                intent = new Intent(PresensiActivity.this, AktivitasActivity.class);
//                            } else if (drawerItem.getIdentifier() == 5) {
//                                intent = new Intent(PresensiActivity.this, PortalActivity.class);
//                            } else if (drawerItem.getIdentifier() == 20) {
//                                intent = new LibsBuilder()
//                                        .withFields(R.string.class.getFields())
//                                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
//                                        .intent(PresensiActivity.this);
//                            }
//                            if (intent != null) {
//                                gps.stopUsingGPS();
//                                gps = null;
//                                PresensiActivity.this.startActivity(intent);
//                                finish();
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
//                finish();
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



}