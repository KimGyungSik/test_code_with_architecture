package com.example.demo.medium;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
@TestPropertySource("classpath:test-application.properties")
@SqlGroup({
        @Sql(value = "/sql/user-service-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/sql/delete-all-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @MockBean
    private JavaMailSender mailSender;

    @Test
    public void getByEmail은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        String email = "0711kyungh@naver.com";

        //when
        User result = userServiceImpl.getByEmail(email);

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
            User result = userServiceImpl.getByEmail(email);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void getById은_ACTIVE_상태인_유저를_찾아올_수_있다() throws Exception {
        //given
        //when
        User result = userServiceImpl.getById(1);

        //then
        assertThat(result.getNickname()).isEqualTo("lok22");
    }
    @Test
    public void getById은_PENDING_상태인_유저를_찾아올_수_없다() throws Exception {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            User result = userServiceImpl.getById(2);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void userCreateDto_를_이용하여_유저를_생성할_수_있다() throws Exception {
        //given
        UserCreate userCreate = UserCreate.builder()
                .email("0714kyun@naver.com")
                .address("GEQ")
                .nickname("saaa")
                .build();
        BDDMockito.doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        //when
        User result = userServiceImpl.create(userCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
//        assertThat(result.getCertificationCode()).isEqualTo("??"); FIXME
    }
    @Test
    public void userUpdateDto_를_이용하여_유저를_수정할_수_있다() throws Exception {
        //given
        UserUpdate userUpdate = UserUpdate.builder()
                .address("Incheon")
                .nickname("saaa22")
                .build();

        //when
        userServiceImpl.update(1, userUpdate);

        //then
        User user = userServiceImpl.getById(1);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getAddress()).isEqualTo("Incheon");
        assertThat(user.getNickname()).isEqualTo("saaa22");
    }
    @Test
    public void user를_로그인_시키면_마지막_로그인_시간이_변경된다() throws Exception {
        //given
        //when
        userServiceImpl.login(1);

        //then
        User user = userServiceImpl.getById(1);
        assertThat(user.getLastLoginAt()).isGreaterThan(0L);
//        assertThat(userEntity.getLastLoginAt()).isEqualTo(????); FIXME
    }
    @Test
    public void PENDING_상태의_사용자는_인증_코드로_ACTIVE_시킬_수_있다() throws Exception {
        //given
        //when
        userServiceImpl.verifyEmail(2, "aaaaaa-aaaaaa-aaaaaab");

        //then
        User user = userServiceImpl.getById(2);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    @Test
    public void PENDING_상태의_사용자는_잘못된_인증_코드를_받으면_에러를_던진다() throws Exception {
        //given
        //when
        //then
        assertThatThrownBy(() -> {
            userServiceImpl.verifyEmail(2,"aaaaaaaaaaaaa");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}