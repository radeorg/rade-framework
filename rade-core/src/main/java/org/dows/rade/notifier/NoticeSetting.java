package org.dows.rade.notifier;

import lombok.Data;

import java.util.List;

@Data
public class NoticeSetting {
    private boolean enable;
    private List<String> wechatKeys;
    private List<String> emails;
}
