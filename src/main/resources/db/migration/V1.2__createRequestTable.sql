CREATE TABLE request
(
    request_id    int(11)      NOT NULL AUTO_INCREMENT,
    group_id      int(11)       NOT NULL,
    user_id         int(11)     NOT NULL,

    PRIMARY KEY (request_id),
    FOREIGN KEY (group_id) REFERENCES group_(group_id)
);
