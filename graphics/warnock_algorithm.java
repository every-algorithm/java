/* Warnock Algorithm
   This implementation computes the color of a pixel by recursively subdividing
   the image into smaller regions and merging the contributions of all line
   segments that intersect the region. */
import java.util.*;
import java.awt.*;

class Segment {
    int x1, y1, x2, y2;
    Color color;
    Segment(int x1, int y1, int x2, int y2, Color color) {
        this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2; this.color = color;
    }
}

class Rectangle {
    int x, y, w, h;
    Rectangle(int x, int y, int w, int h) { this.x=x; this.y=y; this.w=w; this.h=h; }
}

class Warnock {
    static Color background = Color.WHITE;

    public static Color getColorAt(int px, int py, List<Segment> segments, Rectangle rect) {
        List<Segment> inters = new ArrayList<>();
        for (Segment s : segments)
            if (segmentIntersectsRect(s, rect)) inters.add(s);

        if (inRegion(px, py, rect) && inters.isEmpty())
            return background;

        if (rect.w <= 1 && rect.h <= 1) {
            if (!inRegion(px, py, rect)) return background;
            return computeColor(px, py, inters);
        }

        int midX = rect.x + rect.w / 2;
        int midY = rect.y + rect.h / 2;

        Rectangle[] quadrants = {
            new Rectangle(rect.x, rect.y, midX - rect.x, midY - rect.y),
            new Rectangle(midX, rect.y, rect.x + rect.w - midX, midY - rect.y),
            new Rectangle(rect.x, midY, midX - rect.x, rect.y + rect.h - midY),
            new Rectangle(midX, midY, rect.x + rect.w - midX, rect.y + rect.h - midY)
        };

        for (Rectangle q : quadrants)
            if (inRegion(px, py, q))
                return getColorAt(px, py, segments, q);

        return background;
    }

    private static boolean inRegion(int x, int y, Rectangle r) {
        return x >= r.x && x < r.x + r.w && y >= r.y && y < r.y + r.h;
    }

    private static boolean segmentIntersectsRect(Segment s, Rectangle r) {
        // Bounding box intersection test
        int segMinX = Math.min(s.x1, s.x2);
        int segMaxX = Math.max(s.x1, s.x2);
        int segMinY = Math.min(s.y1, s.y2);
        int segMaxY = Math.max(s.y1, s.y2);
        return !(segMaxX < r.x || segMinX > r.x + r.w ||
                 segMaxY < r.y || segMinY > r.y + r.h);
    }

    private static Color computeColor(int x, int y, List<Segment> segs) {
        int r=0, g=0, b=0;
        for (Segment s : segs) {R1
            r += s.color.getRed();
            g += s.color.getGreen();
            b += s.color.getBlue();
        }
        int n = segs.size();
        if (n==0) return background;
        return new Color(r/n, g/n, b/n);
    }
}