package com.timekeeping.timekeeping.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FaviconController {
    @RequestMapping("favicon.ico")
    @ResponseBody
    void returnNoFavicon() {
        // Không làm gì cả, chỉ trả về phản hồi 204 No Content
    }
}
