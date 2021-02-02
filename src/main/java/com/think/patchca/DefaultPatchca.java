package com.think.patchca;

import com.think.patchca.exception.PatchcaIncorrectException;
import com.think.patchca.exception.PatchcaNotFoundException;
import com.think.patchca.exception.PatchcaRenderException;
import com.think.patchca.exception.PatchcaTimeoutException;
import org.patchca.service.Captcha;
import org.patchca.service.ConfigurableCaptchaService;
import org.patchca.utils.encoder.AnimatedGifEncoder;
import org.patchca.utils.encoder.FrameList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static com.think.patchca.Constants.PATCHCA_SESSION_DATE;
import static com.think.patchca.Constants.PATCHCA_SESSION_KEY;

/**
 * 默认验证码实现
 *
 * @author veione
 */
public class DefaultPatchca implements Patchca {
    private final ConfigurableCaptchaService captchaService;

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    public DefaultPatchca(ConfigurableCaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @Override
    public String render() {
        Captcha captcha = captchaService.getCaptcha();
        FrameList frameList = captcha.getFrameList();
        setHeader(frameList);

        String sessionCode = captcha.getChallenge();
        try (ServletOutputStream out = response.getOutputStream()) {
            request.getSession().setAttribute(PATCHCA_SESSION_KEY, sessionCode);
            request.getSession().setAttribute(PATCHCA_SESSION_DATE, System.currentTimeMillis());
            writeCaptcha(captcha, frameList, out);
            return sessionCode;
        } catch (IOException e) {
            throw new PatchcaRenderException(e);
        }
    }

    /**
     * 写出验证码
     *
     * @param captcha
     * @param frameList
     * @param out
     * @throws IOException
     */
    private void writeCaptcha(Captcha captcha, FrameList frameList, ServletOutputStream out) throws IOException {
        if (frameList != null) {
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.setQuality(180);
            encoder.setDelay(100);
            encoder.setRepeat(0);
            encoder.start(out);
            frameList.forEach(frame -> {
                encoder.addFrame(frame);
                frame.flush();
            });
            encoder.finish();
            out.flush();
            out.close();
        } else {
            ImageIO.write(captcha.getImage(), "png", out);
        }
    }

    /**
     * 设置响应头
     *
     * @param frameList
     */
    private void setHeader(FrameList frameList) {
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate");
        response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        if (frameList != null) {
            response.setContentType("image/gif");
        } else {
            response.setContentType("image/jpeg");
        }
    }

    @Override
    public boolean validate(String code) {
        return validate(code, 900);
    }

    @Override
    public boolean validate(String code, long second) {
        HttpSession httpSession = request.getSession(false);
        String sessionCode;
        if (httpSession != null && (sessionCode = (String) httpSession.getAttribute(PATCHCA_SESSION_KEY)) != null) {
            if (sessionCode.equalsIgnoreCase(code)) {
                long sessionTime = (long) httpSession.getAttribute(PATCHCA_SESSION_DATE);
                long duration = (System.currentTimeMillis() - sessionTime) / 1000;
                if (duration < second) {
                    httpSession.removeAttribute(PATCHCA_SESSION_KEY);
                    httpSession.removeAttribute(PATCHCA_SESSION_DATE);
                    return true;
                } else {
                    throw new PatchcaTimeoutException();
                }
            } else {
                throw new PatchcaIncorrectException();
            }
        } else {
            throw new PatchcaNotFoundException();
        }
    }
}
