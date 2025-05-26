package com.example.backend.mapper.side;

import com.example.backend.dto.side.TagDto;
import com.example.backend.entity.side.Tag;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {
    Tag toEntity(TagDto tagDto);

    @Mapping(target = "userId", source = "user.id")
    TagDto toDto(Tag tag);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tag partialUpdate(TagDto tagDto, @MappingTarget Tag tag);


    List<TagDto> toDtoList(List<Tag> tags);
}