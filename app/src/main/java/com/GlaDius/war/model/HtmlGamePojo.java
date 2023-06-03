    package com.GlaDius.war.model;

    import android.os.Parcel;
    import android.os.Parcelable;

    public class HtmlGamePojo implements Parcelable {

    private String id,title;
    private int image;

    public HtmlGamePojo(String id,String title, int image){
        this.id=id;
        this.title=title;
        this.image=image;
    }

        protected HtmlGamePojo(Parcel in) {
            id = in.readString();
            title = in.readString();
            image = in.readInt();
        }

        public static final Creator<HtmlGamePojo> CREATOR = new Creator<HtmlGamePojo>() {
            @Override
            public HtmlGamePojo createFromParcel(Parcel in) {
                return new HtmlGamePojo(in);
            }

            @Override
            public HtmlGamePojo[] newArray(int size) {
                return new HtmlGamePojo[size];
            }
        };

        public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(title);
            dest.writeInt(image);
        }
    }
