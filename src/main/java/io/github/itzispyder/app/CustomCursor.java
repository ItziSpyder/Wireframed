package io.github.itzispyder.app;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CustomCursor {

    public static final Cursor CURSOR_INVISIBLE = Toolkit.getDefaultToolkit().createCustomCursor(
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new Point(0, 0), "wireframed:cursor_invisible");

}
