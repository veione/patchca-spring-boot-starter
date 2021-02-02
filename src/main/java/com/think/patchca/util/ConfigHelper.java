package com.think.patchca.util;

import com.think.patchca.Constants;
import com.think.patchca.spring.boot.PatchcaProperties;
import org.patchca.background.BackgroundFactory;
import org.patchca.background.GradientBackgroundFactory;
import org.patchca.background.SingleColorBackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.color.GradientColorFactory;
import org.patchca.color.RandomColorFactory;
import org.patchca.color.SingleColorFactory;
import org.patchca.filter.ConfigurableFilterFactory;
import org.patchca.filter.FilterFactory;
import org.patchca.filter.predefined.CurvesRippleFilterFactory;
import org.patchca.filter.predefined.DiffuseRippleFilterFactory;
import org.patchca.filter.predefined.DoubleRippleFilterFactory;
import org.patchca.filter.predefined.MarbleRippleFilterFactory;
import org.patchca.filter.predefined.RippleFilterFactory;
import org.patchca.filter.predefined.WobbleRippleFilterFactory;
import org.patchca.font.FontFactory;
import org.patchca.font.RandomFontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.service.GifCaptchaService;
import org.patchca.text.renderer.BestFitTextRenderer;
import org.patchca.text.renderer.GifTextRender;
import org.patchca.text.renderer.TextRenderer;
import org.patchca.word.AdaptiveRandomWordFactory;
import org.patchca.word.WordFactory;

import java.awt.Color;
import java.awt.image.BufferedImageOp;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 配置帮助类
 *
 * @author veione
 */
public final class ConfigHelper {
    /**
     * 类全限定名正则验证
     */
    private static final Pattern FQN_PATTERN =
            Pattern.compile("[a-zA-Z]+[0-9a-zA-Z_]*(\\.[a-zA-Z]+[0-9a-zA-Z_]*)*\\.[a-zA-Z]+[0-9a-zA-Z_]*(\\$[a-zA-Z]+[0-9a-zA-Z_]*)*");

    /**
     * 获取颜色值
     *
     * @param paramName
     * @param paramValue
     * @param defaultColor
     * @return
     */
    public static Color getColor(String paramName, String paramValue, Color defaultColor) {
        Color color;
        if ("".equals(paramValue) || paramValue == null) {
            color = defaultColor;
        } else if (paramValue.indexOf(",") > 0) {
            color = createColorFromCommaSeparatedValues(paramName, paramValue);
        } else {
            color = createColorFromFieldValue(paramName, paramValue);
        }
        return color;
    }

    /**
     * 根据给定的颜色值获取颜色对象
     *
     * @param paramName
     * @param paramValue
     */
    public static Color createColorFromCommaSeparatedValues(String paramName, String paramValue) {
        Color color;
        String[] colorValues = paramValue.split(",");
        try {
            int r = Integer.parseInt(colorValues[0].trim());
            int g = Integer.parseInt(colorValues[1].trim());
            int b = Integer.parseInt(colorValues[2].trim());
            if (colorValues.length == 4) {
                int a = Integer.parseInt(colorValues[3].trim());
                color = new Color(r, g, b, a);
            } else if (colorValues.length == 3) {
                color = new Color(r, g, b);
            } else {
                throw new ConfigException(paramName, paramValue,
                        "Color can only have 3 (RGB) or 4 (RGB with Alpha) values.");
            }
        } catch (NumberFormatException nfe) {
            throw new ConfigException(paramName, paramValue, nfe);
        } catch (ArrayIndexOutOfBoundsException aie) {
            throw new ConfigException(paramName, paramValue, aie);
        } catch (IllegalArgumentException iae) {
            throw new ConfigException(paramName, paramValue, iae);
        }
        return color;
    }

    /**
     * 根据给定的颜色值获取颜色对象
     *
     * @param paramName
     * @param paramValue
     */
    public static Color createColorFromFieldValue(String paramName, String paramValue) {
        Color color;
        try {
            Field field = Class.forName("java.awt.Color").getField(paramValue);
            color = (Color) field.get(null);
        } catch (NoSuchFieldException nsfe) {
            throw new ConfigException(paramName, paramValue, nsfe);
        } catch (ClassNotFoundException cnfe) {
            throw new ConfigException(paramName, paramValue, cnfe);
        } catch (IllegalAccessException iae) {
            throw new ConfigException(paramName, paramValue, iae);
        }
        return color;
    }

