package com.social.group.dtos;

import lombok.Data;


@Data
public class CreateGroupDTO {

    int creatorId;
    String name;
    String description;
}
