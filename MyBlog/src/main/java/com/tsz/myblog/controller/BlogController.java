package com.tsz.myblog.controller;

import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.result.Result;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
import com.tsz.myblog.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/blogs")//所有跟博客接口相关的公共路径前缀
@Slf4j
public class BlogController {
    @Autowired
    private BlogService blogService;

    /**
     * 撰写博客接口
     * POST /api/blogs
     */
    @PostMapping("")
    public Result createBlog(@RequestBody CreateBlogDTO createBlogDTO){
        log.info("createBlogDTO: {}", createBlogDTO);
        CreateBlogVO createBlogVO = blogService.createBlog(createBlogDTO);
        log.info("createBlogVO: {}", createBlogVO);
        return Result.success(createBlogVO);
    }
    //获取博客列表接口
    @GetMapping("")
    public Result getBlogList(){
        //log.info("getBlogListVO: {}", blogService.getBlogList());
        log.info("getBlogList");
        return Result.success(blogService.getBlogList());
    }
}
