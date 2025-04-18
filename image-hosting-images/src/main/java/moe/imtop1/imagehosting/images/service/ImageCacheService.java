package moe.imtop1.imagehosting.images.service;

public interface ImageCacheService {
    void storeMinioObjectInfoInRedis(String objectName);

    Object getMinioObjectInfoFromRedis(String objectName);

    byte[] getMinioObjectDataFromRedis(String objectName);
}
