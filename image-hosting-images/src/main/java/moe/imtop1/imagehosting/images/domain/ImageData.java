package moe.imtop1.imagehosting.images.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import moe.imtop1.imagehosting.framework.base.BaseEntity;


/**
 * @author anoixa
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("image_data")
public class ImageData extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String imageId;
    private String userId;
    private String fileName;
    private String minioKey;
    private String minioUrl;
    private Integer size;
    private Integer width;
    private Integer height;
    private String contentType;
    private Boolean isPublic;
    private String description;
}