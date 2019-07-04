package com.social.group.entities;


import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "group_member")
@NoArgsConstructor
public class GroupMember {

    @EmbeddedId
    GroupMemberCK compositeKey;

    public GroupMemberCK getCompositeKey(){
        return compositeKey;
    }

    public void setCompositeKey(GroupMemberCK compositeKey){
        this.compositeKey = compositeKey;
    }

    //can add stuff relating to activity on group

}
