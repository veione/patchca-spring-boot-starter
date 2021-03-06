/*
 * Copyright (c) 2009 Piotr Piastucki
 *
 * This file is part of Patchca CAPTCHA library.
 *
 *  Patchca is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Patchca is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Patchca. If not, see <http://www.gnu.org/licenses/>.
 */
package org.patchca.service;

import org.patchca.utils.encoder.FrameList;

import java.awt.image.BufferedImage;

public class Captcha {

    private String challenge;
    private BufferedImage image;
    private FrameList frameList;

    public Captcha(String challenge, BufferedImage image) {
        this(challenge, image, null);
    }

    public Captcha(String challenge, BufferedImage image, FrameList frameList) {
        this.challenge = challenge;
        this.image = image;
        this.frameList = frameList;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public BufferedImage getImage() {
        return image;
    }

    public FrameList getFrameList() {
        return frameList;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

}
