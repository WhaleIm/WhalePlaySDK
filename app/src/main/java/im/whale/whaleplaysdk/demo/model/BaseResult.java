package im.whale.whaleplaysdk.demo.model;

/**
 * Created on 2018/12/5.
 *
 * @author ice
 */
public class BaseResult<T> {

    public static final int RESP_CODE_SUCCESS = 10000;

    public int errno;

    public String errmsg;

    public T data;

    public boolean isSuccess() {
        return errno == RESP_CODE_SUCCESS;
    }
}
