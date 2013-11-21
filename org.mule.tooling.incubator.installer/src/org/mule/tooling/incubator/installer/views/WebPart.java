package org.mule.tooling.incubator.installer.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;

public class WebPart {

    private static final String ECLIPSE_MESSAGE = "message:";
    private static final String LISTENER_MESSAGE = "listener:";

    private Browser browser;
    private InstallerService requestHandler;

    public WebPart(Composite parent, int style, String url, InstallerService requestHandler) {
        this.requestHandler = requestHandler;
        this.browser = new Browser(parent, style);
        browser.addStatusTextListener(new StatusBarProtocolHandler());
        browser.setUrl(url);

    }

    private final class StatusBarProtocolHandler implements StatusTextListener {

        @Override
        public void changed(StatusTextEvent event) {
            String message = event.text;
            if (message.startsWith(ECLIPSE_MESSAGE)) {
                handleMessageRequest(message);
            } else if (message.startsWith(LISTENER_MESSAGE)) {
                int idIndex = message.indexOf(":", LISTENER_MESSAGE.length());
                final String listenerId = message.substring(LISTENER_MESSAGE.length(), idIndex);
                final String eventType = message.substring(idIndex + 1);
                IEventDispatcher dispatcher = (IEventDispatcher) requestHandler.getAdapter(IEventDispatcher.class);
                dispatcher.addEventListener(eventType, new IEventListener() {

                    @Override
                    public void onEvent(Object message) {
                        notifyListener(eventType, listenerId, message);
                    }
                });
            }
        }

        public void notifyListener(String eventType, String id, Object message) {
            browser.execute("notifyStudioListener(\"" + eventType + ":" + id + "\",\"" + String.valueOf(message) + "\");");
        }

        protected void handleMessageRequest(String message) {
            int idIndex = message.indexOf(":", ECLIPSE_MESSAGE.length());
            String methodId = message.substring(ECLIPSE_MESSAGE.length(), idIndex);
            String methodCall = message.substring(idIndex + 1);

            String[] methodParts = methodCall.split("->");
            String methodName = methodParts[0];
            Method[] declaredMethods = requestHandler.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                if (method.getName().equals(methodName)) {
                    if (methodParts.length > 1) {
                        String methodArguments = methodParts[1];
                        Object[] arguments = methodArguments.split(",");
                        try {
                            Object result = method.invoke(requestHandler, arguments);
                            notifyListener("callback", methodId, String.valueOf(result));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Object result = method.invoke(requestHandler);
                            notifyListener("callback", methodId, String.valueOf(result));
                        } catch (IllegalArgumentException e) {

                        } catch (IllegalAccessException e) {

                        } catch (InvocationTargetException e) {

                        }
                    }
                }
            }
        }
    }

}
