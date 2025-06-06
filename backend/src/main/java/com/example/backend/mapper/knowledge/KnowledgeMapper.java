package com.example.backend.mapper.knowledge;

import com.example.backend.dto.knowledge.KnowledgeDTO;
import com.example.backend.entity.knowledge.Knowledge;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface KnowledgeMapper {
    Knowledge toEntity(KnowledgeDTO knowledgeDto);

    KnowledgeDTO toDto(Knowledge knowledge);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Knowledge partialUpdate(KnowledgeDTO knowledgeDto, @MappingTarget Knowledge knowledge);
}