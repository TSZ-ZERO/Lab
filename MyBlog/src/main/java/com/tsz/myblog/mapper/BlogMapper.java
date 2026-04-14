package com.tsz.myblog.mapper;

import com.tsz.myblog.pojo.entity.BlogEntity;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogMapper {

    void InsertBlog(BlogEntity blogEntity);

    //GetBlogListVO getBlogList();
    List<BlogEntity> getBlogList();

    @Select("select * from blog where id=#{id}")
    BlogEntity getBlogById(String id);
}
