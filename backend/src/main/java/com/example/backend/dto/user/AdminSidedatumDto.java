package com.example.backend.dto.user;

import com.example.backend.entity.side.Sidedatum;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class AdminSidedatumDto {
    private Integer id;
    private String icon;
    private String name;
    private String href;

    private Map<String, Object> tiptapJson;
    private Instant expiredAt;
    private Sidedatum.Status status;
}
