package com.social.group.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Table(name = "groupp")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    int id;

    String name;

    String description;

    int creatorId;

    boolean active;

    @OneToMany(mappedBy = "compositeKey.group")
    List<GroupMember> members;

    @OneToMany(mappedBy = "group")
    List<Request> requests;

    //owner aka admin

}
