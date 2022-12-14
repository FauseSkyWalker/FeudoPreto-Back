package com.terrasdevastadas.site.service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

//import com.terrasdevastadas.site.builder.UsuarioBuilder;
import com.terrasdevastadas.site.dto.UsuarioDTO;
import com.terrasdevastadas.site.dto.UsuarioLoginDTO;
import com.terrasdevastadas.site.model.Usuario;
import com.terrasdevastadas.site.repository.UsuarioRepository;

@Service
public class UsuarioService {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	

	public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
	
	
	public List<Usuario> getAllUsuarios(){

	        return usuarioRepository.findAll();
	    }
	 
	
	public Optional<Usuario> getById(Long id){
		
		return usuarioRepository.findById(id);
		
	    }
	 
	 
	public Usuario CadastrarUsuario(UsuarioDTO usuarioDTO) {
	       Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioDTO.getEmail());

	        if (usuario.isPresent()) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esse e-mail já está sendo utilizado");
	        } else {
	            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	            usuarioDTO.setSenha(encoder.encode(usuarioDTO.getSenha()));

	            Usuario novoUsuario = new Usuario();
	            		
	            novoUsuario.setNome(usuarioDTO.getNome());
	            novoUsuario.setEmail(usuarioDTO.getEmail());
	            novoUsuario.setSenha(usuarioDTO.getSenha());

	            return usuarioRepository.save(novoUsuario);
	        }
	    }
	
    
    public Optional<UsuarioLoginDTO> validarCredential(Optional<UsuarioLoginDTO> usuarioDTO) {
		
    	BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    	
		Optional<Usuario> usuario = usuarioRepository.findByEmail(usuarioDTO.get().getEmail());
		
		if(usuario.isPresent()) {
			if(encoder.matches(usuarioDTO.get().getSenha(), usuario.get().getSenha())) {
				String auth = usuarioDTO.get().getEmail() + ":" + usuarioDTO.get().getSenha();
				
				byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
				String authHeader = "Basic " + new String(encodedAuth);
				
				usuarioDTO.get().setToken(authHeader);
				usuarioDTO.get().setNome(usuario.get().getNome());
				
				return usuarioDTO;
			}
		}
		
		return null;
	}
    
    
    public Usuario update(Usuario usuario, Long id){

		 Usuario novoUsuario = usuarioRepository.findById(id).get();

	  
	     if (Objects.nonNull(usuario.getNome()) && !"".equalsIgnoreCase(usuario.getNome())) {
	    	 novoUsuario.setNome(usuario.getNome());
	         }
	  
	     if (Objects.nonNull(usuario.getEmail()) && !"".equalsIgnoreCase(usuario.getEmail())) {
	    	 novoUsuario.setEmail(usuario.getEmail());
	         }
	     
	     if (Objects.nonNull(usuario.getSenha()) && !"".equalsIgnoreCase(usuario.getSenha())) {
	    	 BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
	    	 novoUsuario.setSenha(encoder.encode(usuario.getSenha()));
         }
	     
	     if (Objects.nonNull(usuario.getPontos())) {
	    	 novoUsuario.setPontos(usuario.getPontos());
         }
	     
	        return usuarioRepository.save(novoUsuario);
		}
	 
	  
	 
	 public void delete (Long id){

	        usuarioRepository.deleteById(id);
	   }

}
