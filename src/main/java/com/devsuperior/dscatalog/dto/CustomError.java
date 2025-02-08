package com.devsuperior.dscatalog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter @Builder
public class CustomError {

    private Instant timestamp;
    private Integer status;
    private String error;
    private String path;
}
