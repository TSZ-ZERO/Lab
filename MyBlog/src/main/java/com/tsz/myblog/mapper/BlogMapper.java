package com.tsz.myblog.mapper;

import com.tsz.myblog.pojo.entity.BlogEntity;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface BlogMapper {

    void InsertBlog(BlogEntity blogEntity);

    //GetBlogListVO getBlogList();
    List<BlogEntity> getBlogList();
    //获取最近博客列表
    List<BlogEntity> getRecentBlogList();

    @Select("select * from blog where id=#{id}")
    BlogEntity getBlogById(String id);

    @Delete("delete from blog where id=#{id}")
    void deleteBlogById(String id);

    Map<String,Object> getBlogData();
}
