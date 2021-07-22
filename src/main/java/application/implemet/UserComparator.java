package application.implemet;

import java.util.Comparator;

public class UserComparator implements Comparator<MessageImpl> {
    @Override
    public int compare(MessageImpl o1, MessageImpl o2) {
        return (int) (o1.getTimestamp()-o2.getTimestamp());
    }
}