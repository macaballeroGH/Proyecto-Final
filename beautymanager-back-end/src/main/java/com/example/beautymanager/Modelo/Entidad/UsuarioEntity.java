package com.example.beautymanager.Modelo.Entidad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.beautymanager.Modelo.Enums.EstadoUsuarioEnums;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import jakarta.persistence.OneToOne;

@Entity
@Data

@Table(name = "usuario", schema = "beautymanager", uniqueConstraints = { @UniqueConstraint(columnNames = { "email" }) })
public class UsuarioEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "apellido", nullable = false, length = 200)
    private String apellido;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "email", nullable = false, length = 250)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_usuario", nullable = false)
    private EstadoUsuarioEnums estadoUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolEntity rol;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private AdminEntity admin;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private EmpleadoEntity empleado;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private ClienteEntity cliente;

    @OneToOne(mappedBy = "usuario", fetch = FetchType.LAZY)
    private BilleteraEntity billetera;

    @OneToMany(mappedBy = "usuario")
    private List<AuditoriaEntity> auditoria = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<NotificacionEntity> notificaciones = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<CarritoEntity> carritos = new ArrayList<>();

    @OneToMany(mappedBy = "usuario")
    private List<MovimientoFinanzasEntity> movimientoFinanzas = new ArrayList<>();

    // Metodos de UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.rol.getNombre()));

        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.estadoUsuario == EstadoUsuarioEnums.ACTIVO;
    }
}
