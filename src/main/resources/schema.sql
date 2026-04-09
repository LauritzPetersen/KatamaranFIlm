CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       color_code VARCHAR(10) NOT NULL
);

CREATE TABLE movies (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        tmdb_id INT,
                        title VARCHAR(255) NOT NULL,
                        genre VARCHAR(100),
                        release_year INT,
                        poster_url VARCHAR(255),
                        status VARCHAR(20) NOT NULL DEFAULT 'WATCHLIST',
                        added_by_user_id INT NOT NULL,
                        FOREIGN KEY (added_by_user_id) REFERENCES users(id)
);

CREATE TABLE ratings (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         user_id INT NOT NULL,
                         movie_id INT NOT NULL,
                         score DOUBLE NOT NULL,
                         FOREIGN KEY (user_id) REFERENCES users(id),
                         FOREIGN KEY (movie_id) REFERENCES movies(id),
                         UNIQUE (user_id, movie_id)
);
