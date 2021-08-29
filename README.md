## 引言

本文将讨论如何针对 Spring Boot 程序的 Web 层进行单元测试的，借助 MockMvc API 以及常见的测试库实现 Web 层方法的测试覆盖，以及如何定制 MockMvc。



## 准备工作

- Java 8

- Maven 3

- Spring Boot 2.x.x

新建一个 Spring Boot 工程，并且保证项目有以下Maven 依赖：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```





## 配置 MockMvc

首先，创建对应的 Controller 类的单元测试类，使用 `@SpringBootTest` 注解标记测试类，来指明是一个基于 Spring Boot 的单元测试类，运行测试类时，框架会创建一个应用上下文，程序中所有配置过的 Bean 都会被创建到这个应用上下文中，比如 Controller 类所依赖的一些 Service 组件或者配置组件。接着使用 `@AutoConfigureMockMvc`  注解自动装配在 Web 层测试发挥关键作用的 MockMvc 对象，我们编写的控制器方法就是通过 MockMvc 实现测试调用的。

```Java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    //....
}
```

另外，编写测试方法前，官方建议先静态导入下面这些类，用于编写简洁的步骤方法和验证方法。

```Java
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

```

这里简单说明下上方所导入类的作用，`MockMvcRequestBuilder` 用于构建 HTTP 请求数据：参数，方式，请求头等等，API 调用方式采用了建造者模式，值得学习；`MockMvcResultMatcher` 则是对响应结果的信息匹配：状态码，内容，方式等等；`MockMvcResultHandlers` 主要是表示对结果的额外操作，比如打印，日志记录等。



## 测试 GET 方法

从最简单的开始入手，当我们实现了一个 GET 方法请求的接口访问用户信息路径为：

```Java
http://localhost:8080/user/1

```

此时要实现的测试代码如下：

```Java
@Test
void should_get_user() throws Exception {
    mockMvc.perform(get("/user/{id}", 1L))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test"));
}
```

可以看出这是一个请求路径上带参数的 GET 请求，再看下构建 GET 方法签名 `get(String urlTemplate, Object... uriVar)`，可以看出如果存在多个参数的话，以可变参数列表方式补充即可。

另外，上述的测试方法实现里通过`status().isOk()`断言了响应码为 200，通过 `jsonPath("$.username").value("test")` 断言了响应结果为 json 数据时，username 字段的值为 test，这里借助了 *`MockMvcResultMatchers.jsonPath`* 实现对 json 数据快速提取，想要进一步学习的话可以搜索 jsonPath 了解。

上述代码就完成了对一个 GET 请求的测试访问和断言，那问题又来了，如果是请求路径上没有带参数的又该如何写测试呢？这里我们构建了一个GET 请求，访问路径如下：

```Java
http://localhost:8080/user/getScore?id=1

```

对于这种请求编写测试方法如下，使用 `MockHttpServletRequestBuilder.queryParam` 补充具体的请求参数键值对，如果存在多个请求参数的，还可通过链式调用的方式追加。

```Java
@Test
void should_getScore() throws Exception {
    mockMvc.perform(get("/user/getScore").queryParam("id", "1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("100"));
}
```



## 测试 POST 方法

讲解完测试 GET 请求方法，我们再了解下 POST 方法如何测试，这里按照 POST 请求携带的内容类型（Content-Type） 分，大致有三种：表单提交时使用的格式-`application/x-www-form-urlencoded`，JSON 数据格式-`application/json`，文件上传时使用的格式-`multipart/form-data`，每种情况对应的测试代码如下：

**测试表单提交POST方法**：

```Java
@Test
void should_login() throws Exception {
    mockMvc.perform(post("/user/login")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .content("username=test&password=pwd"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test"));
}
```

**测试JSON 数据提交 POST方法**：

```Java
@Test
void should_login2() throws Exception {
    mockMvc.perform(post("/user/login2")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"username\": \"test\",\"password\": \"pwd\"}"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("test"));
}
```

**测试文件上传 POST 方法**：

```Java
@Test
public void should_doc() throws Exception {
    mockMvc.perform(multipart("/doc")
                    .file("file", "ABC".getBytes("UTF-8")))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data").value("ABC"));
}
```

从上面代码可以看出其实各种请求方法的测试代码的编写大同小异，还是很容易掌握的，接下来再了解一些关于 MockMvc 进阶一些的内容。



## MockMvc 进阶

前面我们使用到的 MockMvc 是由 Spring 帮我们注入的，如果我们想要自定义 MockMvc，又该如何做呢？这里官方提供了 `MockMvcBuilders`  帮助我们构建全局的 MockMvc，并且可以进行全局默认的配置，定义一些公共操作，比如打印结果，断言响应码等等，具体实现方法可以参见下方代码示例：

```Java
@SpringBootTest
class MockmvcDemoApplicationTests {

  private MockMvc mockMvc;

  @BeforeAll
  void setUp(WebApplicationContext wac) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
        .alwaysExpect(status().isOk())
        .alwaysExpect(content().contentType(MediaType.APPLICATION_JSON))
        .alwaysDo(print())
        .build();
  }
}
```

另外之前提到使用 @SpringBootTest 会创建一个完整的应用上下文，装载所有 Bean，如果应用本身比较庞大，就会造成测试类启动时间过长的问题，那有什么问题可以在测试 Web 层时加速应用的启动呢？为了加快运行测试用例时应用的启动速度，官方提供了专门的注解 `@WebMvcTest`，保证只初始化 Web 层，而不是整个应用上下文，并且可以指定某个控制器，达到只对特定控制器以及依赖进行初始化的作用，大大加速测试用例的运行。

```Java
@WebMvcTest(controllers = UserController.class)
class UserController2Test {

    @Autowired
    private MockMvc mockMvc;

} 
```

除了用注解方式，我们还可以用 API 形式实现单个控制器的注入和测试，同样也是借助 `MockMvcBuilders` 就可以实现，可参考下方代码：

```Java
class UserController3Test {
    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }
}
```

需要注意的是使用 standaloneSetup 不会读取任何配置，更贴近这个控制器类的单元测试。



## 总结

好了，以上就是本文关于 Spring Boot 程序如何做好 Web 层的测试的全部介绍，可以看出对 Web 层的测试并不是很复杂，相关 API 的可读性也很高，不过需要注意一点的是，利用 MockMvc 对 Web 层进行测试底层并不是真正地走网络请求进行接口访问，也没有启动 Web 容器，底层实际只是对 Servlet API 的 Mock 实现，因此跟传统的端到端集成测试还是有很大的区别的，如果只是正对自己编写 Web 层代码做简单集成测试和单元测试时，可以参考前文介绍的方法。

最后，关于MockMvc 相关以及 Spring Boot 实现 Web 层测试等更深入的内容可以参考文末给出的官方文档地址进一步了解，希望本文的介绍对日常开发中有所帮助，后续将继续介绍关于利用 Spring Boot 做好测试的文章，敬请期待。



## 参考阅读

- 3.7. MockMvc：[https://docs.spring.io/spring-framework/docs/5.3.9/reference/html/testing.html#spring-mvc-test-framework](https://docs.spring.io/spring-framework/docs/5.3.9/reference/html/testing.html#spring-mvc-test-framework)

- **26.3.13. Auto-configured Spring MVC Tests**：[https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications.spring-mvc-tests](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing.spring-boot-applications.spring-mvc-tests)

- 文章代码示例工程：[https://github.com/developer-wenren/mockmvc-demo](https://github.com/developer-wenren/mockmvc-demo)



