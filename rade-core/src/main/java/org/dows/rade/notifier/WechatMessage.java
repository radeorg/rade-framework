package org.dows.rade.notifier;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.rade.annotation.Skip;
import org.dows.rade.exchange.ExchangeMessage;
import org.dows.rade.exchange.Uri;
import org.dows.rade.exchange.UriHeader;
import org.dows.rade.exchange.UriParam;

@Uri("post https://qyapi.weixin.qq.com/cgi-bin/webhook/send")
@Data
public class WechatMessage implements ExchangeMessage {
    // 遇到skip 跳过或忽略该参数
    @Skip
    // 该注解会将参数名和对应的值追加到请求的 URL 中
    @UriParam("key")
    @Schema(description = "企业微信机器人KEY")
    private String key;

    @Schema(description = "消息类型[text, markdown, news, image, voice, video, file]")
    private String msgtype;

    //@UriBody("text")
    @UriHeader
    private String token;

    @Schema(description = "文本消息")
    private String text;

    @Schema(description = "Markdown消息")
    //    @JsonIgnore
    private Object markdown;
    //"UserID1|UserID2|UserID3",
    private String touser;
    //"PartyID1|PartyID2",
    private String toparty;
    //"TagID1 | TagID2",
    private String totag;
    // 1
    private String agentid;
    //0
    private String safe;
    // 0
    private String enable_id_trans;
    //0
    private String enable_duplicate_check;
    //1800
    private String duplicate_check_interval;
    // 是否测试消息，测试消息不会发送到企业微信
    private boolean test;


    public void setMarkdown(Markdown markdown) {
        this.msgtype = Markdown.class.getSimpleName().toLowerCase();
        this.markdown = markdown;
    }


    public void setText(Text text) {
        this.msgtype = Text.class.getSimpleName().toLowerCase();
        this.text = text.getContent();
    }

}
