package com.tsz.myblog.controller;
import com.tsz.myblog.pojo.dto.CreateBlogDTO;
import com.tsz.myblog.pojo.result.Result;
import com.tsz.myblog.pojo.vo.CreateBlogVO;
import com.tsz.myblog.service.CreateBlogService;
import com.tsz.myblog.service.FindRepeatNumbers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * API 控制器
 * 负责处理前端发送的 HTTP 请求并返回响应
 */
@RestController  // 标识为 REST 控制器，返回 JSON 数据
@RequestMapping("/api")  // 所有接口的公共路径前缀
@Slf4j
//@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5173"})作用：允许指定来源的请求访问该接口
public class ApiController {
    @Autowired
    private FindRepeatNumbers findRepeatNumbers;
    @Autowired
    private CreateBlogService createBlogService;

    /**
     * 问候接口
     * GET /api/hello
     *
     * @param name 可选的查询参数，表示称呼的名字
     * @return 包含问候消息和服务器时间戳的 Map
     */
    @GetMapping("/hello")  // 处理 GET 请求，路径为 /api/hello
    public Map<String, String> sayHello(@RequestParam(required = false) String name) {
        Map<String, String> res = new HashMap<>();
        // 如果 name 为空或空白，则使用默认名字 "Guest"
        String userName = (name == null || name.isBlank()) ? "Guest" : name;
        // 构建响应消息
        res.put("message", "Hello, " + userName + "!");
        // 添加服务器时间戳
        res.put("serverTime", String.valueOf(System.currentTimeMillis()));
        return res;
    }
    /**
     * 查找数组中的重复元素
     * POST /api/findDuplicates
     *
     * @param nums 前端传入的整型数组
     * @return 包含原数组、重复元素、重复数量和服务器时间戳的 Map
     */
    @PostMapping("/findDuplicates")  // 处理 POST 请求，路径为 /api/findDuplicates
    public Map<String, Object> findDuplicates(@RequestBody int[] nums) {
        Map<String, Object> res = new HashMap<>();
        // 调用私有方法查找重复元素
        List<Integer> duplicates = findRepeatNumbers.find(nums);
        // 将结果存入响应 Map
        res.put("originalArray", nums);           // 原数组
        res.put("duplicates", duplicates);       // 重复元素列表
        res.put("count", duplicates.size());      // 重复数量
        res.put("serverTime", System.currentTimeMillis());  // 服务器时间戳
        return res;
    }

    /**
     * 创建博客接口
     * POST /api/blogs
     */
    @PostMapping("/blogs")
    public Result createBlog(@RequestBody CreateBlogDTO createBlogDTO){
        log.info("createBlogDTO: {}", createBlogDTO);
        CreateBlogVO createBlogVO = createBlogService.createBlog(createBlogDTO);
        log.info("createBlogVO: {}", createBlogVO);
        return Result.success(createBlogVO);
    }
}
