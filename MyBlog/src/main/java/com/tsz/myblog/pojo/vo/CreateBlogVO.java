package com.tsz.myblog.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateBlogVO {
    private String title;
    private String content;
    private String author;
    private Integer wordCount;

    private String id;
    @JsonFormat(pattern="yyyy-mm-dd hh:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

}
