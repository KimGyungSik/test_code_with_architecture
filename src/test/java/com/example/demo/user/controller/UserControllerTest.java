package com.example.demo.user.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.user.controller.port.UserReadService;
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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        ResponseEntity<UserResponse> result = UserController.builder()
                .userReadService(testContainer.userReadService)
                .build()
                .getUserById(1);

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
        UserController userController = UserController.builder()
                .userReadService(new UserReadService() {
                    @Override
                    public User getByEmail(String email) {
                        return null;
                    }

                    @Override
                    public User getById(long id) {
                        throw new ResourceNotFoundException("Users", id);
                    }
                }).build();
        // when
        // then
        assertThatThrownBy(() -> {
            userController.getUserById(123445);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void 사용자는_인증_코드로_계정을_활성화_시킬_수_있다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaa-aaaaaa-aaaaaab"))
                .andExpect(status().isFound());
        UserEntity userEntity = userJpaRepository.findById(2L).get();
        assertThat(userEntity.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    @Test
    public void 사용자는_인증_코드가_일치하지_않을_경우_권한_없음_에러를_내려준다() throws Exception {
        mockMvc.perform(get("/api/users/2/verify")
                        .queryParam("certificationCode", "aaaaaa-aaaaaa-aaaaaabcccccc"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("자격 증명에 실패하였습니다."));
    }
    @Test
    public void 사용자는_내_정보를_불러올_떄_개인정보인_주소도_갖고_올_수_있다() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("EMAIL","0711kyungh@naver.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("0711kyungh@naver.com"))
                .andExpect(jsonPath("$.nickname").value("lok22"))
                .andExpect(jsonPath("$.address").value("Seoul"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
    @Test
    public void 사용자는_내_정보를_수정할_수_있다() throws Exception {
        //given
        UserUpdate userUpdate = UserUpdate.builder()
                .nickname("lok33")
                .address("Pangli")
                .build();

        //when
        //then
        mockMvc.perform(put("/api/users/me")
                        .header("EMAIL","0711kyungh@naver.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdate))) // jackson serialize해서 컨텐츠로 넘겨줘야함 -> ObjectMapper 필요
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("0711kyungh@naver.com"))
                .andExpect(jsonPath("$.nickname").value("lok33"))
                .andExpect(jsonPath("$.address").value("Pangli"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }
}