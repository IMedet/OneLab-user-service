package kz.medet.userservice.service.impl;

import kz.medet.userservice.entity.User;
import kz.medet.userservice.exceptions.CustomException;
import kz.medet.userservice.repository.RoleRepository;
import kz.medet.userservice.repository.UserRepository;
import kz.medet.userservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public void register(String username, String password, String iin, String phoneNumber, String fio) {
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent()) {
            throw new CustomException("Пользователь с таким именем уже существует!");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setIin(iin);
        newUser.setPhoneNumber(phoneNumber);
        newUser.setFio(fio);

        userRepository.save(newUser);
        System.out.println("Регистрация успешна!");
    }

    public boolean login(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            System.out.println("Ошибка: Пользователь не найден!");
            return false;
        }

        User user = userOptional.get();
        if (!user.getPassword().equals(password)) {
            System.out.println("Ошибка: Неверный пароль!");
            return false;
        }

        System.out.println("Успешный вход! Добро пожаловать, " + username);
        return true;
    }

    @Override
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException("The user doesn't exist"));
    }

    @Override
    public User getUserByIin(String iin) {
        return userRepository.findByIin(iin).orElseThrow(() -> new CustomException("The user doesn't exist"));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new CustomException("The user doesn't exist"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
