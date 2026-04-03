package com.tsz.myblog.controller;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://127.0.0.1:5500")  // 允许前端开发服务器调用
public class ApiController {

    @GetMapping("/hello")
    public Map<String, String> sayHello(@RequestParam(required = false) String name) {
        Map<String, String> res = new HashMap<>();
        String userName = (name == null || name.isBlank()) ? "Guest" : name;
        res.put("message", "Hello, " + userName + "!");
        res.put("serverTime", String.valueOf(System.currentTimeMillis()));
        return res;
    }

    // 示例：后续可以扩展更多接口（如 /api/user, /api/order）
}