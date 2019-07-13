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
    } //todo why did you add this getter ?

    public void setCompositeKey(GroupMemberCK compositeKey){
        this.compositeKey = compositeKey;
    } //todo and this setter ?

    //can add stuff relating to activity on group

}
