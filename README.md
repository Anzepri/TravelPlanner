Travel Planner

Prerequisites

 make sure you have the following installed:

1. Java Development Kit (JDK)

* Version: **Java 17 or higher**
* Check installation:

  ```
  java -version
  ```
Download: https://adoptium.net/

---

2. Maven

Used to build and run the project
Check installation:

  ```
  mvn -version
  ```
  Download: https://maven.apache.org/download.cgi

---

3. JavaFX (Handled by Maven)

* No manual installation required
* Automatically managed through the `pom.xml`

---

4. VS Code (Recommended)
   I am using VSCode to run this project, and using it I was able to just use maven to create it.
* Install extensions:

  * Java Extension Pack (by Microsoft)

---

▶️ How to Run

1. Clone the repository:

   ```
   git clone https://github.com/Anzepri/TravelPlanner.git
   cd TravelPlanner
   ```

2. Run the application:

   ```
   mvn clean javafx:run "-Djavafx.mainClass=com.travelplanner.Main"
   ```

---

## 💾 Notes

* The app creates the following files automatically:

  * `users.txt`
  * `trips.txt`

* These store user accounts and trip data locally.

---

## ✨ Features

* User registration and login
* Create and manage trips
* Add/edit/delete itinerary items
* Time picker for activities
* Multi-user support (each user sees their own trips)
* Data persistence using files
