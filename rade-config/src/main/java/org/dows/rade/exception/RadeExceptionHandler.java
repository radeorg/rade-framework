package org.dows.rade.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.rade.status.CommonStatusCode;
import org.dows.rade.status.ResponseStatusCode;
import org.dows.rade.status.StatusCode;
import org.dows.rade.web.Response;
import org.dows.rade.web.UnifiedMessageSource;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.util.zip.DataFormatException;

import static org.springframework.util.StringUtils.hasText;

/***
 * 统一封装异常、统一处理出参
 */
@Slf4j
@RequiredArgsConstructor
//@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 1)
public abstract class RadeExceptionHandler {

    private final UnifiedMessageSource unifiedMessageSource;
    /**
     * 生产环境
     */
    private final static String ENV_PRD = "prd";

    @Value("${spring.application.name:}")
    private String serviceName;
    /**
     * 当前环境
     */
    @Value("${spring.profiles.active:}")
    private String profile;

//    @Autowired
//    private AacConfig aacConfig;

    /**
     * 参数校验(Valid)异常，validator参数校验异常处理 将校验失败的所有异常组合成一条错误信息
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response handleMethodArgumentNotValidException(HttpServletRequest request, HttpServletResponse response,
                                                          MethodArgumentNotValidException e) {
        log.error("调用={}服务出现方法参数校验异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e);
        /*Response returnResponse = new Response();
        BindingResult result = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        result.getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            errorMap.put(field, message);
        });
        returnResponse.setData(errorMap);*/
        return wrapperBindingResult(e.getBindingResult());
    }


    //    /**
//     * 业务异常处理
//     *
//     * @param e
//     * @return
//     */
//    @ExceptionHandler(RadeException.class)
//    public Response handleRRException(RadeException e) {
//        Response response = new Response();
//        if (ObjUtil.isNotEmpty(e.getData())) {
//            response.put("data", e.getData());
//        } else {
//            response.put("code", e.getCode());
//            response.put("message", e.getMessage());
//        }
//        if (ObjUtil.isNotEmpty(e.getCause())) {
//            log.error(e.getCause().getMessage(), e.getCause());
//        }
//        return response;
//    }
//
    @ExceptionHandler(DuplicateKeyException.class)
    public Response handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return Response.failed("已存在该记录或值不能重复");
    }

    /*@ExceptionHandler(BadCredentialsException.class)
    public Response handleBadCredentialsException(BadCredentialsException e) {
        log.error(e.getMessage(), e);
        return Response.error("账户密码不正确");
    }*/




