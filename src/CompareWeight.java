import java.util.Comparator;

public class CompareWeight implements Comparator<Item> {

    @Override
    public int compare(Item o1, Item o2) {
        return o2.getWeight() - o1.getWeight();
    }
}
