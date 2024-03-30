package org.example.fxfontfallbackdemo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ResourceBundle;
import com.sun.javafx.font.*;

public class DefaultController implements Initializable {

    @FXML
    Label exampleText1;

    @FXML
    Label exampleText2;

    @FXML
    Label exampleText3;

    @FXML
    Label exampleText4;

    final static double FONT_SIZE = 24d;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            System.out.println("Initialized...");
        });

        final Font arialDefaultFont = Font.font("Arial", FONT_SIZE);
        exampleText1.setFont(arialDefaultFont);

        final Font robotoRegularFont = loadFont("/font/Roboto-Regular.ttf");
        exampleText2.setFont(robotoRegularFont);

        final Font compositeFont = constructCompositeFont();
        exampleText3.setFont(compositeFont);

        final Font customCompositeFont = constructCustomCompositeFont();
        exampleText4.setFont(customCompositeFont);

    }

    private Font constructCustomCompositeFont(){
        try {
            Font robotoCustomFont = constructCompositeFont(); //create new Composite Font from resource with fallbacks

            PrismFontWrapper fontWrapperRoboto = new PrismFontWrapper(robotoCustomFont);
            FallbackResource fallbackResourceRoboto = fontWrapperRoboto.getFallbackResource();

            Font kosugiMaruRegularFont = loadFont("/font/KosugiMaru-Regular.ttf"); //load single non composite font from resource

            PrismFontWrapper fontWrapperKosugi = new PrismFontWrapper(kosugiMaruRegularFont);
            FontResource fontResourceKosugi = fontWrapperKosugi.getFontResource();

            //we needed a composite font for fallbacks to work, but we do not want the default fallbacks now, so remove them
            fontWrapperRoboto.clearFallbackFontsOnFallbackResource();

            //add the new fallback font via the designated method
            fallbackResourceRoboto.addSlotFont(fontResourceKosugi);

            //return the modified composite font
            return robotoCustomFont;

        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    //Loading the font this way causes this to have the default fallbacks
    private PrismFontFactory getFontFactory(){
        try {
            Method getFontFactoryMethod = PrismFontLoader.getInstance().getClass().getDeclaredMethod("getFontFactoryFromPipeline");
            getFontFactoryMethod.setAccessible(true);
            PrismFontFactory prismFontFactory = (PrismFontFactory) getFontFactoryMethod.invoke(PrismFontLoader.getInstance());
            return prismFontFactory;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }



    private Font constructCompositeFont(){
        try {
            final URL fontURL = DefaultController.class.getResource("/font/Roboto-Regular.ttf");
            assert fontURL != null;
            Font[] fonts = PrismFontLoader.getInstance().loadFont(fontURL.openStream(), FONT_SIZE, true);
            Font font = fonts[0];
            PrismFontLoader.getInstance().loadFont(font);
            return font;
        }catch (Exception ex){
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    private static Font loadFont(String resourcePath) {
        final URL fontURL = DefaultController.class.getResource(resourcePath);
        final Font font;
        try(final InputStream fontInputStream = fontURL.openStream()){
            font = Font.loadFont(fontInputStream, FONT_SIZE);
        }catch (IOException e) {
            throw new RuntimeException("Couldn't load font", e);
        }

        return font;
    }
}