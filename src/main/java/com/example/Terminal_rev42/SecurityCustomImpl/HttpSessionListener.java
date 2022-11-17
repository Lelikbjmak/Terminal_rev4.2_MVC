package com.example.Terminal_rev42.SecurityCustomImpl;

import com.codahale.metrics.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.context.support.SecurityWebApplicationContextUtils;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSessionEvent;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpSessionListener extends HttpSessionEventPublisher {

    private final AtomicInteger activeSessions;

    private final Counter counterOfActiveSessions;

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionListener.class);

    @Autowired
    SessionRegistry sessionRegistry;

    public HttpSessionListener() {
        super();
        activeSessions = new AtomicInteger();
        this.counterOfActiveSessions = MetricRegisterImpl.metrics.counter("web.sessions.active.count");
    }


    @Override
    public final void sessionCreated(final HttpSessionEvent event) {
        activeSessions.incrementAndGet();
        counterOfActiveSessions.inc();

        logger.info("Session: " + event.getSession().getId() + " is created. Count of active sessions: " + counterOfActiveSessions.getCount());
    }

    ApplicationContext getContext(ServletContext servletContext) {
        return SecurityWebApplicationContextUtils.findRequiredWebApplicationContext(servletContext);
    }

    @Override
    public final void sessionDestroyed(final HttpSessionEvent event) {
        activeSessions.decrementAndGet();
        counterOfActiveSessions.dec();
        event.getSession().invalidate();
        logger.info("Session: " + event.getSession().getId() + " is destroyed. Session info: " + sessionRegistry.getSessionInformation(event.getSession().getId()) + ". Count of active sessions: " + counterOfActiveSessions.getCount());

    }

}
