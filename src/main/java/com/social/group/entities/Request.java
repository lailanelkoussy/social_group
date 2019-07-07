package com.social.group.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "request")
public class Request { //todo how do you know that a request was accepted/rejected ?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    int requestId;

    @Column(name = "user_id")
    int userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

    public Request(int userId, Group group) {
        this.userId = userId;
        this.group = group;
    }

}
