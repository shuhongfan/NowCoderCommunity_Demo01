package com.nowcoder.community.config;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    /**
     * 忽略静态资源的访问
     * @param webSecurity
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/resources/**");
    }

    /**
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        内置的认证规则
//        auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("123456"));

//        自定义认证规则
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = (String) authentication.getCredentials();

                User user = userService.findUserByName(username);
                if (user == null) {
                    throw new UsernameNotFoundException("账户不存在");
                }
                password = CommunityUtil.md5(password + user.getSalt());
                if (!user.getPassword().equals(password)) {
                    throw new BadCredentialsException("密码不正确");
                }

                return new UsernamePasswordAuthenticationToken(user,user.getPassword(),user.getAuthorities());
            }

            /**
             * 支持哪种类型的认证
             * @param aClass
             * @return
             */
            @Override
            public boolean supports(Class<?> aClass) {
                return UsernamePasswordAuthenticationToken.class.equals(aClass);
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        /**
         * 登录相关配置
         */
        http.formLogin()
                .loginPage("/loginpage")
                .loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/index");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
                        httpServletRequest.setAttribute("error", e.getMessage());
                        httpServletRequest.getRequestDispatcher("/loginpage").forward(httpServletRequest,httpServletResponse);
                    }
                });

        /**
         * 退出相关配置
         */
        http.logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
                        httpServletResponse.sendRedirect(httpServletRequest.getContextPath()+"/index");
                    }
                });

        /**
         * 授权配置
         */
        http.authorizeRequests()
                .antMatchers("/letter").hasAnyAuthority("USER")
                .antMatchers("/admin").hasAnyAuthority("ADMIN")
                .and().exceptionHandling().accessDeniedPage("/denied");

        /**
         * 增加Filter 处理验证码
         */
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                if (request.getServletPath().equals("/login")) {
                    String verifyCode = request.getParameter("verifyCode");
                    if (verifyCode == null || !verifyCode.equalsIgnoreCase("1234")) {
                        request.setAttribute("error", "验证码错误");
                        request.getRequestDispatcher("/loginpage").forward(request, response);
                        return;
                    }
                }
//                让请求继续线下执行
                filterChain.doFilter(request, response);
            }
        }, UsernamePasswordAuthenticationFilter.class);


        /**
         * 记住我
         */
        http.rememberMe()
                .tokenRepository(new InMemoryTokenRepositoryImpl())
                .tokenValiditySeconds(3600 * 24)
                .userDetailsService(userService);
    }
}
