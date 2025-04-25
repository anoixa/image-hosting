package moe.imtop1.imagehosting.images.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import moe.imtop1.imagehosting.images.domain.ImageData;

import java.util.List;
import java.util.Map;

/**
 * @author shuomc
 * @Date 2025/4/25
 * @Description
 */
public class BatchUploadResult {
    private List<ImageData> successfulUploads; // 成功上传的图片元数据列表
    private List<String> failedFiles; // 失败的文件名列表

    public BatchUploadResult(List<ImageData> successfulUploads, List<String> failedFiles) {
        this.successfulUploads = successfulUploads;
        this.failedFiles = failedFiles;
    }

    public List<ImageData> getSuccessfulUploads() {
        return successfulUploads;
    }

    public void setSuccessfulUploads(List<ImageData> successfulUploads) {
        this.successfulUploads = successfulUploads;
    }

    public List<String> getFailedFiles() {
        return failedFiles;
    }

    public void setFailedFiles(List<String> failedFiles) {
        this.failedFiles = failedFiles;
    }
}
