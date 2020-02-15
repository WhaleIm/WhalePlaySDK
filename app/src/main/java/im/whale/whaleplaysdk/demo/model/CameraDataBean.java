package im.whale.whaleplaysdk.demo.model;

/**
 * Created on 2019-11-14.
 *
 * @author ice
 */
public class CameraDataBean {

    /*
    {
        "timestamp": "1550911988762",
            "clientSn": "xxxx",
            "cameraSn": "xxxx",
            "data":{
        "action": "approach",
                "id":22,
                "faceId": "1111-2222-2333-4444-555",
                "picturePath": "xxxx/picture_name.jpg",
                "videoPath":"",
                "age":22,
                "sex":"male",
                "emotion":"neutral",
                "beauty":50.94
    }
    }
    */


    public String timestamp;
    public String clientSn;
    public String cameraSn;
    public PersonBean data;

    public String nickName;

    public static class PersonBean {
        public String action;
        public String id;
        public String faceId;
        public String picturePath;
        public String videoPath;
        public int age;
        public String sex;
        public String emotion;
        public String beauty;
    }

    public boolean isApproach() {
        return data != null && "approach".equals(data.action);
    }

    public boolean isCorrectFace() {
        return data != null && data.faceId != null && (!data.faceId.equals(data.id));
    }


    public String getAvatar() {
        if (data == null || data.picturePath == null)
            return "";
        String url = data.picturePath;
        if (url.startsWith("http://") || url.startsWith("https://")) {

        } else {
            url = "http://" + url;
        }

        return url;
    }

}
