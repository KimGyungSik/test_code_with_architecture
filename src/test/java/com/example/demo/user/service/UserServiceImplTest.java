package com.example.demo.user.service;

import com.example.demo.common.domain.exception.CertificationCodeNotMatchedException;
import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.FakeMailSender;
import com.example.demo.mock.FakeUserRepository;
import com.example.demo.mock.TestClockHolder;
import com.example.demo.mock.TestUuidHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserCreate;
import com.example.demo.user.domain.UserStatus;
import com.example.demo.user.domain.UserUpdate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

class UserServiceImplTest {

    private UserServiceImpl userServiceImpl; // 테스트 픽스처(테스트 대상)

    @BeforeEach
    void init() {
        FakeMailSender fakeMailSender = new FakeMailSender();
        FakeUserRepository fakeUserRepository = new FakeUserRepository();
        this.userServiceImpl = UserServiceImpl.builder()
                .certificationService(new CertificationService(fakeMailSender))
                .clockHolder(new TestClockHolder(1678530673958L))
                .uuidHolder(new TestUuidHolder("aaaaaa"))
                .userRepository(fakeUserRepository)
                .build();
        fakeUserRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());
        fakeUserRepository.save(User.builder()
                .id(2L)
                .email("0712kyungh@naver.com")
                .nickname("lok23")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaab")
                .status(UserStatus.PENDING)
                .lastLoginAt(0L)
                .build());
    }

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

        //when
        User result = userServiceImpl.create(userCreate);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(UserStatus.PENDING);
        assertThat(result.getCertificationCode()).isEqualTo("aaaaaa");
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
        assertThat(user.getLastLoginAt()).isEqualTo(1678530673958L);
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