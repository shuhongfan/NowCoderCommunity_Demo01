package com.shf.nowcoder.dao;

import com.shf.nowcoder.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User selectById(@Param("id") int id);

    User selectByName(@Param("username") String username);

    User selectByEmail(@Param("email") String email);

    int insertUser(User user);

    int updateStatus(@Param("id") int id, @Param("status") int status);
    int updateHeader(@Param("id") int id, @Param("header") String header);

    int updatePassword(@Param("id") int id, @Param("password") String password);
}
