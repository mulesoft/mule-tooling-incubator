package org.mule.tooling.incubator.installer.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.widgets.Composite;

public class WebPart {

    private static final String ECLIPSE_MESSAGE = "message:";

    private Browser browser;
    private Object requestHandler;

    public WebPart(Composite parent, int style, String url, Object requestHandler) {
        this.requestHandler = requestHandler;
        this.browser = new Browser(parent, style);
        browser.addStatusTextListener(new StatusBarProtocolHandler());
        browser.setUrl(url);
        
    }

    public void notify(String eventType, Object message) {
        browser.execute("notifyListener(\"" + eventType + "\",\"" + String.valueOf(message) + "\");");
    }

    private final class StatusBarProtocolHandler implements StatusTextListener {

        @Override
        public void changed(StatusTextEvent event) {
            String message = event.text;
            if (message.startsWith(ECLIPSE_MESSAGE)) {
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
                                browser.execute("notifyStudioListener('callback:" + methodId + "',\"" + String.valueOf(result) + "\");");
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
                                browser.execute("notifyStudioListener('callback:" + methodId + "',\"" + String.valueOf(result) + "\");");
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

}
