package verso.caixa.security.jwt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BCryptAdapter {

    public String hash(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray()); //O número 12 é o cost factor — quanto maior, mais seguro (e mais lento).
        /*
        1. Strings são imutáveis
        - Uma vez criada, a String da senha não pode ser modificada.
        - Isso significa que ela fica na memória até que o Garbage Collector decida removê-la — o que pode demorar.
        2. char[] pode ser apagado manualmente
        - Você pode sobrescrever o array com zeros (Arrays.fill(chars, '\0')) logo após o uso.
        - Isso reduz o tempo que a senha fica exposta na memória.
        3. Menor risco em dumps de memória
        - Se a aplicação sofrer um ataque que acessa a heap (ex: memory dump), senhas em String podem ser facilmente encontradas.
        - Com char[], você tem mais controle e pode minimizar esse risco.
         */
    }

    public boolean verify(String plainPassword, String hashedPassword) {
        return BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword).verified;
    }
}

