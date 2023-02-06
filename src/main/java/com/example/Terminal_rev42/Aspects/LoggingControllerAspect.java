package com.example.Terminal_rev42.Aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Aspect
@Component
public class LoggingControllerAspect {

    @Autowired
    LoggingControllerAspect(HttpServletRequest request, HttpServletResponse response){
        this.request = request;
        this.response = response;
    }

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void controllerPointcut() {};

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void exceptionLogging(JoinPoint joinPoint, Exception e) throws IOException {
        log.error(printRequest(request) + "\n" + printException(response, joinPoint, e));
    }

    @AfterReturning(value = "controllerPointcut()", returning = "retVal")
    public void successHandledRequest(JoinPoint joinPoint, Object retVal) throws IOException {
        log.info(printRequest(request) + "\n" + printResponse(response, retVal));

    }

    private String printException(HttpServletResponse response, JoinPoint joinPoint, Exception e){
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        String responseBody = getResponseBody(response);
        return "\n\t----- RESPONSE AFTER EXCEPTION -----" + "\nStatus \t" + status.name() + "(" + status.value() + ")" +
                "\nContent type \t" + response.getContentType() +
                "\nContent \t" + responseBody + "\nException in \t" + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName() + "()" +
                "\nCause \t" + e.getMessage();
    }

    private String printResponse(HttpServletResponse response, Object retVal) throws IOException {
        HttpStatus status = HttpStatus.valueOf(response.getStatus());
        String responseBody = getResponseBody(response);
        return "\n\t----- RESPONSE -----" + "\nStatus \t" + status.name() + "(" + status.value() + ")" +
                "\nContent type \t" + response.getContentType() +
                "\nContent \t" + retVal;
    }

    private String printRequest(HttpServletRequest request) throws IOException {
        final String referer = getReferer(request);
        final String fullURL = getFullURL(request);
        final String clientIpAddress = getClientIpAddress(request);
        final String clientOS = getClientOS(request);
        final String clientBrowser = getClientBrowser(request);
        final String principal = getPrincipal(request);
        final String session = getSession(request);
        final String requestMethod = getRequestType(request);
        final String requestParams = getRequestParams(request);
        final String requestBody = getRequestBody(request);

        return new StringBuilder()
                .append("\n\t----- REQUEST -----")
                .append("\n").append("Session \t").append(session)
                .append("\n").append("Username \t").append(principal)
                .append("\n").append("Operating System \t").append(clientOS)
                .append("\n").append("Browser Name \t").append(clientBrowser)
                .append("\n").append("IP Address \t").append(clientIpAddress)
                .append("\n").append("Request method \t").append(requestMethod)
                .append("\n").append("Request body \t").append(requestBody)
                .append("\n").append("Request params \t").append(requestParams)
                .append("\n").append("Full URL \t").append(fullURL)
                .append("\n").append("Referrer \t").append(referer).toString();
    }


    private String getResponseBody(HttpServletResponse response){
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);
        System.err.println(new String(contentCachingResponseWrapper.getContentAsByteArray()));
        return Arrays.toString(contentCachingResponseWrapper.getContentAsByteArray());
    }
    private String getRequestBody(HttpServletRequest request) throws IOException {
        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        System.err.println(new String(cachingRequestWrapper.getContentAsByteArray()));
        return Arrays.toString(cachingRequestWrapper.getContentAsByteArray());
    }

    private String getRequestType(HttpServletRequest request) {
        return request.getMethod();
    }

    private String getRequestParams(HttpServletRequest request) {

        ContentCachingRequestWrapper cachingRequestWrapper = new ContentCachingRequestWrapper(request);
        StringBuilder params = new StringBuilder();
        cachingRequestWrapper.getParameterMap().forEach((k,v) -> {
            params.append(k).append("=").append(Arrays.toString(v)).append("\n");
        });

        if (params.length() == 0)
            return "NONE";
        else
            return params.toString();
    }

    private String getSession(HttpServletRequest request) {
        return request.getSession().getId();
    }

    private String getPrincipal(HttpServletRequest request) {
        return request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous user";
    }

    private String getReferer(HttpServletRequest request) {
        return request.getHeader("referer");
    }

    public String getFullURL(HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        final String queryString = request.getQueryString();

        return queryString == null ? requestURL.toString() : requestURL.append('?')
                .append(queryString)
                .toString();
    }

    public String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public String getClientOS(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public String getClientBrowser(HttpServletRequest request) {

        final String browserDetails = request.getHeader("User-Agent");

        if (browserDetails != null) {

            final String user = browserDetails.toLowerCase();
            String browser = "";

            if (user.contains("msie")) {

                String substring = browserDetails.substring(browserDetails.indexOf("MSIE")).split(";")[0];
                browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];

            } else if (user.contains("safari") && user.contains("version")) {

                browser = (browserDetails.substring(browserDetails.indexOf("Safari")).split(" ")[0]).split(
                        "/")[0] + "-" + (browserDetails.substring(browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];

            } else if (user.contains("opr") || user.contains("opera")) {

                if (user.contains("opera"))
                    browser = (browserDetails.substring(browserDetails.indexOf("Opera")).split(" ")[0]).split(
                            "/")[0] + "-" + (browserDetails.substring(browserDetails.indexOf("Version")).split(" ")[0]).split("/")[1];
                else if (user.contains("opr"))
                    browser = ((browserDetails.substring(browserDetails.indexOf("OPR")).split(" ")[0]).replace("/",
                            "-")).replace(
                            "OPR", "Opera");

            } else if (user.contains("chrome")) {

                browser = (browserDetails.substring(browserDetails.indexOf("Chrome")).split(" ")[0]).replace("/", "-");

            } else if ((user.contains("mozilla/7.0")) || (user.contains("netscape6")) || (user.contains("mozilla/4.7")) || (user.contains("mozilla/4.78")) || (user.contains("mozilla/4.08")) || (user.contains("mozilla/3"))) {

                //browser=(userAgent.substring(userAgent.indexOf("MSIE")).split(" ")[0]).replace("/", "-");
                browser = "Netscape-?";

            } else if (user.contains("firefox")) {
                browser = (browserDetails.substring(browserDetails.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
            } else if (user.contains("rv")) {
                browser = "IE";
            } else {
                browser = "Unknown, More-Info: " + browserDetails;
            }

            return browser;
        }

        return "NONE";
    }

}
