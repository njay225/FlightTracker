# FlightTracker
Android Application to track flights and bookings.

How it works?

A user first logs into the app using their email and password. Then they go to the home screen, from there they can add a flight
that they want to manage. This is then saved to a database (Firebase), and then the user can see a list of all their flights. On
the day of the flight, the app will search for their flight number to inform the user whether the flight is on time, delayed or 
cancelled.

Backend done using Firebase:
  Firebase Auth
  Firebase Firestore

Flights searched for using:
  Jsoup to webscrape a Google Search of the flight No.
