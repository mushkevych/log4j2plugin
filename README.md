This project provides Log4j2 Plugin capable of rotating logs at the end of given time period (hour, day, etc).

See an exemplary log4j2.xml (src/main/resources/log4j2.xml) for how the plugin is used.

How it works:  

1. FTimeBasedTriggeringPolicy is a copy-paste TimeBasedTriggeringPolicy policy that exposes **RollingFileManager** instance 
as well as **checkRollover** method  
1. FTimeBasedTriggeringPolicy allows 0-length files to be rotated  
1. FTimeBasedTriggeringPolicy instance registers itself in the **LogRotateThread** during initialization  
1. Every few minutes **LogRotateThread** queries **FTimeBasedTriggeringPolicy.checkRollover** method which if needed will 
trigger log rotation  

How-to:  

1. Register FTimeBasedTriggeringPolicy in the <Policies> tag  
1. Start LogRotateThread  
1. If using Routing, make sure to "pre-initialize" log appenders by calling **LogRotateThread.initializeAppenders** method  


Licensed under the Apache License, Version 2.0  
[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)