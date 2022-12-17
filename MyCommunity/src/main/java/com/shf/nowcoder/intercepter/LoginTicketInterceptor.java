package com.shf.nowcoder.intercepter;

import com.shf.nowcoder.entity.LoginTicket;
import com.shf.nowcoder.entity.User;
import com.shf.nowcoder.service.UserService;
import com.shf.nowcoder.util.CookieUtil;
import com.shf.nowcoder.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        从ticket中获取凭证
        String ticket = CookieUtil.getValue(request, "ticket");

        if (ticket != null) {
//            查询凭证
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
//            检测凭证是否有效
            if (loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
//                根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
//                在本次请求中持有用户
                hostHolder.setUser(user);
            }
        }

        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 销毁
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
