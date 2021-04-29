CREATE TABLE IF NOT EXISTS chessRoom
(
    room_id       INT          NOT NULL AUTO_INCREMENT,
    room_name     VARCHAR(255) NOT NULL,
    room_password VARCHAR(255),
    PRIMARY KEY (room_id)
);

CREATE TABLE IF NOT EXISTS chessGame
(
    command_log  INT         NOT NULL AUTO_INCREMENT,
    room_id      INT         NOT NULL,
    target       VARCHAR(12) NOT NULL,
    destination  VARCHAR(12) NOT NULL,
    command_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (command_log)
);
