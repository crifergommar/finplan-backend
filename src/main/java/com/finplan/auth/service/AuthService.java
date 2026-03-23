package com.finplan.auth.service;

import com.finplan.auth.model.*;
import com.finplan.auth.repository.RefreshTokenRepository;
import com.finplan.auth.repository.UsuarioRepository;
import com.finplan.shared.exception.BusinessException;
import com.finplan.shared.exception.ResourceNotFoundException;
import com.finplan.shared.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public AuthResponse registro(RegistroRequest request,
                                 HttpServletResponse response) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(
                    "El email ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombre(request.getNombre())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(Rol.USUARIO)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);
        return generarTokensYRespuesta(usuario, response);
    }

    public AuthResponse login(LoginRequest request,
                              HttpServletResponse response) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        return generarTokensYRespuesta(usuario, response);
    }

    public AuthResponse refresh(String refreshToken,
                                HttpServletResponse response) {
        if (refreshToken == null) {
            throw new BusinessException("Refresh token no encontrado");
        }

        RefreshToken rt = refreshTokenRepository
                .findByToken(refreshToken)
                .orElseThrow(() ->
                        new BusinessException("Refresh token inválido"));

        if (rt.isRevocado() || rt.estaExpirado()) {
            throw new BusinessException("Refresh token expirado");
        }

        Usuario usuario = rt.getUsuario();
        rt.setRevocado(true);
        refreshTokenRepository.save(rt);

        return generarTokensYRespuesta(usuario, response);
    }

    public void logout(String refreshToken,
                       HttpServletResponse response) {
        if (refreshToken != null) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(rt -> {
                        rt.setRevocado(true);
                        refreshTokenRepository.save(rt);
                    });
        }
        eliminarCookie(response);
    }

    public UsuarioPerfilResponse obtenerPerfil(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Usuario no encontrado"));

        return UsuarioPerfilResponse.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    private AuthResponse generarTokensYRespuesta(
            Usuario usuario, HttpServletResponse response) {

        String accessToken = jwtUtil.generarAccessToken(usuario);
        String refreshToken = generarRefreshToken(usuario);
        agregarCookieRefresh(response, refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .tipo("Bearer")
                .email(usuario.getEmail())
                .nombre(usuario.getNombre())
                .rol(usuario.getRol().name())
                .build();
    }

    private String generarRefreshToken(Usuario usuario) {
        refreshTokenRepository.deleteByUsuario(usuario);

        String token = UUID.randomUUID().toString();
        RefreshToken rt = RefreshToken.builder()
                .token(token)
                .usuario(usuario)
                .fechaExpiracion(Instant.now()
                        .plusMillis(refreshTokenExpiration))
                .revocado(false)
                .build();

        refreshTokenRepository.save(rt);
        return token;
    }

    private void agregarCookieRefresh(
            HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true en producción
        cookie.setPath("/");
        cookie.setMaxAge((int)(refreshTokenExpiration / 1000));
        response.addCookie(cookie);
    }

    private void eliminarCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}