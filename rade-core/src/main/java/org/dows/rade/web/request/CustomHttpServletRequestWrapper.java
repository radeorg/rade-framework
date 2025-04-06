//package org.dows.rade.web.request;
//
//import jakarta.annotation.Resource;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletRequestWrapper;
//
//class CustomHttpServletRequestWrapper extends HttpServletRequestWrapper {
//    @Resource
//    private AppConfig appConfig;
//
//    public CustomHttpServletRequestWrapper(HttpServletRequest request) {
//        super(request);
//        String authorization = request.getHeader("Authorization");
//        if (authorization != null && authorization.startsWith("Bearer ")) {
//            String[] tokens = authorization.split("-");
//            // 验证token
//            if (tokens.length == 3) {
//                String id = tokens[0];
//                String key = tokens[1];
//                // 验证id和key
//                if (appConfig.getId().equals(id) && appConfig.getKey().equals(key)) {
//                    // 验证通过
//                    return;
//                } else {
//                    // 验证不通过
//                    throw new RuntimeException("验证不通过");
//                }
//            }
//        }
//    }
//    @Override
//    public String getRequestURI() {
//        return super.getRequestURI();
//    }
//}