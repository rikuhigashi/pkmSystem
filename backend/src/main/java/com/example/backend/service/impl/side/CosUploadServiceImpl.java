package com.example.backend.service.impl.side;

import com.example.backend.config.TencentCosConfig;
import com.example.backend.service.side.CosUploadService;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CosUploadServiceImpl implements CosUploadService {

    private final TencentCosConfig config;

    @Override
    public String upload(MultipartFile file) {
        try {
            // 使用配置类获取密钥和其他配置
            COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
            ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
            COSClient cosClient = new COSClient(cred, clientConfig);

            // 生成一个唯一的文件名
            String key = "images/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

            // 设置文件的元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());

            // 创建请求并上传文件
            PutObjectRequest request = new PutObjectRequest(config.getBucket(), key, file.getInputStream(), metadata);
            request.setCannedAcl(CannedAccessControlList.PublicRead); // 公共读取权限

            // 执行上传
            cosClient.putObject(request);
            cosClient.shutdown();
            // 返回文件的公开访问URL
            return String.format("https://%s.cos.%s.myqcloud.com/%s",
                    config.getBucket(), config.getRegion(), key);
        } catch (IOException e) {
            throw new RuntimeException("上传失败", e);
        }
    }

}
