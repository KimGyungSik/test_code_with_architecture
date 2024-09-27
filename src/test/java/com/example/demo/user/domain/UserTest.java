package com.example.demo.user.domain;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserTest {
    @Test
    public void UsesCreate_객체로_생성할_수_있다() throws Exception {
        //given
        UserCreate userCreate = UserCreate.builder()
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .build();

        //when
        User user = User.from(userCreate, new TestUuidHolder("aaaaaa"));
        
        //then
        assertThat(user.getId()).isNull();
        assertThat(user.getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(user.getNickname()).isEqualTo("kok");
        assertThat(user.getAddress()).isEqualTo("Seoul");
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaa");
    }
    @Test
    public void UsesUpdate_객체로_데이터를_업데이트_할_수_있다() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaa")
                .build();
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("kyungsik")
                .address("Busan")
                .build();

        //when
        user = user.update(userUpdate);

        //then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(user.getNickname()).isEqualTo("kyungsik");
        assertThat(user.getAddress()).isEqualTo("Busan");
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaa");
        assertThat(user.getLastLoginAt()).isEqualTo(100L);
    }
    @Test
    public void 로그인을_할_수_있고_로그인시_마지막_로그인_시간이_변경된다() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaa")
                .build();
        //when
        user = user.login(new TestClockHolder(1678530673958L));

        //then
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
    }
    @Test
    public void 유효한_인증_코드_계정을_활성화_할_수_있다() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaa")
                .build();
        //when
        user = user.certificate("aaaaaa");

        //then
        assertThat(user.getCertificationCode()).isEqualTo("aaaaaa");
    }
    @Test
    public void 잘못된_인증_코드_계정을_활성화_하려하면_에러를_던진다() throws Exception {
        //given
        User user = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .certificationCode("aaaaaa")
                .build();
        //when
        //then
        assertThatThrownBy(() -> {
            user.certificate("aaaaaabb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}
