package com.social.group.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "request")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    int requestId;

    @Column(name = "user_id")
    int userId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    Group group;

}
