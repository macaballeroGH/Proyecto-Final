package com.example.beautymanager.Servicio.servicioImpl;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.example.beautymanager.Modelo.Enums.RolesEnums;
import com.example.beautymanager.Servicio.JWTUtilityService;
import com.example.beautymanager.exception.BusinessException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JWTUtilityServiceImpl implements JWTUtilityService {

    //=======================
    //Configuracion JWT
    //=======================
    private static final Duration TOKEN_DURATION = Duration.ofHours(4);

    private static final String ISSUER = "BeautyManager";

    private static final String AUDIENCE = "BeautyManager-frontend";

    private static final String CLAIM_ROL = "rol";

    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    //=========================
    //Archivos de claves RSA
    //=========================
    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicResource;

    //=============================
    //Claves cargadas en memoria
    //=============================
    private PrivateKey privateKey;

    private PublicKey publicKey;

    //=========================================
    //Inicializar claves al arrancar Spring
    //=========================================
    @PostConstruct
    private void inicializarClaves(){
        try{

            privateKey = cargarPrivateKey(privateResource);

            publicKey = cargarPublicKey(publicResource);

        } catch (Exception e) {

            throw new IllegalStateException("No fue posible inicializar las claves JWT", e);
        }
    }

    //=======================
    //Cargar Private Key
    //=======================
    private PrivateKey cargarPrivateKey(Resource resource) throws Exception{

        byte[] keyBytes;

        try(var inputStream = resource.getInputStream()){
            keyBytes = inputStream.readAllBytes();
        }

        String key = new String(keyBytes, StandardCharsets.UTF_8).replace("-----BEGIN PRIVATE KEY-----","").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s","");

        byte[] decoded = Base64.getDecoder().decode(key);

        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    //=======================
    //Cargar Public Key
    //=======================
    private PublicKey cargarPublicKey(Resource resource) throws Exception{

        byte[] keyBytes;

        try(var inputStream = resource.getInputStream()){
            keyBytes = inputStream.readAllBytes();
        }

        String key = new String(keyBytes, StandardCharsets.UTF_8).replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replaceAll("\\s", "");

        byte[] decoded = Base64.getDecoder().decode(key);

        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
    }

    //=======================
    //Generar JWT
    //=======================
    @Override
    public String generateJWT(Long idUsuario, RolesEnums rol) throws Exception{

        Instant now = Instant.now();

        Instant expiration = now.plus(TOKEN_DURATION);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                
                //Usuario
                .subject(idUsuario.toString())
                
                //Rol
                .claim("rol",rol.name())
                
                //Emisor
                .issuer(ISSUER)
                
                //Destino
                .audience(AUDIENCE)
                
                //Fecha emision
                .issueTime(Date.from(now))
                
                //Fecha expiracion
                .expirationTime(Date.from(expiration))
                
                //ID unico del token
                .jwtID(UUID.randomUUID().toString())
                
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build(), claims);

        signedJWT.sign(new RSASSASigner(privateKey));

        return signedJWT.serialize();
    }

    //=======================
    //Parsear JWT
    //=======================
    @Override
    public JWTClaimsSet parseJWT(String token) throws Exception{

        //=========================
        //Validaciones basicas
        //=========================
        if(token == null || token.isBlank()){
            throw new BusinessException("El token es obligatorio");
        }

        //=====================
        //Parsear token
        //=====================
        SignedJWT signedJWT = SignedJWT.parse(token);

        if(!JWSAlgorithm.RS256.equals(signedJWT.getHeader().getAlgorithm())){
            throw new BusinessException("Algoritmo JWT invalido");
        }

        //=====================
        //Verificar firma RSA
        //=====================
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

        if(!signedJWT.verify(verifier)){
            throw new BusinessException("La firma del token no es valida");
        }

        //=====================
        //Obtener claims
        //=====================
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        //=====================
        //Validar expiracion
        //=====================
        if(claims.getExpirationTime() == null || claims.getExpirationTime().before(new Date())){

            throw new BusinessException("El token ha expirado");
        }

        //=====================
        //Validar Issuer
        //=====================
        if(!ISSUER.equals(claims.getIssuer())){

            throw new BusinessException("Issuer inválido");
        }

        //=====================
        //Validar Audience
        //=====================
        if(claims.getAudience() == null || !claims.getAudience().contains(AUDIENCE)){

            throw new BusinessException("Audience inválido");
        }

        return claims;
    }

    //======================
    //Obtener ID Usuario
    //======================
    @Override
    public Long getUserId(String token) throws Exception{

        return Long.valueOf(parseJWT(token).getSubject());
    }

    //=======================
    //Obtener rol
    //=======================
    @Override
    public RolesEnums getRol(String token) throws Exception{

        return RolesEnums.valueOf(parseJWT(token).getStringClaim(CLAIM_ROL));
    }

    //===========================
    //Obtener fecha expiracion
    //===========================
    @Override
    public LocalDateTime getFechaExpiracion(String token) throws Exception{

       return parseJWT(token).getExpirationTime().toInstant().atZone(DEFAULT_ZONE).toLocalDateTime();
    }

    //=======================
    //Validar JWT
    //=======================
    @Override
    public boolean isTokenValid(String token){

        try {

            parseJWT(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
