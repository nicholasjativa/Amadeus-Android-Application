# Amadeus Android Application

This project is the Android application for one of my person projects, called Amadeus. The purpose of this project is to be an almost exact clone of the iMessage application that currently exists for Macs/iPhones. The application code is written in Java, since I was already at an intermediate level of experience with it, and I do not wish to learn Kotlin yet. The application uses Google Cloud Messaging to receive push notifications from the Amadeus Server. I also use the Volley framework for making HTTP requests.

## Features

The main use of the application is that it establishes a long-running Android service in the background, which listens for incoming text messages that the device receives, and takes this information (sender phone number and message body) and sends it to the server. Moreover, the application also listens for push notifications that it receives from the server, and creates outgoing text messages based on the payload it receives from the server's push notification. As such, there is no real UI that the application needs, at least for now.

## High-Level Description of Amadeus

The Amadeus Front-End web application serves as an interface for users (currently only myself) to create text messages from a web-browser. From the web application, the "text message" gets sent to the back-end Node/Express server, and once the server receives it, it sends a push notification down to the user's (my) Android device. The Android device must have the Amadeus Android app installed. Once the Android app receives the push notification, it parses the payload (which has two notable properties: the text message body and the phone number for which this message must go to). It then programatically creates and transmits a real text message to the designated phone number with the designated message body.

Amadeus works bi-directionally; a user can use the web app to create text messages to be sent out, and the web app also receives real-time text messages that the user's phone receives. I.e., when an Android device receives a text message, the Amadeus Android app will be listening for this event, and contact the back-end server with the time and date, phone number of the sender, and the message payload. From here, the server stores the message in a MySQL database, and also sends the web app a WebSocket message to let the user know that they have a received a text message. The web app then updates that particular conversation with the new message.

## Screenshots

Application Home Screen
![Application Home Screen](http://i68.tinypic.com/5lp6aq.jpg)
