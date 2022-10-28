package com.example.Terminal_rev42.EventsListeners;

import com.codahale.metrics.Counter;
import com.example.Terminal_rev42.resoursec.MetricRegisterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpSessionListener implements javax.servlet.http.HttpSessionListener {

    private final AtomicInteger activeSessions;

    private final Counter counterOfActiveSessions;

    private static final Logger logger = LoggerFactory.getLogger(HttpSessionListener.class);

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

    @Override
    public final void sessionDestroyed(final HttpSessionEvent event) {
        activeSessions.decrementAndGet();
        counterOfActiveSessions.dec();
        logger.info("Session: " + event.getSession().getId() + " is destroyed. Count of active sessions: " + counterOfActiveSessions.getCount());

    }

    public AtomicInteger getActiveSessions() {
        return activeSessions;
    }

    public Counter getCounterOfActiveSessions() {
        return counterOfActiveSessions;
    }
}
