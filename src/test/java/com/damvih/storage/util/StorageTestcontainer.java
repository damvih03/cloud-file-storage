package com.damvih.storage.util;

import com.damvih.storage.config.MinioClientConfig;
import com.damvih.storage.config.MinioClientProperties;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.github.dockerjava.api.model.HealthCheck;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@SpringBootTest(
        classes = {
                MinioClientConfig.class,
                BucketInitializer.class,

                StorageRepository.class,
        }
)
@EnableConfigurationProperties({MinioClientProperties.class})
@Testcontainers
@Slf4j
abstract public class StorageTestcontainer {

    private static final String USERNAME = "test";
    private static final String PASSWORD = "testroot";

    @Container
    private static final GenericContainer<?> storageContainer = new GenericContainer<>("minio/minio")
            .withEnv("MINIO_ROOT_USER", USERNAME)
            .withEnv("MINIO_ROOT_PASSWORD", PASSWORD)
            .withCommand("server /data")
            .withCreateContainerCmdModifier(cmd ->
                    cmd.withHealthcheck(new HealthCheck()
                            .withTest(List.of("CMD-SHELL", "mc ready local"))
                            .withInterval(5_000_000_000L)
                            .withRetries(3)
                    )
            )
            .waitingFor(Wait.forHealthcheck())
            .withExposedPorts(9000);


    @DynamicPropertySource
    public static void storageProperties(DynamicPropertyRegistry registry) {

        if (!storageContainer.isRunning()) {
            storageContainer.start();
        }

        String endpoint = String.format("http://%s:%s", storageContainer.getHost(), storageContainer.getMappedPort(9000));
        log.info("Test endpoint is '{}'", endpoint);

        registry.add("minio.client.endpoint", () -> endpoint);
        registry.add("minio.client.user", () -> USERNAME);
        registry.add("minio.client.password", () -> PASSWORD);
    }

}
