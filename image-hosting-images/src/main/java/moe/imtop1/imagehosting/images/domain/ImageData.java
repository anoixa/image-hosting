package moe.imtop1.imagehosting.images.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.common.entity.base.BaseEntity;


/**
 * @author anoixa
 */
@TableName("image_data")
public class ImageData extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String imageId;

    private String imageType;
    private String imageUrl;
    private String userId;
    private String strategyId;
    private String filePath;
    private String fileName;
    private String fileOriginalName;
    private String fileExtension;
    private Integer fileSize;
    private String description;
    private Boolean isPublic;

    private Integer width;
    private Integer height;
    private String key;

    public ImageData() {
    }

    public ImageData(String imageId, String imageType, String imageUrl, String userId, String strategyId, String filePath, String fileName, String fileOriginalName, String fileExtension, Integer fileSize, String description, Boolean isPublic, Integer width, Integer height, String key) {
        this.imageId = imageId;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.strategyId = strategyId;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileOriginalName = fileOriginalName;
        this.fileExtension = fileExtension;
        this.fileSize = fileSize;
        this.description = description;
        this.isPublic = isPublic;
        this.width = width;
        this.height = height;
        this.key = key;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(String strategyId) {
        this.strategyId = strategyId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileOriginalName() {
        return fileOriginalName;
    }

    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
