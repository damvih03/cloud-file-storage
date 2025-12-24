package com.damvih.storage.listener;

import com.damvih.authentication.dto.UserDto;
import com.damvih.authentication.event.UserCreatedEvent;
import com.damvih.storage.repository.StorageRepository;
import com.damvih.storage.service.PathComponents;
import com.damvih.storage.util.PathComponentsBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCreatedListener {

    private final StorageRepository storageRepository;

    @EventListener
    @Async
    public void perform(UserCreatedEvent userCreatedEvent) {
        UserDto userDto = userCreatedEvent.getUserDto();
        PathComponents pathComponents = PathComponentsBuilder.build("", userDto);
        try {
            storageRepository.createDirectory(pathComponents.getRootDirectory());
            log.info("Root directory created for UserID: {}.", userDto.id());
        } catch (Exception exception) {
            log.error("Failed to create user directory for userId: {}.", userDto.id(), exception);
        }

    }

}
