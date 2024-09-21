package moe.imtop1.imagehosting.common.entity;

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
    private String sourceType;
    private String filePath;
    private String fileName;
    private Integer fileSize;
    private String description;
    private Boolean isPublic;

    public ImageData() {
    }

    public ImageData(String imageId, String imageType, String imageUrl, String userId, String sourceType, String filePath, String fileName, Integer fileSize, String description, Boolean isPublic) {
        this.imageId = imageId;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.sourceType = sourceType;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.description = description;
        this.isPublic = isPublic;
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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
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
}
