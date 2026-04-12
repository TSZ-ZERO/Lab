package com.tsz.myblog.mapper;

import com.tsz.myblog.pojo.entity.CreateBlogEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CreateBlogMapper {

    void Insert(CreateBlogEntity createBlogEntity);
}
