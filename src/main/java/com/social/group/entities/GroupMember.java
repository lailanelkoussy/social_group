package com.social.group.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table(name = "group_member")
@NoArgsConstructor
public class GroupMember {

    @EmbeddedId
    GroupMemberCK compositeKey;



    //can add stuff relating to activity on group

}
