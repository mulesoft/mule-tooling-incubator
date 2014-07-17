package org.mule.tooling.devkit.assist.rules;

import java.util.ArrayList;
import java.util.List;

import org.mule.tooling.devkit.assist.Rule;

public abstract class CompositeRule implements Rule {

    List<Rule> rules = new ArrayList<Rule>();

    public void addRule(Rule rule){
        rules.add(rule);
    }
    public boolean applies() {
        boolean applies = true;
        for (Rule rule : rules) {
            applies &= rule.applies();
        }
        return applies;
    }
}
