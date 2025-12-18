package com.damvih.storage.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StorageResponse {

    private String fullPath;
    private Long size;

}
