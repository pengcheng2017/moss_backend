package com.infinite.prism.moss.model.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 *
 *
 * @author liao.peng
 * @since 2026/3/5 17:13
 */
@Data
public class ImageToImageRequest {

    @NotNull
    private String model;

    @NotNull
    private String prompt;

    private List<String> fatherImageUrls;

    private List<String> matherImageUrls;

    private String size;

}
