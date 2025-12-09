package com.damvih.storage.listener;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.event.UserCreatedEvent;
import com.damvih.storage.repository.MinioRepository;
import com.damvih.storage.service.PathService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final MinioRepository minioRepository;
    private final PathService pathService;

    @EventListener
    @Async
    public void onUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        UserDto userDto = userCreatedEvent.getUserDto();
        String rootDirectoryName = pathService.getUserRootDirectoryName(userDto);
        try {
            minioRepository.createDirectory(rootDirectoryName);
            log.info("Directory created for userId: {}.", userDto.getId());
        } catch (Exception exception) {
            log.error("Failed to create user directory for userId: {}.", userDto.getId(), exception);
        }

    }

}
