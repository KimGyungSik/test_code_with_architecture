package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.controller.port.UserReadService;
import com.example.demo.user.controller.response.MyProfileResponse;
import com.example.demo.user.controller.response.UserResponse;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import com.example.demo.user.infrastructure.UserEntity;
import com.example.demo.user.infrastructure.UserJpaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        ResponseEntity<UserResponse> result =testContainer.userController.getUserById(1);

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
            testContainer.userController.getUserById(1);
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
        assertThat(testContainer.userReadService.getById(1).getStatus()).isEqualTo(UserStatus.ACTIVE);
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
    @Test
    public void 사용자는_내_정보를_불러올_떄_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        // given
        TestContainer testContainer = TestContainer.builder()
                .initialMillis(100).build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(50L)
                .build());

        // when
        ResponseEntity<MyProfileResponse> result =testContainer.userController
                .getMyInfo("0711kyungh@naver.com");

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("lok22");
        assertThat(result.getBody().getAddress()).isEqualTo("Seoul");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
    }
    @Test
    public void 사용자는_내_정보를_수정할_수_있다()  {
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
        ResponseEntity<MyProfileResponse> result =testContainer.userController
                .updateMyInfo("0711kyungh@naver.com", UserUpdate.builder()
                        .address("Busan")
                        .nickname("Newha")
                        .build());

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(200));
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getId()).isEqualTo(1);
        assertThat(result.getBody().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(result.getBody().getNickname()).isEqualTo("Newha");
        assertThat(result.getBody().getAddress()).isEqualTo("Busan");
        assertThat(result.getBody().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(result.getBody().getLastLoginAt()).isEqualTo(100);
    }
}