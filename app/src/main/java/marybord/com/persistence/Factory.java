package marybord.com.persistence;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Factories")
public class Factory implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String address;
    private int linesNumber;

    public Factory(String name, String address, int linesNumber) {
        this.name = name;
        this.address = address;
        this.linesNumber = linesNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getLinesNumber() {
        return linesNumber;
    }

    public void setId(int id) {
        this.id = id;
    }

    private Factory(Parcel parcel) {
        this.name = parcel.readString();
        this.address = parcel.readString();
        this.linesNumber = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(address);
        parcel.writeInt(linesNumber);
    }

    public static final Creator<Factory> CREATOR = new Creator<Factory>() {
        @Override
        public Factory createFromParcel(Parcel parcel) {
            String name = parcel.readString();
            String address = parcel.readString();
            int linesNumber = parcel.readInt();
            return new Factory(name, address, linesNumber);
        }

        @Override
        public Factory[] newArray(int size) {
            return new Factory[size];
        }
    };
}
