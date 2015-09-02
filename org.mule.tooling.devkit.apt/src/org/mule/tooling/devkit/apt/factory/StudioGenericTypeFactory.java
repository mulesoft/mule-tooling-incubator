package org.mule.tooling.devkit.apt.factory;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.mule.devkit.apt.model.GenericTypeImpl;
import org.mule.devkit.apt.model.factory.DefaultGenericTypeFactory;

public class StudioGenericTypeFactory extends DefaultGenericTypeFactory {


    @Override
    public void generateInnerTypeIfNecessary( Types types, Elements elements, Element innerElement,
                                                   String name, TypeMirror typeMirror, Element element,
                                                   GenericTypeImpl child) {
        //TODO ignore for now
    }

}
