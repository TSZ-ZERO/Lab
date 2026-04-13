package com.tsz.myblog.mapper;

import com.tsz.myblog.pojo.entity.CreateBlogEntity;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlogMapper {

    void InsertBlog(CreateBlogEntity createBlogEntity);

    //GetBlogListVO getBlogList();
    List<CreateBlogEntity> getBlogList();
}
