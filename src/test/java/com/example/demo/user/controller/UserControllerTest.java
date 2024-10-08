package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

class UserControllerTest {

    @Test
    public void 사용자는_특정_유저의_정보를_개인정보는_소거된채_전달_받을_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<UserResponse> result =testContainer.userController.getById(1);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("lok22");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
    }
    @Test
    public void 사용자는_존재하지_않는_유저의_아이디로_api_호출할_경우_404_응답을_받는다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.userController.getById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .build());

        // when
        ResponseEntity<Void> result = testContainer.userController
                .verifyEmail(1,"aaaaaa-aaaaaa-aaaaaaa");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        assertThat(result.getBody()).isNull();
        assertThat(testContainer.userRepository.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    @Test
    public void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.PENDING)
                .lastLoginAt(100L)
                .build());

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.userController
                    .verifyEmail(1,"aaaaaa-aaaaaa-aaaaaaabb");
        }).isInstanceOf(CertificationCodeNotMatchedException.class);
    }
}