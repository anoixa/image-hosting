package moe.imtop1.imagehosting.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import moe.imtop1.imagehosting.common.entity.base.BaseEntity;

/**用户-图片关系
 * @author shuomc
 */
@TableName("user_image_rel")
public class UserImageRel extends BaseEntity {
    @TableId(type = IdType.INPUT)
    private String imageId;

    private String userId;
    private Boolean isDelete;

    public UserImageRel() {}

    public UserImageRel(String imageId, String userId, Boolean isDelete) {
        this.imageId = imageId;
        this.userId = userId;
        this.isDelete = isDelete;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Boolean getDelete() {
        return isDelete;
    }

    public void setDelete(Boolean delete) {
        isDelete = delete;
    }
}
