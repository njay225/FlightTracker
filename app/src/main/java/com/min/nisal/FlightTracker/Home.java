package com.min.nisal.FlightTracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1beta1.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class Home extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    private TextView logoutButton;


    //Views in Popup
    private EditText originEditText, destinationEditText, timeEditText, dateEditText, flightNoEditText
            , referenceEditText;

    private Calendar dateCalendar = Calendar.getInstance();

    public Long finaldate = null;

    //Adapter
    RecyclerView recyclerView;
    FlightAdapter adapter;

    List<Flight> flightList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get User
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        //Initialise FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

                View flight_popup_view = getLayoutInflater().inflate(R.layout.add_flight_popup, null);
                builder.setView(flight_popup_view).setNegativeButton("Close", null);

                originEditText = flight_popup_view.findViewById(R.id.originEditText);
                destinationEditText = flight_popup_view.findViewById(R.id.destinationEditText);
                timeEditText = flight_popup_view.findViewById(R.id.timeEditText);
                dateEditText = flight_popup_view.findViewById(R.id.dateEditText);
                flightNoEditText = flight_popup_view.findViewById(R.id.flightNoEditText);
                referenceEditText = flight_popup_view.findViewById(R.id.referenceEditText);

                final String[] origin = new String[1];
                final String[] destination = new String[1];
                final String[] time = new String[1];
                final String[] flightNo = new String[1];
                final String[] reference = new String[1];

                dateEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateCalendar.set(Calendar.YEAR, year);
                                dateCalendar.set(Calendar.MONTH, month);
                                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String dateFormat = "dd/MM/YY";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                                dateEditText.setText(simpleDateFormat.format(dateCalendar.getTime()));
                                finaldate = dateCalendar.getTimeInMillis();
                            }
                        };
                        DatePickerDialog dialog = new DatePickerDialog(Home.this, date, dateCalendar.get(Calendar.YEAR)
                                , dateCalendar.get(Calendar.MONTH), dateCalendar.get(Calendar.DAY_OF_MONTH));

                        dialog.getDatePicker().setMinDate(new Date().getTime());

                        dialog.show();
                    }
                });

                timeEditText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePicker;
                        int hour = dateCalendar.get(Calendar.HOUR_OF_DAY);
                        int minute = dateCalendar.get(Calendar.MINUTE);
                        timePicker = new TimePickerDialog(Home.this, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if(minute<10){
                                    time[0] = hourOfDay + ":0" + minute;
                                }else{
                                    time[0] = hourOfDay + ":" + minute;
                                }
                                timeEditText.setText(time[0]);
                            }
                        }, hour, minute, true);
                        timePicker.show();
                    }
                });


                builder.setPositiveButton("Add", null);
                builder.setTitle("Add New Flight");

                final AlertDialog alertDialog = builder.create();

                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Toast.makeText(Home.this, "Shown", Toast.LENGTH_SHORT).show();
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                origin[0] = originEditText.getText().toString();
                                destination[0] = destinationEditText.getText().toString();
                                flightNo[0] = flightNoEditText.getText().toString();
                                reference[0] = referenceEditText.getText().toString();
                                Boolean infoValid = true;

                                ColorStateList greyColour = ColorStateList.valueOf(Color.parseColor("#777777"));
                                ColorStateList redColour = ColorStateList.valueOf(Color.RED);

                                if(origin[0].length() == 0){
                                    infoValid = false;
                                    ViewCompat.setBackgroundTintList(originEditText, redColour);
                                    originEditText.setHintTextColor(Color.RED);
                                    originEditText.setHint("Enter Origin");
                                }else{
                                    ViewCompat.setBackgroundTintList(originEditText, greyColour);
                                }

                                if(destination[0].length() == 0){
                                    infoValid = false;
                                    ViewCompat.setBackgroundTintList(destinationEditText, redColour);
                                    destinationEditText.setHintTextColor(Color.RED);
                                    destinationEditText.setHint("Enter Destination");
                                }else{
                                    ViewCompat.setBackgroundTintList(destinationEditText, greyColour);
                                }

                                if(flightNo[0].length() == 0){
                                    infoValid = false;
                                    ViewCompat.setBackgroundTintList(flightNoEditText, redColour);
                                    flightNoEditText.setHintTextColor(Color.RED);
                                    flightNoEditText.setHint("Enter Flight No");
                                }else{
                                    ViewCompat.setBackgroundTintList(flightNoEditText, greyColour);
                                }

                                if(time[0] == null){
                                    infoValid = false;
                                    ViewCompat.setBackgroundTintList(timeEditText, redColour);
                                    timeEditText.setHintTextColor(Color.RED);
                                    timeEditText.setHint("Enter Time");
                                }else{
                                    ViewCompat.setBackgroundTintList(timeEditText, greyColour);
                                }

                                if(finaldate == null){
                                    infoValid = false;
                                    ViewCompat.setBackgroundTintList(dateEditText, redColour);
                                    dateEditText.setHintTextColor(Color.RED);
                                    dateEditText.setHint("Enter Date");
                                }else{
                                    ViewCompat.setBackgroundTintList(dateEditText, greyColour);
                                }


                                if(infoValid == false){
                                    Toast.makeText(Home.this, "Please Enter Valid Information", Toast.LENGTH_SHORT).show();
                                }else{
                                    if(reference[0].length() == 0){
                                        reference[0] = "-";
                                    }

                                    Flight flight = new Flight(origin[0], destination[0], time[0], finaldate, flightNo[0], reference[0], firebaseUser.getUid());

                                    db.collection("Flights")
                                            .add(flight)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(Home.this, "Flight Added!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
                    }
                });


                alertDialog.show();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        flightList = new ArrayList<>();


        db.collection("Flights")
                .whereEqualTo("user", firebaseUser.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        flightList = new ArrayList<>();
                        Log.d("Hello", "onEvent: " + queryDocumentSnapshots.getDocuments().get(0).getData().toString());


                        for(int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++){

                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(i);


                            String origin, destination, time, user, ref, flightNo;
                            Long date;

                            origin = document.getData().get("origin").toString();
                            destination = document.getData().get("destination").toString();
                            flightNo = document.getData().get("flightNo").toString();
                            time = document.getData().get("time").toString();
                            user = document.getData().get("user").toString();
                            ref = document.getData().get("reference").toString();
                            date = Long.valueOf((String.valueOf(document.getData().get("date"))));

                            Long currentDate = new Date().getTime();

                            if(currentDate < date + 43200000L){
                                flightList.add(new Flight(origin, destination, time, date, flightNo, ref, user));
                            }


                        }

                        Collections.sort(flightList, new Comparator<Flight>() {
                            @Override
                            public int compare(Flight o1, Flight o2) {
                                return o1.getDate().compareTo(o2.getDate());
                            }
                        });

                        adapter = new FlightAdapter(Home.this, flightList);
                        recyclerView.setAdapter(adapter);
                    }
                });


        logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);

                builder.setTitle("Logout").setMessage("Are you sure?").setNegativeButton("No", null)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAuth.signOut();
                                Intent intent = new Intent(Home.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).show();

            }
        });

    }

}
