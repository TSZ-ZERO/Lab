package com.tsz.myblog.pojo.vo;

import com.tsz.myblog.pojo.entity.BlogEntity;
import lombok.Data;
import java.util.List;
@Data
public class GetBlogListVO {
    List<BlogEntity> blogList;
}
