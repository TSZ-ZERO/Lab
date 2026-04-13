package com.tsz.myblog.pojo.vo;

import com.tsz.myblog.pojo.entity.CreateBlogEntity;
import lombok.Data;
import java.util.List;
@Data
public class GetBlogListVO {
    List<CreateBlogEntity> blogList;
}
