package br.com.senac.gamerx.utils;

import br.com.senac.gamerx.dto.UserDTO;
import br.com.senac.gamerx.model.UserModel;

public class GamerXUtils {

        public static UserDTO convertModelToUserDTO(UserModel userModel) {
            UserDTO userDTO = new UserDTO();
            userDTO.setName(userModel.getNome());
            userDTO.setEmail(userModel.getEmail());
            userDTO.setRole(String.valueOf(userModel.getRole()));
            userDTO.setActive(userModel.isActive());
            return userDTO;
        }
    }

