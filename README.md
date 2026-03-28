# Overview

The Java expense tracker demonstrates how to implement a Java app with a Graphic User Interface using JavaFX and the manipulation of persistent data using a SQLite database.

This software follows a model view controller architecture, also isolating the interaction with the SQLite base in the Repository class and the services class delegates to the repository class and connect with the controller layer, demonstrating a modular construction, following that pattern, the layout is contained in the dashboard.fxml file that is styled using a css file. This software allows the user to add, edit and delete expenses, also the user can track the expenses by category, evolution by month and evolution by days in the different charts. Java specific libraries are used to generate the charts.

The purpose of this software is to give the user the abillity of record and manage his daily expenses with a good UX and to see analize the evolution and composition of his expenses to take better desitions of how to use his money more efficiently.  


Software Demo Video: https://youtu.be/iTLZxxEfchs

# Development Environment

The software was developed using VS code with the Java extensions: Extension Pack for Java, JavaFX Support, Maven for Java, FXML Support, SQLite Viewer. Also I had to install javaJDK 21 and Apache Maven.

The program is writen in Java mostly, also a pom.xml file is used to manage dependencies using Maven and a CSS file is used to style the layout and the fxml layout uses xml too.

# Useful Websites


- Official Java Api documentation: https://docs.oracle.com/en/java/javase/21/docs/api/index.html

- Official JavaFX documentation: https://openjfx.io/

# Future Work



- Data analysis enhaced with calculations for average of expense by category and track of patterns.

- Taxes calculations integrated.


- Savings plan function.