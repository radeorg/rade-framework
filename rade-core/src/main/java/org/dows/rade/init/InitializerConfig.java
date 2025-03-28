//package org.dows.rade.init;
//
//
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hutool.json.JSONUtil;
//import io.swagger.v3.oas.annotations.Operation;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.dows.rade.config.InitializeProperties;
//import org.dows.rade.model.UriSignature;
//import org.dows.rade.util.MethodSignatureResolver;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
//
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Slf4j
//@RequiredArgsConstructor
//@Configuration
//@ConditionalOnProperty(name = "rade.initialize.enabled", havingValue = "true")
//@EnableConfigurationProperties(InitializeProperties.class)
//public class InitializerConfig {
//
//    private final InitializeProperties initializeProperties;
//
//    private final RequestMappingInfoHandlerMapping requestMappingHandlerMapping;
//
//    @Value("${spring.application.appId}")
//    private String appId;
//
//    /**
//     * 扫描并返回所有需要权限处理的接口资源
//     * 这里模拟扫描，借助 org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping
//     */
//    //@Bean
//    public Map<Class<? extends ResourceInitializer>, List<InitializableResource>> buildUriResources() {
//        List<InitializeProperties.Resource> resources = initializeProperties.getResources();
//        Map<Class<? extends ResourceInitializer>, List<InitializableResource>> map = new HashMap<>();
//        for (InitializeProperties.Resource resource : resources) {
//            String beanName = StrUtil.lowerFirst(resource.getInitializer().getSimpleName());
//            try {
//                ResourceInitializer initializer = SpringUtil.getBean(beanName, ResourceInitializer.class);
//                if (initializer != null) {
//                    List<InitializableResource> uriSignatures =
//                            map.computeIfAbsent(resource.getInitializer(), k -> new LinkedList<>());
//                    uriSignatures.addAll(buildRbacUri(resource.getScanPackages()));
//                }
//            } catch (Exception e) {
//                log.error("", e);
//            }
//        }
//        return map;
//        //resourceInitializer.init(list);
//        //return list;
//    }
//
//    public List<UriSignature> buildRbacUri(List<String> scanPackages) {
//        // 接下来要添加到数据库的资源
//        List<UriSignature> list = new LinkedList<>();
//        //List<String> scanPackages = resource.getScanPackages();
//        // 校验 scanPackages 是否有效
//        if (scanPackages == null || scanPackages.isEmpty()) {
//            log.warn("scanPackages is empty or null, no resources will be scanned.");
//            return list;
//        }
//
//        // 获取所有接口信息，并开始遍历
//        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
//        if (handlerMethods.isEmpty()) {
//            log.warn("No handler methods found in requestMappingHandlerMapping.");
//            return list;
//        }
//
//        Set<String> validPackages = scanPackages.stream()
//                .filter(pkg -> pkg != null && !pkg.trim().isEmpty())
//                .collect(Collectors.toSet());
//
//        handlerMethods.forEach((info, handlerMethod) -> {
//            String packageName = handlerMethod.getBeanType().getPackageName();
//            if (validPackages.isEmpty()) {
//                return;
//            }
//
//            for (String pkg : validPackages) {
//                if (packageName.startsWith(pkg)) {
//                    list.add(extracted(info, handlerMethod));
//                    break;
//                }
//            }
//        });
//
//
//        // todo 保存数据库，生成 lock（如果初始话成功，不在初始化）
//        try {
//            Files.writeString(Path.of(System.getProperty("user.dir")).resolve("menu.json"), JSONUtil.toJsonPrettyStr(list));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return list;
//    }
//
//    private UriSignature extracted(RequestMappingInfo info, HandlerMethod handlerMethod) {
//        //{GET [/v1/admin/menus/listByAppId]}
//        String key = info.toString();
//        String[] restUri = key.replaceAll("[\\{\\}\\[\\]]", "").split(" ");
//        String httpMethod1 = restUri[0];
//        String path1 = restUri[1];
//        String javaMethodName = handlerMethod.toString().split("\\(")[0];
//
//        Method method = handlerMethod.getMethod();
//        Operation operation = method.getAnnotation(Operation.class);
//        String description = operation.description();
//        String summary = operation.summary();
//        UriSignature uriSignature = MethodSignatureResolver.parse(method);
//        uriSignature.setDescription(description);
//        uriSignature.setSummary(summary);
//        uriSignature.setJavaMethod(javaMethodName);
//        uriSignature.setHttpMethod(httpMethod1);
//        uriSignature.setUri(path1);
//        uriSignature.setAppId(appId);
//        log.debug("方法签名: {}", JSONUtil.toJsonPrettyStr(uriSignature));
//        return uriSignature;
//    }
//
//
//}
