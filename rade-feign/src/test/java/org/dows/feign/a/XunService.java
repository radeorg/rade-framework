package org.dows.feign.a;

import feign.RequestLine;


public interface XunService {
    @RequestLine("/xun/hello")
    String hello();
}
