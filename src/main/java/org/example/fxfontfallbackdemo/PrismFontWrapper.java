package org.example.fxfontfallbackdemo;

import com.sun.javafx.font.FallbackResource;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.scene.text.FontHelper;
import javafx.scene.text.Font;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class PrismFontWrapper {

    final Font font;

    final Object nativeFont;

    final FontResource fontResource;

    FallbackResource fallbackResource;

    static final Method getFontResourceMethod;

    static final Method getNativeFontMethod;

    static final Class<?> prismFontClass;

    static final Map<Class<?>, Field> cachedFallbackResourceFields;

    private static record FallbackFields(Field linkedFontFilesField, Field linkedFontNamesField, Field fallbacksField){}

    static final Map<Class<?>, FallbackFields> cachedFallbackFields;



    static {
        try {
            prismFontClass = Class.forName("com.sun.javafx.font.PrismFont");
            getFontResourceMethod = prismFontClass.getDeclaredMethod("getFontResource");
            getFontResourceMethod.setAccessible(true);

            getNativeFontMethod = Font.class.getDeclaredMethod("getNativeFont");
            getNativeFontMethod.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        cachedFallbackResourceFields = new HashMap<>();
        cachedFallbackFields = new HashMap<>();

    }


    public PrismFontWrapper(Font font) {
        this.font = font;
        this.nativeFont = getNativeFont();

        if (!prismFontClass.isInstance(this.nativeFont)) {
            throw new IllegalArgumentException("Font should've been a %s but was %s".formatted(prismFontClass, font.getClass()));
        }

        this.fontResource = getFontResource();

    }

    public Font getFont() {
        return font;
    }

    public Object getNativeFont() {
        try {
            return getNativeFontMethod.invoke(font);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public FontResource getFontResource() {
        try {
            return (FontResource) getFontResourceMethod.invoke(nativeFont);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Field getFallbackResourceField() {
        return cachedFallbackResourceFields.computeIfAbsent(fontResource.getClass(), (fontResourceClass) -> {
            try {
                Field fallbackResourceField = fontResourceClass.getDeclaredField("fallbackResource");
                fallbackResourceField.setAccessible(true);
                return fallbackResourceField;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public FallbackResource getFallbackResource() {
        if (fallbackResource != null) {
            return fallbackResource;
        }

        try {
            fallbackResource = (FallbackResource) getFallbackResourceField().get(this.fontResource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return fallbackResource;
    }

    public void clearFallbackFontsOnFallbackResource() {
        try {
            FallbackResource fallbackResource = getFallbackResource();
            //fallbackResource.getClass() should be an implementation of com.sun.javafx.font.FallbackResource.class

            FallbackFields fallbackFields = cachedFallbackFields.computeIfAbsent(fallbackResource.getClass(), (fallbackResourceClass) -> {
                try {
                    Field fallbacksField = fallbackResourceClass.getDeclaredField("fallbacks");
                    fallbacksField.setAccessible(true);

                    Field linkedFontFilesField = fallbackResourceClass.getDeclaredField("linkedFontFiles");
                    linkedFontFilesField.setAccessible(true);

                    Field linkedFontNamesField = fallbackResourceClass.getDeclaredField("linkedFontNames");
                    linkedFontNamesField.setAccessible(true);

                    return new FallbackFields(linkedFontFilesField, linkedFontNamesField, fallbacksField);
                }catch (Exception ex){
                    throw new RuntimeException(ex);
                }
            });

            fallbackFields.fallbacksField().set(fallbackResource, new FontResource[0]);
            fallbackFields.linkedFontFilesField().set(fallbackResource, new String[0]);
            fallbackFields.linkedFontNamesField().set(fallbackResource, new String[0]);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
