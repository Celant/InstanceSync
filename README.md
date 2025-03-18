# InstanceSync

Git hook to allow for modpack version control without moving around jar files.

![](https://i.imgur.com/t4PXzQ7.png)

InstanceSync is a git hook that automagically manages your modpack jar files from the `minecraftinstance.json` file that the CurseForge App generates. This allows you to version control your modpack using git, without moving mod files around, neat!

InstanceSync can:
* Install itself with a simple script
* Integrate seamlessly with git so that all the following happens every time you `git pull`:
  * Scan your `minecraftinstance.json` and `mods` folder to find work it needs to do
  * Automatically download missing mod jars from CurseForge's CDN
  * Delete mod jars that are no longer present in the instance
  * Handle `.jar.disabled` files, renaming them properly if you choose to enable/disable mods

## How to Use

* Download the [latest release](https://github.com/Celant/InstanceSync/releases)
  (the release-package.zip, unless you just need the jar file)
* Extract the `InstanceSync.jar` file, as well as the `.bat` and `.sh` scripts into the root of your repository
* Add the lines in the included `.gitignore` file to your repository's `.gitignore`
  * If your repository doesn't have a `.gitignore` file, just extract the included one
* Run `setup.bat` or `setup.sh`, whichever is appropriate for your OS
* Add something to your repository's README that tells people to run the setup script

## License

This project is licensed under the WTFPL.