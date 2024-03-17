package br.com.senac.gamerx.service;

import br.com.senac.gamerx.model.UserModel;
import br.com.senac.gamerx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserModel registerUser(String nome, String email, String password, String cpf, UserModel.Role role) {
        UserModel newUser = new UserModel();
        newUser.setNome(nome);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setCpf(cpf);
        newUser.setRole(role);
        newUser.setActive(true);
        return userRepository.save(newUser);
    }


}
