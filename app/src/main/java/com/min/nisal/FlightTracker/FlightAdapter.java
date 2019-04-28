package com.min.nisal.FlightTracker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {

    private Context mCtx;
    private List<Flight> flightList;

    public FlightAdapter(Context mCtx, List<Flight> flightList){
        this.mCtx = mCtx;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.flight_card, null);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FlightViewHolder flightViewHolder, int i) {
        final Flight flight = flightList.get(i);

        flightViewHolder.titleTextView.setText(flight.getOrigin() + " ➞ " + flight.getDestination());
        flightViewHolder.referenceTextView.setText("Reference: " + flight.getReference());
        flightViewHolder.flightNoTextView.setText(flight.getFlightNo());

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");

        String date = dateFormat.format(flight.getDate());

        flightViewHolder.timeAndDateTextView.setText(flight.getTime() + "    " + date);

        if(flight.getFlightNo().toLowerCase().startsWith("jq")){
            flightViewHolder.flightImageView.setImageResource(R.drawable.jq);
        }else if(flight.getFlightNo().toLowerCase().startsWith("d7")){
            flightViewHolder.flightImageView.setImageResource(R.drawable.d7);
        }else{
            flightViewHolder.flightImageView.setImageResource(R.drawable.stock);
        }

        Long currentDate = new Date().getTime();

        String currentDateString = dateFormat.format(currentDate);

       if(currentDateString.equals(date)){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Document doc = Jsoup.connect("https://www.google.com/search?q=" + flight.getFlightNo() + "&newwindow=1&gbv=1&sei=BbdGXKXTHZDw9QOKt57gAg")
                                .userAgent("Mozilla/5.0 (Windows; U; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)")
                                .get();
                        Log.d("HELLO", doc.toString());
                        final Elements negativeStatuses = doc.getElementsByClass("fIP9ce");

                        ((Activity)mCtx).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(negativeStatuses.size() > 0){
                                    //flightViewHolder.statusTexView.setText(negativeStatuses.get(0).text());
                                    flightViewHolder.statusTexView.setTextColor(Color.RED);
                                }else{
                                    flightViewHolder.statusTexView.setTextColor(Color.GREEN);
                                }
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    class FlightViewHolder extends RecyclerView.ViewHolder {

        ImageView flightImageView;
        TextView titleTextView, flightNoTextView, timeAndDateTextView, referenceTextView, statusTexView;


        public FlightViewHolder(View itemView){
            super(itemView);

            flightImageView = itemView.findViewById(R.id.flightImageView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            flightNoTextView = itemView.findViewById(R.id.flightNoTextView);
            referenceTextView = itemView.findViewById(R.id.referenceTextView);
            timeAndDateTextView = itemView.findViewById(R.id.timeAndDateTextView);
            statusTexView = itemView.findViewById(R.id.statusTextView);

        }
    }


}
