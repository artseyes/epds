package gov.gao.epds.fileupload.utils;

import java.util.HashMap;

public class ChunkFileInfoStorage {

    private ChunkFileInfoStorage() {
    }
    private static ChunkFileInfoStorage sInstance;

    public static synchronized ChunkFileInfoStorage getInstance() {
        if (sInstance == null) {
            sInstance = new ChunkFileInfoStorage();
        }
        return sInstance;
    }

    private HashMap<String, ChunkFileInfo> mapOfEachChunkUniqueIdentifierAndChunkFileInfo = new HashMap<String, ChunkFileInfo>();

    public synchronized ChunkFileInfo get(int chunkSize, long totalSize,
                             String uniqueIdentifier, String fileName,
                             String relativeFilePath, String serverFilePath) {

        ChunkFileInfo info = mapOfEachChunkUniqueIdentifierAndChunkFileInfo.get(uniqueIdentifier);

        if (info == null) {
            info = new ChunkFileInfo();

            info.chunkSize     = chunkSize;
            info.totalSize     = totalSize;
            info.uniqueIdentifier    = uniqueIdentifier;
            info.fileName      = fileName;
            info.relativeFilePath  = relativeFilePath;
            info.serverFilePath      = serverFilePath;

            mapOfEachChunkUniqueIdentifierAndChunkFileInfo.put(uniqueIdentifier, info);
        }
        return info;
    }

    public void remove(ChunkFileInfo info) {
       mapOfEachChunkUniqueIdentifierAndChunkFileInfo.remove(info.uniqueIdentifier);
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((mapOfEachChunkUniqueIdentifierAndChunkFileInfo == null) ? 0
						: mapOfEachChunkUniqueIdentifierAndChunkFileInfo
								.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkFileInfoStorage other = (ChunkFileInfoStorage) obj;
		if (mapOfEachChunkUniqueIdentifierAndChunkFileInfo == null) {
			if (other.mapOfEachChunkUniqueIdentifierAndChunkFileInfo != null)
				return false;
		} else if (!mapOfEachChunkUniqueIdentifierAndChunkFileInfo
				.equals(other.mapOfEachChunkUniqueIdentifierAndChunkFileInfo))
			return false;
		return true;
	}
    
    
    
}
