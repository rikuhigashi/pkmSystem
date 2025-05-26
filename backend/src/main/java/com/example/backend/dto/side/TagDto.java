package com.example.backend.dto.side;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.backend.entity.side.Tag}
 */
@Value
@Builder
public class TagDto implements Serializable {
    Integer id;
    String name;
    Integer userId;
}