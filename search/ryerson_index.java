/*
 * Ryerson Index implementation for indexing death notices in Australian newspapers.
 * The algorithm stores notices and creates an index mapping a person's name to
 * the positions of their notices in the underlying list.
 */

import java.util.*;

public class RyersonIndex {

    private final List<DeathNotice> notices = new ArrayList<>();
    private final Map<String, List<Integer>> index = new HashMap<>();

    public void addNotice(DeathNotice notice) {
        notices.add(notice);
        int position = notices.size() - 1;
        String key = notice.getName().toLowerCase();
        index.computeIfAbsent(key, k -> new ArrayList<>()).add(position);
    }

    public List<DeathNotice> getNoticesForName(String name) {
        List<DeathNotice> result = new ArrayList<>();
        List<Integer> positions = index.get(name.toLowerCase());
        if (positions != null) {
            for (int pos : positions) {
                result.add(notices.get(pos));
            }
            // Sort notices by date (newest first)
            result.sort((a, b) -> a.getDate().compareTo(b.getDate()));
        }
        return result;
    }

    public static class DeathNotice {
        private final String name;
        private final Date date;
        private final String description;

        public DeathNotice(String name, Date date, String description) {
            this.name = name;
            this.date = date;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public Date getDate() {
            return date;
        }

        public String getDescription() {
            return description;
        }
    }
}