    /**
     * 获取背景工厂对象
     *
     * @param background
     * @return
     */
    public static BackgroundFactory getBackgroundFactory(PatchcaProperties.BackgroundColor background) {
        String type = background.getType();
        if (Constants.PATCHCA_BACKGROUND_SINGLE_TYPE.equals(type)) {
            final String color = background.getColor();
            SingleColorBackgroundFactory backgroundFactory = new SingleColorBackgroundFactory(getColor(color, color, Color.WHITE));
            return backgroundFactory;
        } else if (Constants.PATCHCA_BACKGROUND_GRADIENT_TYPE.equals(type)) {
            final String from = getValue(background.getFrom(), "192, 192, 0");
            final String to = getValue(background.getTo(), "192, 128, 128");
            final String directionValue = background.getDirection();

            final Color startColor = new Color(192, 192, 0);
            final Color endColor = new Color(192, 128, 128);
            final GradientBackgroundFactory.Direction direction = getBackgroundDirection(directionValue);

            final GradientBackgroundFactory backgroundFactory = new GradientBackgroundFactory(getColor("from color", from, startColor), getColor("to color", to, endColor), direction);
            return backgroundFactory;
        } else if (isFqn(type)) {
            BackgroundFactory backgroundFactory = newInstance("background factory type", type, new SingleColorBackgroundFactory());
            return backgroundFactory;
        }

        throw new ConfigException(type, type, "Invalid config type for background factory");
    }

    /**
     * 获取背景渐变方向
     *
     * @param direction
     * @return
     */
    public static GradientBackgroundFactory.Direction getBackgroundDirection(String direction) {
        switch (direction) {
            case "Vertical":
                return GradientBackgroundFactory.Direction.Vertical;
            case "TopLeftBottomRight":
                return GradientBackgroundFactory.Direction.TopLeftBottomRight;
            case "BottomLeftTopRight":
                return GradientBackgroundFactory.Direction.BottomLeftTopRight;
            default:
                return GradientBackgroundFactory.Direction.Horizontal;
        }
    }

    /**
     * 获取属性值
     *
     * @param paramValue
     * @param defaultValue
     * @param <T>
     * @return
     */
    public static <T> T getValue(T paramValue, T defaultValue) {
        if (paramValue == null) {
            return defaultValue;
        } else if (paramValue != null && paramValue instanceof String && ((String) paramValue).length() > 0) {
            return paramValue;
        } else {
            return defaultValue;
        }
    }

    /**
     * 获取文字工厂
     *
     * @param content
     * @return
     */
    public static WordFactory getWordFactory(PatchcaProperties.Content content) {
        AdaptiveRandomWordFactory wordFactory = new AdaptiveRandomWordFactory();
        int length = content.getLength() > 0 ? content.getLength() : Constants.PATCHCA_TEXT_CHAR_LENGTH;
        wordFactory.setMaxLength(length);
        wordFactory.setMinLength(length);
        wordFactory.setCharacters(Constants.PATCHCA_TEXT_CHAR_STRING);
        final String type = content.getType();

        if (type != null && type.length() > 0 && isFqn(type)) {
            wordFactory = newInstance("word factory", type, wordFactory);
            return wordFactory;
        }
        return wordFactory;
    }

    /**
     * 获取字体工厂
     *
     * @param font
     * @return
     */
    public static FontFactory getFontFactory(PatchcaProperties.Font font) {
        final Integer size = font.getSize();
        final Integer fontSize = getValue(size, Constants.PATCHCA_TEXT_FONT_SIZE);
        RandomFontFactory fontFactory = new RandomFontFactory();
        fontFactory.setFamilies(font.getFamilies());
        fontFactory.setMaxSize(fontSize);
        fontFactory.setMinSize(fontSize);
        final String type = font.getType();

        if (type != null && type.length() > 0 && isFqn(type)) {
            fontFactory = newInstance("font factory", type, fontFactory);
            return fontFactory;
        }
        return fontFactory;
    }

    /**
     * 获取文字渲染器
     *
     * @param margin
     * @param captchaType
     * @return
     */
    public static TextRenderer getTextRenderer(PatchcaProperties.Margin margin, String captchaType) {
        if (Constants.PATCHCA_TYPE_GIF.equalsIgnoreCase(captchaType)) {
            return new GifTextRender();
        } else {
            TextRenderer renderer = new BestFitTextRenderer();
            renderer.setTopMargin(getValue(margin.getTop(), Constants.PATCHCA_TEXT_MARGIN_LENGTH));
            renderer.setLeftMargin(getValue(margin.getLeft(), Constants.PATCHCA_TEXT_MARGIN_LENGTH));
            renderer.setBottomMargin(getValue(margin.getBottom(), Constants.PATCHCA_TEXT_MARGIN_LENGTH));
            renderer.setRightMargin(getValue(margin.getRight(), Constants.PATCHCA_TEXT_MARGIN_LENGTH));

            final String type = margin.getType();
            if (type != null && type.length() > 0 && isFqn(type)) {
                renderer = newInstance("text render", type, renderer);
                return renderer;
            }
            return renderer;
        }
    }

