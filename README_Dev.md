## Create install files for all 3 operating systems

### MacOS

#### Memristor-Discovery.app

Build the mail Jar with: `mvn clean package`.

Create an icns file via <https://iconverticons.com/online/>. Place under `_img` with name `icons.icns`.

Manually download the MacOS OpenJDK from <http://jdk.java.net/11/>. Un-tar it.

Prepare the Java 11 JDK for Packr. 

It requires a `jre` folder because of the the way old Java JDKs organized the directory structure. If Packr ever updates, this may not be necessary in the future, but for now it is. Create `jre` under `Contents/Home`. Move `lib` and `bin` into this new `jre` folder. You can also remove `src.zip` under `lib` as it's just extra unneeded bulk. 

Right-click the `jdk-11.0.2.jdk` folder and choose `compress` to create a zip file, which is required by Packr.

Update `JDK_LOCATION` in `MacOSPackr` to reflect the created zip file. 

Run `MacOSPackr` in test package. This will create a `*.app` file which could be dragged into `Applications` and clicked on like a normal app.

#### Memristor-Discovery.dmg

To package the app in an "installer", we need to create a DMG file with a few things in it. 

1. Open Disk Utility
1. File ==> New Image ==> Blank Image

![](_img/DiskUtility.png)

1. Mount and open the DMG in Finder
1. Switch to Icon view
1. Drag `*.app` into it.
1. In Terminal: `ln -s /Volumes/Memristor-Discovery/Applications Applications`
1. Right-click ==> Show View Options
1. Drag the DMG_Background image as the background picture.

![](_img/DMG_Setup.png)

1. Eject the DMG.

1. Compress it with Disk Utility. Images ==> Convert. Select DMG. Choose `compressed`, which is also read-only. (https://support.apple.com/en-gb/guide/disk-utility/convert-a-disk-image-to-another-format-dskutl1002/mac)
