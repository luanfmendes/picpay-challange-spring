package br.com.challenge.picpay.services;

import br.com.challenge.picpay.domain.user.User;
import br.com.challenge.picpay.domain.user.UserType;
import br.com.challenge.picpay.dtos.UserDTO;
import br.com.challenge.picpay.repositories.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    public User createUser(UserDTO userDTO){
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        return repository.save(user);
    }
    public User findUserById(Long id) throws Exception {
        return this.repository.findUserById(id).orElseThrow(() -> new Exception("usuário não encontrado"));
    }
    public List<User> getAllUsers(){
        return this.repository.findAll();
    }

    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        if(sender.getUserType() == UserType.MERCHANT){
            throw new Exception("Usuário do tipo lojista não está autorizado a realizar transação");
        }

        if(sender.getBalance().compareTo(amount) < 0){
            throw new Exception("Saldo insuficiente");
        }
    }

    public void save(User user) {
        this.repository.save(user);
    }
}
