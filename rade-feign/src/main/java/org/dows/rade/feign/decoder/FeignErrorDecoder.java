package org.dows.rade.feign.decoder;

/**
 * Function:
 * <p>
 * <p>
 * Date: 2022/4/28 00:15
 */
public interface FeignErrorDecoder {
    /**
     * custom exception
     *
     * @param methodKey method name
     * @param response  raw response(json)
     * @param e         exception
     * @return custom exception
     */
    Exception decode(String methodKey, String response, Exception e);
}
