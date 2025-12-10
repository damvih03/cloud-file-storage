package com.damvih.storage.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MinioResponse {

    private PathComponents pathComponents;
    private Long size;

}
