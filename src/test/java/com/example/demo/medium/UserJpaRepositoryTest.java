package com.example.demo.medium;

import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest(showSql = true)
@TestPropertySource("classpath:test-application.properties")
@Sql("/sql/user-repository-test-data.sql")
class UserJpaRepositoryTest {
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    public void findByIdAndStatus_로_유저_데이터를_찾아올_수_있다() throws Exception {
        //given
        //when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1L,UserStatus.ACTIVE);

        //then
        assertThat(result.isPresent()).isTrue();
    }
    @Test
    public void findByIdAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다() throws Exception {
        //given
        //when
        Optional<UserEntity> result = userJpaRepository.findByIdAndStatus(1,UserStatus.PENDING);

        //then
//        assertThat(result.isPresent()).isFalse();
        assertThat(result.isEmpty()).isTrue();
    }
    @Test
    public void findByEmailAndStatus_로_유저_데이터를_찾아올_수_있다() throws Exception {
        //given
        //when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("0711kyungh@naver.com",UserStatus.ACTIVE);

        //then
        assertThat(result.isPresent()).isTrue();
    }
    @Test
    public void findByEmailAndStatus_는_데이터가_없으면_Optional_empty_를_내려준다() throws Exception {
        //given
        //when
        Optional<UserEntity> result = userJpaRepository.findByEmailAndStatus("0711kyungh@naver.com",UserStatus.PENDING);

        //then
//        assertThat(result.isPresent()).isFalse();
        assertThat(result.isEmpty()).isTrue();
    }
}