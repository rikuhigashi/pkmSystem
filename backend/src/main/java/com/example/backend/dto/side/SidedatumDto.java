package com.example.backend.dto.side;

import com.example.backend.entity.side.Sidedatum;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for {@link com.example.backend.entity.side.Sidedatum}
 */
@Value
@Builder(toBuilder = true)
public class SidedatumDto implements Serializable {
    Integer id;
    String name;
    @Size(max = 255)
    @Size(max = 255)
    String href;
    @Size(max = 255)
    String icon;
    Map<String, Object> tiptapJson;

    Integer userId;

    Sidedatum.Status status;

}