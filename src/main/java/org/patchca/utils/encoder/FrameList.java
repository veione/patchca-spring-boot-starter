package org.patchca.utils.encoder;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * GIF帧视图对象
 *
 * @author veione
 */
public class FrameList extends ArrayList<BufferedImage> {

    public FrameList() {
        super();
    }

    public FrameList(List<BufferedImage> frames) {
        super.addAll(frames);
    }
}
