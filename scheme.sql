CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE english_word (
                              en_id SERIAL PRIMARY KEY,
                              word VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE russian_word (
                              ru_id SERIAL PRIMARY KEY,
                              word VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE participant (
                             participant_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                             email VARCHAR(255) UNIQUE NOT NULL,
                             username VARCHAR(255) UNIQUE NOT NULL,
                             password VARCHAR(255) NOT NULL
);

CREATE TABLE game_results (
                              results_id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
                              participant_id UUID NOT NULL UNIQUE,
                              speed INT NOT NULL,
                              correctness_percentage INT NOT NULL,
                              username varchar(255) NOT NULL,
                              game_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (participant_id) REFERENCES participant(participant_id)
);