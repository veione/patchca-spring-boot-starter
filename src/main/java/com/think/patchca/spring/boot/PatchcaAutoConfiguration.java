package com.think.patchca.spring.boot;

import com.think.patchca.DefaultPatchca;
import com.think.patchca.Patchca;
import com.think.patchca.util.ConfigHelper;
import org.patchca.background.BackgroundFactory;
import org.patchca.color.ColorFactory;
import org.patchca.filter.FilterFactory;
import org.patchca.font.FontFactory;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.text.renderer.TextRenderer;
import org.patchca.word.WordFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Patchca 验证码 的springBoot快速启动器
 *
 * @author veione
 */
@Configuration
@ConditionalOnClass(DefaultPatchca.class)
@EnableConfigurationProperties(PatchcaProperties.class)
public class PatchcaAutoConfiguration {
    private final PatchcaProperties properties;

    public PatchcaAutoConfiguration(PatchcaProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ConfigurableCaptchaService configurableCaptchaService() {
        ConfigurableCaptchaService configurable = ConfigHelper.getConfigServivce(properties.getType());
        /**
         * 设置宽高
         */
        configurable.setWidth(properties.getWidth());
        configurable.setHeight(properties.getHeight());
        BackgroundFactory backgroundFactory = ConfigHelper.getBackgroundFactory(properties.getBackgroundColor());
        WordFactory wordFactory = ConfigHelper.getWordFactory(properties.getContent());
        FontFactory fontFactory = ConfigHelper.getFontFactory(properties.getFont());
        ColorFactory colorFactory = ConfigHelper.getColorFactory(properties.getColor());
        TextRenderer textRenderer = ConfigHelper.getTextRenderer(properties.getMargin(), properties.getType());
        FilterFactory filterFactory = ConfigHelper.getFilterFactory(properties.getFilter(), colorFactory);

        configurable.setBackgroundFactory(backgroundFactory);
        configurable.setWordFactory(wordFactory);
        configurable.setColorFactory(colorFactory);
        configurable.setFontFactory(fontFactory);
        configurable.setTextRenderer(textRenderer);
        configurable.setFilterFactory(filterFactory);
        return configurable;
    }

    @Bean
    @ConditionalOnMissingBean
    public Patchca patchcaRender(ConfigurableCaptchaService cs) {
        return new DefaultPatchca(cs);
    }
}
