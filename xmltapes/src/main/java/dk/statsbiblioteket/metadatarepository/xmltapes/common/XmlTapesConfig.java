package dk.statsbiblioteket.metadatarepository.xmltapes.common;

import java.io.File;

public class XmlTapesConfig {

    private File tempDir;
    private File cachingDir;
    private File tapingDir;
    private File tapeDir;

    private long tapeWaitBeforeStartInMS;

    private long minAgeToTapeInMS;

    private long tapeSize;

    private String tapeExtension, tapePrefix, tempTapePrefix;
    private String redisHost;
    private int redisPort;
    private int redisDB;
    private boolean fixErrorsOnRestart;
    private boolean rebuildIndexFromTapesOnRestart;

    private String dbDriver;
    private String jdbcUrl;
    private String dbUser;
    private String dbPass;
    
    
    public long getTapeWaitBeforeStartInMS() {
        return tapeWaitBeforeStartInMS;
    }

    public void setTapeWaitBeforeStartInMS(long tapeWaitBeforeStartInMS) {
        this.tapeWaitBeforeStartInMS = tapeWaitBeforeStartInMS;
    }

    public long getMinAgeToTapeInMS() {
        return minAgeToTapeInMS;
    }

    public void setMinAgeToTapeInMS(long minAgeToTapeInMS) {
        this.minAgeToTapeInMS = minAgeToTapeInMS;
    }

    public long getTapeSize() {
        return tapeSize;
    }

    public void setTapeSize(long tapeSize) {
        this.tapeSize = tapeSize;
    }

    public String getTapeExtension() {
        return tapeExtension;
    }

    public void setTapeExtension(String tapeExtension) {
        this.tapeExtension = tapeExtension;
    }

    public String getTapePrefix() {
        return tapePrefix;
    }

    public void setTapePrefix(String tapePrefix) {
        this.tapePrefix = tapePrefix;
    }

    public String getTempTapePrefix() {
        return tempTapePrefix;
    }

    public void setTempTapePrefix(String tempTapePrefix) {
        this.tempTapePrefix = tempTapePrefix;
    }

    public File getTapeDir() {
        return tapeDir;
    }

    public void setTapeDir(File tapeDir) {
        this.tapeDir = tapeDir;
    }

    public String getRedisHost() {
        return redisHost;
    }



    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }



    public boolean isFixErrorsOnRestart() {
        return fixErrorsOnRestart;
    }

    public void setFixErrorsOnRestart(boolean fixErrorsOnRestart) {
        this.fixErrorsOnRestart = fixErrorsOnRestart;
    }

    public boolean isRebuildIndexFromTapesOnRestart() {
        return rebuildIndexFromTapesOnRestart;
    }

    public void setRebuildIndexFromTapesOnRestart(boolean rebuildIndexFromTapesOnRestart) {
        this.rebuildIndexFromTapesOnRestart = rebuildIndexFromTapesOnRestart;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public int getRedisDB() {
        return redisDB;
    }

    public void setRedisDB(int redisDB) {
        this.redisDB = redisDB;
    }

    public File getTempDir() {
        return tempDir;
    }

    public void setTempDir(File tempDir) {
        this.tempDir = tempDir;
    }

    public File getCachingDir() {
        return cachingDir;
    }

    public void setCachingDir(File cachingDir) {
        this.cachingDir = cachingDir;
    }

    public File getTapingDir() {
        return tapingDir;
    }

    public void setTapingDir(File tapingDir) {
        this.tapingDir = tapingDir;
    }
    
    public String getDbDriver() {
        return dbDriver;
    }

    public void setDbDriver(String dbDriver) {
        this.dbDriver = dbDriver;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPass() {
        return dbPass;
    }

    public void setDbPass(String dbPass) {
        this.dbPass = dbPass;
    }

    @Override
    public String toString() {
        return "XmlTapesConfig [tempDir=" + tempDir + ", cachingDir=" + cachingDir + ", tapingDir=" + tapingDir
                + ", tapeDir=" + tapeDir + ", tapeWaitBeforeStartInMS=" + tapeWaitBeforeStartInMS
                + ", minAgeToTapeInMS=" + minAgeToTapeInMS + ", tapeSize=" + tapeSize + ", tapeExtension="
                + tapeExtension + ", tapePrefix=" + tapePrefix + ", tempTapePrefix=" + tempTapePrefix + ", redisHost="
                + redisHost + ", redisPort=" + redisPort + ", redisDB=" + redisDB + ", fixErrorsOnRestart="
                + fixErrorsOnRestart + ", rebuildIndexFromTapesOnRestart=" + rebuildIndexFromTapesOnRestart
                + ", dbDriver=" + dbDriver + ", jdbcUrl=" + jdbcUrl + ", dbUser=" + dbUser + "]";
    }
    
}
