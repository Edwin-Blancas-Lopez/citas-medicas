package com.edwin.auth.services;

import com.edwin.auth.dto.UsuarioRequest;
import com.edwin.auth.dto.UsuarioResponse;
import com.edwin.auth.dto.UsuarioUpdateRequest;

import java.util.Set;

public interface UsuarioService {
    Set<UsuarioResponse> listar();
    UsuarioResponse registrar(UsuarioRequest request);
    UsuarioResponse actualizar(String username, UsuarioUpdateRequest request);
    void eliminar(String username);
}