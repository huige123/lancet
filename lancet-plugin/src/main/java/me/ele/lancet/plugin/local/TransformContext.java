package me.ele.lancet.plugin.local;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.TransformInvocation;
import me.ele.lancet.weaver.internal.graph.Node;
import me.ele.lancet.weaver.internal.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by gengwanpeng on 17/4/26.
 */
public class TransformContext {

    private TransformInvocation invocation;

    private Collection<JarInput> allJars;
    private Collection<JarInput> addedJars;
    private Collection<JarInput> removedJars;
    private Collection<JarInput> changedJars;
    private Collection<DirectoryInput> allDirs;

    private GlobalContext global;
    private List<String> classes;
    private Map<String, Node> nodesMap;

    public TransformContext(TransformInvocation invocation, GlobalContext global) {
        this.global = global;
        this.invocation = invocation;
        init();
    }


    private void init() {
        allJars = new ArrayList<>(invocation.getInputs().size());
        addedJars = new ArrayList<>(invocation.getInputs().size());
        changedJars = new ArrayList<>(invocation.getInputs().size());
        removedJars = new ArrayList<>(invocation.getInputs().size());
        allDirs = new ArrayList<>(invocation.getInputs().size());
        invocation.getInputs().forEach(it -> {
            Log.d(it.toString());
            it.getJarInputs().forEach(j -> {
                allJars.add(j);
                if (invocation.isIncremental()) {
                    switch (j.getStatus()) {
                        case ADDED:
                            addedJars.add(j);
                            break;
                        case REMOVED:
                            removedJars.add(j);
                            break;
                        case CHANGED:
                            changedJars.add(j);
                    }
                }
            });
            allDirs.addAll(it.getDirectoryInputs());
        });
    }


    public boolean isIncremental() {
        return invocation.isIncremental();
    }

    public Collection<JarInput> getAllJars() {
        return Collections.unmodifiableCollection(allJars);
    }

    public Collection<DirectoryInput> getAllDirs() {
        return Collections.unmodifiableCollection(allDirs);
    }

    public Collection<JarInput> getAddedJars() {
        return Collections.unmodifiableCollection(addedJars);
    }

    public Collection<JarInput> getChangedJars() {
        return Collections.unmodifiableCollection(changedJars);
    }

    public Collection<JarInput> getRemovedJars() {
        return Collections.unmodifiableCollection(removedJars);
    }

    public File getRelativeFile(QualifiedContent content) {
        return invocation.getOutputProvider().getContentLocation(content.getName(), content.getContentTypes(), content.getScopes(),
                (content instanceof JarInput ? Format.JAR : Format.DIRECTORY));
    }

    public void clear() throws IOException {
        invocation.getOutputProvider().deleteAll();
    }

    public GlobalContext getGlobal() {
        return global;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setNodesMap(Map<String, Node> nodesMap) {
        this.nodesMap = nodesMap;
    }

    public Map<String, Node> getNodesMap() {
        return nodesMap;
    }
}