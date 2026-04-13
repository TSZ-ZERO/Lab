package com.tsz.myblog.service;

import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.entity.CreateBlogEntity;
import com.tsz.myblog.pojo.vo.CreateBlogVO;

import java.util.List;

public interface BlogService {
    CreateBlogVO createBlog(CreateBlogDTO createBlogDTO);
    
    //GetBlogListVO getBlogList();
    List<CreateBlogEntity> getBlogList();
}
