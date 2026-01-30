package com.bbp.bbp_backend.mapper;

import org.apache.ibatis.annotations.*;

import java.util.Map;
@Mapper
public interface UserMapper {

    @Select("SELECT id, username, password_hash AS \"passwordHash\" " +
            "FROM users " +
            "WHERE username = #{username}")
    Map<String, Object> findByUsername(@Param("username") String username);

    @Insert("INSERT INTO users(username, password_hash) " +
            "VALUES(#{username}, #{passwordHash})")
    int insert(@Param("username") String username,
               @Param("passwordHash") String passwordHash);
}
