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


