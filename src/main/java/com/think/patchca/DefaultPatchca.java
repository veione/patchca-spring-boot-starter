package com.think.patchca;

import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.think.patchca.exception.PatchcaIncorrectException;
import com.think.patchca.exception.PatchcaNotFoundException;
import com.think.patchca.exception.PatchcaRenderException;
import com.think.patchca.exception.PatchcaTimeoutException;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class DefaultPatchca implements Patchca {
    private ConfigurableCaptchaService captchaService = new ConfigurableCaptchaService();

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Override
    public String render() {
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate");
        response.addHeader(HttpHeaders.CACHE_CONTROL, "post-check=0, pre-check=0");
        response.setHeader(HttpHeaders.PRAGMA, "no-cache");
        response.setContentType("image/jpeg");
        String sessionCode = captchaService.getCaptcha().getWord();
        try (ServletOutputStream out = response.getOutputStream()) {
            request.getSession().setAttribute(PATCHCA_SESSION_KEY, sessionCode);
            request.getSession().setAttribute(PATCHCA_SESSION_DATE, System.currentTimeMillis());
            ImageIO.write(captchaService.getCaptcha().getImage(), "png", out);
            return sessionCode;
        } catch (IOException e) {
            throw new PatchcaRenderException(e);
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
