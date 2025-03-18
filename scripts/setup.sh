#!/bin/sh

echo "Setting up post-merge hooks"
echo "#!/bin/sh" > .git/hooks/post-merge
echo "java -jar ./automation/InstanceSync.jar" >> .git/hooks/post-merge
chmod +x .git/hooks/post-merge

echo "Done setting up hooks"
echo "Running InstanceSync"

java -jar InstanceSync.jar