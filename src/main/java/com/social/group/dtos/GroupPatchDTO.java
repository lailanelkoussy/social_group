package com.social.group.dtos;

import lombok.Data;

import java.util.List;

@Data
public class GroupPatchDTO {

    int activate = -1;

    String name;
    String description;

    List<Integer> groupMembersIds;
}
