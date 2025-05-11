package com.example.backend.mapper.side;

import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.side.Sidedatum;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface SidedatumMapper {

    @Mapping(source = "userId", target = "user.id")
    Sidedatum toEntity(SidedatumDto sidedatumDto);

    SidedatumDto toDto(Sidedatum sidedatum);

    List<SidedatumDto> toDtoList(List<Sidedatum> sidedata);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Sidedatum partialUpdate(SidedatumDto sidedatumDto, @MappingTarget Sidedatum sidedatum);
}