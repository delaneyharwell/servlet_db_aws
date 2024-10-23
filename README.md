# Database-Connected Java Servlet with HTML Frontend

## Project Overview

This project is a full-stack web application that integrates a Java Servlet backend with a simple HTML frontend, allowing for seamless interaction with a backend database. The frontend, built using HTML, serves as the user interface where users can submit requests, which are then handled by the Java Servlet. The servlet processes these requests, communicates with the database to retrieve or update information, and returns appropriate responses to the client. The backend uses MySQL for data storage, and all communication between the servlet and database is handled efficiently through JDBC. This project showcases the essential components of a database-driven web application and provides a robust platform for learning about web development, servlet functionality, and database integration.

## Features

The project has several key features. First, the HTML frontend (index.html) provides a basic interface for users to interact with the system. The Java Servlet then processes user inputs, forwards the requests to the database, and dynamically updates the interface based on the results. The database integration allows for operations like data retrieval, updates, and inserts, ensuring the system functions as a dynamic, real-time platform. Additionally, the application is designed to be scalable, making it an ideal candidate for cloud deployment.

## Technology Stack

The project leverages multiple technologies to create a complete web application. The frontend is designed using HTML and CSS, while the backend is built with Java Servlets running on an Apache Tomcat server. The database is managed using MySQL, and JDBC is used for communication between the servlet and the database. The application is deployed on Amazon Web Services (AWS) to ensure scalability and accessibility.

## Deployment on AWS

The project is deployed on AWS to take advantage of the cloud's scalability and reliability. The deployment process involves creating an EC2 instance for hosting the web server (Apache Tomcat) and setting up an AWS RDS instance for the MySQL database. The application files, including the HTML frontend and Java Servlet, are uploaded to the Tomcat web server, and the servlet is configured to communicate with the RDS-hosted database. The database credentials and connection settings are stored securely, and the security groups on AWS are configured to allow HTTP and database traffic between the EC2 instance and the RDS database. Once deployed, the application can be accessed via the public IP address of the EC2 instance, enabling users to interact with the web interface and perform database operations in real time.

## Conclusion

This project effectively demonstrates the integration of a Java Servlet with a frontend and backend database, providing a complete solution for a web-based application. Its deployment on AWS highlights its scalability and potential for handling larger traffic in cloud environments. This project can be further improved by adding features like user authentication, enhanced error handling, and integrating additional cloud services to enhance functionality.
