package org.mule.tooling.ui.contribution.debugger.model;

import org.mule.tooling.core.model.IMuleProject;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class MessageSnapshotDescriptor {

    private String name;
    private MessageSnapshot snapshot;
    private IMuleProject project;

    public MessageSnapshotDescriptor(String name, IMuleProject project, MessageSnapshot snapshot) {
        super();
        this.name = name;
        this.project = project;
        this.snapshot = snapshot;
    }

    public String getName() {
        return name;
    }

    public IMuleProject getProject() {
        return project;
    }

    public MessageSnapshot getSnapshot() {
        return snapshot;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageSnapshotDescriptor other = (MessageSnapshotDescriptor) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

}
