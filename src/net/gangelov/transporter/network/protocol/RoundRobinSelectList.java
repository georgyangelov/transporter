package net.gangelov.transporter.network.protocol;

import java.util.List;

public class RoundRobinSelectList<T> {
    private final List<T> list;
    private int i = 0;

    public RoundRobinSelectList(List<T> list) {
        this.list = list;
    }

    public T select() {
        return list.get(i++ % list.size());
    }
}
