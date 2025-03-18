@echo off

echo Setting up post-merge hooks
type NUL > .git/hooks/post-merge
echo #!/bin/sh > .git/hooks/post-merge
echo java -jar ./automation/InstanceSync.jar >> .git/hooks/post-merge

echo Done setting up hooks
echo Running InstanceSync

java -jar InstanceSync.jar

pause