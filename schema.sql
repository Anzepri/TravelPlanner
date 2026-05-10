DROP TABLE IF EXISTS shared_access;
DROP TABLE IF EXISTS expenses;
DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS itinerary_items;
DROP TABLE IF EXISTS trips;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trips (
    trip_id SERIAL PRIMARY KEY,
    owner_id INT NOT NULL,
    trip_name VARCHAR(100) NOT NULL,
    destination VARCHAR(100),
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE TABLE itinerary_items (
    item_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    item_date DATE,
    item_time TIME,
    location VARCHAR(150),
    notes TEXT,

    CONSTRAINT fk_itinerary_trip
        FOREIGN KEY (trip_id)
        REFERENCES trips(trip_id)
        ON DELETE CASCADE
);

CREATE TABLE budgets (
    budget_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    total_budget NUMERIC(10,2) NOT NULL,

    CONSTRAINT fk_budget_trip
        FOREIGN KEY (trip_id)
        REFERENCES trips(trip_id)
        ON DELETE CASCADE
);

CREATE TABLE expenses (
    expense_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    description VARCHAR(150),
    amount NUMERIC(10,2) NOT NULL,
    expense_date DATE DEFAULT CURRENT_DATE,

    CONSTRAINT fk_expense_trip
        FOREIGN KEY (trip_id)
        REFERENCES trips(trip_id)
        ON DELETE CASCADE
);

CREATE TABLE shared_access (
    access_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    user_id INT NOT NULL,
    permission_level VARCHAR(20) NOT NULL CHECK (permission_level IN ('VIEWER', 'EDITOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shared_trip
        FOREIGN KEY (trip_id)
        REFERENCES trips(trip_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shared_user
        FOREIGN KEY (user_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_trip_user_access
        UNIQUE (trip_id, user_id)
)