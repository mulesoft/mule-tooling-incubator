Mule Tooling Incubator
======================



This project allows user to create and expose easily their own plugins for Mule
Studio.



How To Start
------------



1.  Import all mule-tooling-incubator. File -\> Import -\> Existing Projects
    into Workspace -\> mule-tooling-incubator folder.

2.  Open mule-tooling.target -\> Wait till eclipse finish Resoliving Target
    Definition -\> Click in Set as Target Platform



How To Run your plugin
----------------------



1.  Open studio.product file and click on Launch Eclipse Application Icon (Upper
    Left Green Play Icon)

2.  This should launch a Mule Studio. To add your new plugin, Go to Run
    Configurations .. -\> Plug Ins tab and select your new plugin.

3.  Hack Mule Studio!!!!



What is API and what not?
-------------------------



You can use any part of the code that you want but is likely to change between
releases. We suggest the use of Eclipse Extension points that MuleStudio exposes
or Events, this two are likely to be mantained between releases. We are trying
to document the extension points and Mule Events but this is a work in progress,
be patient.



EventBus
--------

MuleStudio use an* *org.mule.tooling.core.event.EventBus to dispatch Custom
Studio Events. This are very useful to get for example when an element is being
selected or deleted. Or when the current IMuleProject has change, etc.



### How to register



In order to register you need to get an instance of the event bus. For the core
events the way to get it is



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
MuleCorePlugin.getEventBus().registerListener(CoreEventTypes.ON_CURRENT_EDITOR_PART_CHANGED, new IEditorPartChangedListener() {
...
});
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



This method returns an EventRegistrationHandler that allows to easily un
register when required.



Un registering is VERY important to avoid memory leaks.



Extension point
---------------



The most common way of contributing to eclipse is using this mechanism. Mule
Studio has pleanty of extension points that allows to contribute to a given part
of the Tool.



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
<extension
         point="org.mule.tooling.core.contribution">
      <externalContribution
            contributionJar="mule-transport-sap.jar"
            contributionLibPathInMule="/lib"
            contributionLibs="mule-transport-sap.jar"
            contributionNamespace="http://www.mulesoft.org/schema/mule/sap"
            contributionNamespaceFile="http://www.mulesoft.org/schema/mule/sap/current/mule-sap.xsd"
            contributionNamespacePrefix="sap"
            contributionType="cloud-connector"
            icon="icons/small/sap-endpoint-24x16.png"
            minimumVersion="3.5.0"
            name="SAP"
            path="sap.EE.xml"
            version="2.2">
      </externalContribution>
   </extension>
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



This example shows how to contribute with an external contribution.
