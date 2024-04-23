package com.nickngn.demotikaocr.config;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BoundingConfig {

    private Map<String, Document> documents = new HashMap<>();

    @Getter @Setter
    public static class Document {
        private int standardWidth;
        private int standardHeight;
        private ArrayList<Field> fields;

        public Document calcScaledConfig(int width, int height) {
            Document scaledConfig = new Document();
            scaledConfig.setFields((ArrayList<Field>) fields.clone());
            double scaledX = (double) standardWidth / width;
            double scaledY = (double) standardHeight / height;
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
        private FieldType type;  // Optional, can be null for passport fields
    }

    @Getter
    public enum FieldType {
        TEXT("text"),
        IMAGE("image");

        @JsonValue
        private final String type;

        FieldType(String type) {
            this.type = type;
        }
    }


}
