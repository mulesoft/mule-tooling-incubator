package org.mule.tooling.incubator.maven.core;

import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class DependencyParser {
    public static TreeNode<Dependency> parseFile(File dependencyOutput) throws IOException {
        List<String> lines= FileUtils.readLines(dependencyOutput);
        return toDependencyTree(lines);
    }

    private static TreeNode<Dependency> toDependencyTree(List<String> lines) {
        int currentLvl=0;

        Stack<TreeNode<Dependency>> stack = new Stack<TreeNode<Dependency>>();
        TreeNode<Dependency> root = new TreeNode<Dependency>();
        for(String line : lines){
            String[] sections =  line.split(":");
            Dependency dep = new Dependency();
            if(sections.length==5){
                String tempSection=sections[0];
                String prefix=tempSection.substring(0,tempSection.lastIndexOf(" ")+1);
                String groupId=(tempSection.contains(" ")?tempSection.substring(tempSection.lastIndexOf(" ")+1):tempSection);
                dep.setGroupId(groupId);
                dep.setArtifactId(sections[1]);
                dep.setType(sections[2]);
                dep.setVersion(sections[3]);
                dep.setScope(sections[4]);
                int lvl = getLevel(prefix);
                TreeNode<Dependency> innerNode = new TreeNode<Dependency>();
                innerNode.nodeItem=dep;
                if(lvl>currentLvl){
                   //Do nothing
                }else if(lvl<currentLvl){
                    while(lvl<=currentLvl){
                        stack.pop();
                        currentLvl--;
                    }
                }else{
                    stack.pop();

                }
                stack.peek().childItems.add(innerNode);
                stack.push(innerNode);
                currentLvl=lvl;
            }else
            if(sections.length==4){
                root.nodeItem=dep;
                dep.setGroupId(sections[0]);
                dep.setArtifactId(sections[1]);
                dep.setType(sections[2]);
                dep.setVersion(sections[3]);
                stack.push(root);
            }
        }
        return root;
    }

    private static int getLevel(String prefix) {
        return prefix.length()/3;
    }
}
