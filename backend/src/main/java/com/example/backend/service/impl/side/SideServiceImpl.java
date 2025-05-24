package com.example.backend.service.impl.side;

import com.example.backend.dto.side.SidedatumDto;
import com.example.backend.entity.message.Notification;
import com.example.backend.entity.side.Sidedatum;
import com.example.backend.entity.user.User;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.mapper.side.SidedatumMapper;
import com.example.backend.repository.message.NotificationRepository;
import com.example.backend.repository.side.SidedatumRepository;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.service.side.SideService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class SideServiceImpl implements SideService {
    private final SidedatumRepository sidedatumRepository;
    private final SidedatumMapper sidedatumMapper;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @Autowired
    public SideServiceImpl(SidedatumRepository sidedatumRepository, SidedatumMapper sidedatumMapper, UserRepository userRepository, NotificationRepository notificationRepository) {
        this.sidedatumRepository = sidedatumRepository;
        this.sidedatumMapper = sidedatumMapper;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }


    @Override
    public List<SidedatumDto> getAllSideDataByUser(User user) {
        List<Sidedatum> sidedata = sidedatumRepository.findAllByUser(user);

        return sidedatumMapper.toDtoList(sidedata);
    }

    @Override
    public SidedatumDto getFullSidedata(Integer id, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Sidedatum sideData = sidedatumRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("数据不存在或无权访问"));
        return sidedatumMapper.toDto(sideData);
    }


    //    更新
    @Override
    public SidedatumDto updataItem(Integer id, SidedatumDto sidedatumDto) {
        Sidedatum sidedatum = sidedatumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sidedatum not found with id: " + id));


        // 重置拒绝状态
        if (sidedatum.getStatus() == Sidedatum.Status.REJECTED_PENDING) {
            sidedatum.setStatus(Sidedatum.Status.PENDING);
            sidedatum.setExpiredAt(null);
            sidedatum.setRejectReason(null);
        }


        sidedatumMapper.partialUpdate(sidedatumDto, sidedatum);
        return sidedatumMapper.toDto(sidedatumRepository.save(sidedatum));
    }

    //    复制/粘贴
    @Override
    public SidedatumDto copySideData(Integer id) {
        Sidedatum original = sidedatumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("数据不存在"));

        Sidedatum copy = new Sidedatum();
        copy.setName(original.getName() + "副本");
        copy.setHref(original.getHref());
        copy.setIcon(original.getIcon());
        copy.setTiptapJson(original.getTiptapJson());
        copy.setUser(original.getUser());

        return sidedatumMapper.toDto(sidedatumRepository.save(copy));
    }

    //    删除
    @Override
    @Transactional
    public void deledeSideData(Integer id) {

        Sidedatum sidedatum = sidedatumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("数据不存在"));

        // 获取用户所有侧边栏项数量
        Long count = sidedatumRepository.countByUser(sidedatum.getUser());

        if (count <= 1) {
            throw new IllegalStateException("必须保留至少一个文档");
        }

        // 删除操作
        sidedatumRepository.delete(sidedatum); // 或 deleteById(id)


    }

    /**
     * 批准数据，设置为永久保存
     *
     * @param id 数据ID
     */
    @Transactional
    @Override
    public void approveData(Integer id) {
        Sidedatum data = sidedatumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("数据不存在"));
        data.setStatus(Sidedatum.Status.APPROVED);
        data.setExpiredAt(null); // 设置为null表示永久保存
        sidedatumRepository.save(data);
    }

    /**
     * 拒绝数据，立即过期
     *
     * @param id 数据ID
     */
    @Transactional
    @Override
    public void rejectData(Integer id, String reason) {
        Sidedatum data = sidedatumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("数据不存在"));
//        data.setStatus(Sidedatum.Status.REJECTED);
//        data.setExpiredAt(Instant.now()); // 设置为当前时间，触发立即清理
//        sidedatumRepository.save(data);

        log.info("开始处理拒绝请求 ID: {} 原因: {}", id, reason);

        // 设置24小时缓冲期
        data.setStatus(Sidedatum.Status.REJECTED_PENDING);
        data.setExpiredAt(Instant.now().plus(24, ChronoUnit.HOURS));
        data.setRejectReason(reason);
        sidedatumRepository.save(data);


        // 创建通知
        Notification notification = new Notification();
        notification.setUser(data.getUser());
        notification.setContent(String.format(
                "您的数据【%s】因【%s】将被删除，请及时修改！",
                data.getName(), reason
        ));
        notification.setRelatedDataId(data.getId());
        notification.setExpireAt(data.getExpiredAt());
        notificationRepository.save(notification);

        log.info("已标记数据待删除 ID: {}, 原因: {}", id, reason);

    }

    @Scheduled(cron = "0 0 * * * *") // 每小时执行一次
    @Transactional
    public void cleanRejectedData() {
        List<Sidedatum> expiredData = sidedatumRepository
                .findAllByStatusAndExpiredAtLessThanEqual(Sidedatum.Status.REJECTED_PENDING, Instant.now());

        expiredData.forEach(data -> {
            // 发送最终通知
            Notification notification = new Notification();
            notification.setUser(data.getUser());
            notification.setContent(String.format("您的数据'%s'已因'%s'被删除", data.getName(), data.getRejectReason()));
            notificationRepository.save(notification);

            sidedatumRepository.delete(data);
        });
    }


    /**
     * 用户上传数据，默认设置7天有效期
     *
     * @param sidedatumDto 数据实体
     * @return 保存后的数据
     */
    @Transactional
    @Override
    public SidedatumDto addSideData(SidedatumDto sidedatumDto) {
        Sidedatum sidedatum = sidedatumMapper.toEntity(sidedatumDto);

        sidedatum.setStatus(Sidedatum.Status.PENDING);
        sidedatum.setExpiredAt(Instant.now().plus(7, ChronoUnit.DAYS)); // 默认7天后过期

        Sidedatum savedData = sidedatumRepository.save(sidedatum);

        return sidedatumMapper.toDto(savedData);
    }


    @Override
    public Page<Sidedatum> getPendingData(String name, Instant expiredBefore, Pageable pageable) {
        Specification<Sidedatum> spec = Specification.anyOf();

        // 筛选状态为PENDING
        spec = spec.and((root, query, cb) ->
                cb.equal(root.get("status"), Sidedatum.Status.PENDING)
        );

        // 按名称模糊查询
        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("name"), name + "%")
            );
        }

        // 按过期时间筛选
        if (expiredBefore != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("expiredAt"), expiredBefore)
            );
        }

        return sidedatumRepository.findAll(spec, pageable);
    }


}