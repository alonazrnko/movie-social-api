MERGE INTO genres (id, name) VALUES
(1, 'Comedy'),
(2, 'Drama'),
(3, 'Animation'),
(4, 'Thriller'),
(5, 'Documentary'),
(6, 'Action');

MERGE INTO mpa_ratings (id, name) KEY (id) VALUES
(1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');