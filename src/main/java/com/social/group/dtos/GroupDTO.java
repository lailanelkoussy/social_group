package com.social.group.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GroupDTO {

    int id;

    String name;

    String description;

    int creatorId;

    int activate = -1;

    List<Integer> groupMembersIds;

}
