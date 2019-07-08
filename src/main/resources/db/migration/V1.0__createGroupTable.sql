CREATE TABLE group_
(
    group_id    int(11)      NOT NULL AUTO_INCREMENT,
    name        varchar(255) NOT NULL,
    description varchar(510)          DEFAULT NULL,
    creator_id  int(11)      NOT NULL,
    active      boolean      NOT NULL DEFAULT TRUE,

    PRIMARY KEY (group_id)
);