    /**
     * 获取颜色工厂
     *
     * @param color
     * @return
     */
    public static ColorFactory getColorFactory(PatchcaProperties.Color color) {
        final String type = color.getType();
        if (Constants.PATCHCA_COLOR_FACTORY_SINGLE.equalsIgnoreCase(type)) {
            Color colorValue = getColor("single color", color.getValue(), Color.BLACK);
            ColorFactory colorFactory = new SingleColorFactory(colorValue);
            return colorFactory;
        } else if (Constants.PATCHCA_COLOR_FACTORY_RANDOM.equalsIgnoreCase(type)) {
            RandomColorFactory colorFactory = new RandomColorFactory();
            colorFactory.setMin(getColor("from color", color.getFrom(), new Color(20, 40, 80)));
            colorFactory.setMax(getColor("to color", color.getTo(), new Color(21, 50, 140)));
            return colorFactory;
        } else if (Constants.PATCHCA_COLOR_FACTORY_GRADIENT.equalsIgnoreCase(type)) {
            GradientColorFactory colorFactory = new GradientColorFactory();
            colorFactory.setStart(getColor("from color", color.getFrom(), new Color(192, 192, 0)));
            colorFactory.setStep(getColor("to color", color.getTo(), new Color(192, 128, 128)));
            return colorFactory;
        } else if (isFqn(type)) {
            ColorFactory colorFactory = newInstance("color factory", type, new SingleColorFactory(Color.BLACK));
            return colorFactory;
        }

        throw new ConfigException(type, type, "Invalid config type for color factory");
    }

    /**
     * 获取滤镜工厂
     *
     * @param filter
     * @param colorFactory
     * @return
     */
    public static FilterFactory getFilterFactory(PatchcaProperties.Filter filter, ColorFactory colorFactory) {
        final String type = filter.getType();

        if (Constants.PATCHCA_FILTER_CONFIGURABLE.equalsIgnoreCase(type)) {
            final ConfigurableFilterFactory filterFactory = new ConfigurableFilterFactory();
            List<BufferedImageOp> filters = new ArrayList<>();
            filterFactory.setFilters(filters);
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_CURVESRIPPLE.equalsIgnoreCase(type)) {
            CurvesRippleFilterFactory filterFactory = new CurvesRippleFilterFactory(colorFactory);
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_DIFFUSERIPPLE.equalsIgnoreCase(type)) {
            DiffuseRippleFilterFactory filterFactory = new DiffuseRippleFilterFactory();
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_DOUBLERIPPLE.equalsIgnoreCase(type)) {
            DoubleRippleFilterFactory filterFactory = new DoubleRippleFilterFactory();
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_MARBLERIPPLE.equalsIgnoreCase(type)) {
            MarbleRippleFilterFactory filterFactory = new MarbleRippleFilterFactory();
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_RIPPLE.equalsIgnoreCase(type)) {
            RippleFilterFactory filterFactory = new RippleFilterFactory();
            return filterFactory;
        } else if (Constants.PATCHCA_FILTER_WOBBLERIPPLE.equalsIgnoreCase(type)) {
            WobbleRippleFilterFactory filterFactory = new WobbleRippleFilterFactory();
            return filterFactory;
        } else if (isFqn(type)) {
            FilterFactory filterFactory = newInstance("filter factory", type, new CurvesRippleFilterFactory(colorFactory));
            return filterFactory;
        }
        throw new ConfigException(type, type, "Invalid config type for filter factory");
    }

    /**
     * 实例化给定的对象
     *
     * @param paramName
     * @param paramValue
     * @param defaultInstance
     * @return
     */
    public static <T> T newInstance(String paramName, String paramValue, T defaultInstance) {
        T instance;
        if ("".equals(paramValue) || paramValue == null) {
            instance = defaultInstance;
        } else {
            try {
                instance = (T) Class.forName(paramValue).newInstance();
            } catch (IllegalAccessException iae) {
                throw new ConfigException(paramName, paramValue, iae);
            } catch (ClassNotFoundException cnfe) {
                throw new ConfigException(paramName, paramValue, cnfe);
            } catch (InstantiationException ie) {
                throw new ConfigException(paramName, paramValue, ie);
            }
        }

        return instance;
    }

    /**
     * 判断是否为Java全限定名
     *
     * @param clazz
     * @return
     */
    private static boolean isFqn(String clazz) {
        final Matcher matcher = FQN_PATTERN.matcher(clazz);
        return matcher.find();
    }

    /**
     * 获取验证码服务
     *
     * @param type
     * @return
     */
    public static ConfigurableCaptchaService getConfigServivce(String type) {
        if (Constants.PATCHCA_TYPE_GIF.equalsIgnoreCase(type)) {
            return new GifCaptchaService();
        } else {
            return new ConfigurableCaptchaService();
        }
    }
}
