package com.example.demo.service.implement;

import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepo;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;




@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepo userRepo;
//    @Override
//    public List<User> getAllUser() {
//        return userRepo.findAll(Sort.by(Sort.Direction.ASC),"id");
//    }

    @Override
    public User updateUser(User newUser) {
        User exUser = userRepo.findById(newUser.getId()).get();
        if(newUser.getEmail() != exUser.getEmail()){
            exUser.setEmail(newUser.getEmail());
        }
        exUser.setLastName(newUser.getLastName());
        exUser.setFirstName(newUser.getFirstName());
        exUser.setPhoneNumber(newUser.getPhoneNumber());
        exUser.setAddress(newUser.getAddress());
        exUser.setAvatar(newUser.getAvatar());
        exUser.setBirthday(newUser.getBirthday());
        exUser.setMale(false);
        if (newUser.isMale()) {
            exUser.setMale(true);
        } else exUser.setMale(false);
        return userRepo.save(exUser);

    }

    @Override
    public User save(User user) {
        User newObj = new User();
        newObj.setEmail(user.getEmail());
        newObj.setPassword(user.getPhoneNumber());
        newObj.setLastName(user.getLastName());
        newObj.setFirstName(user.getFirstName());
        newObj.setPhoneNumber(user.getPhoneNumber());
        newObj.setAddress(user.getAddress());
        newObj.setAvatar(user.getAvatar());
        newObj.setBirthday(user.getBirthday());
        newObj.setRole(Role.EMPLOYEE);
        newObj.setMale(false);

        if (user.isMale()) {
            newObj.setMale(true);
        }
        return userRepo.save(newObj);
    }

    @Override
    public void deleteUserById(int id) {
        userRepo.deleteById(id);
    }

    @Override
    public User getUserById(int id) {
        return userRepo.findById(id).get();
    }



    @Override
    public Page<User> findPaginated(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return this.userRepo.findAll(pageable);

    }
}
