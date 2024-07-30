package com.example.demo_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class hospital_detail_page extends AppCompatActivity {

    private Spinner Gender;
    private EditText editTextAddress,editTextFullName,editTextProblem,editTextAge,textViewSelectedDate;
    private ImageView imageViewDatePicker;
    private TextView textViewHospitalName,textViewDoctorName,textViewSpeciality,textViewHospitalAddress;
    private Button buttonBookAppointment,buttonConfirmBooking;
    private RelativeLayout form_for_booking_appointment;
    private Calendar calendar;
    private String popUpContent,doctorName,hospitalName,speciality,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_detail_page);

        Intent intent = getIntent();
        doctorName = intent.getStringExtra("doctorName");
        hospitalName = intent.getStringExtra("hospitalName");
        speciality = intent.getStringExtra("speciality");
        address = intent.getStringExtra("address");

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextAge = findViewById(R.id.editTextAge);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextProblem = findViewById(R.id.editTextProblem);

        Gender = findViewById(R.id.Gender);
        imageViewDatePicker = findViewById(R.id.imageViewDatePicker);

        textViewHospitalName = findViewById(R.id.textViewHospitalName);
        textViewDoctorName = findViewById(R.id.textViewDoctorName);
        textViewSpeciality = findViewById(R.id.textViewSpeciality);
        textViewHospitalAddress = findViewById(R.id.textViewHospitalAddress);

        buttonBookAppointment = findViewById(R.id.buttonBookAppointment);
        buttonConfirmBooking = findViewById(R.id.buttonConfirmBooking);

        form_for_booking_appointment = findViewById(R.id.form_for_booking_appointment);

        calendar = Calendar.getInstance();


        popUpContent = getIntent().getStringExtra("hospitalName");
        textViewHospitalName.setText(popUpContent);
        textViewDoctorName.setText(getString(R.string.doctor_name_format, doctorName));
        textViewSpeciality.setText(getString(R.string.doctor_speciality, speciality));
        textViewHospitalAddress.setText(getString(R.string.hospital_address, address));

        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog();
            }
        });
        buttonBookAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                form_for_booking_appointment.setVisibility(View.VISIBLE);
            }
        });

        buttonConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmBooking();
            }
        });
    }
    private void showDateTimePickerDialog() {
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        // Calculate the maximum date as six months from the current date
        Calendar maxDateCalendar = Calendar.getInstance();
        maxDateCalendar.setTimeInMillis(System.currentTimeMillis());
        maxDateCalendar.add(Calendar.MONTH, 6);

        // Date Picker Dialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int monthOfYear, int dayOfMonth) {
                calendar.set(selectedYear, monthOfYear, dayOfMonth);
                // Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(hospital_detail_page.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                updateEditText();
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDateCalendar.getTimeInMillis());
        datePickerDialog.show();
    }


    private void updateEditText() {
        String myFormat = "dd/MM/yyyy HH:mm"; // Define your format here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        textViewSelectedDate.setText(sdf.format(calendar.getTime()));
    }

    private void confirmBooking() {
        String fullName = editTextFullName.getText().toString();
        String age = editTextAge.getText().toString();
        String gender = Gender.getSelectedItem().toString();
        String appointmentDateTime = textViewSelectedDate.getText().toString();
        String address = editTextAddress.getText().toString();
        String problem = editTextProblem.getText().toString();

        Map<String, Object> bookedAppointment = new HashMap<>();
        bookedAppointment.put("fullName", fullName);
        bookedAppointment.put("age", age);
        bookedAppointment.put("gender", gender);
        bookedAppointment.put("appointmentDateTime", appointmentDateTime);
        bookedAppointment.put("address", address);
        bookedAppointment.put("problem", problem);

        if (fullName.isEmpty() || age.isEmpty() || gender.isEmpty() || appointmentDateTime.isEmpty() || address.isEmpty() || problem.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users");

                String appointmentKey = mDatabase.child(userId).child("appointments").child(popUpContent).push().getKey();
                DatabaseReference appointmentRef = mDatabase.child(userId).child("appointments").child(popUpContent).child(Objects.requireNonNull(appointmentKey));

                appointmentRef.updateChildren(bookedAppointment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            editTextFullName.setText("");
                            editTextAge.setText("");
                            editTextAddress.setText("");
                            textViewSelectedDate.setText("");
                            editTextProblem.setText("");

                            AppointmentFragment appointmentFragment = new AppointmentFragment();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fl_main,appointmentFragment).commit();

                        } else {
                            Toast.makeText(hospital_detail_page.this, "Failed to store data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
