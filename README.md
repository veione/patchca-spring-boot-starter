<p align="center">
	<strong>简单快速集成 Patchca验证码</strong>
</p>

<p align="center">
    <a href="http://www.apache.org/licenses/LICENSE-2.0.html" target="_blank">
        <img src="http://img.shields.io/:license-apache-brightgreen.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/JDK-1.8+-green.svg" >
    </a>
    <a>
        <img src="https://img.shields.io/badge/springBoot-2.0+-green.svg" >
    </a>
</p>

## 项目介绍
`Patchca` 是 `Piotr Piastucki` 写的一个 **java** 验证码，打包成 **jar** 文件发布，`Patchca` 简单但功能强大。
`patchca-spring-boot-starter` 项目主要是为 `Patchca` 提供 **SpringBoot** 启动器的支持，
并且在此基础上增加了`GIF`动态验证码的功能，基于动态可配置的启动器，你可以直接在项目中引入该依赖，以减少后期的编码处理，大大简化了我们加入一个验证码功能模块的工作量。

## 显示效果
<div align="center">
    <div class="icon">
        <img src="https://bkimg.cdn.bcebos.com/pic/5243fbf2b2119313228319f765380cd790238db1?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1"></img>
        <p align="center">曲线波纹</p>
    </div>
     <div class="icon">
        <img src="https://bkimg.cdn.bcebos.com/pic/00e93901213fb80ed1f5809036d12f2eb9389455?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1"></img>
        <p align="center">双波纹</p>
    </div>
    <div class="icon">
        <img src="https://bkimg.cdn.bcebos.com/pic/359b033b5bb5c9ea9130e5a4d539b6003bf3b3b2?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1"></img>
        <p align="center">摆波纹</p>
    </div>
    <div class="icon">
        <img src="https://bkimg.cdn.bcebos.com/pic/500fd9f9d72a60595937005d2834349b033bba56?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1"></img>
        <p align="center">漫波纹</p>
    </div>
    <div class="icon">
        <img src="https://bkimg.cdn.bcebos.com/pic/0df3d7ca7bcb0a469d3d6a7e6b63f6246b60af5e?x-bce-process=image/resize,m_lfit,w_220,h_220,limit_1"></img>
        <p align="center">大理石波纹</p>
    </div>
</div>

## 如何使用

1. 引入 patchca-spring-boot-starter。

```xml
<dependency>
  <groupId>com.think</groupId>
  <artifactId>patchca-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```

2. 在Controller使用`Patchca`。

```java
@RestController
@RequestMapping("/patchca")
public class PatchcaController {

  @Autowired
  private Patchca patchca;

  @GetMapping("/render")
  public void render() {
      patchca.render();
  }

  @PostMapping("/valid")
  public void validDefaultTime(@RequestParam String code) {
    //default timeout 900 seconds
      patchca.validate(code);
  }

  @PostMapping("/validTime")
  public void validCustomTime(@RequestParam String code) {
      patchca.validate(code, 60);
  }

}
```

3. 发生错误会抛出异常，建议使用全局异常来处理。

```java
PatchcaException  //super Exception

PatchcaIncorrectException

PatchcaNotFoundException

PatchcaTimeoutException

PatchcaRenderException //If something is wrong then Image.write when render.
```

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = PatchcaException.class)
  public String patchcaExceptionHandler(PatchcaException patchcaException) {
    if (PatchcaException instanceof PatchcaIncorrectException) {
      return "验证码不正确";
    } else if (patchcaException instanceof PatchcaNotFoundException) {
      return "验证码未找到";
    } else if (patchcaException instanceof PatchcaTimeoutException) {
      return "验证码过期";
    } else {
      return "验证码渲染失败";
    }
  }
}
```

4. 自定义验证码参数,以下为默认配置。

```yaml
patchca:
  background-color:
    type: single
    from: yellow
    to: red
    direction: Vertical
    color: pink
  color:
    type: single
    value: blue
  font:
    size: 45
    families: Verdana, Tahoma
  width: 170
  height: 40

  margin:
    top: 10
    left: 10
    right: 10
    bottom: 10

  content:
    length: 6
    source: ABCDEFG123456789

  filter:
    type: MarbleRipple
  type: gif
```

4. 感谢
- [kaptcha-spring-boot-starter](https://gitee.com/baomidou/kaptcha-spring-boot-starter)
- [EasyCaptcha](https://gitee.com/whvse/EasyCaptcha/blob/master/README.md)
- Kevin Weiner编写的AnimatedGifEncoder.java
