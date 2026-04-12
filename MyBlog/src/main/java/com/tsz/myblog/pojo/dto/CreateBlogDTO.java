package com.tsz.myblog.pojo.dto;

import lombok.Data;

@Data
public class CreateBlogDTO {
    private String title;
    private String content;
    private String author;
    private Integer wordCount;
}
