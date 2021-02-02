package com.think.patchca;

/**
 * Patchca常量类
 *
 * @author veione
 */
public class Constants {
    /**
     * 验证码存储键
     */
    public final static String PATCHCA_SESSION_KEY = "PATCHCA_SESSION_KEY";
    /**
     * 验证码存储时间键
     */
    public final static String PATCHCA_SESSION_DATE = "PATCHCA_SESSION_DATE";
    /**
     * 验证码默认宽度
     */
    public static final int PATCHCA_IMAGE_WIDTH = 160;
    /**
     * 验证码默认高度
     */
    public static final int PATCHCA_IMAGE_HEIGHT = 70;
    /**
     * 背景类型：单项背景
     */
    public final static String PATCHCA_BACKGROUND_SINGLE_TYPE = "single";
    /**
     * 背景类型：渐变背景
     */
    public final static String PATCHCA_BACKGROUND_GRADIENT_TYPE = "gradient";
    /**
     * 验证码默认字符个数
     */
    public final static int PATCHCA_TEXT_CHAR_LENGTH = 6;
    /**
     * 验证码默认字符串
     */
    public final static String PATCHCA_TEXT_CHAR_STRING = "absdegkmnopwx23456789";
    /**
     * 验证码默认字体大小
     */
    public final static int PATCHCA_TEXT_FONT_SIZE = 45;
    /**
     * 验证码默认外边距
     */
    public final static int PATCHCA_TEXT_MARGIN_LENGTH = 10;
    /**
     * 字体颜色类型：单项
     */
    public static final String PATCHCA_COLOR_FACTORY_SINGLE = "single";
    /**
     * 字体颜色类型：随机
     */
    public static final String PATCHCA_COLOR_FACTORY_RANDOM = "random";
    /**
     * 字体颜色类型：渐变
     */
    public static final String PATCHCA_COLOR_FACTORY_GRADIENT = "gradient";
    /**
     * 文字滤镜：自定义配置滤镜
     */
    public static final String PATCHCA_FILTER_CONFIGURABLE = "Configurable";
    /**
     * 文字滤镜：曲线波纹
     */
    public static final String PATCHCA_FILTER_CURVESRIPPLE = "CurvesRipple";
    /**
     * 文字滤镜：漫波纹
     */
    public static final String PATCHCA_FILTER_DIFFUSERIPPLE = "DiffuseRipple";
    /**
     * 文字滤镜：双波纹
     */
    public static final String PATCHCA_FILTER_DOUBLERIPPLE = "DoubleRipple";
    /**
     * 文字滤镜：大理石波纹
     */
    public static final String PATCHCA_FILTER_MARBLERIPPLE = "MarbleRipple";
    /**
     * 文字滤镜：波纹
     */
    public static final String PATCHCA_FILTER_RIPPLE = "Ripple";
    /**
     * 文字滤镜：摆动波纹
     */
    public static final String PATCHCA_FILTER_WOBBLERIPPLE = "WobbleRipple";
    /**
     * 验证码类型：普通静态
     */
    public static final String PATCHCA_TYPE_NORMAL = "normal";
    /**
     * 验证码类型：动态GIF
     */
    public static final String PATCHCA_TYPE_GIF = "gif";
}
