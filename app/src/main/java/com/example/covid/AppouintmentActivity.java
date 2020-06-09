package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Orders;
import ViewHolder.OrderViewHolder;

public class AppouintmentActivity extends AppCompatActivity {

    private RecyclerView appointments;
    private DatabaseReference orderReff, removeReff;
    private FirebaseUser user;
    private String name, email, id;
    private TextView defaultMess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appouintment);

        user = FirebaseAuth.getInstance().getCurrentUser();
        appointments = findViewById(R.id.appointments);
        id = user.getUid();

        defaultMess = findViewById(R.id.defaultMess);

        orderReff = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Appointments");
        removeReff = FirebaseDatabase.getInstance().getReference().child("Doctors");
        appointments.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        appointments.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Orders> options = new FirebaseRecyclerOptions.Builder<Orders>()
                .setQuery(orderReff, Orders.class).build();

        FirebaseRecyclerAdapter<Orders, OrderViewHolder> adapter = new FirebaseRecyclerAdapter<Orders, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, int position, @NonNull final Orders model) {
                defaultMess.setVisibility(View.INVISIBLE);
                appointments.setVisibility(View.VISIBLE);

                holder.date.setText(model.getDate());
                holder.docName.setText(model.getDoctorName());
                Glide.with(holder.docAvatar).load(model.getDoctorPhoto()).into(holder.docAvatar);
                holder.cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderReff.child(model.getOrderID()).removeValue();
                        removeReff.child(model.getDoctorID()).child("Appointments").child(model.getOrderID()).removeValue();
                    }
                });
            }

            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_layout, parent,false);
                OrderViewHolder holder = new OrderViewHolder(view);
                return holder;
            }
        };
        appointments.setAdapter(adapter);
        adapter.startListening();
    }
}