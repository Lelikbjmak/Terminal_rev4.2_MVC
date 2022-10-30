package com.example.Terminal_rev42.SecurityCustomImpl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public final class MetricRegisterImpl {

    public static final MetricRegistry metrics = new MetricRegistry();

    static {
        Logger logger = LoggerFactory.getLogger("monitoring");
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics).outputTo(logger).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();
        reporter.start(30, TimeUnit.MINUTES);
    }

    private MetricRegisterImpl() {
        throw new AssertionError();
    }

}
