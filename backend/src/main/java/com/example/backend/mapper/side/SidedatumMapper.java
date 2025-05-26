package com.example.backend.mapper.side;

import com.example.backend.dto.side.SidedatumDto;

import com.example.backend.dto.side.TagDto;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.side.Tag;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SidedatumMapper {

    @Mapping(source = "userId", target = "user.id")
    Sidedatum toEntity(SidedatumDto sidedatumDto);

    @Mapping(target = "tag", source = "tag", qualifiedByName = "toTagSimpleDto")
    SidedatumDto toDto(Sidedatum sidedatum);

    @Named("toTagSimpleDto")
    default TagDto toTagSimpleDto(Tag tag) {
        if (tag == null) return null;
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }

    List<SidedatumDto> toDtoList(List<Sidedatum> sidedata);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Sidedatum partialUpdate(SidedatumDto sidedatumDto, @MappingTarget Sidedatum sidedatum);
}