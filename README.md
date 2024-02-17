> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project is a mock server that allows for functionality in multiple ways to make queries or parsing of data. It’s a server that acts as a proxy API to the ACS survey on broadband access by counties across America. It also allows a user to load in their own data and query in their, similar to CSVParser.
# Design Choices
We make the classes by the method to we are implementing. We have four handler classes, one for viewCSV, loadCSV, SearchCSV, and Broadband. We have a server class that routes code to the proper endpoint based on the input by the user. We have a special exception class called DataSource Exception for our custom inputs.we also have a cache class that implements googles cache library.
# Errors/Bugs
There are no bugs that we know of
# Tests
Honestly, we tested as we went using the browser and running the server, and at the end of the project we did not have sufficient time to create mock data to test using unit testing.
# How to
To run the server you can call ./run in the terminal, and follow the local host link to your browser. In the search browser, you can add to the url the endpoint with which you want to use and the any parameters it requires.
