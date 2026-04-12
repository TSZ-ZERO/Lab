package com.tsz.myblog.pojo.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBlogEntity {
    private String title;
    private String content;
    private String author;
    private Integer wordCount;

    private String id;
    private LocalDateTime createTime;
}
