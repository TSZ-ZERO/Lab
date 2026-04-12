package com.tsz.myblog.service.impl;

import com.tsz.myblog.mapper.CreateBlogMapper;
import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.entity.CreateBlogEntity;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
import com.tsz.myblog.service.CreateBlogService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CreateBlogServiceImpl implements CreateBlogService {
    @Autowired
    private CreateBlogMapper createBlogMapper;

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
        createBlogMapper.Insert(createBlogEntity);
        CreateBlogVO createBlogVO = new CreateBlogVO();
        BeanUtils.copyProperties(createBlogEntity, createBlogVO);
        return createBlogVO;
    }
}
