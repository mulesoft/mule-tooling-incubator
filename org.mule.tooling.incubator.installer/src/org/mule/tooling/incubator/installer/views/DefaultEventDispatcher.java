package org.mule.tooling.incubator.installer.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEventDispatcher implements IEventDispatcher {

    private Map<String, List<IEventListener>> listeners;

    public DefaultEventDispatcher() {
        super();
        this.listeners = new HashMap<String, List<IEventListener>>();
    }

    @Override
    public void addEventListener(String eventType, IEventListener listener) {
        if (!listeners.containsKey(eventType)) {
            listeners.put(eventType, new ArrayList<IEventListener>());
        }
        listeners.get(eventType).add(listener);
    }

    @Override
    public void removeEventListener(String eventType, IEventListener listener) {
        if (listeners.containsKey(eventType)) {
            listeners.get(eventType).remove(listener);
        }

    }

    @Override
    public void dispatchEvent(String eventType, Object message) {
        if (listeners.containsKey(eventType)) {
            List<IEventListener> eventListeners = listeners.get(eventType);
            for (IEventListener listener : eventListeners) {
                listener.onEvent(message);
            }
        }

    }

}
