package com.example.beautymanager.Servicio.servicioImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.DTO.ActualizarEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.CrearEmpleadoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerEmpleadoDTO;
import com.example.beautymanager.Modelo.Entidad.EmpleadoEntity;
import com.example.beautymanager.Modelo.Entidad.RolEntity;
import com.example.beautymanager.Modelo.Entidad.UsuarioEntity;
import com.example.beautymanager.Modelo.Enums.EspecialidadEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoEmpleadoEnums;
import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;
import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.example.beautymanager.Repositorio.EmpleadoRepository;
import com.example.beautymanager.Repositorio.RolRepository;
import com.example.beautymanager.Repositorio.UsuarioRepository;
import com.example.beautymanager.Servicio.EmpleadoService;
import com.example.beautymanager.exception.BusinessException;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //======================
    //Crear empleado
    //======================
    @Override
    public ObtenerEmpleadoDTO crearEmpleado (CrearEmpleadoDTO crearEmpleado) {
        
        if(crearEmpleado == null){
            throw new BusinessException("El empleado no puede ser nulo");
        }

        if(crearEmpleado.getNombre() == null || crearEmpleado.getNombre().isBlank()){
            throw new BusinessException("El nombre es obligatorio");
        }

        if(crearEmpleado.getApellido() == null || crearEmpleado.getApellido().isBlank()){
            throw new BusinessException("El apellido es obligatorio");
        }

        if(crearEmpleado.getEmail() == null || crearEmpleado.getEmail().isBlank()){
            throw new BusinessException("El email es obligatorio");
        }

        String email = crearEmpleado.getEmail().trim().toLowerCase();

        if(usuarioRepository.existsByEmail(email)){
            throw new BusinessException("Ya existe un usuario con ese email");
        }

        if(crearEmpleado.getPassword() == null || crearEmpleado.getPassword().isBlank()){
            throw new BusinessException("La contraseña es obligatoria");
        }

        if(crearEmpleado.getTelefono() == null || crearEmpleado.getTelefono().isBlank()){
            throw new BusinessException("El telefono es obligatorio");
        }

        if(crearEmpleado.getEspecialidad() == null){
            throw new BusinessException("La especialidad es obligatoria");
        }

        RolEntity rol = rolRepository.findByNombre(RolesEnums.EMPLEADO)
                .orElseThrow(() -> new BusinessException("Rol EMPLEADO no encontrado"));

       

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setNombre(crearEmpleado.getNombre().trim());
        usuario.setApellido(crearEmpleado.getApellido().trim());
        usuario.setEmail(email);
        usuario.setTelefono(crearEmpleado.getTelefono().trim());
        usuario.setPassword(passwordEncoder.encode(crearEmpleado.getPassword()));
        usuario.setRol(rol);
        usuario.setEstadoUsuario(EstadoUsuarioEnums.ACTIVO);

        usuario = usuarioRepository.save(usuario);

        EmpleadoEntity empleado = new EmpleadoEntity();

        empleado.setUsuario(usuario);
        empleado.setEspecialidad(crearEmpleado.getEspecialidad());
        empleado.setEstadoEmpleado(EstadoEmpleadoEnums.ACTIVO);
        empleado.setFechaAlta(LocalDateTime.now());

        return toMap(empleadoRepository.save(empleado));
    }

    //=======================
    //Actualizar empleado
    //=======================
    @Override
    public ObtenerEmpleadoDTO actualizarEmpleado(Long id, ActualizarEmpleadoDTO actualizarEmpleado) {
        
        EmpleadoEntity existente = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        if(actualizarEmpleado == null){
            throw new BusinessException("Datos invalidos");
        }

        if(actualizarEmpleado.getEspecialidad() != null){
            existente.setEspecialidad(actualizarEmpleado.getEspecialidad());
        }

        if(actualizarEmpleado.getEstadoEmpleado() != null){
            existente.setEstadoEmpleado(actualizarEmpleado.getEstadoEmpleado());
        }

        return toMap(empleadoRepository.save(existente));
    }

    //=======================
    //Eliminar empleado
    //=======================
    @Override
    public void eliminarEmpleado(Long id){
        EmpleadoEntity empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        empleadoRepository.delete(empleado);
    }

    //===========================
    //Buscar empleado por id
    //===========================
    @Override
    public ObtenerEmpleadoDTO buscarPorId(Long id){

        return toMap(empleadoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado")));
    }

    //================================
    //Listar todos los empleados
    //================================
    @Override
    public List<ObtenerEmpleadoDTO> listarTodos(){
        
        return empleadoRepository.findAll().stream().map(this::toMap).toList();
    }

    //======================================
    //Buscar empleado por especialidad
    //======================================
    @Override
    public List<ObtenerEmpleadoDTO> buscarPorEspecialidad(EspecialidadEmpleadoEnums especialidad){
        return empleadoRepository.findByEspecialidad(especialidad).stream().map(this::toMap).toList();
    }

    //=============================
    //Buscar empleado por estado
    //=============================
    @Override
    public List<ObtenerEmpleadoDTO> buscarPorEstado(EstadoEmpleadoEnums estadoEmpleado){
        return empleadoRepository.findByEstadoEmpleado(estadoEmpleado).stream().map(this::toMap).toList();
    }

    //===============================================
    //Buscar Empleado por especialidad y estado
    //===============================================
    @Override
    public List<ObtenerEmpleadoDTO> buscarPorEspecialidadAndEstado(EspecialidadEmpleadoEnums especialidad, EstadoEmpleadoEnums estadoEmpleado){
        return empleadoRepository.findByEspecialidadAndEstadoEmpleado(especialidad, estadoEmpleado).stream().map(this::toMap).toList();
    }

    //==============================
    //Buscar empleado por email
    //==============================
    @Override
    public ObtenerEmpleadoDTO buscarPorEmail(String email){
        return toMap(empleadoRepository.findByUsuario_Email(email)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado")));
    }

    //=================================
    //Buscar por el id del usuario
    //=================================
    @Override
    public ObtenerEmpleadoDTO buscarPorUsuarioId(Long idUsuario){
        return toMap(empleadoRepository.findByUsuario_Id(idUsuario)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado")));
    }

    //=======================
    //Existe por usuario
    //=======================
    @Override
    public boolean existePorUsuario(Long idUsuario){
        return empleadoRepository.existsByUsuario_Id(idUsuario);
    }

    //=======================
    //Cambiar estado
    //=======================
    @Override
    public ObtenerEmpleadoDTO cambiarEstado(Long idEmpleado, EstadoEmpleadoEnums nuevoEstado){
        
        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));
        
        empleado.setEstadoEmpleado(nuevoEstado);

        return toMap(empleadoRepository.save(empleado));
    }

    //=============================
    //Dar de baja a un empleado
    //=============================
    @Override 
    public ObtenerEmpleadoDTO darDeBaja(Long idEmpleado){

        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        empleado.setEstadoEmpleado(EstadoEmpleadoEnums.INACTIVO);

        empleado.setFechaBaja(LocalDateTime.now());

        UsuarioEntity usuario = empleado.getUsuario();
        
        usuario.setEstadoUsuario(EstadoUsuarioEnums.INACTIVO);

        return toMap(empleadoRepository.save(empleado));
    }

    //===========================
    //Reactivar un empleado
    //===========================
    @Override
    public ObtenerEmpleadoDTO reactivarEmpleado(Long idEmpleado){

        EmpleadoEntity empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new BusinessException("Empleado no encontrado"));

        empleado.setEstadoEmpleado(EstadoEmpleadoEnums.ACTIVO);

        empleado.setFechaBaja(null);

        empleado.getUsuario().setEstadoUsuario(EstadoUsuarioEnums.ACTIVO);

        return toMap(empleadoRepository.save(empleado));
    }

    //==============================
    //Listar empleados activos
    //==============================
    @Override
    public List<ObtenerEmpleadoDTO> empleadosActivos(){

        return empleadoRepository.findByEstadoEmpleado(EstadoEmpleadoEnums.ACTIVO).stream().map(this::toMap).toList();
    }

    //==============================
    //Listar empleados inactivos
    //==============================
    @Override
    public List<ObtenerEmpleadoDTO> empleadosInactivos(){

        return empleadoRepository.findByFechaBajaIsNotNull().stream().map(this::toMap).toList();
    }

    //==============================
    //Listar altas entre fechas
    //==============================
    @Override
    public List<ObtenerEmpleadoDTO> altasEntreFechas(LocalDateTime inicio, LocalDateTime fin){

        return empleadoRepository.findByFechaAltaBetween(inicio, fin).stream().map(this::toMap).toList();
    }

    //==============================
    //Listar bajas entre fechas
    //==============================
    @Override
    public List<ObtenerEmpleadoDTO> bajasEntreFechas(LocalDateTime inicio, LocalDateTime fin){

        return empleadoRepository.findByFechaBajaBetween(inicio, fin).stream().map(this::toMap).toList();
    }

    @Override
    public ObtenerEmpleadoDTO toMap(EmpleadoEntity empleado) {

        ObtenerEmpleadoDTO obtenerEmpleadoDTO = new ObtenerEmpleadoDTO();

        obtenerEmpleadoDTO.setIdEmpleado(empleado.getId());

        //===================
        //Usuario
        //===================
        obtenerEmpleadoDTO.setIdUsuario(empleado.getUsuario().getId());
        obtenerEmpleadoDTO.setNombre(empleado.getUsuario().getNombre());
        obtenerEmpleadoDTO.setApellido(empleado.getUsuario().getApellido());
        obtenerEmpleadoDTO.setEmail(empleado.getUsuario().getEmail());
        obtenerEmpleadoDTO.setTelefono(empleado.getUsuario().getTelefono());
        obtenerEmpleadoDTO.setEstadoUsuario(empleado.getUsuario().getEstadoUsuario());

        //===================
        //Empleado
        //===================
        obtenerEmpleadoDTO.setEspecialidad(empleado.getEspecialidad());
        obtenerEmpleadoDTO.setFechaAlta(empleado.getFechaAlta());
        obtenerEmpleadoDTO.setFechaBaja(empleado.getFechaBaja());
        obtenerEmpleadoDTO.setEstadoEmpleado(empleado.getEstadoEmpleado());

        return obtenerEmpleadoDTO;
    }
}
