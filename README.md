If hard to read or copy, the Readme SQL setup pgadmin4.pdf has a clearer version of the instructions. 

Travel Planner - PostgreSQL / pgAdmin4 Setup Guide 
1. Install PostgreSQL + pgAdmin4 
Download and install PostgreSQL: 
•  
https://www.postgresql.org/download/ 
During installation: 
•  
Remember your PostgreSQL password •  
Keep the default port: 
•  
5432 
pgAdmin4 is usually installed automatically with PostgreSQL. 
2. Create the Database Open: 
pgAdmin4 
Then: 

1.  
Expand: 

Servers 
→ PostgreSQL → Databases 

1.  
Right click: 

Databases 
→ Create 
→ Database 
1.  
Create database: travel_planner 

1.  
1
Press: 

Save 
3. Open Query Tool 
Right click: 
travel_planner 
→ Query Tool 

4. Run the Full Schema 
Copy and run ALL of the SQL below. 
CREATE TABLE users ( 
user_id SERIAL PRIMARY KEY, 
username VARCHAR(255) UNIQUE NOT NULL, 
 VARCHAR(255) NOT NULL, 
role VARCHAR(50) DEFAULT 'USER', 
uid VARCHAR(20) UNIQUE 
); 


CREATE TABLE trips ( 
trip_id SERIAL PRIMARY KEY, 
trip_name VARCHAR(255) NOT NULL, 
destination VARCHAR(255) NOT NULL, 
start_date VARCHAR(50) NOT NULL, 
end_date VARCHAR(50) NOT NULL, 
owner_email VARCHAR(255) 
); 


CREATE TABLE itinerary_items ( 
item_id SERIAL PRIMARY KEY, 
trip_id INTEGER REFERENCES trips(trip_id) ON DELETE CASCADE, title VARCHAR(255) NOT NULL, 
item_date VARCHAR(50) NOT NULL, 
item_time VARCHAR(50) NOT NULL, 
location VARCHAR(255) 
); 


CREATE TABLE friend_requests ( 
request_id SERIAL PRIMARY KEY, 
sender_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, receiver_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, 
2
status VARCHAR(20) DEFAULT 'PENDING' 
); 


CREATE TABLE friends ( 
friendship_id SERIAL PRIMARY KEY, 
user1_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, user2_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE ); 


CREATE TABLE shared_trips ( 
shared_trip_id SERIAL PRIMARY KEY, 
trip_id INTEGER REFERENCES trips(trip_id) ON DELETE CASCADE, owner_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, shared_with_id INTEGER REFERENCES users(user_id) ON DELETE CASCADE, can_edit BOOLEAN DEFAULT FALSE 
); 


Press: 
Execute Script 
or: 
F5 


5. Generate User UIDs Automatically Run this SQL AFTER the main schema: 
CREATE OR REPLACE FUNCTION generate_uid() 
RETURNS TRIGGER AS $$ 
BEGIN 
NEW.uid := 'USR' || LPAD(NEW.user_id::TEXT, 5, '0'); 

RETURN NEW; 

END; 

$$ LANGUAGE plpgsql; 

CREATE TRIGGER user_uid_trigger 
AFTER INSERT ON users 
FOR EACH ROW 
EXECUTE FUNCTION generate_uid(); 

Press: 
F5 

6. Update DatabaseConnection.java Open: 
src/main/java/com/travelplanner/DatabaseConnection.java Update: 
private static final String URL = 
"jdbc:postgresql://localhost:5432/travel_planner";

private static final String USER = 
"postgres"; 

private static final String  = 
"YourPasswordHere"; 

Replace: 
YourPasswordHere 
with your own PostgreSQL password. 
8. Run the Application 
Open the project in IntelliJ. 
Run: 
Main.java 
9. First Time Testing 
Recommended test flow: 
1. Register User 1 
4

2.  3.  4.  
Register User 2 Login as User 1 

Create a trip 
5.  
Add itinerary activities 6.  
Send friend request 

7.  8.  
Login as User 2 

Accept request 
9.  
Share trip 
10.  
Test VIEW ONLY permissions 
11.  
Test CAN EDIT permissions 
9. Important Notes DO NOT SHARE: 

•  •  •  •  
PostgreSQL passwords .idea/ 
target/ 
out/ 

10. Recommended .gitignore 
# Build files 
target/ 
out/ 
*.class 
*.log 
# IDE files 
.idea/ 
.vscode/ 
# macOS 
.DS_Store 
# Legacy local storage files 
users.txt 
trips.txt 
# Environment / secrets 
.env 
5
11. Common Errors 
"Connection refused" 
Make sure PostgreSQL server is running. 
"password authentication failed" Incorrect PostgreSQL password in: 
DatabaseConnection.java 
"database travel_planner does not exist" You forgot to create the database. 
"relation already exists" 
The tables already exist. 
This is safe. 
12. Project Features 
The project currently supports: 

•  •  •  
User registration/login PostgreSQL persistence 

Trip creation 
•  
Itinerary management 

•  •  •  
Friend requests Friends list 

Shared trips 
•  
View-only permissions 

•  •  
Editable shared trips 

Collaboration system 

•  
Modern JavaFX UI 

END OF README 
6
