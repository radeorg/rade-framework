package org.dows.feign;

import lombok.Data;

@Data
public class GitHubRes {


    /**
     * login : geeker-lait
     * id : 15684156
     * node_id : MDQ6VXNlcjE1Njg0MTU2
     * avatar_url : https://avatars3.githubusercontent.com/u/15684156?v=4
     * gravatar_id :
     * url : https://api.github.com/users/geeker-lait
     * html_url : https://github.com/geeker-lait
     * followers_url : https://api.github.com/users/geeker-lait/followers
     * following_url : https://api.github.com/users/geeker-lait/following{/other_user}
     * gists_url : https://api.github.com/users/geeker-lait/gists{/gist_id}
     * starred_url : https://api.github.com/users/geeker-lait/starred{/owner}{/repo}
     * subscriptions_url : https://api.github.com/users/geeker-lait/subscriptions
     * organizations_url : https://api.github.com/users/geeker-lait/orgs
     * repos_url : https://api.github.com/users/geeker-lait/repos
     * events_url : https://api.github.com/users/geeker-lait/events{/privacy}
     * received_events_url : https://api.github.com/users/geeker-lait/received_events
     * type : User
     * site_admin : false
     * contributions : 1
     */

    private String login;
    private int id;
    private String node_id;
    private String avatar_url;
    private String gravatar_id;
    private String url;
    private String html_url;
    private String followers_url;
    private String following_url;
    private String gists_url;
    private String starred_url;
    private String subscriptions_url;
    private String organizations_url;
    private String repos_url;
    private String events_url;
    private String received_events_url;
    private String type;
    private boolean site_admin;
    private int contributions;

}
