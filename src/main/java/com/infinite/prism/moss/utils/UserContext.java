package com.infinite.prism.moss.utils;

import com.infinite.prism.moss.entity.VO.UserVO;
import org.springframework.stereotype.Component;

@Component
public class UserContext {
    
    private static final ThreadLocal<UserVO> USER_VO_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        UserVO userVO = USER_VO_THREAD_LOCAL.get();
        if (userVO == null) {
            userVO = new UserVO();
        }
        userVO.setId(userId);
        USER_VO_THREAD_LOCAL.set(userVO);
    }

    public static void setUsername(String username) {
        UserVO userVO = USER_VO_THREAD_LOCAL.get();
        if (userVO == null) {
            userVO = new UserVO();
        }
        userVO.setUsername(username);
        USER_VO_THREAD_LOCAL.set(userVO);
    }

    public static void setMainAccountId(String mainAccountId) {
        UserVO userVO = USER_VO_THREAD_LOCAL.get();
        if (userVO == null) {
            userVO = new UserVO();
        }
        userVO.setMainAccountId(mainAccountId);
        USER_VO_THREAD_LOCAL.set(userVO);
    }

    public static void setToken(String token) {
        UserVO userVO = USER_VO_THREAD_LOCAL.get();
        if (userVO == null) {
            userVO = new UserVO();
        }
        userVO.setToken(token);
        USER_VO_THREAD_LOCAL.set(userVO);
    }

    public static UserVO getUserVO() {
        return USER_VO_THREAD_LOCAL.get();
    }
    
    public static void clear() {
        USER_VO_THREAD_LOCAL.remove();
    }
}