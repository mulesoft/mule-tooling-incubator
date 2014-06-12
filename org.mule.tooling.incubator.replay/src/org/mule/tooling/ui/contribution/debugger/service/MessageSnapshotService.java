package org.mule.tooling.ui.contribution.debugger.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.mule.tooling.core.event.EventBus;
import org.mule.tooling.core.model.IMuleProject;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotClearedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotRemovedEvent;
import org.mule.tooling.ui.contribution.debugger.controller.events.SnapshotTakenEvent;
import org.mule.tooling.ui.contribution.debugger.model.MessageSnapshotDecorator;
import org.mule.tooling.ui.widgets.util.SilentRunner;

import com.mulesoft.mule.debugger.commons.MessageSnapshot;

public class MessageSnapshotService {

    private List<MessageSnapshotDecorator> snapshots;
    private EventBus eventBus;
    private IMuleProject muleProject;

    public MessageSnapshotService(EventBus eventBus) {
        this.eventBus = eventBus;
        snapshots = new ArrayList<MessageSnapshotDecorator>();
    }

    public void loadAllFromProject(IMuleProject muleProject) {
        if (muleProject != this.muleProject) {
            store();
            this.muleProject = muleProject;
            File snapshots = getSnapshotsFolder(muleProject);
            loadContent(snapshots);
        }
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
        final File[] listFiles = directorySnapshot.listFiles();
        if (listFiles != null) {
            for (final File snapshot : listFiles) {
                if (snapshot.isFile()) {
                    final MessageSnapshot messageSnapshot = SilentRunner.run(new Callable<MessageSnapshot>() {

                        @Override
                        public MessageSnapshot call() throws Exception {
                            return load(snapshot);
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
            try {
                FileUtils.deleteDirectory(snapshots);
            } catch (IOException e) {

            }
            snapshots.mkdirs();
            List<MessageSnapshotDecorator> allDefinedSnapshots = getAllDefinedSnapshots();
            for (final MessageSnapshotDecorator entry : allDefinedSnapshots) {
                final File snapshotFile = new File(snapshots, entry.getName());
                SilentRunner.run(new Callable<Void>() {

                    @Override
                    public Void call() throws Exception {
                        store(entry.getSnapshot(), snapshotFile);
                        return null;
                    }
                }, null);
            }

        }
    }

    public void clear() {
        this.snapshots.clear();
        this.eventBus.fireEvent(new SnapshotClearedEvent());
    }

    public void addSnaphost(String name, MessageSnapshot snapshot) {
        final MessageSnapshotDecorator messageSnapshotDescriptor = new MessageSnapshotDecorator(name, muleProject, snapshot);
        this.snapshots.add(messageSnapshotDescriptor);
        this.eventBus.fireEvent(new SnapshotTakenEvent(messageSnapshotDescriptor));
    }

    public MessageSnapshot getSnapshot(String name) {
        for (MessageSnapshotDecorator messageSnapshot : snapshots) {
            if (messageSnapshot.getName().equals(name)) {
                return messageSnapshot.getSnapshot();
            }
        }
        return null;
    }

    public MessageSnapshotDecorator getSnapshotDescriptor(String name) {
        for (MessageSnapshotDecorator messageSnapshot : snapshots) {
            if (messageSnapshot.getName().equals(name)) {
                return messageSnapshot;
            }
        }
        return null;
    }

    public void removeSnapshot(String name) {
        MessageSnapshotDecorator snapshot = getSnapshotDescriptor(name);
        if (snapshot != null) {
            this.snapshots.remove(name);
            this.eventBus.fireEvent(new SnapshotRemovedEvent(snapshot));
        }
    }

    public List<MessageSnapshotDecorator> getAllDefinedSnapshots() {
        return Collections.unmodifiableList(snapshots);
    }

    public static MessageSnapshot load(final File snapshot) throws IOException, FileNotFoundException, ClassNotFoundException {
        final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(snapshot));
        try {
            final Object readObject = objectInputStream.readObject();
            return (MessageSnapshot) (readObject instanceof MessageSnapshot ? readObject : null);
        } finally {
            objectInputStream.close();
        }
    }

    public static void store(final MessageSnapshot snapshot, final File snapshotFile) throws IOException, FileNotFoundException {
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(snapshotFile));
        try {
            objectOutputStream.writeObject(snapshot);
            objectOutputStream.flush();
        } finally {
            objectOutputStream.close();
        }

    }

}