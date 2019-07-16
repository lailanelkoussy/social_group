package com.social.group.entities;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "group_member")
@NoArgsConstructor
@Data
public class GroupMember {

    @EmbeddedId
    GroupMemberCK compositeKey;

}
