CREATE TABLE group_member
(
    user_id        int(11) NOT NULL,
    group_id       int(11) NOT NULL,

    PRIMARY KEY (group_id, user_id),
    FOREIGN KEY (group_id) REFERENCES group_ (group_id)
        ON DELETE CASCADE
);