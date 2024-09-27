package com.example.demo.post.controller.response;

import com.example.demo.post.domain.Post;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PostResponseTest {
    @Test
    public void Post로_응답을_생성할_수_있다() throws Exception {
        //given
        Post post = Post.builder()
                .content("helloworld")
                .writer(User.builder()
                        .email("0711kyungh@naver.com")
                        .nickname("kok")
                        .address("Seoul")
                        .status(UserStatus.ACTIVE)
                        .certificationCode("aaaaaa")
                        .build())
                .build();

        //when
        PostResponse postResponse = PostResponse.from(post);
        
        //then
        assertThat(post.getContent()).isEqualTo("helloworld");
        assertThat(post.getWriter().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("kok");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}
