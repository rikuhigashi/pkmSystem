package com.example.backend.service.impl.side;

import com.example.backend.dto.side.TagDto;
import com.example.backend.entity.side.Tag;
import com.example.backend.entity.user.User;
import com.example.backend.exception.OperationNotAllowedException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.side.TagMapper;
import com.example.backend.repository.side.TagRepository;
import com.example.backend.repository.side.SidedatumRepository;
import com.example.backend.service.side.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final SidedatumRepository sidedatumRepository;
    private final TagMapper tagMapper;

    /**
     * 创建tag
     */
    @Transactional
    @Override
    public TagDto createTag(TagDto tagDto, User user) {
        if (tagRepository.existsByNameAndUser(tagDto.getName(), user)) {
            throw new OperationNotAllowedException("标签名称已存在");
        }
        Tag tag = tagMapper.toEntity(tagDto);
        tag.setUser(user);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    /**
     * 更新tag
     */
    @Transactional
    @Override
    public TagDto updateTagName(Integer tagId, String newName, User user) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        if (!tag.getUser().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("无权修改此标签");
        }
        tag.setName(newName);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    /**
     * 删除tag
     */
    @Transactional
    @Override
    public void deleteTag(Integer tagId, User user) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("标签不存在"));
        if (!tag.getUser().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("无权删除此标签");
        }
        if (sidedatumRepository.existsByTag(tag)) {
            throw new OperationNotAllowedException("标签下存在数据，无法删除");
        }
        tagRepository.delete(tag);
    }

    /**
     * 获取tag
     */
    @Override
    public List<TagDto> getUserTags(User user) {
        return tagMapper.toDtoList(tagRepository.findByUser(user));
    }
}
