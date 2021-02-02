package com.think.patchca.spring.boot;

import com.think.patchca.Constants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Arrays;
import java.util.List;

/**
 * 验证码组件参数
 *
 * @author veione
 */
@Data
@ConfigurationProperties(prefix = "patchca")
public class PatchcaProperties {
    /**
     * 宽度
     */
    private Integer width = Constants.PATCHCA_IMAGE_WIDTH;
    /**
     * 高度
     */
    private Integer height = Constants.PATCHCA_IMAGE_HEIGHT;
    /**
     * 验证码类型：normal | gif
     */
    private String type = Constants.PATCHCA_TYPE_NORMAL;
    /**
     * 内容
     */
    @NestedConfigurationProperty
    private Content content = new Content();
    /**
     * 背景色
     */
    @NestedConfigurationProperty
    private BackgroundColor backgroundColor = new BackgroundColor();
    /**
     * 字体
     */
    @NestedConfigurationProperty
    private Font font = new Font();
    /**
     * 颜色
     */
    @NestedConfigurationProperty
    private Color border = new Color();

    /**
     * 內边框
     */
    @NestedConfigurationProperty
    private Margin margin = new Margin();
    /**
     * 文字颜色
     */
    @NestedConfigurationProperty
    private Color color = new Color();

    @NestedConfigurationProperty
    private Filter filter = new Filter();

    @Data
    public static class Filter {
        /**
         * 滤镜类型：Configurable | CurvesRipple | DiffuseRipple | DoubleRipple | MarbleRipple | Ripple | WobbleRipple
         */
        private String type = "CurvesRipple";
    }

    @Data
    public static class BackgroundColor {
        /**
         * 背景类型 single：单项颜色值, gradient：渐变色
         */
        private String type = "single";
        /**
         * 开始渐变色
         */
        private String from = "lightGray";
        /**
         * 结束渐变色
         */
        private String to = "white";
        /**
         * 方向：horizontal | vertical | topLeftBottomRight | bottomLeftTopRight
         */
        private String direction = "horizontal";
        /**
         * 设置单项颜色值
         */
        private String color;
    }

    @Data
    public static class Content {
        /**
         * 自定义类型
         */
        private String type;
        /**
         * 内容源
         */
        private String source = "absdegkmnopwx23456789";
        /**
         * 内容长度
         */
        private Integer length = 6;
    }

    @Data
    public static class Font {
        /**
         * 自定义类型
         */
        private String type;
        /**
         * 大小
         */
        private Integer size = Constants.PATCHCA_TEXT_FONT_SIZE;
        /**
         * 字体列表
         */
        private List<String> families = Arrays.asList("Verdana", "Tahoma");
    }

    @Data
    public static class Margin {
        /**
         * 自定义类型
         */
        private String type;
        /**
         * 上边框
         */
        private Integer top = 0;
        /**
         * 左边框
         */
        private Integer left = Constants.PATCHCA_TEXT_MARGIN_LENGTH;
        /**
         * 右边框
         */
        private Integer right = Constants.PATCHCA_TEXT_MARGIN_LENGTH;
        /**
         * 下边框
         */
        private Integer bottom = 0;
    }

    @Data
    public static class Color {
        /**
         * 颜色类型：single：单项颜色 | random：随机颜色 | gradient：渐变色
         */
        private String type = "single";
        /**
         * 渐变颜色起始值
         */
        private String from = "20, 40, 80";
        /**
         * 渐变颜色随机值
         */
        private String to = "21,50,140";
        /**
         * 单项颜色值
         */
        private String value = "black";
    }
}
