package com.example.demo_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AppointmentFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference usersRef;


    public AppointmentFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_users);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firebase database reference
        usersRef = FirebaseDatabase.getInstance().getReference().child("users");

        // Fetch user data from database
        fetchAppointmentDataFromDatabase();

        return view;
    }

    // Method to fetch appointment data from the database
    private void fetchAppointmentDataFromDatabase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(),"data is not accessed from database",Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUserId = currentUser.getUid();
        DatabaseReference userAppointmentsRef = usersRef.child(currentUserId).child("appointments");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Check if the dataSnapshot is empty
                if (!dataSnapshot.exists()) {
                    Toast.makeText(getContext(), "You did not book any appointment", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Appointment> appointmentList = new ArrayList<>();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot hospitalSnapshot : userSnapshot.child("appointments").getChildren()) {
                        String hospitalName = hospitalSnapshot.getKey();
                        for (DataSnapshot appointmentSnapshot : hospitalSnapshot.getChildren()) {
                            String appointmentId = appointmentSnapshot.getKey();
                            String appointmentDateTime = appointmentSnapshot.child("appointmentDateTime").getValue(String.class);
                            String fullName = appointmentSnapshot.child("fullName").getValue(String.class);

                            // Check if all required fields are not null
                            if (appointmentDateTime != null && fullName != null) {
                                // Create an Appointment object and add it to the list
                                Appointment appointment = new Appointment(hospitalName, appointmentDateTime, fullName);
                                appointmentList.add(appointment);
                            }
                        }
                    }
                }

                // Set up RecyclerView adapter
                AppointmentAdapter adapter = new AppointmentAdapter(appointmentList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                databaseError.toException().printStackTrace();
            }
        });
    }



    // RecyclerView Adapter
    private class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {
        private List<Appointment> appointmentList;

        public AppointmentAdapter(List<Appointment> appointmentList) {
            this.appointmentList = appointmentList;
        }

        @NonNull
        @Override
        public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booked_appointment_data, parent, false);
            return new AppointmentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
            Appointment appointment = appointmentList.get(position);
            holder.bind(appointment);
        }

        @Override
        public int getItemCount() {
            return appointmentList.size();
        }

        public class AppointmentViewHolder extends RecyclerView.ViewHolder {
            private TextView textViewHospitalName;
            private TextView textViewAppointmentDateTime;
            private TextView textViewFullName;

            public AppointmentViewHolder(@NonNull View itemView) {
                super(itemView);
                textViewHospitalName = itemView.findViewById(R.id.text_hospital_name);
                textViewAppointmentDateTime = itemView.findViewById(R.id.text_appointment_datetime);
                textViewFullName = itemView.findViewById(R.id.text_full_name);
            }

            public void bind(Appointment appointment) {
                textViewHospitalName.setText("Hospital Name : " + appointment.getHospitalName());
                textViewAppointmentDateTime.setText("Date & Time: " + appointment.getAppointmentDateTime());
                textViewFullName.setText("Patient Name : " + appointment.getFullName());
            }
        }
    }

    // Appointment data model
    private class Appointment {
//        private String appointmentId;
        private String hospitalName;
        private String appointmentDateTime;
        private String fullName;

        public Appointment(String hospitalName, String appointmentDateTime, String fullName) {
//            this.appointmentId = appointmentId;
            this.hospitalName = hospitalName;
            this.appointmentDateTime = appointmentDateTime;
            this.fullName = fullName;
        }

//        public String getAppointmentId() {
//            return appointmentId;
//        }

        public String getHospitalName() {
            return hospitalName;
        }

        public String getAppointmentDateTime() {
            return appointmentDateTime;
        }

        public String getFullName() {
            return fullName;
        }
    }
}
