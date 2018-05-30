package bd.edu.daffodilvarsity.classorganizer.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateResponse implements Parcelable {
    @SerializedName("datetime")
    @Expose
    private long datetime;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("md5")
    @Expose
    private String md5;
    @SerializedName("size")
    @Expose
    private long size;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("version")
    @Expose
    private int version;

    public UpdateResponse() {
    }

    public UpdateResponse(int datetime, String filename, String md5, int size, String url, int version) {
        this.datetime = datetime;
        this.filename = filename;
        this.md5 = md5;
        this.size = size;
        this.url = url;
        this.version = version;
    }

    public long getDatetime() {
        return datetime;
    }

    public void setDatetime(long datetime) {
        this.datetime = datetime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.datetime);
        dest.writeString(this.filename);
        dest.writeString(this.md5);
        dest.writeLong(this.size);
        dest.writeString(this.url);
        dest.writeInt(this.version);
    }

    protected UpdateResponse(Parcel in) {
        this.datetime = in.readLong();
        this.filename = in.readString();
        this.md5 = in.readString();
        this.size = in.readLong();
        this.url = in.readString();
        this.version = in.readInt();
    }

    public static final Parcelable.Creator<UpdateResponse> CREATOR = new Parcelable.Creator<UpdateResponse>() {
        @Override
        public UpdateResponse createFromParcel(Parcel source) {
            return new UpdateResponse(source);
        }

        @Override
        public UpdateResponse[] newArray(int size) {
            return new UpdateResponse[size];
        }
    };
}