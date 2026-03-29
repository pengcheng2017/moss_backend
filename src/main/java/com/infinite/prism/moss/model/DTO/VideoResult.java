package com.infinite.prism.moss.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoResult {

    /** 任务ID，用于异步查询 */
    private String taskId;

    /** 视频生成状态 */
    private VideoStatus status;

    /** 视频 URL，只有生成成功才有 */
    private String videoUrl;

    /** 可选：视频时长（秒） */
    private Integer duration;

    /** 可选：视频格式，比如 mp4 */
    private String format;

    // 构造简化版本
    public VideoResult(String taskId, VideoStatus status) {
        this.taskId = taskId;
        this.status = status;
    }

    public VideoResult(String taskId, VideoStatus status, String videoUrl) {
        this.taskId = taskId;
        this.status = status;
        this.videoUrl = videoUrl;
    }
}
