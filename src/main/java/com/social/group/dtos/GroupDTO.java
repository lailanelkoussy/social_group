package com.social.group.dtos;

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

}
