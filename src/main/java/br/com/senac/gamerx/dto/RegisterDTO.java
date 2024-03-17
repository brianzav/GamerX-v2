package br.com.senac.gamerx.dto;

import br.com.senac.gamerx.model.UserModel.Role;
import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
        private String nome;
        private String email;
        private String password;
        private Role role;
        private String cpf;
}

