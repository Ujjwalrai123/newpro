package com.InventoryManagement.InventoryManagement.service.impl;

import com.InventoryManagement.InventoryManagement.exception.UserCustomException;
import com.InventoryManagement.InventoryManagement.model.entity.UserBE;
import com.InventoryManagement.InventoryManagement.model.request.UserRequest;
import com.InventoryManagement.InventoryManagement.repository.UserRepository;
import com.InventoryManagement.InventoryManagement.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.InventoryManagement.InventoryManagement.constants.InventoryConstants.USER_NOT_FOUND;

@Service  // ✅ This is mandatory to make it a Spring-managed bean
public class UserServiceIMPL implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserBE addUser(UserRequest userRequest) {
        String encodedPassword=passwordEncoder.encode(userRequest.getPassword());
        UserBE userBE = modelMapper.map(userRequest, UserBE.class);
        //yha pe user login ka data save ho rha hai
        userBE.setPassword(encodedPassword);
        return userRepository.save(userBE);
    }

    @Override
    public List<UserBE> getALlUser() {
        return userRepository.findAll();
    }

    @Override
    public UserBE getUserById(Long id) {
        Optional<UserBE> userBE = userRepository.findById(id);
        if(userBE.isEmpty()){
            throw new UserCustomException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return userBE.get();
    }

    @Override
    public boolean deleteUser(Long id) {
        Optional<UserBE> userBE = userRepository.findById(id);
        if (userBE.isEmpty()) {
            throw new UserCustomException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
        return true;
    }


    @Override
    public UserBE authenticate(String userName, String password) {
        Optional<UserBE> optionalUser = userRepository.findByUserName(userName);
        if (optionalUser.isPresent()) {
            UserBE user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public UserBE authenticateByEmail(String email, String password) {
        Optional<UserBE> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            UserBE user = optionalUser.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }


}
