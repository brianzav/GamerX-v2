package br.com.senac.gamerx.service;

import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final HashingService hashingService;

    @Autowired
    public UserService(UserRepository userRepository, HashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    public UserModel createUser(String nome, String email, String password, String cpf, UserModel.Role role) {
        String hashedPassword = hashingService.hashPassword(password);
        UserModel newUser = new UserModel(nome, email, hashedPassword, cpf, role, true);
        return userRepository.save(newUser);
    }


}
