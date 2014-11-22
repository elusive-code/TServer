package com.elusive_code.tserver.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;

/**
 * @author Vladislav Dolgikh
 */
public class ImageHelper {

    private ImageHelper(){

    }

    public static BufferedImage loadImage(Object source) throws IOException {
        if (source == null) return null;
        if (source instanceof Collection){
            Collection c = (Collection)source;
            Validate.isTrue(c.size()==1);
            return loadImage(c.iterator().next());
        } else if (source instanceof BufferedImage) {
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

    public static File writeImage(Object source, String format) throws IOException{
        if (source == null) return null;
        if (source instanceof File) return (File)source;
        if (source instanceof String) return new File((String)source);

        BufferedImage image = loadImage(source);
        File f = createTempFile(null,format);
        ImageIO.write(image,format,f);

        return f;
    }

    public static byte[] data(Object o){
        return null;
    }

    private static File createTempFile(String name, String format) throws IOException {
        if (name == null) name = UUID.randomUUID().toString();
        if (StringUtils.isBlank(format)) {
            format = null;
        } else if (!format.startsWith(".")){
            format = "."+format;
        }

        File f = File.createTempFile(name,format);
        f.deleteOnExit();

        return f;
    }


}
