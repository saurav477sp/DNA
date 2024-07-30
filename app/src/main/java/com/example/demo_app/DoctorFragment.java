package com.example.demo_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DoctorFragment extends Fragment {

    private LinearLayout cardContainer;
    private DatabaseReference mDatabase;

    public DoctorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_doctor, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("doctors");

        Spinner spinnerDoctorSpecialties = rootView.findViewById(R.id.spinner_doctor_specialties);
        cardContainer = rootView.findViewById(R.id.card_container);
        cardContainer.removeAllViews();

        // Populate spinner with data from string array resource
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.specialities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctorSpecialties.setAdapter(adapter);

        spinnerDoctorSpecialties.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    String selectedSpeciality = parent.getItemAtPosition(position).toString();
                    retrieveDoctorsBySpecialty(selectedSpeciality);
                } else {
                    Toast.makeText(getContext(),"Please Select Any Speciality",Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    private void retrieveDoctorsBySpecialty(String specialty) {
        DatabaseReference doctorsRef = mDatabase;

        doctorsRef.orderByChild("profile/specialty").equalTo(specialty)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        List<DataSnapshot> doctorSnapshots = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            doctorSnapshots.add(snapshot);
                        }
                        displayDoctorsData(doctorSnapshots);
                        Toast.makeText(getContext(),""+doctorSnapshots, Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "" + databaseError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayDoctorsData(List<DataSnapshot> doctorSnapshots) {
        for (DataSnapshot snapshot : doctorSnapshots) {
            String doctorName = snapshot.child("profile/name").getValue(String.class);
            String hospitalName = snapshot.child("profile/hospitalName").getValue(String.class);
            String rating = snapshot.child("profile/rating").getValue(String.class);
            String address = snapshot.child("profile/address").getValue(String.class);
            String speciality = snapshot.child("profile/specialty").getValue(String.class);

            generateCardView(speciality,doctorName, hospitalName, rating, address);
        }
    }

    private void generateCardView(String specialty, String doctorName, String hospitalName, String rating, String address) {
        // Create a CardView for each doctor
        Resources res = getResources();
        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT // Changed height to wrap_content
        );
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, margin, margin, margin);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        cardView.setContentPadding(padding, padding, padding, padding);

        cardView.setLayoutParams(layoutParams);
        cardView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.background_for_cardView)));
        cardView.setCardElevation(6);
        cardView.setRadius(8);



        LinearLayout cardLayout = new LinearLayout(requireContext());
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView specialtyTextView = new TextView(requireContext());
        LinearLayout.LayoutParams specialtyLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        specialtyTextView.setLayoutParams(specialtyLayoutParams);
        specialtyTextView.setText(String.format(res.getString(R.string.specialty), specialty));
        specialtyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        specialtyTextView.setTextColor(Color.BLACK);
        cardLayout.addView(specialtyTextView);

        TextView doctorNameTextView = new TextView(requireContext());
        LinearLayout.LayoutParams doctorNameLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        doctorNameTextView.setLayoutParams(doctorNameLayoutParams);
        doctorNameTextView.setText(String.format(res.getString(R.string.doctor_name), doctorName));
        doctorNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        doctorNameTextView.setTextColor(Color.BLACK);
        cardLayout.addView(doctorNameTextView);

        TextView hospitalNameTextView = new TextView(requireContext());
        LinearLayout.LayoutParams hospitalNameLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        hospitalNameTextView.setLayoutParams(hospitalNameLayoutParams);
        hospitalNameTextView.setText(String.format(res.getString(R.string.hospital_name), hospitalName));
        hospitalNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        hospitalNameTextView.setTextColor(Color.BLACK);
        cardLayout.addView(hospitalNameTextView);

        // Add Rating TextView
        TextView ratingTextView = new TextView(requireContext());
        LinearLayout.LayoutParams ratingLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        ratingTextView.setLayoutParams(ratingLayoutParams);
        ratingTextView.setText(String.format(res.getString(R.string.rating), rating));
        ratingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        ratingTextView.setTextColor(Color.BLACK);
        cardLayout.addView(ratingTextView);

        // Add Address TextView
        TextView addressTextView = new TextView(requireContext());
        LinearLayout.LayoutParams addressLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        addressTextView.setLayoutParams(addressLayoutParams);
        addressTextView.setText(String.format(res.getString(R.string.address), address));
        addressTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        addressTextView.setTextColor(Color.BLACK);
        cardLayout.addView(addressTextView);

        LinearLayout buttonLayout = new LinearLayout(requireContext());
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button bookAppointmentButton = new Button(requireContext());
        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f // Set weight to 1 to make the buttons share the space equally
        );
        buttonLayoutParams.topMargin = margin;
        bookAppointmentButton.setLayoutParams(buttonLayoutParams);
        bookAppointmentButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Book_Appointment_button)));
        bookAppointmentButton.setText(R.string.book_appointment);
        buttonLayout.addView(bookAppointmentButton);
        bookAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to the new activity and pass details as extras
                Intent intent = new Intent(getContext(), hospital_detail_page.class);
                intent.putExtra("doctorName", doctorName);
                intent.putExtra("hospitalName", hospitalName);
                intent.putExtra("speciality", specialty);
                intent.putExtra("address", address);
                startActivity(intent);
            }
        });

        Button feedbackButton = new Button(requireContext());
        feedbackButton.setLayoutParams(buttonLayoutParams);
        feedbackButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.Feedback_button)));
        feedbackButton.setText(R.string.feedback);
        buttonLayout.addView(feedbackButton);
        feedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Rate out of 5");

                // Inflate the layout for the dialog
                View view = getLayoutInflater().inflate(R.layout.rating_bar, null);
                builder.setView(view);

                // Find rating bar in the layout
                RatingBar ratingBar = view.findViewById(R.id.ratingBar);

                // Set a listener for the rating bar
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        // You can handle the rating change here if needed
                    }
                });

                // Add OK button to the dialog
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the rating selected by the user
                        float rating = ratingBar.getRating();
                        // You can do something with the rating here, such as sending it to a server
                    }
                });

                // Add Cancel button to the dialog
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if Cancel is clicked
                        dialog.dismiss();
                    }
                });

                // Show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        cardLayout.addView(buttonLayout); // Add button layout to card layout

        cardView.addView(cardLayout);
        cardContainer.addView(cardView);
    }


}