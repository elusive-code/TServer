package com.elusive_code.tserver.util;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Vladislav Dolgikh
 */
public class ImageHelper {

    private ImageHelper(){

    }

    public static BufferedImage load(Object source) throws IOException {
        if (source == null) return null;
        if (source instanceof BufferedImage) {
            return (BufferedImage) source;
        } else if (source instanceof File) {
            return ImageIO.read((File) source);
        } else if (source instanceof URL) {
            return ImageIO.read((URL) source);
        } else if (source instanceof InputStream) {
            return ImageIO.read((InputStream) source);
        } else if (source instanceof ImageInputStream) {
            return ImageIO.read((ImageInputStream) source);
        } else if (source instanceof String) {
            return ImageIO.read(new File((String) source));
        } else if (source instanceof byte[]) {
            try (ByteArrayInputStream in = new ByteArrayInputStream((byte[]) source)) {
                return ImageIO.read(in);
            }
        }
        throw new IllegalArgumentException("Unknown image source type "+source.getClass()+": "+source);
    }

}
