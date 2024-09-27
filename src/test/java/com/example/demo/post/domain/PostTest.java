package com.example.demo.post.domain;

import com.example.demo.mock.TestClockHolder;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

public class PostTest {
    @Test
    public void PostCreate으로_게시물을_만들_수_있다() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .writerId(1)
                .content("helloworld")
                .build();
        User writer = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaa")
                .build();

        //when
        Post post = Post.from(postCreate, writer,new TestClockHolder(1678530673958L));

        //then
        assertThat(post.getContent()).isEqualTo("helloworld");
        assertThat(post.getWriter().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("kok");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getCreatedAt()).isEqualTo(1678530673958L);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaa");
    }
    @Test
    public void PostUpdate로_게시물을_수정할_수_있다() {
        //given
        PostUpdate postUpdate = PostUpdate.builder()
                .content("helloworld2")
                .build();
        User writer = User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("kok")
                .address("Seoul")
                .status(UserStatus.ACTIVE)
                .certificationCode("aaaaaa")
                .build();
        Post post = Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(writer)
                .build();

        //when
        post = post.update(postUpdate, new TestClockHolder(1678530673958L));

        //then
        assertThat(post.getContent()).isEqualTo("helloworld2");
        assertThat(post.getWriter().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(post.getWriter().getNickname()).isEqualTo("kok");
        assertThat(post.getWriter().getAddress()).isEqualTo("Seoul");
        assertThat(post.getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(post.getCreatedAt()).isEqualTo(1678530673958L);
        assertThat(post.getModifiedAt()).isEqualTo(1678530673958L);
        assertThat(post.getWriter().getCertificationCode()).isEqualTo("aaaaaa");
    }
}
