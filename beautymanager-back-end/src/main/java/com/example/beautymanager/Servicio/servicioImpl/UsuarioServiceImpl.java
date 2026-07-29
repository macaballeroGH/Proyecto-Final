package com.example.beautymanager.Servicio.servicioImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.beautymanager.Modelo.DTO.ActualizarUsuarioDTO;
import com.example.beautymanager.Modelo.DTO.CambiarPasswordDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerUsuarioDTO;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.CarpetaArchivoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.ArchivoService;
import com.example.beautymanager.Servicio.UsuarioService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ArchivoService archivoService;

    @Override
    public List<ObtenerUsuarioDTO> findAllUsuario() {
        return usuarioRepository.findAll().stream().map(this::toMap).toList();
    }

    //=======================
    //Obtener por id
    //=======================
    @Override
    public ObtenerUsuarioDTO obtenerPorId(Long id) {

        return toMap(obtenerEntidadPorId(id));
    }

    //=======================
    //Obtener por email
    //=======================
    @Override
    public ObtenerUsuarioDTO obtenerPorEmail(String email) {

        if(email == null || email.isBlank()) {
            throw new BusinessException("El email es obligatorio");
        }

        return toMap(usuarioRepository.findByEmail(email.trim())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado")));
    }

    //=======================
    //Buscar por nombre
    //=======================
    @Override
    public List<ObtenerUsuarioDTO> buscarPorNombre(String nombre) {

        if(nombre == null || nombre.isBlank()) {
            throw new BusinessException(("El nombre es obligatorio"));
        }

        return usuarioRepository.findByNombreContainingIgnoreCase(nombre.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por apellido
    //=======================
    @Override
    public List<ObtenerUsuarioDTO> buscarPorApellido(String apellido) {

        if(apellido == null || apellido.isBlank()) {
            throw new BusinessException("El apellido es obligatorio");
        }

        return usuarioRepository.findByApellidoContainingIgnoreCase(apellido.trim()).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por rol
    //=======================
    @Override
    public List<ObtenerUsuarioDTO> buscarPorRol(Long idRol) {

        if(idRol == null || idRol <= 0) {
            throw new BusinessException("ID de rol invalido");
        }

        return usuarioRepository.findByRolId(idRol).stream().map(this::toMap).toList();
    }

    //=======================
    //Buscar por estado
    //=======================
    @Override
    public List<ObtenerUsuarioDTO> buscarPorEstado(EstadoUsuarioEnums estado){

        if(estado == null){
            throw new BusinessException("El estado es obligatorio");
        }

        return usuarioRepository.findByEstadoUsuario(estado).stream().map(this::toMap).toList();
    }

    //=======================
    //Actualizar usuario
    //=======================
    @Override
    public ObtenerUsuarioDTO actualizarUsuario(Long id, ActualizarUsuarioDTO actualizarUsuario) {

        UsuarioEntity usuario = obtenerEntidadPorId(id);

        if(actualizarUsuario == null) {
            throw new BusinessException("Los datos del usuario son obligatorios");
        }

        if(actualizarUsuario.getNombre() != null && !actualizarUsuario.getNombre().isBlank()) {
            usuario.setNombre(actualizarUsuario.getNombre().trim());
        }

        if(actualizarUsuario.getApellido() != null && !actualizarUsuario.getApellido().isBlank()) {
            usuario.setApellido(actualizarUsuario.getApellido().trim());
        }

        if(actualizarUsuario.getTelefono() != null && !actualizarUsuario.getTelefono().isBlank()) {
            usuario.setTelefono(actualizarUsuario.getTelefono().trim());
        }

        if(actualizarUsuario.getEmail() != null && !actualizarUsuario.getEmail().isBlank()) {

            String nuevoEmail = actualizarUsuario.getEmail().trim();

            if(!usuario.getEmail().equalsIgnoreCase(nuevoEmail) && usuarioRepository.existsByEmail(nuevoEmail)) {
                throw new BusinessException("El email ya esta registrado");
            }

            usuario.setEmail(nuevoEmail);
        }

        return toMap(usuarioRepository.save(usuario));
    }

    //=======================
    //Verificar email
    //=======================
    @Override
    public boolean existeEmail(String email) {

        if(email == null || email.isBlank()) {
            return false;
        }

        return usuarioRepository.existsByEmail(email.trim());
    }

    //=======================
    //Cambiar password
    //=======================
    @Override
    public void cambiarPassword(Long idUsuario, CambiarPasswordDTO cambiarPassword) {

        if(idUsuario == null || idUsuario <= 0){
            throw new BusinessException("ID de usuario invalido");
        }

        if(cambiarPassword == null){
            throw new BusinessException("Los datos son obligatorios");
        }

        UsuarioEntity usuario = obtenerEntidadPorId(idUsuario);

        if(cambiarPassword.getPasswordActual() == null || cambiarPassword.getPasswordActual().isBlank()){
            throw new BusinessException("La contraseña actual es obligatoria");
        }

        if(cambiarPassword.getPasswordNueva() == null || cambiarPassword.getPasswordNueva().isBlank()){
            throw new BusinessException("La contraseña nueva es obligatoria");
        }

        if(cambiarPassword.getConfirmarPasswordNueva() == null || cambiarPassword.getConfirmarPasswordNueva().isBlank()){
            throw new BusinessException("Debe confirmar la contraseña nueva");
        }

        if(!passwordEncoder.matches(cambiarPassword.getPasswordActual(), usuario.getPassword())){
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        if(!cambiarPassword.getPasswordNueva().equals(cambiarPassword.getConfirmarPasswordNueva())){
            throw new BusinessException("Las contraseñas no coinciden");
        }

        usuario.setPassword(passwordEncoder.encode(cambiarPassword.getPasswordNueva()));

        usuarioRepository.save(usuario);
    }

    //=======================
    //Actualizar foto perfil
    //=======================
    @Override
    public void actualizarFotoPerfil(Long idUsuario, MultipartFile foto){
        
        if(idUsuario == null || idUsuario <= 0){
            throw new BusinessException("ID de usuario invalido");
        }

        if(foto == null || foto.isEmpty()){
            throw new BusinessException("La foto de perfil es obligatoria");
        }

        UsuarioEntity usuario = obtenerEntidadPorId(idUsuario);

        if(usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isBlank()){
            archivoService.eliminarArchivo(usuario.getFotoPerfil(), CarpetaArchivoEnums.PERFILES);
        }

        String nombreArchivo = archivoService.guardarArchivo(foto, CarpetaArchivoEnums.PERFILES);

        usuario.setFotoPerfil(nombreArchivo);

        usuarioRepository.save(usuario);
    }

    //=======================
    //Eliminar foto perfil
    //=======================
    @Override
    public void eliminarFotoPerfil(Long idUsuario){
        
        if(idUsuario == null || idUsuario <= 0){
            throw new BusinessException("ID de usuario invalido");
        }

        UsuarioEntity usuario = obtenerEntidadPorId(idUsuario);

        if(usuario.getFotoPerfil() == null || usuario.getFotoPerfil().isBlank()){
            throw new BusinessException("El usuario no tiene foto de perfil");
        }

        archivoService.eliminarArchivo(usuario.getFotoPerfil(), CarpetaArchivoEnums.PERFILES);

        usuario.setFotoPerfil(null);

        usuarioRepository.save(usuario);
    }

    private UsuarioEntity obtenerEntidadPorId(Long id){

        if(id == null || id <= 0){
            throw new BusinessException("ID de usuario invalido");
        }

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
    }

    @Override
    public ObtenerUsuarioDTO toMap(UsuarioEntity usuario) {

        ObtenerUsuarioDTO obtenerUsuarioDTO = new ObtenerUsuarioDTO();

        obtenerUsuarioDTO.setIdUsuario(usuario.getId());
        obtenerUsuarioDTO.setNombre(usuario.getNombre());
        obtenerUsuarioDTO.setApellido(usuario.getApellido());
        obtenerUsuarioDTO.setTelefono(usuario.getTelefono());
        obtenerUsuarioDTO.setEmail(usuario.getEmail());
        obtenerUsuarioDTO.setFotoPerfil(archivoService.obtenerRutaArchivo(usuario.getFotoPerfil(), CarpetaArchivoEnums.PERFILES));
        obtenerUsuarioDTO.setRol(usuario.getRol().getNombre());
        obtenerUsuarioDTO.setEstadoUsuario(usuario.getEstadoUsuario());

        return obtenerUsuarioDTO;
    }

}
