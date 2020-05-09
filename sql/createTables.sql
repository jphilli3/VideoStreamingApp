CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username varchar(50) NOT NULL,
    password varchar(100) NOT NULL
);

CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    streamid varchar(50) NOT NULL,
    from_user varchar(50) NOT NULL,
    user_message varchar(2000) NOT NULL,
    message_time varchar(20) NOT NULL
);