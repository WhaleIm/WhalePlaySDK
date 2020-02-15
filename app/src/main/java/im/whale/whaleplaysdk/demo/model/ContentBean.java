package im.whale.whaleplaysdk.demo.model;

/**
 * Created on 2019-11-20.
 *
 * @author ice
 */
public class ContentBean {

    public String source;
    public int type;

    public boolean isVideo() {
        return type == 2;
    }

    public boolean isImage() {
        return type == 1;
    }

    public String getSourceUrl() {
        if (source == null)
            return null;

        String url = source;
        if (url.startsWith("http://") || url.startsWith("https://")) {
        } else {
            url = "http://" + url;
        }
        return url;
    }

}
