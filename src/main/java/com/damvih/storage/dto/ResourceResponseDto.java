package com.damvih.storage.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceResponseDto {

    private String path;

    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long size;

    private ResourceType type;

}
