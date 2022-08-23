package io.gxstar.lesson1;

import com.google.common.collect.ImmutableMap;

import io.jaegertracing.Configuration;
import io.jaegertracing.internal.JaegerTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;

/**
 * Created by @author Ifeanyichukwu Otiwa
 * 23/08/2022
 */

public class Hello {

    private final Tracer tracer;

    public Hello(final Tracer tracer) {
        this.tracer = tracer;
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("Expecting One argument");
        }

        String helloTo = args[0];
        Tracer tracer = initTracer("say-hello");
        new Hello(tracer).sayHello(helloTo);
    }

    public static JaegerTracer initTracer(String service) {
        Configuration.SamplerConfiguration samplerConfig =
                Configuration.SamplerConfiguration.fromEnv().withType("const").withParam(1);
        Configuration.ReporterConfiguration reporterConfig =
                Configuration.ReporterConfiguration.fromEnv().withLogSpans(true);
        Configuration config = new Configuration(service).withSampler(samplerConfig).withReporter(reporterConfig);
        return config.getTracer();

    }

    private void sayHello(String helloTo) {
        Span span = tracer.buildSpan("Say-Hello").start();
        span.setTag("hello-to", helloTo);

        String helloStr = String.format("Hello, %s!", helloTo);
        span.log(ImmutableMap.of("event", "string-format", "value", helloStr));

        System.out.println(helloStr);
        span.log(ImmutableMap.of("event", "println"));
        span.finish();

    }
}