//
//    @ExceptionHandler(Exception.class)
//    public Response handleException(Exception e) {
//        log.error(e.getMessage(), e);
//        return Response.error();
//    }


    /**
     * 自定义异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = RadeException.class)
    public Response<?> handleRadeException(HttpServletRequest request, HttpServletResponse response, RadeException e) {
        log.error("调用={}服务出现自定义异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e.getMessage(), e);
        if (e.getStatusCode() != null) {
            return Response.failed(e.getStatusCode());
        }
        return Response.failed(getMessage(e));
    }

    /**
     * Controller上一层相关异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NoHandlerFoundException.class,
            HttpMediaTypeNotSupportedException.class, HttpMediaTypeNotAcceptableException.class,
            MissingPathVariableException.class,
            HttpMessageNotReadableException.class, HttpMessageNotWritableException.class,
            ServletRequestBindingException.class, ConversionNotSupportedException.class,
            MissingServletRequestPartException.class})
    public Response<?> handleServletException(HttpServletRequest request, HttpServletResponse response,
                                              Exception e) {
        log.error("调用={}服务出现controller的上层出现异常，请求的url是={}，请求的方法是={}，原因={}", serviceName, request.getRequestURL(),
                request.getMethod(), e);
        String code = CommonStatusCode.SERVER_ERROR.getCode();
        try {
            ResponseStatusCode servletExceptionEnum = ResponseStatusCode.valueOf(e.getClass().getSimpleName());
            code = servletExceptionEnum.getCode();
        } catch (Exception e1) {
            log.error("class [{}] not defined in enum {}", e.getClass().getName(), ResponseStatusCode.class.getName());
        }

        if (ENV_PRD.equals(profile)) {
            // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如404.
            code = CommonStatusCode.SERVER_ERROR.getCode();
            RadeException RadeException = new RadeException(CommonStatusCode.SERVER_ERROR);
            String message = getMessage(RadeException);
            return Response.failed(code, message);
        }

        return Response.failed(code, e.getMessage());
    }

    /**
     * 参数绑定异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = BindException.class)
    public Response<?> handleBindException(HttpServletRequest request, HttpServletResponse response, BindException e) {
        log.error("调用={}服务出现参数绑定校验异常，请求的url是={}，请求的方法是={}，原因={}",
                serviceName, request.getRequestURL(), request.getMethod(), e);
        return wrapperBindingResult(e.getBindingResult());
    }



    /**
     * 其他未定义的异常
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param e        异常
     * @return 异常结果
     */
    @ExceptionHandler(value = Exception.class)
    public Object resolveException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.error("调用={}服务出现异常了，请求的url是={}，请求的方法是={}，原因={}",
                serviceName, request.getRequestURL(), request.getMethod(), e.getMessage(), e);

        if (isJsonRequest(request)) {
            if (ENV_PRD.equals(profile)) {
                // 当为生产环境, 不适合把具体的异常信息展示给用户, 比如数据库异常信息.
                /*RadeException RadeException = new RadeException(CommonStatusCode.SERVER_EXCEPTION);
                String message = getMessage(RadeException);*/
                return Response.failed(CommonStatusCode.SERVER_EXCEPTION);
            }
            return Response.failed(e.getMessage());
        } else {
            ModelAndView mav = new ModelAndView();
            mav.addObject("exception", e);
            mav.addObject("url", request.getRequestURL());
            mav.setViewName("error");
            return mav;
        }

    }


    /**
     * 获取国际化消息
     *
     * @param e 异常
     * @return 国际化消息
     */
    public String getMessage(RadeException e) {
        String message = "";

        if (null != e.getCode()) {
            StatusCode statusCode = e.getStatusCode();
            if (null == statusCode) {
                statusCode = CommonStatusCode.FAILED;
            }
            String code = "response." + statusCode.getCode();
            message = unifiedMessageSource.getMessage(code, e.getArgs());
        }

        if (!hasText(message)) {
            return e.getMessage();
        }

        return message;
    }

    /**
     * 包装绑定异常结果
     *
     * @param bindingResult 绑定结果
     * @return 异常结果
     */
    private Response<?> wrapperBindingResult(BindingResult bindingResult) {
        StringBuilder msg = new StringBuilder();

        for (ObjectError error : bindingResult.getAllErrors()) {
            msg.append(", ");
            if (error instanceof FieldError) {
                msg.append(((FieldError) error).getField()).append(": ");
            }
            msg.append(error.getDefaultMessage() == null ? "" : error.getDefaultMessage());

        }
        String descr = msg.substring(2);
        return Response.failed(descr);
    }


    /**
     * http请求的方法不正确
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage(), e);
        return Response.failed("不支持该请求方式，请区分POST、GET等请求方式是否正确");
    }

    /**
     * 请求参数不全
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Response missingServletRequestParameterExceptionHandler(MissingServletRequestParameterException e) {
        log.error("请求参数不全:【" + e.getMessage() + "】", e);
        return Response.failed("请求参数不全");
    }

    /**
     * 请求参数类型不正确
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public Response typeMismatchExceptionHandler(TypeMismatchException e) {
        log.error("请求参数类型不正确:【" + e.getMessage() + "】", e);
        return Response.failed("请求参数类型不正确");
    }

    /**
     * 数据格式不正确
     */
    @ExceptionHandler(DataFormatException.class)
    @ResponseBody
    public Response dataFormatExceptionHandler(DataFormatException e) {
        log.error("数据格式不正确:【" + e.getMessage() + "】", e);
        return Response.failed("数据格式不正确");
    }

    /**
     * 非法输入
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Response illegalArgumentExceptionHandler(IllegalArgumentException e) {
        log.error("非法输入:【" + e.getMessage() + "】", e);
        return Response.failed("非法输入:" + e.getMessage());
    }


    @ExceptionHandler(SQLException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(SQLException e) {
        log.info("sql:【" + e.getMessage() + "】", e);
        return Response.failed(e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(AccessDeniedException e) {
        log.info("拒绝访问:【" + e.getMessage() + "】", e);
        return Response.failed(e.getMessage());
    }

    /**
     * 判断是否为 JSON 格式的请求
     *
     * @param httpRequest 请求对象
     * @return 如果是 JSON 请求返回 true，否则返回 false
     */
    public static boolean isJsonRequest(HttpServletRequest httpRequest) {
        String contentType = httpRequest.getContentType();
        return contentType != null && contentType.contains("application/json");
    }
    /**
     * 判断是否ajax请求
     *
     * @param httpRequest
     * @return
     */
    public static boolean isAjax(HttpServletRequest httpRequest) {
        return (httpRequest.getHeader("X-Requested-With") != null
                && "XMLHttpRequest".equals(httpRequest.getHeader("X-Requested-With").toString()));
    }

    @ExceptionHandler(value = AsyncRequestTimeoutException.class)
    public Response<?> asyncRequestTimeoutHandler(HttpServletRequest request, HttpServletResponse response, AsyncRequestTimeoutException e) {
        log.warn("异步请求超时:【" + e.getMessage() + "】");
        return Response.failed("304", "长轮询超时重试");
    }


