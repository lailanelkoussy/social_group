package com.social.group.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    int requestId;

    @Column(name = "user_id")
    @NotNull
    int userId;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_id")
    @NotNull
    Group group;

    public Request(int userId, Group group) {
        this.userId = userId;
        this.group = group;
    }

}
