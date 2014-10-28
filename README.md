This project provides Log4j2 Plugin capable of rotating logs at the end of given time period (hour, day, etc).

See an exemplary log4j2.xml (src/main/resources/log4j2.xml) for how the plugin is used.

How it works:  

1. FTimeBasedTriggeringPolicy wraps standard TimeBasedTriggeringPolicy policy and exposes **RollingFileManager** instance 
as well as **checkRollover** method.  
1. FTimeBasedTriggeringPolicy instance registers itself in the **LogRotateThread** during initialization  
1. Every few minutes **LogRotateThread** queries **FTimeBasedTriggeringPolicy.checkRollover** method which if needed will 
trigger log rotation  

How-to:  

1. Register FTimeBasedTriggeringPolicy in the <Policies> tag
2. Start LogRotateThread
3. If using Routing, make sure to "pre-initialize" log appenders by calling **LogRotateThread.initializeAppenders** method
