# Travel Planner

Travel Planner is a JavaFX desktop app for creating trips, managing itinerary items, adding friends, and sharing trips with view-only or edit permissions. Data is stored in a local PostgreSQL database.

## Features

- User registration and login
- SHA-256 password hashing
- UID system for finding and adding friends
- Trip creation and management
- Itinerary item creation, editing, and deletion
- Friend requests and friends list
- Shared trips with view-only or editable permissions
- Trip advisor role for viewing all trips
- PostgreSQL persistence

## Prerequisites

Make sure you have the following installed:

1. Java Development Kit, version 17 or higher

   ```bash
   java -version
   ```

   Download: https://adoptium.net/

2. Maven

   ```bash
   mvn -version
   ```

   Download: https://maven.apache.org/download.cgi

3. PostgreSQL and pgAdmin 4

   Download: https://www.postgresql.org/download/

4. VS Code, recommended

   Install the Java Extension Pack by Microsoft.

JavaFX is handled by Maven through `pom.xml`, so it does not need to be installed separately.

## Database Setup

1. Install PostgreSQL and pgAdmin 4.

   During installation, keep the default PostgreSQL port as `5432` and remember the password for the `postgres` user.

2. Open pgAdmin 4 and create a database named:

   ```text
   travel_planner
   ```

3. Select the `travel_planner` database, open the Query Tool, and run this schema:

   ```sql
   DROP TABLE IF EXISTS shared_trips CASCADE;
   DROP TABLE IF EXISTS friends CASCADE;
   DROP TABLE IF EXISTS friend_requests CASCADE;
   DROP TABLE IF EXISTS itinerary_items CASCADE;
   DROP TABLE IF EXISTS trips CASCADE;
   DROP TABLE IF EXISTS users CASCADE;

   CREATE TABLE users (
       user_id SERIAL PRIMARY KEY,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       role VARCHAR(50) DEFAULT 'USER',
       uid VARCHAR(20) UNIQUE
   );

   CREATE TABLE trips (
       trip_id SERIAL PRIMARY KEY,
       owner_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       trip_name VARCHAR(255),
       destination VARCHAR(255),
       start_date VARCHAR(50),
       end_date VARCHAR(50)
   );

   CREATE TABLE itinerary_items (
       item_id SERIAL PRIMARY KEY,
       trip_id INTEGER REFERENCES trips(trip_id)
           ON DELETE CASCADE,
       title VARCHAR(255),
       item_date VARCHAR(50),
       item_time VARCHAR(50),
       location VARCHAR(255)
   );

   CREATE TABLE friend_requests (
       request_id SERIAL PRIMARY KEY,
       sender_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       receiver_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       status VARCHAR(20) DEFAULT 'PENDING'
   );

   CREATE TABLE friends (
       friendship_id SERIAL PRIMARY KEY,
       user1_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       user2_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE
   );

   CREATE TABLE shared_trips (
       shared_id SERIAL PRIMARY KEY,
       trip_id INTEGER REFERENCES trips(trip_id)
           ON DELETE CASCADE,
       owner_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       shared_with_id INTEGER REFERENCES users(user_id)
           ON DELETE CASCADE,
       can_edit BOOLEAN DEFAULT FALSE
   );
   ```

4. Update the database password in `src/main/java/com/travelplanner/DatabaseConnection.java` if needed:

   ```java
   private static final String URL = "jdbc:postgresql://localhost:5432/travel_planner";
   private static final String USER = "postgres";
   private static final String PASSWORD = "YourPasswordHere";
   ```

## Run the App

Clone the repository:

```bash
git clone https://github.com/Anzepri/TravelPlanner.git
cd TravelPlanner
```

Run the JavaFX application:

```bash
mvn clean javafx:run "-Djavafx.mainClass=com.travelplanner.Main"
```

The `javafx-maven-plugin` is already configured with `com.travelplanner.Main`, so this may also work:

```bash
mvn javafx:run
```

## Troubleshooting

Password authentication failed:

- Check the username and password in `DatabaseConnection.java`.
- Confirm the PostgreSQL password you set during installation.

Database `travel_planner` does not exist:

- Create the database in pgAdmin 4.

Relation or table does not exist:

- Run the full database schema again in the `travel_planner` database.

Port `5432` refused:

- Make sure the PostgreSQL service is running.
- Reopen pgAdmin 4 and confirm the server is connected.

## Notes

- PostgreSQL is required for the current version of the project.
- `DatabaseTest.java` can be used to verify PostgreSQL connectivity.
- Local text files such as `users.txt` and `trips.txt` are from the earlier file-based version and are not the primary data store now.
