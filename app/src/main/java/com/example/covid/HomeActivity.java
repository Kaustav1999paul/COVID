package com.example.covid;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    private TextView cases, deaths, recovered,currentUser, emaila;
    private PieChart graphChart;
    private String url, country="IN";
    private CircleImageView avatar , profileI;
    private FloatingActionButton  logout, refresh;
    FirebaseUser user;
    private String userID, email, name;
    private Uri userphoto;
    private Dialog accountDialog;
    FirebaseAuth.AuthStateListener mAuthListener;
    private ImageView next, orders;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> list;
    private Spinner spinner;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        spinner = findViewById(R.id.spinner);
        orders = findViewById(R.id.orders);
        accountDialog = new Dialog(this);
        next = findViewById(R.id.next);
        cases = findViewById(R.id.cas);
        deaths = findViewById(R.id.dea);
        recovered = findViewById(R.id.rec);
        graphChart = findViewById(R.id.pieChart);
        avatar = findViewById(R.id.avatar);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();
        userphoto = user.getPhotoUrl();
        Picasso.get().load(userphoto).into(avatar);
        email = user.getEmail();
        name = user.getDisplayName();
        mAuth = FirebaseAuth.getInstance();
        refresh = findViewById(R.id.refresh);

        orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AppouintmentActivity.class));
            }
        });

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            }
        };

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DoctorsActivity.class));
            }
        });


        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        openAccountDialog(name, email, userphoto);
            }
        });

        list = new ArrayList<>();
        list.add("IN");
        list.add("US");
        list.add("AF");
        list.add("AT");
        list.add("BD");
        list.add("BR");
        list.add("CA");
        list.add("CN");
        list.add("FR");
        list.add("PK");

        arrayAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                country = list.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData(country);
            }
        });
        getData("IN");
    }

    private void openAccountDialog(String name, String email, Uri userphoto) {
        accountDialog.setContentView(R.layout.account_popup);
        accountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        accountDialog.show();

        currentUser = accountDialog.findViewById(R.id.ownName);
        emaila = accountDialog.findViewById(R.id.ownEmail);
        profileI = accountDialog.findViewById(R.id.ownimage);
        logout = accountDialog.findViewById(R.id.logout);

        currentUser.setText(name);
        emaila.setText(email);
        Glide.with(profileI).load(userphoto).into(profileI);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
    }



    private void getData(String country) {
        url = "https://covid19-api.org/api/status/"+country;

        StringRequest requestAll = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject object = new JSONObject(response.toString());

                    cases.setText(object.getString("cases"));
                    deaths.setText(object.getString("deaths"));
                    recovered.setText(object.getString("recovered"));


                    graphChart.addPieSlice(new PieModel("Cases", Integer.parseInt(cases.getText().toString()), Color.parseColor("#00BCD4")));
                    graphChart.addPieSlice(new PieModel("Deaths", Integer.parseInt(deaths.getText().toString()), Color.parseColor("#F44336")));
                    graphChart.addPieSlice(new PieModel("Recovered", Integer.parseInt(recovered.getText().toString()), Color.parseColor("#4CAF50")));

                    graphChart.startAnimation();

                }catch (JSONException ex){
                    String a = ex.getMessage();
                    Toast.makeText(HomeActivity.this, "Error1: "+a, Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errormessage = error.getMessage().toString();

                Toast.makeText(HomeActivity.this, "Error2: "+errormessage, Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue req = Volley.newRequestQueue(this);
        req.add(requestAll);
    }
}
