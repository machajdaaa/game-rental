INSERT INTO roles (name) VALUES ('ROLE_USER');
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');

INSERT INTO users (username, email, password, fullname)
VALUES ('admin', 'admin@admin.cz', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Admin');

INSERT INTO users (username, email, password, fullname)
VALUES ('user', 'user@user.cz', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Běžný uživatel');

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';

INSERT INTO user_role (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'user' AND r.name = 'ROLE_USER';

INSERT INTO publishers (name, country) VALUES ('Česká herna', 'CZ');
INSERT INTO publishers (name, country) VALUES ('Games Workshop', 'UK');

INSERT INTO game_categories (name) VALUES ('Strategická');
INSERT INTO game_categories (name) VALUES ('Party');
INSERT INTO game_categories (name) VALUES ('RPG');

INSERT INTO games (title, min_players, max_players, min_age, duration_minutes, publisher_id)
VALUES ('Catan', 3, 4, 10, 90, 1);
INSERT INTO games (title, min_players, max_players, min_age, duration_minutes, publisher_id)
VALUES ('Codenames', 2, 8, 12, 30, 2);

INSERT INTO game_copies (inventory_code, condition, available, game_id)
VALUES ('INV001', 'NEW', true, 1);
INSERT INTO game_copies (inventory_code, condition, available, game_id)
VALUES ('INV002', 'GOOD', true, 2);
