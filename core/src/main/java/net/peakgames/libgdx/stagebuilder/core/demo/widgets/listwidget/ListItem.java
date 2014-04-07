package net.peakgames.libgdx.stagebuilder.core.demo.widgets.listwidget;


import java.util.Random;

public class ListItem {

    private static final String[] FRAMES = {"facebook_logo", "twitter_logo", "google_plus_logo", "linkedin_logo"};
    private static final String[] NAMES = {"Ilkin Balkanay", "Fuat Coskun", "Guven Salgun", "Emre Dirican", "Erol Kocaman", "Engin Mercan", "Muratcan Bulut"};

    private static final Random random = new Random(System.currentTimeMillis());
    private String name;
    private long chips;
    private String frame;

    public String getName() {
        return name;
    }

    public long getChips() {
        return chips;
    }

    public String getFrame() {
        return frame;
    }

    public static ListItem generateRandom() {
        ListItem item = new ListItem();
        item.name = NAMES[random.nextInt(NAMES.length)];
        item.chips = random.nextInt(100) * 5000;
        item.frame = FRAMES[random.nextInt(FRAMES.length)];
        return item;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "name='" + name + '\'' +
                ", chips=" + chips +
                ", frame='" + frame + '\'' +
                '}';
    }
}
