package moe.imtop1.imagehosting.images.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import moe.imtop1.imagehosting.images.service.ImageCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

/**
 * @author shuomc
 * @Date 2025/4/17
 * @Description
 */
@Service
public class ImageCacheServiceImpl implements ImageCacheService{

    private static final Logger logger = LoggerFactory.getLogger(ImageCacheServiceImpl.class);

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${minio.bucketName}")
    private String bucketName;

    private static final String REDIS_KEY_PREFIX = "minio:object:";
    private static final long REDIS_EXPIRATION_TIME_SECONDS = 3600;

    public byte[] getMinioObjectDataFromRedis(String objectName) {

        String redisKey = REDIS_KEY_PREFIX + objectName;

        byte[] objectData = new byte[0];
        try {
            // 检查 Redis 中是否已存在缓存
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                logger.info("Found object info in Redis for: {}", objectName);
                Object cachedInfo = redisTemplate.opsForValue().get(redisKey);
                logger.info("Cached Info: {}", cachedInfo);
                return (byte[]) cachedInfo;
            }

            // 从 Minio 获取对象信息
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build();

            try (GetObjectResponse response = minioClient.getObject(getObjectArgs)) {
                objectData = response.readAllBytes();
                logger.info("Read {} bytes from Minio object: {}", objectData.length, objectName);

                // 将对象数据存储到 Redis
                redisTemplate.opsForValue().set(redisKey, objectData, REDIS_EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS);
                logger.info("Stored {} bytes of {} in Redis with key: {}", objectData.length, objectName, redisKey);

            } catch (MinioException | IOException | NoSuchAlgorithmException e) {
                logger.error("Error while fetching object from Minio: {}", e.getMessage());
            }

        } catch (Exception e) {
            logger.error("Error interacting with Redis: {}", e.getMessage());
        }
        return objectData;
    }

    @Override
    public void storeMinioObjectInfoInRedis(String objectName) {
        return;
    }

    public Object getMinioObjectInfoFromRedis(String objectName) {
        String redisKey = REDIS_KEY_PREFIX + objectName;
        return redisTemplate.opsForValue().get(redisKey);
    }
}
