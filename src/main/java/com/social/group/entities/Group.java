package com.social.group.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "group_")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    int id;
    int creatorId;

    String name;
    String description;

    boolean active;

    @OneToMany(mappedBy = "compositeKey.group")
    Set<GroupMember> members;

    @OneToMany(mappedBy = "group")
    Set<Request> requests;

    {
        members = new HashSet<>();
        requests = new HashSet<>();
    }

}
