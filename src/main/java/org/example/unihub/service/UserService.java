package org.example.unihub.service;

import lombok.RequiredArgsConstructor;
import org.example.unihub.config.CacheConfig;
import org.example.unihub.dto.UserDTO;
import org.example.unihub.dto.UpdateUserRequest;
import org.example.unihub.entity.User;
import org.example.unihub.repository.UserRepository;
import org.example.unihub.utils.Mapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final Mapper mapper;
    @Cacheable(value = CacheConfig.USERS_CACHE, key = "#id")
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(mapper::toUserDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(mapper::toUserDTO)
                .collect(Collectors.toList());
    }


    @CachePut(value = CacheConfig.USERS_CACHE, key = "#id")
    public UserDTO updateUser(Long id, UpdateUserRequest updateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (updateRequest.getFirstName() != null) {
            existingUser.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            existingUser.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            existingUser.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getAddress() != null) {
            existingUser.setAddress(updateRequest.getAddress());
        }
        
        User savedUser = userRepository.save(existingUser);
        return mapper.toUserDTO(savedUser);
    }

    @CacheEvict(value = CacheConfig.USERS_CACHE, key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    @CacheEvict(value = CacheConfig.USERS_CACHE, allEntries = true)
    public void clearCache() {
        // This method will clear the entire users cache
    }

}