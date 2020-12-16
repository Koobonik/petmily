package com.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("admin")
public class AdminController {
    @RequestMapping(value = "", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String main()  {
        return "good page";
    }
    @RequestMapping(value = "test", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String test()  {
        return "good page";
    }
}
