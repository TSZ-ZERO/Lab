package com.tsz.myblog.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
public class DataVO {
    private int totalBlogs;
    private int totalWords;
    private int avgWordsPerBlog;
    @JsonFormat(pattern="yyyy-mm-dd hh-mm-ss",timezone = "GMT+8")
    private LocalDateTime lastUpdated;
}
