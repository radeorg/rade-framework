package org.dows.feign;

import feign.Headers;
import org.dows.rade.feign.register.FeignPlusClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignPlusClient(name = "github", url = "${github.url}", port = "${github.port}")
public interface Github {

    @GetMapping("/repos/{owner}/{repo}/contributors")
    @Headers("Content-Type: application/json")
    List<GitHubRes> contributors(@PathVariable("owner") String owner, @PathVariable("repo") String repo);
}
