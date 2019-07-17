package com.social.group.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {

    int id;
    int creatorId;
    int activate = -1;

    String name;
    String description;

    List<Integer> groupMembersIds;

    @JsonIgnore
    public void setId(int id){
        this.id = id;
    }

    @JsonProperty
    public int getId(){
        return id;
    }

}
