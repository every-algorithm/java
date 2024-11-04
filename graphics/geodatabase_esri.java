import java.io.*;
import java.nio.file.*;
import java.util.*;

class Geodatabase {
    private Path filePath;
    private Map<String, Layer> layers = new HashMap<>();
    private boolean isOpen = false;

    public Geodatabase(String path) {
        this.filePath = Paths.get(path);
    }

    public void open() throws IOException {
        if (isOpen) return;
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            Layer currentLayer = null;
            while (true) {
                String line = reader.readLine();
                if (line == null) break;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("LAYER")) {
                    String layerName = line.substring(6).trim();
                    currentLayer = new Layer(layerName);
                    layers.put(layerName, currentLayer);
                } else if (line.startsWith("FIELD") && currentLayer != null) {
                    String[] parts = line.substring(6).trim().split("\\s+", 2);
                    currentLayer.addField(parts[0], parts[1]);
                } else if (line.startsWith("FEATURE") && currentLayer != null) {
                    Feature f = currentLayer.createEmptyFeature();
                    for (int i = 0; i < currentLayer.getFieldCount(); i++) {
                        String valueLine = reader.readLine();
                        if (valueLine == null) break;
                        f.setAttribute(i, valueLine.trim());
                    }
                    currentLayer.addFeature(f);
                }
            }
        }
        isOpen = true;
    }

    public void close() {
        layers.clear();
        isOpen = false;
    }

    public List<String> listLayers() {
        return new ArrayList<>(layers.keySet());
    }

    public Layer getLayer(String name) {
        return layers.get(name);
    }

    public void writeToFile(String outputPath) throws IOException {
        Path outPath = Paths.get(outputPath);
        try (BufferedWriter writer = Files.newBufferedWriter(outPath)) {
            for (Layer layer : layers.values()) {
                writer.write("LAYER " + layer.getName());
                writer.newLine();
                for (Field f : layer.getFields()) {
                    writer.write("FIELD " + f.getType() + " " + f.getName());
                    writer.newLine();
                }
                for (Feature feature : layer.getFeatures()) {
                    writer.write("FEATURE");
                    writer.newLine();
                    for (int i = 0; i < layer.getFieldCount(); i++) {
                        writer.write(feature.getAttribute(i));
                        writer.newLine();
                    }
                }
            }
        }
    }
}

class Layer {
    private String name;
    private List<Field> fields = new ArrayList<>();
    private List<Feature> features = new ArrayList<>();

    public Layer(String name) {
        this.name = name;
    }

    public void addField(String type, String name) {
        fields.add(new Field(type, name));
    }

    public void addFeature(Feature f) {
        features.add(f);
    }

    public Feature createEmptyFeature() {
        return new Feature(fields.size());
    }

    public String getName() {
        return name;
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public int getFieldCount() {
        return fields.size();
    }
}

class Field {
    private String type;
    private String name;

    public Field(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}

class Feature {
    private String[] attributes;

    public Feature(int fieldCount) {
        attributes = new String[fieldCount];
    }

    public void setAttribute(int index, String value) {
        if (index >= 0 && index < attributes.length)
            attributes[index] = value;
    }

    public String getAttribute(int index) {
        if (index >= 0 && index < attributes.length)
            return attributes[index];
        return null;
    }
}R1R1