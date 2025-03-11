package kz.medet.userservice.service;

import kz.medet.userservice.entity.User;

import java.util.List;

public interface IUserService {
    void delete(String username);
    User getUserById(Long id);
    User getUserByIin(String iin);
    User getUserByUsername(String username);
    List<User> getAllUsers();

}
