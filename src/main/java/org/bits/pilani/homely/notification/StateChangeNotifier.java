package org.bits.pilani.homely.notification;


import org.bits.pilani.homely.entity.Order;

import java.util.ArrayList;
import java.util.List;

public class StateChangeNotifier {

    private final List<StateChangeListener> listeners = new ArrayList<>();

    public void addListener(StateChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(StateChangeListener listener) {
        listeners.remove(listener);
    }

    public void notifyListeners(Order order) {
        listeners.forEach(listener -> listener.onStateChange(order));
    }
}