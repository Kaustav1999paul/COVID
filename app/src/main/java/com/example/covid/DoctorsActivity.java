package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Doctor;
import ViewHolder.DoctorViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener  {

    private RecyclerView doctor;
    private DatabaseReference docRef, OrderReff, OrdeDocReff;
    private Dialog accountDialog, confDial;
    private String na, qua, em, docID,photo,selectedDate, saveCurrentDate, saveCurrentTime, ProductRandomKey;
    private CircleImageView docImage;
    private TextView docName, date;
    private EditText problem;
    private FloatingActionButton selectDate, ok;
    private Button placeBook;
    private FirebaseUser me;
    private TextView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors);

        loading = findViewById(R.id.loading);
        me = FirebaseAuth.getInstance().getCurrentUser();

        doctor = findViewById(R.id.doctorList);
        accountDialog = new Dialog(this);
        confDial = new Dialog(this);

        confDial.setContentView(R.layout.confirm_layout);
        confDial.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        doctor.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        doctor.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        OrdeDocReff  = FirebaseDatabase.getInstance().getReference().child("Doctors");
        OrderReff = FirebaseDatabase.getInstance().getReference().child("Users");
        docRef = FirebaseDatabase.getInstance().getReference().child("Doctors");

        FirebaseRecyclerOptions<Doctor> options = new FirebaseRecyclerOptions.Builder<Doctor>()
                .setQuery(docRef, Doctor.class).build();

        FirebaseRecyclerAdapter<Doctor, DoctorViewHolder> adapter = new FirebaseRecyclerAdapter<Doctor, DoctorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DoctorViewHolder holder, int position, @NonNull final Doctor model) {
                loading.setVisibility(View.INVISIBLE);
                doctor.setVisibility(View.VISIBLE);

                Glide.with(holder.doctorAvatar).load(model.getURL()).into(holder.doctorAvatar);
                holder.doctorName.setText(model.getName());
                holder.doctorQual.setText(model.getQual());

                holder.book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        na = model.getName();
                        photo = model.getURL();
                        docID = model.getId();

                        bookApp(na,photo, docID);
                    }
                });
            }
            @NonNull
            @Override
            public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_layout, parent,false);
                DoctorViewHolder holder = new DoctorViewHolder(view);
                return holder;
            }
        };
        doctor.setAdapter(adapter);
        adapter.startListening();
    }

    private void bookApp(final String docID, final String photo, final String na) {
        accountDialog.setContentView(R.layout.place_order_layout);
        accountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        accountDialog.show();

        date = accountDialog.findViewById(R.id.date);
        selectDate = accountDialog.findViewById(R.id.chooseDate);
        problem = accountDialog.findViewById(R.id.problems);
        placeBook = accountDialog.findViewById(R.id.placeOrder);
        docImage = accountDialog.findViewById(R.id.docAVa);
        docName = accountDialog.findViewById(R.id.docName);

        docName.setText(docID);
        Picasso.get().load(photo).into(docImage);
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dateSelect = new DateSelectFragment();
                dateSelect.show(getSupportFragmentManager(), "Select Date");
            }
        });

        placeBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String p = problem.getText().toString();
                String userID, meEmail, mePhoto, meName;
                userID = me.getUid();
                meEmail = me.getEmail();
                meName = me.getDisplayName();
                mePhoto = me.getPhotoUrl().toString();
                storeData(docID,selectedDate, p, photo, na,userID, meEmail, mePhoto, meName );
            }
        });

    }

    private void storeData(final String docID, String selectedDate, String p, String photo, String na, final String userID, final String meEmail, String mePhoto, String meName) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());
        ProductRandomKey = saveCurrentDate + saveCurrentTime + userID;

        final HashMap<String, Object> map = new HashMap<>();
        map.put("orderID", ProductRandomKey);
        map.put("doctorID", na);
        map.put("doctorName", docID);
        map.put("doctorPhoto", photo);
        map.put("problem", p);
        map.put("date", selectedDate);
        map.put("patientID", userID);
        map.put("patientName", meName);
        map.put("patientEmail", meEmail);
        map.put("patientPhoto", mePhoto);
        OrdeDocReff.child(na).child("Appointments").child(ProductRandomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    OrderReff.child(userID).child("Appointments").child(ProductRandomKey).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            accountDialog.cancel();

                            confDial.show();
                            confDial.setCanceledOnTouchOutside(false);

                            ok = confDial.findViewById(R.id.ok);
                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    finish();
                                }
                            });
                        }
                    });
                }
            }
        });

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        c.set(Calendar.MONTH, month);
        selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
        date.setText(selectedDate);
    }
}