package com.shf.nowcoder.config;

import com.shf.nowcoder.util.CommunityConstant;
import com.shf.nowcoder.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "unfollow"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN,
                        AUTHORITY_MODERATOR,
                        AUTHORITY_USER
                )
                .antMatchers(
                        "/discuss/top",
                        "/discuss/wonderful"
                )
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR
                )
                .antMatchers(
                        "/discuss/delete",
                        "/data/**"
                )
                .hasAnyAuthority(
                        AUTHORITY_ADMIN
                )
                .anyRequest().permitAll()
                .and().csrf().disable();

//        权限不够时的处理
        http.exceptionHandling()
                /**
                 * 没有登录
                 */
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        String xRequestWith = httpServletRequest.getHeader("x-requested-with");
                        if (xRequestWith.equals("XMLHttpRequest")) {
                            /**
                             * 返回JSON
                             */
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你还没有登录!"));
                        } else {
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/login");
                        }
                    }
                })
                /**
                 * 权限不足
                 */
                .accessDeniedHandler(new AccessDeniedHandler() {
                    @Override
                    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestWith = httpServletRequest.getHeader("x-requested-with");
                        if (xRequestWith.equals("XMLHttpRequest")) {
                            /**
                             * 返回JSON
                             */
                            httpServletResponse.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = httpServletResponse.getWriter();
                            writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                        } else {
                            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/denied");
                        }
                    }
                });

        /**
         * Security
         */
        http.logout().logoutUrl("/securitylogout");

    }
}
