package com.think.patchca.spring.boot;

import com.think.patchca.Constants;
import com.think.patchca.DefaultPatchca;
import com.think.patchca.Patchca;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

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
    public DefaultPatchca defaultPatchca() {
        Properties prop = new Properties();

        prop.setProperty(Constants.PATCHCA_IMAGE_WIDTH, String.valueOf(properties.getWidth()));
        prop.setProperty(Constants.PATCHCA_IMAGE_HEIGHT, String.valueOf(properties.getHeight()));

        PatchcaProperties.Content content = properties.getContent();
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_CHAR_STRING, content.getSource());
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_CHAR_LENGTH, String.valueOf(content.getLength()));
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_CHAR_SPACE, String.valueOf(content.getSpace()));

        PatchcaProperties.BackgroundColor backgroundColor = properties.getBackgroundColor();
        prop.setProperty(Constants.PATCHCA_BACKGROUND_CLR_FROM, backgroundColor.getFrom());
        prop.setProperty(Constants.PATCHCA_BACKGROUND_CLR_TO, backgroundColor.getTo());

        PatchcaProperties.Border border = properties.getBorder();
        prop.setProperty(Constants.PATCHCA_BORDER, border.getEnabled() ? "yes" : "no");
        prop.setProperty(Constants.PATCHCA_BORDER_COLOR, border.getColor());
        prop.setProperty(Constants.PATCHCA_BORDER_THICKNESS, String.valueOf(border.getThickness()));

        PatchcaProperties.Font font = properties.getFont();
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_FONT_NAMES, font.getName());
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_FONT_SIZE, String.valueOf(font.getSize()));
        prop.setProperty(Constants.PATCHCA_TEXTPRODUCER_FONT_COLOR, font.getColor());

        DefaultPatchca defaultPatchca = new DefaultPatchca();
        //defaultPatchca.setConfig(new Config(prop));
        return defaultPatchca;
    }

    @Bean
    @ConditionalOnMissingBean
    public Patchca patchcaRender(DefaultPatchca defaultPatchca) {
        return new DefaultPatchca();
    }

}
