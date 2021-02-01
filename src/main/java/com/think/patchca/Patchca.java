package com.think.patchca;

/**
 * 验证码组件接口
 *
 * @author veione
 */
public interface Patchca {
    /**
     * 渲染验证码
     *
     * @return 验证码内容
     */
    String render();

    /**
     * 校验验证码,默认超时15分钟(900s)
     *
     * @param code
     * @return
     */
    boolean validate(String code);

    /**
     * 校验验证码
     *
     * @param code   需要验证的字符串
     * @param second 超时时间（秒）
     * @return 是否验证成功
     */
    boolean validate(String code, long second);
}
