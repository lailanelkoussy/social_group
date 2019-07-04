package com.social.group.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
public class GroupMemberCK implements Serializable {
    @Column(name = "user_id")
    int userId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    Group group;
}
