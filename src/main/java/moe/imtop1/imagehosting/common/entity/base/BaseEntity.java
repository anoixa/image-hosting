package moe.imtop1.imagehosting.common.entity.base;

import java.time.LocalDateTime;

/**
 * @author anoixa
 */
public abstract class BaseEntity {

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean isDelete;

    // Getters and Setters
    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Boolean getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Boolean isDelete) {
        this.isDelete = isDelete;
    }
}