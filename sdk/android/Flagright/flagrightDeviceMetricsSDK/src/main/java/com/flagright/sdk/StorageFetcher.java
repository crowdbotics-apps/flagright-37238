package com.flagright.sdk;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import com.flagright.sdk.models.StorageResponseModal;

import java.io.File;
import java.math.BigInteger;

/**
 * Class exposes the methods to get the storage information
 */
public class StorageFetcher {
    private static StorageFetcher mStorageFetcher;
    private final int BYTES = 1024;

    private StorageFetcher() {

    }

    /**
     * get the instance of the {@link StorageFetcher}
     *
     * @return return instance of {@link StorageFetcher}
     */
    public static StorageFetcher getInstance() {
        if (mStorageFetcher == null) {
            mStorageFetcher = new StorageFetcher();
        }
        return mStorageFetcher;
    }

    /**
     * Method return the total internal storage in GB provided by {@link Environment} class of Android SDK
     * @return size of internal storage in GB
     */
    public StorageResponseModal getTotalInternalStorage() {
        StorageResponseModal storageResponseModal = new StorageResponseModal();
        try {
            StatFs rootDir = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            StatFs dataDir = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            BigInteger rootDirCapacity = getDirTotalCapacity(rootDir);
            BigInteger dataDirCapacity = getDirTotalCapacity(dataDir);

            double storageInGB = roundAvoid(rootDirCapacity.add(dataDirCapacity).doubleValue() / (BYTES * BYTES * BYTES),1);
            storageResponseModal.setFound(true);
            storageResponseModal.setStorageInGB(storageInGB);
            return  storageResponseModal;
        } catch (Exception e) {
            storageResponseModal.setFound(false);
            return storageResponseModal;
        }
    }

    /**
     * Method calculate the available internal storage in GB
     * @return available internal storage in GB
     */
    public StorageResponseModal getFreeInternalStorage() {
        StorageResponseModal storageResponseModal = new StorageResponseModal();
        try {
            StatFs rootDir = new StatFs(Environment.getRootDirectory().getAbsolutePath());
            StatFs dataDir = new StatFs(Environment.getDataDirectory().getAbsolutePath());

            long rootAvailableBlocks = rootDir.getAvailableBlocksLong();
            long rootBlockSize = rootDir.getBlockSizeLong();
            double rootFree = BigInteger.valueOf(rootAvailableBlocks).multiply(BigInteger.valueOf(rootBlockSize)).doubleValue();

            long dataAvailableBlocks = dataDir.getAvailableBlocksLong();
            long dataBlockSize = dataDir.getBlockSizeLong();
            double dataFree = BigInteger.valueOf(dataAvailableBlocks).multiply(BigInteger.valueOf(dataBlockSize)).doubleValue();
            storageResponseModal.setFound(true);
            storageResponseModal.setStorageInGB(roundAvoid((rootFree + dataFree)/(BYTES * BYTES * BYTES),1));
            return storageResponseModal;
        } catch (Exception e) {
            storageResponseModal.setFound(false);
            return storageResponseModal;
        }
    }

    /**
     * Method check if external SD card is attached with the device and calculate the size of the
     * external card
     * @param forFreeStorage if true, then method will return only the available size;
     *                       otherwise return the total size
     * @return size of the external storage in GB
     */
    public StorageResponseModal getExternalSdCardSize(boolean forFreeStorage) {
        StorageResponseModal storageResponseModal = new StorageResponseModal();
        File storage = new File("/storage");
        String external_storage_path = "";
        double size = 0;
        try {

            if (storage.exists()) {
                File[] files = storage.listFiles();

                if (files != null) {
                for (File file : files) {
                    if (file.exists()) {
                        try {
                            if (Environment.isExternalStorageRemovable(file)) {
                                // storage is removable
                                external_storage_path = file.getAbsolutePath();
                                break;
                            }
                        } catch (Exception e) {
                            Log.e("TAG", e.toString());
                        }
                    }
                }
                }
            }

            if (!external_storage_path.isEmpty()) {
                File external_storage = new File(external_storage_path);
                if (external_storage.exists()) {
                    try {
                        StatFs sdCardDir = new StatFs(external_storage.getPath());
                        if (!forFreeStorage) {
                            BigInteger rootDirCapacity = getDirTotalCapacity(sdCardDir);
                            size = rootDirCapacity.doubleValue() / (BYTES * BYTES * BYTES);
                        } else {
                            long sdCardAvailableBlocks = sdCardDir.getAvailableBlocksLong();
                            long sdCardBlockSize = sdCardDir.getBlockSizeLong();
                            double sdCardFree = BigInteger.valueOf(sdCardAvailableBlocks).multiply(BigInteger.valueOf(sdCardBlockSize)).doubleValue();
                            size = sdCardFree / (BYTES * BYTES * BYTES);
                        }

                    } catch (Exception ex) {
                        size = 0;
                    }
                }
            }
            storageResponseModal.setFound(true);
            storageResponseModal.setStorageInGB(roundAvoid(size, 1));
            return storageResponseModal;
        }catch (Exception ex) {
           storageResponseModal.setFound(false);
           return storageResponseModal;
        }
    }


    /**
     * Returns round off a double value
     * @param value its a target value
     * @param places indicates up to which decimal place round off method has been called
     * @return round off value
     */
    private double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    /**
     *
     * @param dir targeted storage directory
     * @return
     */
    private BigInteger getDirTotalCapacity(StatFs dir) {
        long blockCount =  dir.getBlockCountLong();
        long blockSize =  dir.getBlockSizeLong();
        return BigInteger.valueOf(blockCount).multiply(BigInteger.valueOf(blockSize));
    }
}
