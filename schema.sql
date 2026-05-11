DROP TABLE IF EXISTS shared_trips;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS friend_requests;
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
    uid VARCHAR(20) UNIQUE NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
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

CREATE TABLE friend_requests (
    request_id SERIAL PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_sender
        FOREIGN KEY (sender_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_request_receiver
        FOREIGN KEY (receiver_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_friend_request
        UNIQUE (sender_id, receiver_id)
);

CREATE TABLE friends (
    friend_id SERIAL PRIMARY KEY,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_friend_user1
        FOREIGN KEY (user1_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_friend_user2
        FOREIGN KEY (user2_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_friend_pair
        UNIQUE (user1_id, user2_id),

    CONSTRAINT no_self_friend
        CHECK (user1_id <> user2_id)
);

CREATE TABLE shared_trips (
    shared_trip_id SERIAL PRIMARY KEY,
    trip_id INT NOT NULL,
    owner_id INT NOT NULL,
    shared_with_id INT NOT NULL,
    can_edit BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_shared_trip_trip
        FOREIGN KEY (trip_id)
        REFERENCES trips(trip_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shared_trip_owner
        FOREIGN KEY (owner_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shared_trip_user
        FOREIGN KEY (shared_with_id)
        REFERENCES users(user_id)
        ON DELETE CASCADE,

    CONSTRAINT unique_shared_trip_user
        UNIQUE (trip_id, shared_with_id)
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
