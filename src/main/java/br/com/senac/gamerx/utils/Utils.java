package br.com.senac.gamerx.utils;

import br.com.senac.gamerx.dto.UserDTO;
import br.com.senac.gamerx.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<UserDTO> convertModelToUserDTO(List<UserModel> userModels) {
        List<UserDTO> userListDTO = new ArrayList<>();
        for (UserModel userModel : userModels) {
            UserDTO userDTO = new UserDTO();
            userDTO.setName(userModel.getNome());
            userDTO.setEmail(userModel.getEmail());
            userDTO.setRole(String.valueOf(userModel.getRole()));
            userDTO.setActive(userModel.isActive());
            userListDTO.add(userDTO);
        }

        return userListDTO;
    }
}
