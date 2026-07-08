package org.racetobid.racetobid.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * OpenTelemetry SDK'yı OTLP HTTP exporter ile tam olarak konfigüre eder.
 * spring-boot-starter-opentelemetry auto-configuration'ı @ConditionalOnMissingBean(OpenTelemetry.class)
 * kullandığı için bu explicit bean devreye girer; auto-config devre dışı kalır.
 */
@Configuration
public class OpenTelemetryConfig {

    @Value("${management.otlp.tracing.endpoint:http://localhost:4318/v1/traces}")
    private String tracingEndpoint;

    @Value("${spring.application.name:RaceToBid}")
    private String serviceName;

    private OpenTelemetry openTelemetry;

    /**
     * OpenTelemetry SDK bean'i: SigNoz'a OTLP/HTTP üzerinden span gönderen tam konfigürasyon.
     * Bu @Bean, Spring Boot auto-config'in @ConditionalOnMissingBean koşulunu geçerek
     * tek ve kesin OpenTelemetry örneği olarak tüm uygulama genelinde kullanılır.
     */
    @Bean
    public OpenTelemetry openTelemetry() {
        // service.name → SigNoz UI'da görünecek servis adı
        Resource resource = Resource.getDefault()
                .merge(Resource.create(
                        Attributes.of(AttributeKey.stringKey("service.name"), serviceName)
                ));

        // OTLP HTTP exporter → SigNoz collector'ın 4318 HTTP portuna gönderir
        OtlpHttpSpanExporter spanExporter = OtlpHttpSpanExporter.builder()
                .setEndpoint(tracingEndpoint)
                .setTimeout(Duration.ofSeconds(10))
                .build();

        // BatchSpanProcessor: span'leri biriktirip toplu gönderir (performans için)
        SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter)
                        .setScheduleDelay(Duration.ofMillis(500))
                        .build())
                .setResource(resource)
                .build();

        // W3C TraceContext propagation (traceparent/tracestate header'ları)
        this.openTelemetry = OpenTelemetrySdk.builder()
                .setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();

        return this.openTelemetry;
    }

    /**
     * Uygulama başladığında Logback OTel Appender'ına SDK instance'ını bağlar.
     * Bu sayede loglar da trace/span context bilgisiyle birlikte SigNoz'a iletilir.
     */
    @PostConstruct
    public void setupLoggingAppender() {
        if (this.openTelemetry != null) {
            OpenTelemetryAppender.install(this.openTelemetry);
        }
    }

    /**
     * Servis sınıflarında manuel span oluşturmak için Tracer döner.
     * Örnek: openTelemetryConfig.getTracer(BiddingServiceImpl.class)
     */
    public Tracer getTracer(Class<?> type) {
        return openTelemetry.getTracer(type.getName());
    }

    /**
     * Servis sınıflarında custom metrik oluşturmak için Meter döner.
     */
    public Meter getMeter(Class<?> type) {
        return openTelemetry.getMeter(type.getName());
    }
}