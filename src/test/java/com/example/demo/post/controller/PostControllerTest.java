package com.example.demo.post.controller;

import com.example.demo.common.domain.exception.ResourceNotFoundException;
import com.example.demo.mock.TestContainer;
import com.example.demo.post.controller.response.PostResponse;
import com.example.demo.post.domain.Post;
import com.example.demo.post.domain.PostUpdate;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

class PostControllerTest {

    @Test
    public void 사용자가_게시물을_단건_조회_할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());
        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(testContainer.userRepository.getById(1))
                .build());

        // when
        ResponseEntity<PostResponse> result = testContainer.postController.getById(1);

        // then
        assertThat(result.getBody().getContent()).isEqualTo("helloworld");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getWriter().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("lok22");
        assertThat(result.getBody().getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
    @Test
    public void 사용자는_존재하지_않는_게시물을_조회할_경우_에러가_난다() {
        // given
        TestContainer testContainer = TestContainer.builder().build();

        // when
        // then
        assertThatThrownBy(() -> {
            testContainer.postController
                    .getById(1);
        }).isInstanceOf(ResourceNotFoundException.class);
    }
    @Test
    public void 사용자는_게시물을_수정할_수_있다() {
        // given
        TestContainer testContainer = TestContainer.builder().initialMillis(100L).build();
        testContainer.userRepository.save(User.builder()
                .id(1L)
                .email("0711kyungh@naver.com")
                .nickname("lok22")
                .address("Seoul")
                .certificationCode("aaaaaa-aaaaaa-aaaaaaa")
                .status(UserStatus.ACTIVE)
                .lastLoginAt(0L)
                .build());
        testContainer.postRepository.save(Post.builder()
                .id(1L)
                .content("helloworld")
                .createdAt(1678530673958L)
                .modifiedAt(0L)
                .writer(testContainer.userRepository.getById(1))
                .build());
        PostUpdate postUpdate = PostUpdate.builder()
                .content("helloworld22")
                .build();
        // when
        ResponseEntity<PostResponse> result = testContainer.postController.update(1,postUpdate);

        // then
        assertThat(result.getBody().getContent()).isEqualTo("helloworld22");
        assertThat(result.getBody().getCreatedAt()).isEqualTo(1678530673958L);
        assertThat(result.getBody().getModifiedAt()).isEqualTo(100);
        assertThat(result.getBody().getWriter().getEmail()).isEqualTo("0711kyungh@naver.com");
        assertThat(result.getBody().getWriter().getNickname()).isEqualTo("lok22");
        assertThat(result.getBody().getWriter().getStatus()).isEqualTo(UserStatus.ACTIVE);
    }
}