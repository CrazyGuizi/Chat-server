package bean;

public class BaseJSON<T> {

    public static final int CODE_OK = 0;
    public static final int CODE_FAILED = 1;
    public static final int CODE_EXCEPTION = 2;


    private int code;
    private String msg;
    private T data;

    public BaseJSON() {}

    public BaseJSON(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