/*    @ExceptionHandler(value = {BadSqlGrammarException.class})
    public Response<?> badSqlGrammarExceptionHandler(HttpServletRequest request, HttpServletResponse response, BadSqlGrammarException e) {
        log.warn("sql错误:【" + e.getMessage() + "】");
        return Response.failed("500", e.getSQLException().getMessage());
    }*/

    @ExceptionHandler(value = {SQLSyntaxErrorException.class})
    public Response<?> sqlSyntaxErrorExceptionHandler(HttpServletRequest request, HttpServletResponse response, SQLSyntaxErrorException e) {
        log.warn("sql语法错误:【" + e.getMessage() + "】");
        return Response.failed("500", e.getCause().getMessage());
    }


    @ExceptionHandler(value = {RuntimeException.class})
    public Response<?> runtimeExceptionHandler(HttpServletRequest request, HttpServletResponse response, RuntimeException e) {
        return Response.failed("500", e.getMessage());
    }

    @ExceptionHandler(value = {SignatureException.class})
    public Response<?> signatureExceptionHandler(SignatureException e) {
        return Response.failed("500", e.getMessage());
    }





            /*@ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Response authenticationExceptionHandler(AuthenticationException e) {
        log.info("认证失败授权:【" + e.getMessage() + "】", e);
        // 如果有开启则返回ok
        if (aacConfig.isEnableLogin()) {
            if (e instanceof BadCredentialsException) {
                return Response.authFailed(AuthStatusCode.PASSWORD_ERROR);
            } else if (e instanceof UsernameNotFoundException) {
                return Response.authFailed(AuthStatusCode.PASSWORD_ERROR);
            } else {
                return Response.unauthorized(e.getMessage());
            }
        }
        return Response.ok();
    }*/

    /*@ExceptionHandler  //处理其他异常
    @ResponseBody
    public Object allExceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) {
        System.out.println(e.getStackTrace());
        log.error("具体错误信息:【" + e.getMessage() + "】"); //会记录出错的代码行等具体信息
        e.printStackTrace();
        if (isAjax(request)) {
            return Response.failed(e.getMessage());
        } else {
            ModelAndView mav = new ModelAndView();
            mav.addObject("exception", e);
            mav.addObject("url", request.getRequestURL());
            mav.setViewName("error");
            return mav;
        }
    }*/


    /*@ExceptionHandler(value = {TokenExpiredException.class})
    public Response<?> tokenExpiredExceptionHandler(HttpServletRequest request, HttpServletResponse response, TokenExpiredException e) {
        return Response.failed("401", e.getMessage());
    }*/


}
