package barqsoft.footballscores.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

/**
 * Created by Anton on 7/23/2015.
 */
public class Team implements Parcelable {

    @Expose
    private String crestUrl;

    @Expose
    private String name;

    @Expose
    private String shortName;

    @Expose

    private String code;

    public Team() {
    }

    private Team(Parcel in) {
        crestUrl = in.readString();
        name = in.readString();
        shortName = in.readString();
        code = in.readString();
    }

    public static final Parcelable.Creator<Team> CREATOR
            = new Parcelable.Creator<Team>() {
        public Team createFromParcel(Parcel in) {
            return new Team(in);
        }

        public Team[] newArray(int size) {
            return new Team[size];
        }
    };

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(crestUrl);
        out.writeString(name);
        out.writeString(shortName);
        out.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    public void setCrestUrl(String crestUrl) {
        this.crestUrl = crestUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
