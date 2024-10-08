package com.example.demo.mock;

import com.example.demo.post.controller.PostController;
import com.example.demo.post.controller.PostCreateController;
import com.example.demo.post.controller.port.PostService;
import com.example.demo.post.service.PostServiceImpl;
import com.example.demo.post.service.port.PostRepository;
import com.example.demo.user.controller.MyInfoController;
import com.example.demo.user.controller.UserController;
import com.example.demo.user.controller.UserCreateController;
import com.example.demo.user.controller.port.UserService;
import com.example.demo.user.service.CertificationService;
import com.example.demo.user.service.UserServiceImpl;
import com.example.demo.user.service.port.MailSender;
import com.example.demo.user.service.port.UserRepository;
import lombok.Builder;

public class TestContainer {
    public final MailSender mailSender;
    public final UserRepository userRepository;
    public final PostRepository postRepository;
    public final PostService postService;
    public final CertificationService certificationService;
    public final UserController userController;
    public final MyInfoController myInfoController;
    public final UserCreateController userCreateController;
    public final PostController postController;
    public final PostCreateController postCreateController;
    public final TestClockHolder testClockHolder;
    public final TestUuidHolder testUuidHolder;


    @Builder
    public TestContainer(long initialMillis, String initialUuid) {
        this.testClockHolder = new TestClockHolder(initialMillis);
        this.testUuidHolder = new TestUuidHolder(initialUuid);
        this.mailSender = new FakeMailSender();
        this.userRepository = new FakeUserRepository();
        this.postRepository = new FakePostRepository();
        this.postService = PostServiceImpl.builder()
                .postRepository(this.postRepository)
                .userRepository(this.userRepository)
                .clockHolder(testClockHolder)
                .build();
        this.certificationService = new CertificationService(this.mailSender);
        UserService userService = UserServiceImpl.builder()
                .uuidHolder(testUuidHolder)
                .clockHolder(testClockHolder)
                .userRepository(this.userRepository)
                .certificationService(this.certificationService)
                .build();
        this.userController = UserController.builder()
                .userService(userService)
                .build();
        this.myInfoController = MyInfoController.builder()
                .userService(userService)
                .build();
        this.userCreateController = UserCreateController.builder()
                .userService(userService)
                .build();
        this.postController = PostController.builder()
                .userController(userController)
                .postService(postService)
                .build();
        this.postCreateController = PostCreateController.builder()
                .postService(postService)
                .build();
    }
}
