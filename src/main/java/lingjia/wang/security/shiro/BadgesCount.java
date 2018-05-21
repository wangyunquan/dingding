package lingjia.wang.security.shiro;

import java.io.Serializable;

/**
 * @author lingjia on 2018/1/16
 */


public class BadgesCount implements Serializable {
    private static final long serialVersionUID = 8276459939240769498L;

    private int notifies; // 通知数量
    private int messages; // 私信数量

    public int getNotifies() {
        return notifies;
    }

    public void setNotifies(int notifies) {
        this.notifies = notifies;
    }

    public int getMessages() {
        return messages;
    }

    public void setMessages(int messages) {
        this.messages = messages;
    }
}
