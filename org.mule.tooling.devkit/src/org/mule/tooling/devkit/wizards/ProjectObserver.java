package org.mule.tooling.devkit.wizards;

import java.util.Observable;


public class ProjectObserver extends Observable {

    public void broadcastChange(){
        setChanged();
        notifyObservers();
    }
    
}