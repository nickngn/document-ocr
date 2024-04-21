package com.nickngn.demotikaocr.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BoundingConfig {

    private DocumentType nric;
    private DocumentType fin;
    private DocumentType passport;

    @Getter @Setter
    public static class DocumentType {
        private int standardWidth;
        private int standardHeight;
        private ArrayList<Field> fields;

        public DocumentType calcScaledConfig(int width, int height) {
            DocumentType scaledConfig = new DocumentType();
            scaledConfig.setFields((ArrayList<Field>) fields.clone());
            double scaledX = standardWidth / width;
            double scaledY = standardHeight / height;
            for (Field field : scaledConfig.fields) {
                field.x = (int)(scaledX * field.x);
                field.y = (int)(scaledY * field.y);
                field.width = (int)(scaledX * field.width);
                field.height = (int)(scaledY * field.height);
            }
            return scaledConfig;
        }

        public Rectangle roi(String name) {
            Field field = getField(name);
            return new Rectangle(field.x, field.y, field.width, field.height);
        }

        public Field getField(String name) {
            return fields.stream().filter(f -> f.getName().equals(name)).findFirst()
                         .orElseThrow(() -> new IllegalArgumentException("Field " + name + " not found"));
        }
    }

    @Getter
    @Setter
    public static class Field {
        private String name;
        private int x;
        private int y;
        private int width;
        private int height;
        private String type;  // Optional, can be null for passport fields
    }


}
