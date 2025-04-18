package moe.imtop1.imagehosting.images.domain.dto;

import lombok.*;

import java.io.InputStream;

/**
 * @author shuomc
 * @Date 2025/4/15
 * @Description
 */
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ImageStreamData {

    private InputStream inputStream;  // MinIO 图片输入流

//    private byte[] imageData;

    private String contentType;

    private Integer size;

    private String fileName;
}
