package com.tsz.myblog.service.impl;

import com.tsz.myblog.mapper.BlogMapper;
import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.entity.CreateBlogEntity;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
import com.tsz.myblog.pojo.vo.GetBlogListVO;
import com.tsz.myblog.service.BlogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BlogServiceImpl implements BlogService {
    @Autowired
    private BlogMapper blogMapper;

    @Override
    public CreateBlogVO createBlog(CreateBlogDTO createBlogDTO) {
        CreateBlogEntity createBlogEntity = new CreateBlogEntity();
        //使用Hutool包的属性拷贝， 将 DTO 转换为实体
        BeanUtils.copyProperties(createBlogDTO, createBlogEntity);
        //设置博客UUID
        createBlogEntity.setId(UUID.randomUUID().toString());
        //设置博客创建时间
        createBlogEntity.setCreateTime(LocalDateTime.now());
        //传到DAO层持久化到数据库
        blogMapper.InsertBlog(createBlogEntity);
        CreateBlogVO createBlogVO = new CreateBlogVO();
        BeanUtils.copyProperties(createBlogEntity, createBlogVO);
        return createBlogVO;
    }

    /*
    public GetBlogListVO getBlogList() {
        //获取博客列表
        GetBlogListVO getBlogListVO = new GetBlogListVO();
        // 获取博客列表并设置到VO中
        //getBlogListVO.setBlogList(blogMapper.getBlogList());
        List<CreateBlogEntity> blogList = blogMapper.getBlogList();
        getBlogListVO.setBlogList(blogList);
        return getBlogListVO; */
    @Override
    public List<CreateBlogEntity> getBlogList() {
        return blogMapper.getBlogList();
    }
}
