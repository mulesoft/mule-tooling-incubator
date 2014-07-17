package org.mule.tooling.devkit.assist.rules;

public class Negation extends CompositeRule {

    public boolean applies() {
        return !super.applies();
    }
}
