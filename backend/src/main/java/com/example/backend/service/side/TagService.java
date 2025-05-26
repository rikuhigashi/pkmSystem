package com.example.backend.service.side;

import com.example.backend.dto.side.TagDto;
import com.example.backend.entity.user.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface  TagService {

    @Transactional
    TagDto createTag(TagDto tagDto, User user);

    @Transactional
    TagDto updateTagName(Integer tagId, String newName, User user);

    @Transactional
    void deleteTag(Integer tagId, User user);

    List<TagDto> getUserTags(User user);
}
