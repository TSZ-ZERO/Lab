package com.tsz.myblog.service.impl;

import com.tsz.myblog.mapper.BlogMapper;
import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.entity.BlogEntity;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
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
        BlogEntity blogEntity = new BlogEntity();
        //使用Hutool包的属性拷贝， 将 DTO 转换为实体
        BeanUtils.copyProperties(createBlogDTO, blogEntity);
        //设置博客UUID
        blogEntity.setId(UUID.randomUUID().toString());
        //设置博客创建时间
        blogEntity.setCreateTime(LocalDateTime.now());
        //传到DAO层持久化到数据库
        blogMapper.InsertBlog(blogEntity);
        CreateBlogVO createBlogVO = new CreateBlogVO();
        BeanUtils.copyProperties(blogEntity, createBlogVO);
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

    /**
     * 获取博客列表
     * @return
     */
    @Override
    public List<BlogEntity> getBlogList() {
        return blogMapper.getBlogList();
    }
    @Override
    public BlogEntity getBlogById(String id){
        return blogMapper.getBlogById(id);
    }

    @Override
    public void deleteBlogById(String id) {
        blogMapper.deleteBlogById(id);
    }
}
