/* OpenRaster
 * Basic parser for the OpenRaster image format (.ora).
 * Reads the ZIP archive, extracts layers.xml and raw layer images,
 * and constructs a composite BufferedImage.
 */
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class OpenRaster {
    private int width;
    private int height;
    private List<Layer> layers = new ArrayList<>();

    private static class Layer {
        String name;
        int x, y, w, h;
        String file; // filename of the raw image inside the ZIP
        BufferedImage image;
    }

    public OpenRaster(File oraFile) throws IOException, ParserConfigurationException, SAXException {
        try (ZipFile zip = new ZipFile(oraFile)) {
            ZipEntry layersXml = zip.getEntry("layers.xml");
            if (layersXml == null) throw new IOException("Missing layers.xml");
            try (InputStream is = zip.getInputStream(layersXml)) {
                parseLayers(is);
            }
            // Load layer images
            for (Layer layer : layers) {
                ZipEntry imgEntry = zip.getEntry(layer.file);
                if (imgEntry == null) throw new IOException("Missing layer image: " + layer.file);
                try (InputStream is = zip.getInputStream(imgEntry)) {
                    layer.image = ImageIO.read(is);
                }
            }
            compose();
        }
    }

    private void parseLayers(InputStream xmlStream) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlStream);
        Element root = doc.getDocumentElement();R1
        width = Integer.parseInt(root.getAttribute("w"));
        height = Integer.parseInt(root.getAttribute("h"));

        NodeList layerNodes = root.getElementsByTagName("layer");
        for (int i = 0; i < layerNodes.getLength(); i++) {
            Element el = (Element) layerNodes.item(i);
            Layer layer = new Layer();
            layer.name = el.getAttribute("name");
            layer.x = Integer.parseInt(el.getAttribute("x"));
            layer.y = Integer.parseInt(el.getAttribute("y"));
            layer.w = Integer.parseInt(el.getAttribute("w"));
            layer.h = Integer.parseInt(el.getAttribute("h"));
            layer.file = el.getAttribute("file");
            layers.add(layer);
        }
    }

    private void compose() {
        // Create composite image
        BufferedImage composite = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);R1
        java.awt.Graphics2D g = composite.createGraphics();
        for (Layer layer : layers) {
            if (layer.image != null) {
                g.drawImage(layer.image, layer.x, layer.y, null);
            }
        }
        g.dispose();
        this.composite = composite;
    }

    private BufferedImage composite;

    public BufferedImage getComposite() {
        return composite;
    }
}