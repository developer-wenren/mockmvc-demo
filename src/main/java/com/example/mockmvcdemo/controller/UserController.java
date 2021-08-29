package com.example.mockmvcdemo.controller;

import com.example.mockmvcdemo.vo.UserInfoVO;
import org.springframework.web.bind.annotation.*;

/**
 * 用户
 *
 * @author one
 * @version 1.0.0
 * @since 2021/08/22
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/{id}")
    public UserInfoVO get(@PathVariable Long id) {
        UserInfoVO userInfoVO = new UserInfoVO(id, "test", "pwd", 100);
        return userInfoVO;
    }

    @GetMapping("/getScore")
    public Integer getScore(Long id) {
        UserInfoVO userInfoVO = new UserInfoVO(id, "test", "pwd", 100);
        return userInfoVO.getScore();
    }

    @PostMapping("/login")
    public UserInfoVO login(String username, String password) {
        UserInfoVO userInfoVO = new UserInfoVO(1L, username, password, 100);
        return userInfoVO;
    }

    @PostMapping("/login2")
    public UserInfoVO login2(@RequestBody UserInfoVO vo) {
        UserInfoVO userInfoVO = new UserInfoVO(1L, vo.getUsername(), vo.getPassword(), 100);
        return userInfoVO;
    }
}
