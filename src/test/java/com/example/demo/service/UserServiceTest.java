package com.example.demo.service;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        String email = "0711kyungh@naver.com";

        //when
        UserEntity result = userService.getByEmail(email);

        //then
        assertThat(result.getNickname()).isEqualTo("lok22");
    }
    @Test
    public void getByEmail은_PENDING_상태인_유저를_찾아올_수_없다() throws Exception {
        //given
        String email = "0712kyungh@naver.com";

        //when
        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        //when
        UserEntity result = userService.getById(1);

        //then
        assertThat(result.getNickname()).isEqualTo("lok22");
    }
    @Test
    public void getById은_PENDING_상태인_유저를_찾아올_수_없다() throws Exception {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            UserEntity result = userService.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }

}