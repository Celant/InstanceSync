package vazkii.instancesync;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import vazkii.instancesync.Instance.Addon;
import vazkii.instancesync.Instance.Addon.AddonFile;
import vazkii.instancesync.Instance.Scan;

public class DownloadManager {

	private final File modsDir;

	private List<String> acceptableFilenames = new LinkedList<>();
	private ExecutorService executor;
	private int downloadCount;

	public DownloadManager(File modsDir) {
		this.modsDir = modsDir;
	}

	public void downloadInstance(Instance instance) {
		executor = Executors.newFixedThreadPool(10);

		System.out.println("Downloading any missing mods");
		long time = System.currentTimeMillis();

		for(Addon a : instance.installedAddons)
			downloadAddonIfNeeded(a);

		if(downloadCount == 0) {
			System.out.println("No mods need to be downloaded, yay!");
		} else try {
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.DAYS);

			float secs = (float) (System.currentTimeMillis() - time) / 1000F;
			System.out.printf("Finished downloading %d mods (Took %.2fs)%n%n", downloadCount, secs);
		} catch (InterruptedException e) {
			System.out.println("Downloads were interrupted!");
			e.printStackTrace();
		}

		for(Addon a : instance.installedAddons)
			setModEnabledState(a);

		for(Scan s : instance.cachedScans)
			acceptableFilenames.add(s.folderName);

		deleteRemovedMods();
	}

	private void downloadAddonIfNeeded(Addon addon) {
		AddonFile file = addon.installedFile;
		if(file == null)
			return;

		String filenameOnDisk = file.getFileName() + (addon.isEnabled ? "" : ".disabled");
		acceptableFilenames.add(filenameOnDisk);

		if(!modIsDownloaded(addon)) {
			File modFile = new File(modsDir, filenameOnDisk);
			download(modFile, file.downloadUrl);
		}
	}

	private void download(final File target, final String downloadUrl) {
		Runnable run = () -> {
			String name = target.getName();

			try {
				System.out.println("Downloading " + name);
				long time = System.currentTimeMillis();

				URL url = new URL(downloadUrl);
				FileOutputStream out = new FileOutputStream(target);

				URLConnection connection = url.openConnection();
				InputStream in = connection.getInputStream();

				byte[] buf = new byte[4096];
				int read = 0;

				while((read = in.read(buf)) > 0)
					out.write(buf, 0, read);

				out.close();
				in.close();

				float secs = (float) (System.currentTimeMillis() - time) / 1000F;
				System.out.printf("Finished downloading %s (Took %.2fs)%n", name, secs);
			} catch(Exception e) {
				System.out.println("Failed to download " + name);
				e.printStackTrace();
			}
		};

		downloadCount++;
		executor.submit(run);
	}

	private void deleteRemovedMods() {
		System.out.println("Deleting any removed mods");
		File[] files = modsDir.listFiles(f -> !f.isDirectory() && !acceptableFilenames.contains(f.getName()));

		if(files.length == 0)
			System.out.println("No mods were removed, woo!");
		else {
			for(File f : files) {
				System.out.println("Found removed mod " + f.getName());
				f.delete();
			}

			System.out.println("Deleted " + files.length + " old mods");
		}
	}

	private boolean modIsDownloaded(Addon addon) {
		File modFile = new File(modsDir, addon.installedFile.getFileName());

		if (modFile.exists())
			return true;

		modFile = new File(modsDir, addon.installedFile.getFileName() + ".disabled");
		if (modFile.exists() && !addon.isEnabled)
			return true;

		return false;
	}

	private void setModEnabledState(Addon addon) {
		if(addon.isEnabled == null) {
			System.out.println("Addon " + addon.installedFile.getFileName() + " has no enabled state, skipping");
			return;
		}

		File file = new File(modsDir, addon.installedFile.getFileName());
		if (!file.exists()) {
			file = new File(modsDir, addon.installedFile.getFileName() + ".disabled");
			if (!file.exists()) {
				System.out.println("Addon " + file.getName() + " not found, skipping");
				return;
			}
		}

		File desiredFile = new File(modsDir, addon.installedFile.getFileName() + (addon.isEnabled ? "" : ".disabled"));
		if(file.equals(desiredFile))
			return;

		System.out.println("Setting enabled state for " + file.getName() + " to " + addon.isEnabled);
		if (!file.renameTo(desiredFile))
			System.out.println("Failed to set enabled state for " + file.getName());
	}

}