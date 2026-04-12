package com.tsz.myblog.service;

import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.vo.CreateBlogVO;

public interface CreateBlogService {
    CreateBlogVO createBlog(CreateBlogDTO createBlogDTO);
}
