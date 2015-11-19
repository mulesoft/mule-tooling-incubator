/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.tooling.editor.model.element;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created by pablocabrera on 11/18/15.
 */
@XmlEnum
public enum InputType
{
    DATE,
    TIME,
    DATETIME
}
