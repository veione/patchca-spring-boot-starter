package org.patchca.service;

import org.patchca.utils.encoder.FrameList;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Gif实现
 *
 * @author veione
 */
public class GifCaptchaService extends ConfigurableCaptchaService {
    protected final int length = 6;

    @Override
    public Captcha getCaptcha() {
        String word = wordFactory.getNextWord();
        List<BufferedImage> frames = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            backgroundFactory.fillBackground(frame);
            textRenderer.draw(word, frame, fontFactory, colorFactory);
            frame = filterFactory.applyFilters(frame);
            frames.add(frame);
        }
        return new Captcha(word, null, new FrameList(frames));
    }
}
