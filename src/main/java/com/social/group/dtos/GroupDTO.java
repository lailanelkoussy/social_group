package com.social.group.dtos;

import lombok.Data;

@Data
public class GroupDTO {

    int id;

    String name;

    String description;

    int creatorId;

    boolean active;

}
