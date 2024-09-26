# Toy for test on spring!

해당 레포지토리는 테스트 코드 추가를 위한 토이 프로젝트 입니다.
코드가 얼마나 정상 동작하는지, 프로덕션에서 잘 동작하는지를 검증하지는 말아주세요.
스프링에 테스트를 넣는 과정을 보여드리기 위해 만들어진 레포지토리입니다.
당연히 완벽하지 않습니다.

## 실행하기

### 00. 바로 시작

h2를 이용하여 `auto create table`을 하고 있기 때문에 바로 실행이 가능합니다.

### 01. 이메일 인증

> 단 이 프로젝트는 사용자가 가입할 때 이메일 인증을 하기위해 메일을 발송하는 코드가 있습니다.

이메일이 제대로 발송되는지 확인해보고 싶으신 분들은 [해당 document 파일](./document/connect-mail-sender.md)을 따라해주세요.
관련된 자료는 라이브러리나 Gmail 정책에 따라 UI와 방법이 달라질 수 있습니다.
최신화 된 정보를 제공하지 않으니, 가급적 문서를 참조해주시고, contribution 해주시면 감사하겠습니다.

## 관리 도구로 바로가기

- [h2-console](http://localhost:8080/h2-console)
- [Openapi-doc](http://localhost:8080/swagger-ui.html)

![img_4.png](img_4.png)
* ### jpa와 h2 연결 테스트
![img.png](img.png)
* ### sava하는 코드들 중복
![img_2.png](img_2.png)
![img_5.png](img_5.png)
![img_3.png](img_3.png)
* 해결

* ## UserService 리팩터링
  * ### get은 애초에 데이터가 없으면 에러를 던진다는 의미가 내포되어 있음
    * ![img_6.png](img_6.png)
    * ![img_7.png](img_7.png)
* ## UserServiceTest
  * ### 두번쨰 테스트 진행시 데이터가 이미 있어서 충돌 발생 
    * ![img_8.png](img_8.png)
  * ### Repository 테스트에서는 @DataJpaTest가 자동으로 롤백해줬기 때문에 가능했던것
  * ### 해결 -> @SqlGroup 사용
    * #### @SqlGroup에서는 Sql 파일을 여러 개 지정해서 실행시킬 수 있음
    * ![img_9.png](img_9.png)
    * ![img_10.png](img_10.png)
  * ### JavaMailSender를 더미로 대체
    * #### SimpleMailMessage를 사용하는 send가 호출돼도 아무것도 하지말라는 코드
      * ![img_12.png](img_12.png)
    * #### JavaMailSender라는 Bean객체를 Mock으로 선언된 객체로 덮어쓰기 하는것
      * ![img_11.png](img_11.png)

  * ### ObjectMapper
    * ![img_13.png](img_13.png)
    * ![img_14.png](img_14.png)

* ## UserCreateDto, UserUpdateDto들은 Service패키지에서 참조해야 하므로 domain패키지로 이동
* ## UserRepository를 infrastructure에 두면 상위 모듈인 service에서 infrastructure인 패키지에 의존하는 그림이 되게 되는데 그렇게 되면 안되기 떄문에
  * ### -> service 쪽으로 이동
  * ### service에서 사용하는 인터페이스들을 port패키지에 몰아 넣어줌
* ![img_15.png](img_15.png)
  * ### MailSender를 주입해줘야하는데 fake로 만들어줌
  * ### 실제로 어떤 값이 들어왔는지 볼 수 있게 멤버변수 선언
    * ![img_16.png](img_16.png)
    * ![img_17.png](img_17.png)
* ## 도메인과 영속성 객체를 구분해서 우리의 시스템을 외부 시스템과 완전히 독립적이게 구성이 가능
  * ### User 도메인에서 영속성 객체로 바꿔주는 방법 -> X 도메인은 인프라 레이어의 정보를 모르는 것이 좋기 때문
    * ![img_18.png](img_18.png)
    * ![img_19.png](img_19.png)
    * 