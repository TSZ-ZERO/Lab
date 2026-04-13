package com.tsz.myblog.controller;

import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.result.Result;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
import com.tsz.myblog.service.CreateBlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/blogs")//所有跟博客接口相关的公共路径前缀
@Slf4j
public class BlogController {
    @Autowired
    private CreateBlogService createBlogService;

    /**
     * 撰写博客接口
     * POST /api/blogs
     */
    @PostMapping("")
    public Result createBlog(@RequestBody CreateBlogDTO createBlogDTO){
        log.info("createBlogDTO: {}", createBlogDTO);
        CreateBlogVO createBlogVO = createBlogService.createBlog(createBlogDTO);
        log.info("createBlogVO: {}", createBlogVO);
        return Result.success(createBlogVO);
    }
}
