package com.app.ecom_microservices.service;

import com.app.ecom_microservices.dto.AddressDTO;
import com.app.ecom_microservices.dto.UserRequest;
import com.app.ecom_microservices.dto.UserResponse;
import com.app.ecom_microservices.module.Address;
import com.app.ecom_microservices.module.User;
import com.app.ecom_microservices.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public List<UserResponse> fetchAllUsers() {
        return repository.findAll()
                .stream().map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public Optional<User> fetchOptionalUser(Long id) {
        return repository.findById(id);
    }

    public Optional<UserResponse> fetchUser(Long id) {
        return fetchOptionalUser(id).map(this::mapToUserResponse);
    }

    public void createUser(UserRequest userRequest) {
        var user = new User();
        updateUserFromRequest(user, userRequest);
        repository.save(user);
    }

    public boolean updateUser(Long id, UserRequest updatedUser) {
        return fetchOptionalUser(id)
                .map(existingUser -> {
                    updateUserFromRequest(existingUser, updatedUser);
                    repository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    public void deleteUser(Long id) {
        repository.deleteById(id);
    }

    private void updateUserFromRequest(User user, UserRequest userRequest) {
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());

        if (userRequest.getAddress() != null) {
            var address = new Address()
                    .withCity(userRequest.getAddress().getCity())
                    .withStreet(userRequest.getAddress().getStreet())
                    .withCountry(userRequest.getAddress().getCountry())
                    .withState(userRequest.getAddress().getState())
                    .withZipcode(userRequest.getAddress().getZipcode());
            user.setAddress(address);
        }
    }

    private UserResponse mapToUserResponse(User user) {
        var address = new AddressDTO()
                .withId(String.valueOf(user.getAddress().getId()))
                .withCity(user.getAddress().getCity())
                .withStreet(user.getAddress().getStreet())
                .withCountry(user.getAddress().getCountry())
                .withState(user.getAddress().getState())
                .withZipcode(user.getAddress().getZipcode());

        return new UserResponse()
                .withId(String.valueOf(user.getId()))
                .withFirstName(user.getFirstName())
                .withLastName(user.getLastName())
                .withEmail(user.getEmail())
                .withPhone(user.getPhone())
                .withRole(user.getRole())
                .withAddress(address);
    }

}
