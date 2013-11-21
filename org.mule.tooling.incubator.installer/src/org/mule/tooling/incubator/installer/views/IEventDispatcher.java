package org.mule.tooling.incubator.installer.views;

public interface IEventDispatcher {

    void addEventListener(String eventType, IEventListener listener);

    void removeEventListener(String eventType, IEventListener listener);

    void dispatchEvent(String eventType, Object message);
}
