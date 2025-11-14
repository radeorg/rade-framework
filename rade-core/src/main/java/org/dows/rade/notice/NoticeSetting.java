package org.dows.rade.notice;

import lombok.Data;

import java.util.List;

@Data
public class NoticeSetting {
    private boolean enable;
    private List<String> wechatKeys;
}
