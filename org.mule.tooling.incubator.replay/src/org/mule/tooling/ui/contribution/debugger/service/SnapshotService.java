package org.mule.tooling.ui.contribution.debugger.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotClearedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotRemovedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotTakenEvent;
import org.mule.tooling.ui.widgets.util.SilentRunner;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class SnapshotService {

    private Map<String, MessageSnapshot> snapshots;
    private EventBus eventBus;
    private IMuleProject muleProject;

    public SnapshotService(EventBus eventBus) {
        this.eventBus = eventBus;
        snapshots = new LinkedHashMap<String, MessageSnapshot>();
    }

    public void loadAllFromProject(IMuleProject muleProject) {
        store();
        this.muleProject = muleProject;
        File snapshots = getSnapshotsFolder(muleProject);
        loadContent(snapshots);
    }

    public File getSnapshotsFolder(IMuleProject muleProject) {

        final File snapshots = new File(new File(muleProject.getProjectFile().getProject().getLocationURI()), ".snapshots");
        if (!snapshots.exists()) {
            snapshots.mkdirs();
        }
        return snapshots;
    }

    protected void loadContent(final File snapshots) {
        clear();
        final File directorySnapshot = snapshots;
        File[] listFiles = directorySnapshot.listFiles();
        if (listFiles != null) {
            for (final File snapshot : listFiles) {
                if (snapshot.isFile()) {

                    MessageSnapshot messageSnapshot = SilentRunner.run(new Callable<MessageSnapshot>() {

                        @Override
                        public MessageSnapshot call() throws Exception {
                            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(snapshot));
                            Object readObject = objectInputStream.readObject();
                            objectInputStream.close();
                            return (MessageSnapshot) (readObject instanceof MessageSnapshot ? readObject : null);
                        }
                    }, null);
                    addSnaphost(snapshot.getName(), messageSnapshot);

                }
            }
        }
    }

    public void store() {
        if (muleProject != null) {
            final File snapshots = getSnapshotsFolder(muleProject);
            final File[] listFiles = snapshots.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            final Set<Entry<String, MessageSnapshot>> entrySet = getAllDefinedSnapshots().entrySet();
            for (final Entry<String, MessageSnapshot> entry : entrySet) {
                final File snapshotFile = new File(snapshots, entry.getKey());
                SilentRunner.run(new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(snapshotFile));
                        objectOutputStream.writeObject(entry.getValue());
                        objectOutputStream.flush();
                        objectOutputStream.close();
                        return null;
                    }
                }, null);
            }

        }
    }

    public void clear() {
        this.snapshots.clear();
        eventBus.fireEvent(new SnapshotClearedEvent());
    }

    public Collection<MessageSnapshot> getSnapshots() {
        return snapshots.values();
    }

    public void addSnaphost(String name, MessageSnapshot snapshot) {
        this.snapshots.put(name, snapshot);
        eventBus.fireEvent(new SnapshotTakenEvent(name, snapshot));
    }

    public MessageSnapshot getSnapshot(String name) {
        return snapshots.get(name);
    }

    public void removeSnapshot(String name) {
        this.snapshots.remove(name);
        eventBus.fireEvent(new SnapshotRemovedEvent(name));
    }

    public Map<String, MessageSnapshot> getAllDefinedSnapshots() {
        return snapshots;
    }

}