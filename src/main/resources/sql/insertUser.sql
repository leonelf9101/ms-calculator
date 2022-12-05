INSERT INTO users (username, password, enabled) VALUES
(:USERNAME,:PASSWORD, :ENABLED)
RETURNING id;
