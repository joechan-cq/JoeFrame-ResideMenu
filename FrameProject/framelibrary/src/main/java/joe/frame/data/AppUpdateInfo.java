package joe.frame.data;

/**
 * Description  应用升级信息
 * Created by chenqiao on 2015/9/2.
 */
public class AppUpdateInfo {

    private boolean isNeedToUpdate;

    private String versionName;

    private int versionCode;

    private String downloadUrl;

    private String appName;

    private String updateInfo;

    private String suffixName;


    public String getSuffixName() {
        return suffixName;
    }

    public void setSuffixName(String suffixName) {
        this.suffixName = suffixName;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public boolean isNeedToUpdate() {
        return isNeedToUpdate;
    }

    public void setIsNeedToUpdate(boolean isNeedToUpdate) {
        this.isNeedToUpdate = isNeedToUpdate;
    }

    public AppUpdateInfo(boolean isNeedToUpdate, String suffixName, String updateInfo, String appName, String downloadUrl, int versionCode, String versionName) {
        this.isNeedToUpdate = isNeedToUpdate;
        this.suffixName = suffixName;
        this.updateInfo = updateInfo;
        this.appName = appName;
        this.downloadUrl = downloadUrl;
        this.versionCode = versionCode;
        this.versionName = versionName;
    }

    public AppUpdateInfo() {
    }
}
