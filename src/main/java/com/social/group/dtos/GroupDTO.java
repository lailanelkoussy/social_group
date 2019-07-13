package com.social.group.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {

    int id;

    String name; //todo why are you adding all these new lines ? you can group the members of the same type together

    String description;

    int creatorId;

    int activate = -1;

    List<Integer> groupMembersIds;

}
