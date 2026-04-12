package com.tsz.myblog.pojo.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateBlogVO {
    private String title;
    private String content;
    private String author;
    private Integer wordCount;

    private String id;
    private LocalDateTime createTime;

}
